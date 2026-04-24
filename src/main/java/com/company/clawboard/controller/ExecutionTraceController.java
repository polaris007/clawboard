package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.ExecutionTraceResponse;
import com.company.clawboard.service.ExecutionTraceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 执行链路追踪控制器
 */
@RestController
@RequestMapping("/api/v1/turns")
@RequiredArgsConstructor
@Slf4j
public class ExecutionTraceController {
    
    private final ExecutionTraceService executionTraceService;
    
    /**
     * 查询指定轮次的执行链路
     * @param turnId 轮次ID
     * @return 执行链路响应
     */
    @GetMapping("/{turnId}/trace")
    public ApiResponse<ExecutionTraceResponse> getTrace(@PathVariable Long turnId) {
        log.info("查询执行链路: turnId={}", turnId);
        ExecutionTraceResponse response = executionTraceService.getTraceByTurnId(turnId);
        return ApiResponse.ok(response);
    }
}
