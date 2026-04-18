package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<?> getSummary() {
        return ApiResponse.ok(dashboardService.getSummary());
    }

    @GetMapping("/global-stats")
    public ApiResponse<?> getGlobalStats() {
        return ApiResponse.ok(dashboardService.getGlobalStats());
    }

    @GetMapping("/users")
    public ApiResponse<?> getUserSummaries() {
        return ApiResponse.ok(dashboardService.getUserSummaries());
    }

    @GetMapping("/skills")
    public ApiResponse<?> getSkills() {
        return ApiResponse.ok(dashboardService.getSkillOptions());
    }
}
