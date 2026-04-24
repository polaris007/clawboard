package com.company.clawboard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 执行链路追踪实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardExecutionTrace {
    private Long id;
    private Long scanId;
    private Long turnId;
    private Integer nodeIndex;
    private String nodeType;
    private String toolName;
    private String toolCallId;
    private String content;
    private Long timestampMs;
    private Integer durationMs;
    private Boolean success;
    private String errorMessage;
    private LocalDateTime createdAt;
}
