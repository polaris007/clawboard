package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行链路响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionTraceResponse {
    private Long turnId;
    private List<TraceNodeVO> nodes;
}
