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
        
        Set<LocalDateTime> hoursToAggregate = new HashSet<>();
        int hoursFromFiles = 0;
        int hoursFromWindow = 0;
        
        // 1. 添加从扫描文件中提取的小时
        if (config.isEnableFileBasedIncremental() && changedHoursFromScan != null && !changedHoursFromScan.isEmpty()) {
            hoursToAggregate.addAll(changedHoursFromScan);
            hoursFromFiles = changedHoursFromScan.size();
            log.info("Added {} hours from scanned files", hoursFromFiles);
        }
        
        // 2. 添加时间窗口内的小时（兜底）
        if (config.isEnableTimeWindow()) {
            int windowHours = config.getTimeWindowHours();
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(windowHours);
            
            List<HourlyStatsMapper.EmployeeHourKey> recentHours = 
                hourlyStatsMapper.selectDistinctEmployeeHoursByEmployeesAndTimeRange(
                    employeeIds, cutoffTime);
            
            recentHours.forEach(eh -> hoursToAggregate.add(eh.statHour()));
            hoursFromWindow = recentHours.size();
            log.info("Added {} hours from time window (last {} hours)", 
                hoursFromWindow, windowHours);
        }
        
        // 3. 如果没有收集到任何小时，回退到全量聚合
        if (hoursToAggregate.isEmpty()) {
            log.warn("No hours collected from files or window, falling back to full aggregation for {} employees", 
                employeeIds.size());
            aggregateForEmployeesLegacy(employeeIds);
            
            // 记录统计信息（全量聚合无法提供细分统计）
            long duration = System.currentTimeMillis() - start;
            updateScanHistoryWithAggStats(scanId, 0, 0, 0, duration);
            return;
        }
        
        int totalHours = hoursToAggregate.size();
        log.info("Total hours to aggregate: {} (files={}, window={}, overlap={})", 
            totalHours, hoursFromFiles, hoursFromWindow, 
            (hoursFromFiles + hoursFromWindow - totalHours));
        
        // 4. 转换为 EmployeeHourKey 列表
        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = 
            hoursToAggregate.stream()
                .flatMap(hour -> employeeIds.stream()
                    .map(empId -> new HourlyStatsMapper.EmployeeHourKey(empId, hour)))
                .distinct()
                .collect(Collectors.toList());
        
        // 5. 执行聚合
        processEmployeeHours(employeeHours, start);
        
        // 6. 记录聚合统计到 scan_history
        long duration = System.currentTimeMillis() - start;
        updateScanHistoryWithAggStats(scanId, hoursFromFiles, hoursFromWindow, totalHours, duration);
        
        log.info("Smart aggregation completed in {}ms: total={} hours", duration, totalHours);
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
}