package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.scanner.ScanOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanOrchestrator scanOrchestrator;

    @PostMapping("/trigger")
    public ApiResponse<?> triggerScan() {
        Long scanId = scanOrchestrator.executeScan("manual");
        return ApiResponse.ok(new java.util.HashMap<>() {{
            put("scanId", scanId);
        }});
    }

    @GetMapping("/status")
    public ApiResponse<?> getStatus() {
        return ApiResponse.ok(new java.util.HashMap<>() {{
            put("scanning", false);
        }});
    }

    @GetMapping("/history")
    public ApiResponse<?> getHistory(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(java.util.List.of());
    }
}
