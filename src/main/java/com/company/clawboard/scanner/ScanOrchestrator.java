package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.*;
import com.company.clawboard.parser.TranscriptParser;
import com.company.clawboard.service.DataIngestionService;
import com.company.clawboard.service.ReportGenerator;
import com.company.clawboard.service.ScanProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
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
            // Step 1: Load accounts mapping from CSV
            String csvPath = properties.getNas().getAccountsCsvPath();
            if (csvPath != null && !csvPath.isEmpty()) {
                Path csvFilePath = Path.of(csvPath);
                if (!csvFilePath.isAbsolute()) {
                    // Resolve relative to current working directory
                    csvFilePath = Path.of(System.getProperty("user.dir"), csvPath);
                }
                accountsReader.loadFromCsv(csvFilePath);
                log.info("Loaded {} employee mappings", accountsReader.getEmployeeCount());
            }

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
            int skippedFiles = 0;
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
                    skippedFiles += result.filesSkipped();
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

            // Update scan history with results
            history.setDirsScanned(users.size());
            history.setFilesTotal(totalFiles);
            history.setFilesProcessed(processedFiles);
            history.setFilesSkipped(skippedFiles);
            history.setFilesError(errorFiles);
            history.setNewMessages(totalMessages);
            history.setNewTurns(totalTurns);
            history.setNewIssues(totalIssues);
            history.setNewSkillCalls(totalSkills);

            finishScan(scanId, history, "completed", startTime, null);

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
     */
    private UserScanResult scanUserDirectory(String username, Long scanId) {
        int totalFiles = 0;
        int processedFiles = 0;
        int skippedFiles = 0;
        int errorFiles = 0;
        int totalMessages = 0;
        int totalTurns = 0;
        int totalIssues = 0;
        int totalSkills = 0;
        
        try {
            // Build user's openclaw directory path
            String basePath = properties.getNas().getBasePath();
            String openclawDir = properties.getNas().getOpenclawDir();
            
            Path userDir;
            if (openclawDir != null && !openclawDir.isEmpty()) {
                // Standard structure: basePath/username/openclawDir/agents/main/sessions
                userDir = Path.of(basePath, username, openclawDir, "agents", "main", "sessions");
            } else {
                // Flat structure: basePath/agents/main/sessions (for testing)
                userDir = Path.of(basePath, "agents", "main", "sessions");
            }

            if (!userDir.toFile().exists()) {
                log.debug("User sessions directory not found: {}", userDir);
                return new UserScanResult(0, 0, 0, 0, 0, 0, 0, 0);
            }

            // Scan for JSONL files
            List<Path> jsonlFiles = fileScanner.scanForJsonlFiles(userDir);
            totalFiles = jsonlFiles.size();
            log.info("User {}: found {} transcript files", username, jsonlFiles.size());

            // Process each file sequentially within this user's thread
            for (Path jsonlFile : jsonlFiles) {
                try {
                    // Extract employee info from path
                    AccountsCsvReader.EmployeeInfo employee = accountsReader.extractEmployeeFromPath(jsonlFile);
                    String employeeId = employee != null ? employee.getEmployeeId() : "unknown";

                    log.debug("Processing file: {} (employee: {})", jsonlFile.getFileName(), employeeId);

                    // Parse transcript file
                    var parsed = transcriptParser.parseFile(jsonlFile, employeeId);

                    if (parsed.sessionId() == null) {
                        log.warn("Skipping file with no session ID: {}", jsonlFile);
                        skippedFiles++;
                        continue;
                    }

                    // Batch insert into database
                    dataIngestionService.ingestParsedTranscript(scanId, employeeId, parsed);
                    
                    int msgCount = parsed.messages().size();
                    int turnCount = parsed.turns().size();
                    int issueCount = parsed.issues().size();
                    int skillCount = parsed.skillInvocations().size();

                    totalMessages += msgCount;
                    totalTurns += turnCount;
                    totalIssues += issueCount;
                    totalSkills += skillCount;
                    processedFiles++;

                    log.debug("Parsed file: {} messages, {} turns, {} issues, {} skills",
                            msgCount, turnCount, issueCount, skillCount);

                } catch (Exception e) {
                    log.error("Failed to process file: {}", jsonlFile, e);
                    errorFiles++;
                }
            }

        } catch (Exception e) {
            log.error("Failed to scan user: {}", username, e);
        }
        
        return new UserScanResult(totalFiles, processedFiles, skippedFiles, errorFiles,
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
        
        if ("completed".equals(status)) {
            scanHistoryMapper.updateStatus(scanId, status, history.getDurationMs(), null);
        } else {
            scanHistoryMapper.updateStatus(scanId, status, history.getDurationMs(), 
                history.getErrorMessage());
        }
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
