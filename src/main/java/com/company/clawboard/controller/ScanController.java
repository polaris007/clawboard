package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.ScanConflictResponse;
import com.company.clawboard.scanner.ScanOrchestrator;
import com.company.clawboard.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;
    private final ScanOrchestrator scanOrchestrator; // 保留用于其他接口

    @PostMapping("/trigger")
    public ApiResponse<?> triggerScan() {
        Object result = scanService.checkAndTriggerScan("manual");
        
        // 判断是成功还是冲突
        if (result instanceof ScanConflictResponse) {
            return ApiResponse.error(409, "A scan is already running", result);
        } else {
            return ApiResponse.ok(result);
        }
    }

    @GetMapping("/status")
    public ApiResponse<?> getStatus() {
        return ApiResponse.ok(scanService.getScanStatus());
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
