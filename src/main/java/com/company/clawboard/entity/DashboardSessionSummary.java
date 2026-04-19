package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Session summary entity for dashboard_session_summary table
 */
@Data
public class DashboardSessionSummary {
    private String sessionId;
    private String employeeId;
    private String agentName;
    private Integer totalMessages;
    private Integer totalTurns;
    private Integer totalIssues;
    private Integer totalSkills;
    private LocalDateTime firstMessageAt;
    private LocalDateTime lastMessageAt;
    private Long lastScanId;
    private LocalDateTime lastUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
