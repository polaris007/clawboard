package com.company.clawboard.scanner;

import com.company.clawboard.entity.DashboardHourlyStats;
import com.company.clawboard.mapper.HourlyStatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyStatsAggregator {

    private final HourlyStatsMapper hourlyStatsMapper;

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");

    public void aggregateAll() {
        log.info("Starting hourly stats aggregation for all employees");
        long start = System.currentTimeMillis();

        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = hourlyStatsMapper.selectDistinctEmployeeHours();
        log.info("Found {} distinct (employee, hour) combinations to aggregate", employeeHours.size());

        processEmployeeHours(employeeHours, start);
    }

    public void aggregateForEmployees(List<String> employeeIds) {
        log.info("Starting hourly stats aggregation for employees: {}", employeeIds);
        long start = System.currentTimeMillis();

        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = hourlyStatsMapper.selectDistinctEmployeeHoursByEmployees(employeeIds);
        log.info("Found {} distinct (employee, hour) combinations to aggregate for {} employees", employeeHours.size(), employeeIds.size());

        processEmployeeHours(employeeHours, start);
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