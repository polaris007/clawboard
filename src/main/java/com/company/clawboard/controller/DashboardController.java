package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.TimeRangeRequest;
import com.company.clawboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard/global-stats")
    public ApiResponse<?> getGlobalStats() {
        return ApiResponse.ok(dashboardService.getGlobalStats());
    }

    @PostMapping("/dashboard/summary")
    public ApiResponse<?> getSummary(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(dashboardService.getSummary(request));
    }

    @PostMapping("/dashboard/trend")
    public ApiResponse<?> getTrend(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(dashboardService.getTrend(request));
    }

    @PostMapping("/dashboard/usersummary")
    public ApiResponse<?> getUserSummaries(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(dashboardService.getUserSummaries(request));
    }
}
