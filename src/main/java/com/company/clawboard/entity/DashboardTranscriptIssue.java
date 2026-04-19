package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardTranscriptIssue {
    private Long id;
    private Long scanId;
    private String sessionId;
    private String messageId;
    private String employeeId;
    private String errorType;
    private String severity;
    private String description;
    private String errorMessage;
    private String userInput;
    private String causeAnalysis;
    private String filePath;
    private String errorLineContent;
    private String nextLineContent;
    private String eventType;
    private String runId;
    private String provider;
    private String model;
    private Integer lineNumber;
    private LocalDateTime occurredAt;
    private Long turnId;
    private LocalDateTime createdAt;
}
