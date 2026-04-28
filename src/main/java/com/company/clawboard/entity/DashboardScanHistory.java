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
    private Integer aggHoursFromFiles;      // 从文件提取的小时数
    private Integer aggHoursFromWindow;     // 从时间窗口获取的小时数
    private Integer aggHoursTotal;          // 聚合的总小时数（去重后）
    private Long aggDurationMs;             // 聚合耗时(ms)
    private String errorMessage;
    private LocalDateTime createdAt;
}
