package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.entity.DashboardScanProgress;
import com.company.clawboard.mapper.*;
import com.company.clawboard.parser.TranscriptParser;
import com.company.clawboard.service.DataIngestionService;
import com.company.clawboard.service.IngestionResult;
import com.company.clawboard.service.IngestionStatus;
import com.company.clawboard.service.ReportGenerator;
import com.company.clawboard.service.ScanProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanOrchestrator {

    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    
    private final ClawboardProperties properties;
    private final UserScanner userScanner;
    private final TranscriptFileScanner fileScanner;
    private final AccountsCsvReader accountsReader;
    private final TranscriptParser transcriptParser;
    private final ScanProgressService scanProgressService;
    private final DataIngestionService dataIngestionService;
    private final ReportGenerator reportGenerator;
    private final HourlyStatsAggregator hourlyStatsAggregator;
    
    // Mappers for batch insert
    private final MessageMapper messageMapper;
    private final ConversationTurnMapper turnMapper;
    private final SkillInvocationMapper skillMapper;
    private final TranscriptIssueMapper issueMapper;
    private final ScanHistoryMapper scanHistoryMapper;
    
    @Qualifier("scanExecutor")
    private final Executor scanExecutor;
    
    // Concurrency control
    private final AtomicBoolean scanning = new AtomicBoolean(false);
    private volatile Long currentScanId = null;
    
    // Thread-safe set to collect all scanned file paths (relative to base path)
    private Set<String> scannedFilePaths;
    
    // Thread-safe set to collect all employee IDs scanned in this run
    private Set<String> scannedEmployeeIds;
    
    // Thread-safe map to collect all skipped files with their error messages
    private ConcurrentHashMap<String, String> skippedFiles;
    
    public boolean isScanning() { return scanning.get(); }
    public Long getCurrentScanId() { return currentScanId; }

    @Scheduled(cron = "${clawboard.scan.cron}")
    public void scheduledScan() {
        if (!properties.getScan().isEnabled()) {
            log.info("Scanning is disabled");
            return;
        }
        executeScan("scheduled");
    }

    public Long executeScan(String triggerType) {
        // Concurrency control: prevent overlapping scans
        if (!scanning.compareAndSet(false, true)) {
            log.warn("Scan skipped: previous scan still running (scanId={})", currentScanId);
            return null;
        }
        
        log.info("Starting {} scan", triggerType);
        long startTime = System.currentTimeMillis();

        DashboardScanHistory history = new DashboardScanHistory();
        history.setTriggerType(triggerType);
        history.setStatus("running");
        history.setStartedAt(LocalDateTime.now());
        history.setUsersScanned(0);
        history.setDirsScanned(0);
        history.setFilesTotal(0);
        history.setFilesProcessed(0);
        history.setFilesSkipped(0);
        history.setFilesError(0);
        history.setNewMessages(0);
        history.setNewTurns(0);
        history.setNewIssues(0);
        history.setNewSkillCalls(0);

        scanHistoryMapper.insertAndGetId(history);
        Long scanId = history.getId();
        currentScanId = scanId;
        log.info("Created scan record with ID: {}", scanId);

        try {
            // Step 1: Load accounts mapping from database
            // This replaces the old CSV-based approach
            accountsReader.loadFromDatabase();
            log.info("Loaded {} employee mappings from database", accountsReader.getEmployeeCount());

            // For backward compatibility, still load from CSV if database load fails
            if (accountsReader.getEmployeeCount() == 0) {
                String csvPath = properties.getNas().getAccountsCsvPath();
                if (csvPath != null && !csvPath.isEmpty()) {
                    Path csvFilePath = Path.of(csvPath);
                    if (!csvFilePath.isAbsolute()) {
                        // Resolve relative to current working directory
                        csvFilePath = Path.of(System.getProperty("user.dir"), csvPath);
                    }
                    accountsReader.loadFromCsv(csvFilePath);
                    log.info("Fallback: Loaded {} employee mappings from CSV", accountsReader.getEmployeeCount());
                }
            }

            // Initialize file path collections
            scannedFilePaths = ConcurrentHashMap.newKeySet();
            scannedEmployeeIds = ConcurrentHashMap.newKeySet();
            skippedFiles = new ConcurrentHashMap<>();

            // Step 2: Scan users
            var users = userScanner.scanUsers();
            history.setUsersScanned(users.size());
            log.info("Found {} users to scan", users.size());

            if (users.isEmpty()) {
                log.info("No users found to scan");
                finishScan(scanId, history, "completed", startTime, null);
                return scanId;
            }

            // Step 3: Submit parallel scan tasks for each user
            var futures = new ConcurrentHashMap<String, CompletableFuture<UserScanResult>>();
            for (String employeeId : users) {
                CompletableFuture<UserScanResult> future = CompletableFuture.supplyAsync(
                    () -> scanUserDirectory(employeeId, scanId),
                    scanExecutor
                );
                futures.put(employeeId, future);
            }

            // Step 4: Collect results from all users
            int totalFiles = 0;
            int processedFiles = 0;
            int skippedFilesCount = 0;
            int errorFiles = 0;
            int totalMessages = 0;
            int totalTurns = 0;
            int totalIssues = 0;
            int totalSkills = 0;

            // ✅ 在一次遍历中同时完成统计和用户计数
            int actualUsersScanned = 0;
            for (var entry : futures.entrySet()) {
                try {
                    UserScanResult result = entry.getValue().get(30, TimeUnit.MINUTES);
                    
                    // 累加统计数据
                    totalFiles += result.filesTotal();
                    processedFiles += result.filesProcessed();
                    skippedFilesCount += result.filesSkipped();
                    errorFiles += result.filesError();
                    totalMessages += result.totalMessages();
                    totalTurns += result.totalTurns();
                    totalIssues += result.totalIssues();
                    totalSkills += result.totalSkills();
                    
                    // 计算实际用户数
                    if (result.filesProcessed() > 0 || result.filesSkipped() > 0 || result.filesError() > 0) {
                        actualUsersScanned++;
                    }
                    
                    log.debug("User {} scan completed: {} files, {} messages, {} turns",
                        entry.getKey(), result.filesProcessed(), result.totalMessages(), result.totalTurns());
                } catch (Exception e) {
                    log.error("Scan failed for user {}", entry.getKey(), e);
                    errorFiles++;
                }
            }

            // Update scan history with results
            history.setUsersScanned(actualUsersScanned);
            history.setDirsScanned(actualUsersScanned);  // 每个用户对应一个目录
            history.setFilesTotal(totalFiles);
            history.setFilesProcessed(processedFiles);
            history.setFilesSkipped(skippedFilesCount);
            history.setFilesError(errorFiles);
            history.setNewMessages(totalMessages);
            history.setNewTurns(totalTurns);
            history.setNewIssues(totalIssues);
            history.setNewSkillCalls(totalSkills);

            // ✅ 验证统计等式
            int calculatedTotal = processedFiles + skippedFilesCount + errorFiles;
            if (calculatedTotal != totalFiles) {
                log.warn("Statistics mismatch: processed({}) + skipped({}) + error({}) = {} != total({})",
                    processedFiles, skippedFilesCount, errorFiles, calculatedTotal, totalFiles);
                // 不抛出异常，但记录警告以便调试
            }

            finishScan(scanId, history, "completed", startTime, null);

            // Aggregate hourly stats from the scanned data
            try {
                if (!scannedEmployeeIds.isEmpty()) {
                    // ✅ 获取从扫描中收集的小时
                    Set<LocalDateTime> changedHours = dataIngestionService.getAndClearCurrentScanHours();
                    log.info("Collected {} changed hours from this scan (employee count: {})", 
                        changedHours.size(), scannedEmployeeIds.size());
                    
                    // 如果 changedHours 为空，记录警告
                    if (changedHours.isEmpty()) {
                        log.warn("No hours collected from scan. Possible reasons:");
                        log.warn("  1. All sessions were already scanned (skipped)");
                        log.warn("  2. Message/turn timestamps are invalid (epochMs = 0)");
                        log.warn("  3. No files were processed successfully");
                    }
                    
                    // ✅ 调用新的智能聚合方法
                    hourlyStatsAggregator.aggregateForEmployeesWithStats(
                        new ArrayList<>(scannedEmployeeIds),
                        changedHours,
                        scanId  // ✅ 传递 scanId 用于记录统计
                    );
                } else {
                    log.info("No employees scanned, skipping hourly stats aggregation");
                }
            } catch (Exception e) {
                log.error("Failed to aggregate hourly stats for scan {}", scanId, e);
                
                // ✅ 更新扫描历史的错误信息
                try {
                    DashboardScanHistory errorHistory = new DashboardScanHistory();
                    errorHistory.setId(scanId);
                    errorHistory.setStatus("agg_error");  // 缩短状态值，避免超过 VARCHAR(20)
                    errorHistory.setErrorMessage("Aggregation failed: " + e.getMessage());
                    scanHistoryMapper.updateStatusAndError(errorHistory);
                } catch (Exception ex) {
                    log.error("Failed to update scan error status", ex);
                }
                
                // ✅ 抛出异常，让上层处理
                throw e;
            }

            log.info("Scan {} completed in {}ms - Users: {}, Files: {}/{}, Messages: {}, Turns: {}, Issues: {}, Skills: {}",
                    scanId, history.getDurationMs(),
                    history.getUsersScanned(),
                    processedFiles, totalFiles,
                    totalMessages, totalTurns, totalIssues, totalSkills);

            // Generate report after successful scan
            try {
                LocalDateTime scanStartTime = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(startTime), 
                    java.time.ZoneId.systemDefault());
                reportGenerator.generateReport(scanId, scanStartTime);
            } catch (Exception e) {
                log.error("Failed to generate report for scan {}", scanId, e);
                // Don't fail the scan if report generation fails
            }

            // Save scanned files list for comparison with Python
            try {
                saveScannedFilesList(scanId);
                saveSkippedFilesList(scanId);
            } catch (Exception e) {
                log.error("Failed to save files lists", e);
            }

            return scanId;

        } catch (Exception e) {
            log.error("Scan failed", e);
            finishScan(scanId, history, "failed", startTime, e.getMessage());
            throw e;
        } finally {
            currentScanId = null;
            scanning.set(false);
        }
    }
    
    /**
     * Scan a single user's directory (executed in parallel for different users)
     * 
     * Supports both standard and hash-based directory structures:
     * - Standard: basePath/employeeId/openclawDir/agents/main/sessions
     * - Hash-based: basePath/{sha512_hash}/agents/{agentName}/sessions
     * - Flat: basePath/*.jsonl (for testing)
     */
    private UserScanResult scanUserDirectory(String employeeId, Long scanId) {
        int totalFiles = 0;
        int processedFiles = 0;
        int skippedFilesCount = 0;
        int errorFiles = 0;
        int totalMessages = 0;
        int totalTurns = 0;
        int totalIssues = 0;
        int totalSkills = 0;
        
        try {
            String basePath = properties.getNas().getBasePath();
            String openclawDir = properties.getNas().getOpenclawDir();
            
            // Find the hash directory for this user
            Path userHashDir = findUserHashDirectory(basePath, employeeId, openclawDir);
            
            if (userHashDir == null || !userHashDir.toFile().exists()) {
                log.debug("User directory not found for: {}", employeeId);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }
            
            // Scan all agent directories under this hash directory (use configured openclawDir)
            Path agentsDir = userHashDir.resolve(openclawDir);
            if (!agentsDir.toFile().exists() || !agentsDir.toFile().isDirectory()) {
                log.debug("Agents directory not found: {}", agentsDir);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }
            
            // Get employee info (may resolve sha-xxx fallback to real employee ID)
            String resolvedEmployeeId = resolveEmployeeId(employeeId);
            scannedEmployeeIds.add(resolvedEmployeeId);
            log.info("Scanning employee {} (resolved to: {})", employeeId, resolvedEmployeeId);
            
            // ✅ basePathObj 用于计算相对路径（只在处理成功时使用）
            Path basePathObj = Path.of(basePath);
            
            // Scan all agent subdirectories (e.g., "main", or other names)
            File[] agentDirs = agentsDir.toFile().listFiles(File::isDirectory);
            if (agentDirs == null || agentDirs.length == 0) {
                log.debug("No agent directories found under: {}", agentsDir);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }
            
            // Process each agent directory
            for (File agentDir : agentDirs) {
                Path sessionsDir = agentDir.toPath().resolve("sessions");
                if (!sessionsDir.toFile().exists()) {
                    log.debug("Sessions directory not found: {}", sessionsDir);
                    continue;
                }
                
                // Scan for JSONL files in this agent's sessions directory
                List<Path> jsonlFiles = fileScanner.scanForJsonlFiles(sessionsDir);
                totalFiles += jsonlFiles.size();
                log.info("Agent {}: found {} transcript files", agentDir.getName(), jsonlFiles.size());
                
                // Process each file sequentially within this user's thread
                for (Path jsonlFile : jsonlFiles) {
                    try {
                        // ✅ 前置检查：文件 mtime
                        boolean shouldSkip = false;
                        String skipReason = null;
                        
                        try {
                            long fileMtime = Files.getLastModifiedTime(jsonlFile).toMillis();
                            var progress = scanProgressMapper.selectByEmployeeAndFile(resolvedEmployeeId, jsonlFile.toString());
                            
                            if (progress != null && fileMtime <= progress.getFileMtime()) {
                                shouldSkip = true;
                                skipReason = "File not modified (mtime: " + fileMtime + ")";
                                log.debug("Skipping unmodified file: {}", jsonlFile.getFileName());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to check file mtime for {}, proceeding with parse", jsonlFile.getFileName(), e);
                        }
                        
                        if (shouldSkip) {
                            // ✅ 计入 skipped，不解析文件
                            skippedFiles.put(jsonlFile.toString(), skipReason);
                            skippedFilesCount++;
                            continue;
                        }
                        
                        log.debug("Processing file: {} (employee: {})", jsonlFile.getFileName(), resolvedEmployeeId);
                        
                        // Parse transcript file
                        var parsed = transcriptParser.parseFile(jsonlFile, resolvedEmployeeId);
                        
                        // Defensive check: skip if sessionId is still null (should not happen after TranscriptParser fix)
                        if (parsed.sessionId() == null) {
                            String errorMsg = "Skipping file with no session ID (unexpected)";
                            log.debug("{}", errorMsg);
                            skippedFiles.put(jsonlFile.toString(), errorMsg);
                            skippedFilesCount++;
                            continue;
                        }
                        
                        // Batch insert into database
                        var result = dataIngestionService.ingestParsedTranscript(scanId, resolvedEmployeeId, parsed);
                        
                        // ✅ 更新扫描进度
                        try {
                            DashboardScanProgress progress = new DashboardScanProgress();
                            progress.setEmployeeId(employeeId);
                            progress.setFilePath(jsonlFile.toString());
                            
                            // 获取文件信息
                            if (Files.exists(jsonlFile)) {
                                long fileSize = Files.size(jsonlFile);
                                progress.setFileSize(fileSize);
                                progress.setLastOffset(fileSize);  // ✅ 全量解析，offset 等于文件大小
                                progress.setFileMtime(Files.getLastModifiedTime(jsonlFile).toMillis());
                            } else {
                                progress.setLastOffset(0L);  // ✅ 文件不存在时设为 0
                            }
                            
                            // Session ID
                            progress.setSessionId(parsed.sessionId());
                            
                            // 最后一条消息的信息
                            if (!parsed.messages().isEmpty()) {
                                var lastMsg = parsed.messages().get(parsed.messages().size() - 1);
                                progress.setLastMessageId(lastMsg.id());
                                if (lastMsg.epochMs() > 0) {
                                    progress.setLastMessageTs(LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(lastMsg.epochMs()),
                                        BEIJING_ZONE
                                    ));
                                }
                            }
                            
                            scanProgressService.updateProgress(progress);
                            log.debug("Updated scan progress for file: {}", jsonlFile.getFileName());
                        } catch (Exception e) {
                            log.warn("Failed to update scan progress for file {}, but data ingestion succeeded", 
                                jsonlFile.getFileName(), e);
                            // 不抛出异常，避免影响主流程
                        }
                        
                        if (result.status() == IngestionStatus.PROCESSED) {
                            // ✅ 只有实际处理的文件才记录到 scannedFilePaths
                            String relativePath = basePathObj.relativize(jsonlFile).toString();
                            scannedFilePaths.add(relativePath);
                            
                            // 累加统计数据
                            totalMessages += result.messageCount();
                            totalTurns += result.turnCount();
                            totalIssues += result.issueCount();
                            totalSkills += result.skillCount();
                            processedFiles++;
                            
                            log.debug("Parsed file: {} messages, {} turns, {} issues, {} skills",
                                result.messageCount(), result.turnCount(), result.issueCount(), result.skillCount());
                            
                        } else if (result.status() == IngestionStatus.SKIPPED_MTIME) {
                            // ✅ mtime 未变化（兜底检查触发）
                            skippedFiles.put(jsonlFile.toString(), "File not modified (checked in ingestion service)");
                            skippedFilesCount++;
                            
                        } else if (result.status() == IngestionStatus.FAILED) {
                            // ✅ 处理失败
                            String errorMsg = "Ingestion failed: " + result.errorMessage();
                            skippedFiles.put(jsonlFile.toString(), errorMsg);
                            errorFiles++;
                            log.error("{}", errorMsg);
                        }
                        
                    } catch (Exception e) {
                        String errorMsg = "Failed to process file: " + e.getMessage();
                        log.error("{}", errorMsg, e);
                        skippedFiles.put(jsonlFile.toString(), errorMsg);
                        errorFiles++;
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to scan employee: {}", employeeId, e);
        }
        
        return new UserScanResult(totalFiles, processedFiles, skippedFilesCount, errorFiles,
            totalMessages, totalTurns, totalIssues, totalSkills);
    }
    
    /**
     * Finish scan and update history
     */
    private void finishScan(Long scanId, DashboardScanHistory history, String status, 
                           long startTime, String errorMessage) {
        history.setStatus(status);
        history.setFinishedAt(LocalDateTime.now());
        history.setDurationMs(System.currentTimeMillis() - startTime);
        if (errorMessage != null) {
            history.setErrorMessage(errorMessage.length() > 1000 ? 
                errorMessage.substring(0, 1000) : errorMessage);
        }
        
        // Update scan history with all statistics
        scanHistoryMapper.updateStatus(
            scanId, 
            status, 
            history.getDurationMs(), 
            history.getErrorMessage(),
            history.getUsersScanned(),
            history.getDirsScanned(),
            history.getFilesTotal(),
            history.getFilesProcessed(),
            history.getFilesSkipped(),
            history.getFilesError(),
            history.getNewMessages(),
            history.getNewTurns(),
            history.getNewIssues(),
            history.getNewSkillCalls()
        );
    }
    
    /**
     * Find the hash-based directory for a user
     * 
     * @param basePath Base path containing hash directories
     * @param employeeId Employee ID or truncated hash (may include "sha-" prefix)
     * @param openclawDir Configured openclaw directory name (e.g., "agents")
     * @return Path to the hash directory, or null if not found
     */
    private Path findUserHashDirectory(String basePath, String employeeId, String openclawDir) {
        File baseDir = new File(basePath);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return null;
        }
        
        // If employeeId looks like a full SHA512 hash (128 hex chars), use it directly
        if (employeeId.length() == 128 && employeeId.matches("[0-9a-f]+")) {
            Path hashDir = Path.of(basePath, employeeId);
            if (hashDir.toFile().exists()) {
                return hashDir;
            }
        }
        
        // Otherwise, search for matching hash directory
        File[] hashDirs = baseDir.listFiles(File::isDirectory);
        if (hashDirs != null) {
            // ✅ 处理 "sha-" 前缀的 fallback ID
            String actualHash = employeeId;
            if (employeeId.startsWith("sha-")) {
                actualHash = employeeId.substring(4);  // 去掉 "sha-" 前缀
            }
            
            for (File hashDir : hashDirs) {
                String dirName = hashDir.getName();
                
                // Check if this is a valid hash directory (has configured openclawDir subdirectory)
                Path agentsPath = hashDir.toPath().resolve(openclawDir);
                if (!agentsPath.toFile().exists()) {
                    continue;
                }
                
                // Try to match by employee ID
                AccountsCsvReader.EmployeeInfo employee = accountsReader.getEmployeeByHash(dirName);
                if (employee != null && actualHash.equals(employee.getEmployeeId())) {
                    return hashDir.toPath();
                }
                
                // Try prefix matching (for truncated hashes)
                if (dirName.startsWith(actualHash) || actualHash.startsWith(dirName.substring(0, Math.min(16, dirName.length())))) {
                    log.debug("Found matching hash directory: {} for employeeId: {}", dirName, employeeId);
                    return hashDir.toPath();
                }
            }
        }
        
        log.warn("Could not find hash directory for employeeId: {}", employeeId);
        return null;
    }
    
    /**
     * Resolve employee ID from input (which could be real employee ID or sha-xxx fallback)
     * 
     * @param employeeId Input identifier (real employee ID or "sha-xxx" fallback)
     * @return Resolved employee ID (real ID if found, or unchanged fallback)
     */
    private String resolveEmployeeId(String employeeId) {
        // If employeeId is already a real employee ID (from accounts.csv), return it
        var employees = accountsReader.getAllEmployees();
        for (var emp : employees) {
            if (employeeId.equals(emp.getEmployeeId())) {
                return employeeId;
            }
        }
        
        // If employeeId looks like a hash, try to find matching employee
        if (employeeId.length() >= 16 && employeeId.matches("[0-9a-f]+")) {
            File baseDir = new File(properties.getNas().getBasePath());
            File[] hashDirs = baseDir.listFiles(File::isDirectory);
            if (hashDirs != null) {
                for (File hashDir : hashDirs) {
                    String dirName = hashDir.getName();
                    if (dirName.startsWith(employeeId) || employeeId.startsWith(dirName.substring(0, Math.min(16, dirName.length())))) {
                        AccountsCsvReader.EmployeeInfo employee = accountsReader.getEmployeeByHash(dirName);
                        if (employee != null) {
                            return employee.getEmployeeId();
                        }
                    }
                }
            }
        }
        
        // ✅ Fallback: employeeId is already a marked identifier (e.g., "sha-xxx")
        // No need to add prefix again - UserScanner already marked it
        return employeeId;
    }
    
    /**
     * Save scanned files list to a text file for comparison with Python
     */
    private void saveScannedFilesList(Long scanId) throws IOException {
        if (scannedFilePaths == null || scannedFilePaths.isEmpty()) {
            log.warn("No scanned files to save");
            return;
        }
        
        // Get reports directory from configuration
        String reportsDir = properties.getReports().getOutputDir();
        
        // Create report directory with date-based structure
        String dateStr = LocalDateTime.now().toString().substring(0, 10);
        Path reportPath = Path.of(reportsDir, dateStr);
        Files.createDirectories(reportPath);
        
        // Save file list with scanId in filename
        Path filePath = reportPath.resolve("java-scanned-files-scan-" + scanId + ".txt");
        List<String> sortedPaths = scannedFilePaths.stream().sorted().toList();
        Files.write(filePath, sortedPaths);
        
        log.info("Java scanned files list saved to: {} ({} files)", filePath.toAbsolutePath(), sortedPaths.size());
    }
    
    /**
     * Save skipped files list to a text file with error messages
     */
    private void saveSkippedFilesList(Long scanId) throws IOException {
        if (skippedFiles == null || skippedFiles.isEmpty()) {
            log.warn("No skipped files to save");
            return;
        }
        
        // Get reports directory from configuration
        String reportsDir = properties.getReports().getOutputDir();
        
        // Create report directory with date-based structure
        String dateStr = LocalDateTime.now().toString().substring(0, 10);
        Path reportPath = Path.of(reportsDir, dateStr);
        Files.createDirectories(reportPath);
        
        // Save skipped files list with scanId in filename
        Path filePath = reportPath.resolve("java-skipped-files-scan-" + scanId + ".txt");
        List<String> skippedLines = new java.util.ArrayList<>();
        skippedFiles.forEach((file, error) -> {
            skippedLines.add(file + " | " + error);
        });
        Files.write(filePath, skippedLines);
        
        log.info("Java skipped files list saved to: {} ({} files)", filePath.toAbsolutePath(), skippedFiles.size());
    }
    
    /**
     * Record class to hold scan results for a single user
     */
    private record UserScanResult(
        int filesTotal,
        int filesProcessed,
        int filesSkipped,
        int filesError,
        int totalMessages,
        int totalTurns,
        int totalIssues,
        int totalSkills
    ) {}
}
