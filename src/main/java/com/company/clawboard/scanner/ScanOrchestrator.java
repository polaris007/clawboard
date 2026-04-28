package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.*;
import com.company.clawboard.parser.TranscriptParser;
import com.company.clawboard.service.DataIngestionService;
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
import java.time.LocalDateTime;
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
    private final ScanProgressMapper scanProgressMapper;  // For mtime check
    
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
            for (String username : users) {
                CompletableFuture<UserScanResult> future = CompletableFuture.supplyAsync(
                    () -> scanUserDirectory(username, scanId),
                    scanExecutor
                );
                futures.put(username, future);
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

            for (var entry : futures.entrySet()) {
                try {
                    UserScanResult result = entry.getValue().get(30, TimeUnit.MINUTES);
                    totalFiles += result.filesTotal();
                    processedFiles += result.filesProcessed();
                    skippedFilesCount += result.filesSkipped();
                    errorFiles += result.filesError();
                    totalMessages += result.totalMessages();
                    totalTurns += result.totalTurns();
                    totalIssues += result.totalIssues();
                    totalSkills += result.totalSkills();
                    
                    log.debug("User {} scan completed: {} files, {} messages, {} turns",
                        entry.getKey(), result.filesProcessed(), result.totalMessages(), result.totalTurns());
                } catch (Exception e) {
                    log.error("Scan failed for user {}", entry.getKey(), e);
                    errorFiles++;
                }
            }

            // ✅ 计算实际扫描的用户数（有 processed 或 skipped 文件的用户）
            int actualUsersScanned = 0;
            for (var entry : futures.entrySet()) {
                try {
                    UserScanResult result = entry.getValue().get(30, TimeUnit.MINUTES);
                    if (result.filesProcessed() > 0 || result.filesSkipped() > 0 || result.filesError() > 0) {
                        actualUsersScanned++;
                    }
                } catch (Exception e) {
                    log.error("Failed to get result for user {}", entry.getKey(), e);
                }
            }

            // ✅ 验证统计等式
            int calculatedTotal = processedFiles + skippedFilesCount + errorFiles;
            if (calculatedTotal != totalFiles) {
                log.warn("Statistics mismatch: processed({}) + skipped({}) + error({}) = {} != total({})",
                    processedFiles, skippedFilesCount, errorFiles, calculatedTotal, totalFiles);
                // 不抛出异常，但记录警告以便调试
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

            finishScan(scanId, history, "completed", startTime, null);

            // Aggregate hourly stats from the scanned data
            try {
                if (!scannedEmployeeIds.isEmpty()) {
                    hourlyStatsAggregator.aggregateForEmployees(new ArrayList<>(scannedEmployeeIds));
                } else {
                    log.info("No employees scanned, skipping hourly stats aggregation");
                }
            } catch (Exception e) {
                log.error("Failed to aggregate hourly stats for scan {}", scanId, e);
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
     * - Standard: basePath/username/openclawDir/agents/main/sessions
     * - Hash-based: basePath/{sha512_hash}/agents/{agentName}/sessions
     * - Flat: basePath/*.jsonl (for testing)
     */
    private UserScanResult scanUserDirectory(String username, Long scanId) {
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
            Path userHashDir = findUserHashDirectory(basePath, username, openclawDir);
            
            if (userHashDir == null || !userHashDir.toFile().exists()) {
                log.debug("User directory not found for: {}", username);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }
            
            // Scan all agent directories under this hash directory (use configured openclawDir)
            Path agentsDir = userHashDir.resolve(openclawDir);
            if (!agentsDir.toFile().exists() || !agentsDir.toFile().isDirectory()) {
                log.debug("Agents directory not found: {}", agentsDir);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }
            
            // Get employee info
            String employeeId = resolveEmployeeId(username);
            scannedEmployeeIds.add(employeeId);
            log.info("Scanning user {} (employee ID: {})", username, employeeId);
            
            // Scan all agent subdirectories (e.g., "main", or other names)
            File[] agentDirs = agentsDir.toFile().listFiles(File::isDirectory);
            if (agentDirs == null || agentDirs.length == 0) {
                log.debug("No agent directories found under: {}", agentsDir);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }
            
            // Process each agent directory
            Path basePathObj = Path.of(basePath);
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
                            var progress = scanProgressMapper.selectByEmployeeAndFile(employeeId, jsonlFile.toString());
                            
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
                        
                        log.debug("Processing file: {} (employee: {})", jsonlFile.getFileName(), employeeId);
                        
                        // Parse transcript file
                        var parsed = transcriptParser.parseFile(jsonlFile, employeeId);
                        
                        // Defensive check: skip if sessionId is still null (should not happen after TranscriptParser fix)
                        if (parsed.sessionId() == null) {
                            String errorMsg = "Skipping file with no session ID (unexpected)";
                            log.debug("{}", errorMsg);
                            skippedFiles.put(jsonlFile.toString(), errorMsg);
                            skippedFilesCount++;
                            continue;
                        }
                        
                        // ✅ 入库并获取结果
                        var result = dataIngestionService.ingestParsedTranscript(scanId, employeeId, parsed);
                        
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
            log.error("Failed to scan user: {}", username, e);
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
     * @param username Employee ID or truncated hash
     * @param openclawDir Configured openclaw directory name (e.g., "agents")
     * @return Path to the hash directory, or null if not found
     */
    private Path findUserHashDirectory(String basePath, String username, String openclawDir) {
        File baseDir = new File(basePath);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return null;
        }
        
        // If username looks like a full SHA512 hash (128 hex chars), use it directly
        if (username.length() == 128 && username.matches("[0-9a-f]+")) {
            Path hashDir = Path.of(basePath, username);
            if (hashDir.toFile().exists()) {
                return hashDir;
            }
        }
        
        // Otherwise, search for matching hash directory
        File[] hashDirs = baseDir.listFiles(File::isDirectory);
        if (hashDirs != null) {
            for (File hashDir : hashDirs) {
                String dirName = hashDir.getName();
                
                // Check if this is a valid hash directory (has configured openclawDir subdirectory)
                Path agentsPath = hashDir.toPath().resolve(openclawDir);
                if (!agentsPath.toFile().exists()) {
                    continue;
                }
                
                // Try to match by employee ID
                AccountsCsvReader.EmployeeInfo employee = accountsReader.getEmployeeByHash(dirName);
                if (employee != null && username.equals(employee.getEmployeeId())) {
                    return hashDir.toPath();
                }
                
                // Try prefix matching (for truncated hashes)
                // Handle "sha-" prefixed usernames from unknown hashes
                String hashPart = username.startsWith("sha-") ? username.substring(4) : username;
                if (dirName.startsWith(hashPart) || hashPart.startsWith(dirName.substring(0, Math.min(10, dirName.length())))) {
                    log.debug("Found matching hash directory: {} for username: {}", dirName, username);
                    return hashDir.toPath();
                }
            }
        }
        
        log.warn("Could not find hash directory for username: {}", username);
        return null;
    }
    
    /**
     * Resolve employee ID from username (which could be employee ID or truncated hash)
     * 
     * @param username Username/identifier from UserScanner
     * @return Employee ID or "unknown"
     */
    private String resolveEmployeeId(String username) {
        // If username is already an employee ID (from accounts.csv), return it
        var employees = accountsReader.getAllEmployees();
        for (var emp : employees) {
            if (username.equals(emp.getEmployeeId())) {
                return username;
            }
        }
        
        // If username looks like a hash (with or without "sha-" prefix), try to find matching employee
        String hashPart = username.startsWith("sha-") ? username.substring(4) : username;
        if (hashPart.length() >= 10 && hashPart.matches("[0-9a-f]+")) {
            File baseDir = new File(properties.getNas().getBasePath());
            File[] hashDirs = baseDir.listFiles(File::isDirectory);
            if (hashDirs != null) {
                for (File hashDir : hashDirs) {
                    String dirName = hashDir.getName();
                    if (dirName.startsWith(hashPart) || hashPart.startsWith(dirName.substring(0, Math.min(10, dirName.length())))) {
                        AccountsCsvReader.EmployeeInfo employee = accountsReader.getEmployeeByHash(dirName);
                        if (employee != null) {
                            return employee.getEmployeeId();
                        }
                    }
                }
            }
        }
        
        // Fallback: use "sha-" prefix with first 10 characters of hash as employee ID
        if (username != null && username.length() >= 10) {
            // If username already has "sha-" prefix, return it as-is
            if (username.startsWith("sha-")) {
                return username;
            }
            // Otherwise, add the prefix
            return "sha-" + username.substring(0, 10);
        }
        
        // If all else fails, use username as-is
        return username;
    }
    
    /**
     * Save scanned files list to a text file for comparison with Python
     */
    private void saveScannedFilesList(Long scanId) throws IOException {
        // Get reports directory from configuration
        String reportsDir = properties.getReports().getOutputDir();
        
        // Create report directory with date-based structure
        String dateStr = LocalDateTime.now().toString().substring(0, 10);
        Path reportPath = Path.of(reportsDir, dateStr);
        Files.createDirectories(reportPath);
        
        // Save file list with scanId in filename (even if empty)
        Path filePath = reportPath.resolve("java-scanned-files-scan-" + scanId + ".txt");
        List<String> sortedPaths = new java.util.ArrayList<>();
        if (scannedFilePaths != null && !scannedFilePaths.isEmpty()) {
            sortedPaths = scannedFilePaths.stream().sorted().toList();
        }
        Files.write(filePath, sortedPaths);
        
        log.info("Java scanned files list saved to: {} ({} files)", filePath.toAbsolutePath(), sortedPaths.size());
    }
    
    /**
     * Save skipped files list to a text file with error messages
     */
    private void saveSkippedFilesList(Long scanId) throws IOException {
        // Get reports directory from configuration
        String reportsDir = properties.getReports().getOutputDir();
        
        // Create report directory with date-based structure
        String dateStr = LocalDateTime.now().toString().substring(0, 10);
        Path reportPath = Path.of(reportsDir, dateStr);
        Files.createDirectories(reportPath);
        
        // Save skipped files list with scanId in filename (even if empty)
        Path filePath = reportPath.resolve("java-skipped-files-scan-" + scanId + ".txt");
        List<String> skippedLines = new java.util.ArrayList<>();
        if (skippedFiles != null && !skippedFiles.isEmpty()) {
            skippedFiles.forEach((file, error) -> {
                skippedLines.add(file + " | " + error);
            });
        }
        Files.write(filePath, skippedLines);
        
        log.info("Java skipped files list saved to: {} ({} files)", filePath.toAbsolutePath(), skippedLines.size());
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
