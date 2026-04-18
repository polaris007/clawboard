package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardTranscriptIssue {
    private Long id;
    private String sessionId;
    private String messageId;
    private String employeeId;
    private String errorType;
    private String severity;
    private String description;
    private String errorMessage;
    private String eventType;
    private String provider;
    private String model;
    private Integer lineNumber;
    private LocalDateTime occurredAt;
    private Long turnId;
    private LocalDateTime createdAt;
}
