package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.scanner.ScanOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanOrchestrator scanOrchestrator;

    @PostMapping("/trigger")
    public ApiResponse<?> triggerScan() {
        Long scanId = scanOrchestrator.executeScan("manual");
        return ApiResponse.ok(new java.util.HashMap<>() {{
            put("scanId", scanId);
            put("triggerType", "manual");
            put("startedAt", System.currentTimeMillis());
        }});
    }

    @GetMapping("/status")
    public ApiResponse<?> getStatus() {
        return ApiResponse.ok(new java.util.HashMap<>() {{
            put("scanning", false);
            put("nextScheduledAt", null);
            put("currentScan", null);
            put("lastCompletedScan", null);
        }});
    }

    @GetMapping("/history")
    public ApiResponse<?> getHistory(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.ok(new java.util.HashMap<>() {{
            put("total", 0);
            put("page", page);
            put("pageSize", pageSize);
            put("list", java.util.List.of());
        }});
    }
}
