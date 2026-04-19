package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardTranscriptIssue {
    private Long id;
    private Long scanId;  // Added for tracking which scan this issue belongs to
    private String sessionId;
    private String messageId;
    private String employeeId;
    private String errorType;
    private String severity;
    private String description;
    private String errorMessage;
    private String userInput;           // User input content
    private String causeAnalysis;       // Cause analysis
    private String filePath;            // File path
    private String errorLineContent;    // Error line content
    private String nextLineContent;     // Next line content
    private String eventType;
    private String provider;
    private String model;
    private Integer lineNumber;
    private LocalDateTime occurredAt;
    private Long turnId;
    private LocalDateTime createdAt;
}
