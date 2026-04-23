package com.company.clawboard.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DashboardMessage {
    private Long scanId;  // Added for tracking which scan this message belongs to
    private String sessionId;
    private Long turnId;  // Added for turn association
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
    private String errorMessage;  // Added for storing detailed error messages
    private String toolName;
    private String toolCallId;
    private String parentId;
    private Integer isSystem; // 是否系统消息
    private LocalDateTime createdAt;
}
