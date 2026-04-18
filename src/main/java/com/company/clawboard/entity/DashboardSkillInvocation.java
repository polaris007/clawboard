package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardSkillInvocation {
    private Long id;
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
    private LocalDateTime createdAt;
}
