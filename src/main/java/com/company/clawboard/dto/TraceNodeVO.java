package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 链路节点 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceNodeVO {
    private Integer nodeIndex;
    private String nodeType;
    private String content;
    private Long timestampMs;
    private Integer durationMs;
    private String toolName;
    private String toolCallId;
    private Boolean success;
    private String errorMessage;
}
