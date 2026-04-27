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
    private String messageId;      // NEW: 关联消息ID
    private Integer nodeIndex;
    private String nodeType;
    private String content;
    private Long timestamp;        // 改回 timestamp
    private Integer durationMs;
    private String toolName;
    private String toolCallId;
    private Boolean status;        // 改回 status
    private String errorMessage;
}
