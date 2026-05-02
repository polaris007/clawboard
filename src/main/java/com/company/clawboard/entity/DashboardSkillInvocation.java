package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardSkillInvocation {
    private Long id;
    private Long scanId;  // Added for tracking which scan this skill invocation belongs to
    private String sessionId;
    private String employeeId;
    private Long turnId;
    private String skillName;
    private String skillPath;
    private LocalDateTime invokedAt;
    private String readMessageId;
    private String resultMessageId;
    private Integer isError;
    private String triggerType;
    private Integer durationMs;
    private Integer sequenceOrder;  // 同一turn中的skill调用顺序
    private LocalDateTime createdAt;
}
