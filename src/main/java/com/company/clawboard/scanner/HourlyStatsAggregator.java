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
        
        // ✅ 4. 按员工批量聚合（新优化方案）
        processEmployeeHoursBatch(employeeIds, employeeHoursSet, start);
        
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
     * ✅ 按员工批量聚合（新优化方案）
     * 对每个员工执行 3 条 SQL，一次性获取该员工指定小时的数据
     */
    private void processEmployeeHoursBatch(
        List<String> employeeIds,
        Set<HourlyStatsMapper.EmployeeHourKey> employeeHoursSet,
        long start
    ) {
        log.info("Processing {} employees with batch aggregation...", employeeIds.size());
        
        // 按员工分组，找出每个员工需要聚合的小时
        Map<String, Set<LocalDateTime>> hoursByEmployee = employeeHoursSet.stream()
            .collect(Collectors.groupingBy(
                HourlyStatsMapper.EmployeeHourKey::employeeId,
                Collectors.mapping(HourlyStatsMapper.EmployeeHourKey::statHour, Collectors.toSet())
            ));
        
        int successCount = 0;
        int errorCount = 0;
        
        for (String employeeId : employeeIds) {
            try {
                Set<LocalDateTime> hours = hoursByEmployee.getOrDefault(employeeId, Collections.emptySet());
                if (hours.isEmpty()) {
                    log.debug("No hours to aggregate for employee {}", employeeId);
                    continue;
                }
                
                // 找出该员工的最小和最大小时，用于范围查询
                LocalDateTime minHour = hours.stream().min(LocalDateTime::compareTo).orElse(null);
                LocalDateTime maxHour = hours.stream().max(LocalDateTime::compareTo).orElse(null);
                
                if (minHour == null || maxHour == null) {
                    continue;
                }
                
                // 批量聚合：查询该员工在 [minHour, maxHour+1h) 范围内的所有数据
                LocalDateTime rangeStart = minHour;
                LocalDateTime rangeEnd = maxHour.plusHours(1);
                
                // 1. 批量聚合 token 数据
                List<DashboardHourlyStats> tokenList = hourlyStatsMapper.batchAggregateTokensByEmployee(
                    employeeId, rangeStart, rangeEnd);
                
                // 2. 批量聚合 turn 数据
                List<DashboardHourlyStats> turnList = hourlyStatsMapper.batchAggregateTurnsByEmployee(
                    employeeId, rangeStart, rangeEnd);
                
                // 3. 批量聚合 issue 数据
                List<HourlyStatsMapper.IssueCount> issueList = hourlyStatsMapper.batchAggregateIssuesByEmployee(
                    employeeId, rangeStart, rangeEnd);
                
                // 4. 合并数据并 upsert（只处理在 hours 集合中的小时）
                mergeAndUpsertBatch(employeeId, tokenList, turnList, issueList, hours);
                
                successCount++;
            } catch (Exception e) {
                log.error("Failed to aggregate stats for employee {}", employeeId, e);
                errorCount++;
            }
        }
        
        long duration = System.currentTimeMillis() - start;
        log.info("Batch aggregation completed in {}ms: {} employees success, {} errors", 
            duration, successCount, errorCount);
    }
    
    /**
     * 合并批量聚合的数据并执行 upsert
     * @param hours 需要聚合的小时集合（用于过滤）
     */
    private void mergeAndUpsertBatch(
        String employeeId,
        List<DashboardHourlyStats> tokenList,
        List<DashboardHourlyStats> turnList,
        List<HourlyStatsMapper.IssueCount> issueList,
        Set<LocalDateTime> hours
    ) {
        // 将 turn 和 issue 数据转为 Map，方便查找
        Map<LocalDateTime, DashboardHourlyStats> turnMap = new HashMap<>();
        if (turnList != null) {
            for (DashboardHourlyStats turn : turnList) {
                turnMap.put(turn.getStatHour(), turn);
            }
        }
        
        Map<LocalDateTime, Integer> issueMap = new HashMap<>();
        if (issueList != null) {
            for (HourlyStatsMapper.IssueCount issue : issueList) {
                issueMap.put(issue.statHour(), issue.issueCount());
            }
        }
        
        // 遍历 token 数据（作为主数据源），合并其他数据并 upsert
        if (tokenList != null) {
            for (DashboardHourlyStats tokenStats : tokenList) {
                LocalDateTime statHour = tokenStats.getStatHour();
                
                // ✅ 只处理在 hours 集合中的小时
                if (!hours.contains(statHour)) {
                    continue;
                }
                
                DashboardHourlyStats stats = new DashboardHourlyStats();
                stats.setEmployeeId(employeeId);
                stats.setStatHour(statHour);
                
                // Token 数据
                stats.setTotalTokens(tokenStats.getTotalTokens() != null ? tokenStats.getTotalTokens() : 0L);
                stats.setInputTokens(tokenStats.getInputTokens() != null ? tokenStats.getInputTokens() : 0L);
                stats.setOutputTokens(tokenStats.getOutputTokens() != null ? tokenStats.getOutputTokens() : 0L);
                stats.setTotalCost(tokenStats.getTotalCost() != null ? tokenStats.getTotalCost() : BigDecimal.ZERO);
                stats.setCacheReadTokens(tokenStats.getCacheReadTokens() != null ? tokenStats.getCacheReadTokens() : 0L);
                stats.setCacheWriteTokens(tokenStats.getCacheWriteTokens() != null ? tokenStats.getCacheWriteTokens() : 0L);
                
                // Turn 数据
                DashboardHourlyStats turnStats = turnMap.get(statHour);
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
                
                // Issue 数据
                stats.setErrorCount(issueMap.getOrDefault(statHour, 0));
                stats.setUpdatedAt(LocalDateTime.now());
                
                // Upsert
                hourlyStatsMapper.upsertStats(stats);
            }
        }
        
        log.debug("Merged and upserted {} hours for employee {}", 
            tokenList != null ? tokenList.size() : 0, employeeId);
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

        // ✅ 优化：检查是否全 0
        if (isAllFieldsZero(stats)) {
            // 只有全 0 时才查询是否存在记录
            DashboardHourlyStats existing = hourlyStatsMapper.selectByEmployeeAndHour(employeeId, statHour);
            
            if (existing == null) {
                // 首次聚合且全 0 → 跳过
                log.debug("Skipping initial upsert for all-zero stats: employee={}, hour={}", 
                    employeeId, statHourStr);
                return;
            } else {
                // 已有记录但重新聚合为全 0 → 执行 upsert（可能意味着数据被删除）
                log.info("Updating existing record to all-zero (data may have been deleted): employee={}, hour={}", 
                    employeeId, statHourStr);
                hourlyStatsMapper.upsertStats(stats);
                return;
            }
        }

        // 非全 0 数据 → 正常 upsert
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