package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.ErrorSearchRequest;
import com.company.clawboard.dto.TimeRangeRequest;
import com.company.clawboard.dto.TurnSearchRequest;
import com.company.clawboard.service.TurnErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TurnErrorController {

    private final TurnErrorService turnErrorService;

    @PostMapping("/turns/search")
    public ApiResponse<?> searchTurns(@RequestBody TurnSearchRequest request) {
        return ApiResponse.ok(turnErrorService.searchTurns(request));
    }

    @PostMapping("/errors/summary")
    public ApiResponse<?> getErrorSummary(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(turnErrorService.getErrorSummary(request));
    }

    @PostMapping("/errors/search")
    public ApiResponse<?> searchErrors(@RequestBody ErrorSearchRequest request) {
        return ApiResponse.ok(turnErrorService.searchErrors(request));
    }
}
