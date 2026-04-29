package com.company.clawboard.dto;

import com.company.clawboard.entity.DashboardScanHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanConflictResponse {
    private Long scanId;
    private String status;  // "running"
    private String triggerType;
    private LocalDateTime startedAt;
    private Long durationMs;  // 当前已运行时间
    private String message;   // "A scan is already running"
    
    public static ScanConflictResponse from(DashboardScanHistory history) {
        ScanConflictResponse response = new ScanConflictResponse();
        response.setScanId(history.getId());
        response.setStatus(history.getStatus());
        response.setTriggerType(history.getTriggerType());
        response.setStartedAt(history.getStartedAt());
        
        // 计算当前已运行时间
        if (history.getStartedAt() != null) {
            response.setDurationMs(
                java.time.Duration.between(history.getStartedAt(), LocalDateTime.now()).toMillis()
            );
        }
        
        response.setMessage("A scan is already running. Please wait for it to complete.");
        return response;
    }
}
