package com.company.clawboard.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorSearchItem {
    private Long id;
    private String errorType;
    private String severity;
    private String description;
    private String errorMessage;
    private String employeeName;
    private LocalDateTime occurredAt;
    private Long turnId;
}
