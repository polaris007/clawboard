package com.company.clawboard.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DashboardHourlyStats {
    private String employeeId;
    private LocalDateTime statHour;
    private Long totalTokens;
    private Long inputTokens;
    private Long outputTokens;
    private BigDecimal totalCost;
    private Long cacheReadTokens;
    private Long cacheWriteTokens;
    private Integer conversationTurns;
    private Integer completeTurns;
    private Integer errorTurns;
    private Integer skillInvocations;
    private Integer skillErrors;
    private Integer toolCalls;
    private Integer toolErrors;
    private Integer errorCount;
    private LocalDateTime updatedAt;
}
