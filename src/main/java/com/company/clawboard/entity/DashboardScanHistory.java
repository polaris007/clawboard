package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardScanHistory {
    private Long id;
    private String triggerType;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationMs;
    private Integer usersScanned;
    private Integer dirsScanned;
    private Integer filesTotal;
    private Integer filesProcessed;
    private Integer filesSkipped;
    private Integer filesError;
    private Integer newMessages;
    private Integer newTurns;
    private Integer newIssues;
    private Integer newSkillCalls;
    private String errorMessage;
    private LocalDateTime createdAt;
}
