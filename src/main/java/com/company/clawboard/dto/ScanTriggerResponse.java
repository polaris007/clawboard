package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanTriggerResponse {
    private Long scanId;
    private String triggerType;
    private String status;  // "triggered"
    private String message; // "Scan triggered successfully"
    private LocalDateTime startedAt;
}
