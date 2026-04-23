package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.TimeRangeReportRequest;
import com.company.clawboard.service.TimeRangeReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TimeRangeReportService timeRangeReportService;

    /**
     * Generate report by time range (async, fire-and-forget)
     * 
     * Example:
     * POST /api/v1/reports/generate-by-time-range
     * {
     *   "startTime": "2026-04-20 00:00:00",
     *   "endTime": "2026-04-23 23:59:59"
     * }
     * 
     * Response:
     * {
     *   "code": 200,
     *   "message": "Success",
     *   "data": {
     *     "message": "报告生成任务已启动，完成后将保存到 reports/ 目录",
     *     "outputDirectory": "reports/2026-04-23"
     *   }
     * }
     * 
     * Check reports/ directory for generated file.
     */
    @PostMapping("/generate-by-time-range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateReportByTimeRange(
            @Valid @RequestBody TimeRangeReportRequest request) {
        
        log.info("Received report generation request for time range: {} - {}", 
            request.getStartTime(), request.getEndTime());
        
        // Start async task (fire-and-forget)
        timeRangeReportService.generateReportByTimeRange(
            request.getStartTime(), 
            request.getEndTime()
        );
        
        // Return immediately
        Map<String, Object> data = new HashMap<>();
        data.put("message", "报告生成任务已启动，完成后将保存到 reports/ 目录");
        data.put("outputDirectory", "reports/" + LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
