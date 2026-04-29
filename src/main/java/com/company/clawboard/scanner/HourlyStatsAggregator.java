package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardHourlyStats;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.HourlyStatsMapper;
import com.company.clawboard.mapper.ScanHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyStatsAggregator {

    private final HourlyStatsMapper hourlyStatsMapper;
    private final ClawboardProperties properties;
    private final ScanHistoryMapper scanHistoryMapper;

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");

    /**
     * 智能混合聚合
     * @param employeeIds 需要聚合的员工ID列表
     * @param changedHoursFromScan 从扫描中收集的小时集合
     * @param scanId 扫描ID，用于记录统计信息
     */
    public void aggregateForEmployeesWithStats(
        List<String> employeeIds, 
        Set<LocalDateTime> changedHoursFromScan,
        Long scanId
    ) {
        log.info("Starting smart hourly stats aggregation for {} employees", employeeIds.size());
        long start = System.currentTimeMillis();

        ClawboardProperties.AggregationConfig config = properties.getAggregation();
        
        if (!config.isEnableSmartAggregation()) {
            log.info("Smart aggregation disabled, using legacy full aggregation");
            aggregateForEmployeesLegacy(employeeIds);
            return;
        }
        
        // ✅ 收集实际的 (employee_id, hour) 组合
        Set<HourlyStatsMapper.EmployeeHourKey> employeeHoursSet = new HashSet<>();
        int hoursFromFiles = 0;
        int hoursFromWindow = 0;
        
        // 1. 从扫描文件提取的小时（与所有员工组合）
        if (config.isEnableFileBasedIncremental() && changedHoursFromScan != null && !changedHoursFromScan.isEmpty()) {
            // 为每个小时与每个员工生成组合
            for (LocalDateTime hour : changedHoursFromScan) {
                for (String empId : employeeIds) {
                    employeeHoursSet.add(new HourlyStatsMapper.EmployeeHourKey(empId, hour));
                }
            }
            hoursFromFiles = changedHoursFromScan.size();
            log.info("Added {} hours from scanned files ({} employee-hour combinations)", 
                hoursFromFiles, changedHoursFromScan.size() * employeeIds.size());
        }
        
        // 2. 从时间窗口获取的组合（直接使用完整组合，不再生成笛卡尔积）
        if (config.isEnableTimeWindow()) {
            int windowHours = config.getTimeWindowHours();
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(windowHours);
            
            List<HourlyStatsMapper.EmployeeHourKey> recentHours = 
                hourlyStatsMapper.selectDistinctEmployeeHoursByEmployeesAndTimeRange(
                    employeeIds, cutoffTime);
            
            employeeHoursSet.addAll(recentHours);  // ← 直接添加完整组合
            hoursFromWindow = (int) recentHours.stream()
                .map(HourlyStatsMapper.EmployeeHourKey::statHour)
                .distinct()
                .count();
            log.info("Added {} hours from time window (last {} hours)", 
                hoursFromWindow, windowHours);
        }
        
        // 3. 如果没有收集到任何组合，回退到全量聚合
        if (employeeHoursSet.isEmpty()) {
            log.warn("No hours collected from files or window, falling back to full aggregation for {} employees", 
                employeeIds.size());
            aggregateForEmployeesLegacy(employeeIds);
            
            // 记录统计信息（全量聚合无法提供细分统计）
            long duration = System.currentTimeMillis() - start;
            updateScanHistoryWithAggStats(scanId, 0, 0, 0, duration);
            return;
        }
        
        int totalCombinations = employeeHoursSet.size();
        int uniqueHours = (int) employeeHoursSet.stream()
            .map(HourlyStatsMapper.EmployeeHourKey::statHour)
            .distinct()
            .count();
        log.info("Total (employee, hour) combinations to aggregate: {} (files={}, window={}, unique_hours={})", 
            totalCombinations, hoursFromFiles, hoursFromWindow, uniqueHours);
        
        // 4. 转换为列表并执行聚合（不再重新生成笛卡尔积）
        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = new ArrayList<>(employeeHoursSet);
        processEmployeeHours(employeeHours, start);
        
        // 5. 记录聚合统计到 scan_history
        long duration = System.currentTimeMillis() - start;
        updateScanHistoryWithAggStats(scanId, hoursFromFiles, hoursFromWindow, uniqueHours, duration);
        
        log.info("Smart aggregation completed in {}ms: total={} combinations", duration, totalCombinations);
    }
    
    /**
     * 更新扫描历史中的聚合统计
     */
    private void updateScanHistoryWithAggStats(
        Long scanId, 
        int hoursFromFiles, 
        int hoursFromWindow, 
        int totalHours,
        long durationMs
    ) {
        if (scanId == null) {
            log.debug("Scan ID is null, skipping aggregation stats update");
            return;
        }
        
        try {
            DashboardScanHistory history = new DashboardScanHistory();
            history.setId(scanId);
            history.setAggHoursFromFiles(hoursFromFiles);
            history.setAggHoursFromWindow(hoursFromWindow);
            history.setAggHoursTotal(totalHours);
            history.setAggDurationMs(durationMs);
            
            scanHistoryMapper.updateAggregationStats(history);
            log.debug("Updated scan {} with aggregation stats", scanId);
        } catch (Exception e) {
            log.error("Failed to update aggregation stats for scan {}", scanId, e);
            // 不抛出异常，避免影响主流程
        }
    }
    
    /**
     * 传统的全量聚合（向后兼容）
     */
    private void aggregateForEmployeesLegacy(List<String> employeeIds) {
        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = 
            hourlyStatsMapper.selectDistinctEmployeeHoursByEmployees(employeeIds);
        log.info("Found {} distinct (employee, hour) combinations to aggregate", employeeHours.size());
        processEmployeeHours(employeeHours, System.currentTimeMillis());
    }

    /**
     * 原有方法保留（向后兼容）
     */
    public void aggregateForEmployees(List<String> employeeIds) {
        aggregateForEmployeesWithStats(employeeIds, Collections.emptySet(), null);
    }

    private void processEmployeeHours(List<HourlyStatsMapper.EmployeeHourKey> employeeHours, long start) {
        int successCount = 0;
        int errorCount = 0;

        for (HourlyStatsMapper.EmployeeHourKey key : employeeHours) {
            try {
                aggregateForEmployeeHour(key.employeeId(), key.statHour());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to aggregate stats for employee {} hour {}", key.employeeId(), key.statHour(), e);
                errorCount++;
            }
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Hourly stats aggregation completed in {}ms: {} success, {} errors", duration, successCount, errorCount);
    }

    public void aggregateForEmployeeHour(String employeeId, LocalDateTime statHour) {
        String statHourStr = statHour.format(HOUR_FORMATTER);

        DashboardHourlyStats tokenStats = hourlyStatsMapper.aggregateTokensByHour(employeeId, statHourStr);
        DashboardHourlyStats turnStats = hourlyStatsMapper.aggregateTurnsByHour(employeeId, statHourStr);
        int errorCount = hourlyStatsMapper.aggregateIssuesByHour(employeeId, statHourStr);

        DashboardHourlyStats stats = new DashboardHourlyStats();
        stats.setEmployeeId(employeeId);
        stats.setStatHour(statHour);
        stats.setTotalTokens(tokenStats != null ? tokenStats.getTotalTokens() : 0L);
        stats.setInputTokens(tokenStats != null ? tokenStats.getInputTokens() : 0L);
        stats.setOutputTokens(tokenStats != null ? tokenStats.getOutputTokens() : 0L);
        stats.setTotalCost(tokenStats != null ? tokenStats.getTotalCost() : BigDecimal.ZERO);
        stats.setCacheReadTokens(tokenStats != null ? tokenStats.getCacheReadTokens() : 0L);
        stats.setCacheWriteTokens(tokenStats != null ? tokenStats.getCacheWriteTokens() : 0L);

        if (turnStats != null) {
            stats.setConversationTurns(turnStats.getConversationTurns());
            stats.setCompleteTurns(turnStats.getCompleteTurns());
            stats.setErrorTurns(turnStats.getErrorTurns());
            stats.setToolCalls(turnStats.getToolCalls());
            stats.setToolErrors(turnStats.getToolErrors());
            stats.setSkillInvocations(turnStats.getSkillInvocations());
            stats.setSkillErrors(turnStats.getSkillErrors());
        } else {
            stats.setConversationTurns(0);
            stats.setCompleteTurns(0);
            stats.setErrorTurns(0);
            stats.setToolCalls(0);
            stats.setToolErrors(0);
            stats.setSkillInvocations(0);
            stats.setSkillErrors(0);
        }

        stats.setErrorCount(errorCount);
        stats.setUpdatedAt(LocalDateTime.now());

        hourlyStatsMapper.upsertStats(stats);
        log.debug("Upserted hourly stats for employee {} hour {}: tokens={}, turns={}, complete={}, errors={}",
                employeeId, statHourStr, stats.getTotalTokens(), stats.getConversationTurns(),
                stats.getCompleteTurns(), stats.getErrorCount());
    }

    /**
     * 检查所有统计字段是否都为 0
     * @param stats 统计数据
     * @return true 如果所有字段都为 0
     */
    private boolean isAllFieldsZero(DashboardHourlyStats stats) {
        return stats.getTotalTokens() == 0L
            && stats.getInputTokens() == 0L
            && stats.getOutputTokens() == 0L
            && stats.getTotalCost().compareTo(BigDecimal.ZERO) == 0
            && stats.getCacheReadTokens() == 0L
            && stats.getCacheWriteTokens() == 0L
            && stats.getConversationTurns() == 0
            && stats.getCompleteTurns() == 0
            && stats.getErrorTurns() == 0
            && stats.getSkillInvocations() == 0
            && stats.getSkillErrors() == 0
            && stats.getToolCalls() == 0
            && stats.getToolErrors() == 0
            && stats.getErrorCount() == 0;
    }
}