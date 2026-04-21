package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DatabaseService databaseService;

    @PostMapping("/reset-database")
    public ApiResponse<String> resetDatabase(@RequestParam String confirmCode) {
        String expectedCode = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (!expectedCode.equals(confirmCode)) {
            return ApiResponse.error(500, "确认码无效或已过期，请使用当天的日期作为确认码");
        }

        databaseService.truncateAllTables();
        return ApiResponse.ok("数据库已成功清空");
    }
}
