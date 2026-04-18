package com.company.clawboard.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ErrorSummaryResponse {
    private Integer totalErrors;
    private Integer totalTurns;
    private Double problemRate;
    private List<ErrorByType> byType;
    private Map<String, Integer> bySeverity;

    @Data
    public static class ErrorByType {
        private String type;
        private Integer count;
    }
}
