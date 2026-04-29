package com.company.clawboard.dto;

import com.company.clawboard.entity.DashboardScanHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanDetail {
    private Long scanId;
    private String triggerType;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationMs;
    private Integer usersScanned;
    private Integer filesProcessed;
    private Integer filesTotal;
    private Integer newMessages;
    private Integer newTurns;
    private Integer newIssues;
    
    public static ScanDetail from(DashboardScanHistory history) {
        if (history == null) {
            return null;
        }
        
        ScanDetail detail = new ScanDetail();
        detail.setScanId(history.getId());
        detail.setTriggerType(history.getTriggerType());
        detail.setStatus(history.getStatus());
        detail.setStartedAt(history.getStartedAt());
        detail.setFinishedAt(history.getFinishedAt());
        detail.setDurationMs(history.getDurationMs());
        detail.setUsersScanned(history.getUsersScanned());
        detail.setFilesProcessed(history.getFilesProcessed());
        detail.setFilesTotal(history.getFilesTotal());
        detail.setNewMessages(history.getNewMessages());
        detail.setNewTurns(history.getNewTurns());
        detail.setNewIssues(history.getNewIssues());
        return detail;
    }
}
