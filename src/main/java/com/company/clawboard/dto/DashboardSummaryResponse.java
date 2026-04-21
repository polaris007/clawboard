package com.company.clawboard.dto;

import lombok.Data;

@Data
public class DashboardSummaryResponse {
    private Long consumedTokens;
    private Integer conversationTurns;
    private Integer skillCalls;
    private Integer activeUsers;
    private Double taskSuccessRate;
}
