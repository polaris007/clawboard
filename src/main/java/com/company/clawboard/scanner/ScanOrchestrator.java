package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.ScanHistoryMapper;
import com.company.clawboard.parser.TranscriptParser;
import com.company.clawboard.service.ScanProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanOrchestrator {

    private final ClawboardProperties properties;
    private final UserScanner userScanner;
    private final TranscriptParser transcriptParser;
    private final ScanProgressService scanProgressService;
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
        history.setFilesTotal(0);
        history.setFilesProcessed(0);
        history.setNewMessages(0);
        history.setNewTurns(0);
        history.setNewIssues(0);
        history.setNewSkillCalls(0);

        scanHistoryMapper.insertAndGetId(history);
        Long scanId = history.getId();

        try {
            // Scan users
            var users = userScanner.scanUsers();
            history.setUsersScanned(users.size());

            // TODO: Process each user's transcript files
            // For now, just mark as completed
            history.setStatus("completed");
            history.setFinishedAt(LocalDateTime.now());
            history.setDurationMs(System.currentTimeMillis() - startTime);
            scanHistoryMapper.updateStatus(scanId, "completed", history.getDurationMs(), null);

            log.info("Scan {} completed in {}ms", scanId, history.getDurationMs());
            return scanId;
        } catch (Exception e) {
            log.error("Scan failed", e);
            long duration = System.currentTimeMillis() - startTime;
            scanHistoryMapper.updateStatus(scanId, "failed", duration, e.getMessage());
            throw e;
        }
    }
}
