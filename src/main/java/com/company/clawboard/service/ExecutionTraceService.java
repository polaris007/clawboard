package com.company.clawboard.service;

import com.company.clawboard.dto.ExecutionTraceResponse;
import com.company.clawboard.dto.TraceNodeVO;
import com.company.clawboard.entity.DashboardExecutionTrace;
import com.company.clawboard.mapper.ExecutionTraceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行链路追踪服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExecutionTraceService {
    
    private final ExecutionTraceMapper executionTraceMapper;
    
    /**
     * 获取指定轮次的执行链路
     * @param turnId 轮次ID
     * @return 执行链路响应
     */
    public ExecutionTraceResponse getTraceByTurnId(String turnId) {
        List<DashboardExecutionTrace> nodes = executionTraceMapper.selectByTurnId(turnId);
        
        if (nodes == null || nodes.isEmpty()) {
            throw new RuntimeException("未找到轮次ID为 " + turnId + " 的执行链路");
        }
        
        return ExecutionTraceResponse.builder()
            .turnId(turnId)
            .nodes(nodes.stream()
                .map(this::convertToNodeVO)
                .collect(Collectors.toList()))
            .build();
    }
    
    /**
     * 转换 Entity 为 VO
     */
    private TraceNodeVO convertToNodeVO(DashboardExecutionTrace entity) {
        return TraceNodeVO.builder()
            .messageId(entity.getMessageId())
            .nodeIndex(entity.getNodeIndex())
            .nodeType(entity.getNodeType())
            .content(entity.getContent())
            .timestamp(entity.getTimestampMs())
            .durationMs(entity.getDurationMs())
            .toolName(entity.getToolName())
            .toolCallId(entity.getToolCallId())
            .status(entity.getSuccess())
            .errorMessage(entity.getErrorMessage())
            .build();
    }
}
