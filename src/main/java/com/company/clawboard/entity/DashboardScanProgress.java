package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardScanProgress {
    private String employeeId;
    private String filePath;
    private Long lastOffset;
    private Long fileSize;
    private Long fileMtime;
    private String sessionId;
    private String lastMessageId;
    private LocalDateTime lastMessageTs;
    private LocalDateTime updatedAt;
}
