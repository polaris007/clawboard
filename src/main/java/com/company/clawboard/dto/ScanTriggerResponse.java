package com.company.clawboard.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScanTriggerResponse {
    private Long scanId;
    private String triggerType;
    private LocalDateTime startedAt;
}
