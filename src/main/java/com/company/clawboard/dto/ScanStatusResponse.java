package com.company.clawboard.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScanStatusResponse {
    private Boolean scanning;
    private LocalDateTime nextScheduledAt;
    private ScanInfo currentScan;
    private ScanInfo lastCompletedScan;

    @Data
    public static class ScanInfo {
        private Long scanId;
        private String status;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
    }
}
