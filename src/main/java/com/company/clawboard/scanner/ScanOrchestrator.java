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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

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

    @Scheduled(cron = "${clawboard.scan.cron}")
    public void scheduledScan() {
        if (!properties.getScan().isEnabled()) {
            log.info("Scanning is disabled");
            return;
        }
        executeScan("scheduled");
    }

    public Long executeScan(String triggerType) {
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

            // Step 3: Process each user's transcript files
            int totalFiles = 0;
            int processedFiles = 0;
            int skippedFiles = 0;
            int errorFiles = 0;
            int totalMessages = 0;
            int totalTurns = 0;
            int totalIssues = 0;
            int totalSkills = 0;

            for (String username : users) {
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
                        continue;
                    }

                    // Scan for JSONL files
                    List<Path> jsonlFiles = fileScanner.scanForJsonlFiles(userDir);
                    totalFiles += jsonlFiles.size();
                    log.info("User {}: found {} transcript files", username, jsonlFiles.size());

                    // Process each file
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
            history.setStatus("completed");
            history.setFinishedAt(LocalDateTime.now());
            history.setDurationMs(System.currentTimeMillis() - startTime);

            scanHistoryMapper.updateStatus(scanId, "completed", history.getDurationMs(), null);

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
            long duration = System.currentTimeMillis() - startTime;
            scanHistoryMapper.updateStatus(scanId, "failed", duration, e.getMessage());
            throw e;
        }
    }
}
