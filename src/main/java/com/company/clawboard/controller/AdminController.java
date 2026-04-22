package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.service.DatabaseService;
import com.company.clawboard.mapper.ConversationTurnMapper;
import com.company.clawboard.mapper.ScanHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DatabaseService databaseService;
    private final ConversationTurnMapper turnMapper;
    private final ScanHistoryMapper scanHistoryMapper;

    @PostMapping("/reset-database")
    public ApiResponse<String> resetDatabase(@RequestParam String confirmCode) {
        String expectedCode = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (!expectedCode.equals(confirmCode)) {
            return ApiResponse.error(500, "确认码无效或已过期，请使用当天日期作为确认码");
        }

        databaseService.truncateAllTables();
        return ApiResponse.ok("数据库已成功清空");
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询对话轮次总数
        Integer totalTurns = turnMapper.countNonSystemTurns(null, null, null, null);
        stats.put("totalNonSystemTurns", totalTurns != null ? totalTurns : 0);
        
        // 查询所有轮次（包括系统轮次）
        // 我们直接调用一个简单的count查询
        Integer allTurns = turnMapper.selectAll().size();
        stats.put("totalAllTurns", allTurns);
        
        // 查询最后一次扫描记录
        var scanHistory = scanHistoryMapper.selectLatest();
        if (scanHistory != null) {
            stats.put("lastScanNewTurns", scanHistory.getNewTurns());
            stats.put("lastScanId", scanHistory.getId());
            stats.put("lastScanStatus", scanHistory.getStatus());
        }
        
        return ApiResponse.ok(stats);
    }
}
