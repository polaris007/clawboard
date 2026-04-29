package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanStatusResponse {
    private Boolean scanning;
    private ScanDetail currentScan;
    private ScanDetail lastCompletedScan;
    private LocalDateTime nextScheduledAt;
}
