package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.ErrorSearchRequest;
import com.company.clawboard.dto.TurnSearchRequest;
import com.company.clawboard.service.TurnErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TurnErrorController {

    private final TurnErrorService turnErrorService;

    @PostMapping("/turns/search")
    public ApiResponse<?> searchTurns(@RequestBody TurnSearchRequest request) {
        return ApiResponse.ok(turnErrorService.searchTurns(request));
    }

    @GetMapping("/turns/{turnId}/trace")
    public ApiResponse<?> getTrace(@PathVariable Long turnId) {
        return ApiResponse.ok(turnErrorService.getTrace(turnId));
    }

    @GetMapping("/errors/summary")
    public ApiResponse<?> getErrorSummary() {
        return ApiResponse.ok(turnErrorService.getErrorSummary());
    }

    @PostMapping("/errors/search")
    public ApiResponse<?> searchErrors(@RequestBody ErrorSearchRequest request) {
        return ApiResponse.ok(turnErrorService.searchErrors(request));
    }
}
