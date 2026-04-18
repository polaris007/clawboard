package com.company.clawboard.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DashboardMessage {
    private String sessionId;
    private String messageId;
    private String employeeId;
    private String role;
    private LocalDateTime messageTimestamp;
    private Integer inputTokens;
    private Integer outputTokens;
    private Integer cacheReadTokens;
    private Integer cacheWriteTokens;
    private Integer totalTokens;
    private BigDecimal costTotal;
    private String provider;
    private String model;
    private String stopReason;
    private Integer durationMs;
    private Integer isError;
    private String toolName;
    private String toolCallId;
    private String parentId;
    private LocalDateTime createdAt;
}
