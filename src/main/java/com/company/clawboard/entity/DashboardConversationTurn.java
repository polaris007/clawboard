package com.company.clawboard.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DashboardConversationTurn {
    private Long id;
    private Long scanId;  // Added for tracking which scan this turn belongs to
    private String sessionId;
    private String employeeId;
    private Integer turnIndex;
    private String startMessageId;
    private String endMessageId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String userInput;
    private Integer totalInputTokens;
    private Integer totalOutputTokens;
    private Integer totalTokens;
    private BigDecimal totalCost;
    private Integer toolCallsCount;
    private Integer toolCallsSuccess;
    private Integer toolCallsError;
    private Integer skillCallsCount;
    private Integer skillCallsSuccess;
    private Integer skillCallsError;
    private Integer isComplete;
    private Integer hasError;
    private String status;
    private Integer totalDurationMs;
    private Integer toolDurationMs;
    private Integer modelDurationMs;
    private String chainSummary;
    private String logFilePath;
    private Integer qualityStatus;
    private LocalDateTime createdAt;
}
