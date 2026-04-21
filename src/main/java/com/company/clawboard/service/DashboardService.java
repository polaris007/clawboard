package com.company.clawboard.service;

import com.company.clawboard.dto.*;
import com.company.clawboard.entity.DashboardHourlyStats;
import com.company.clawboard.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HourlyStatsMapper hourlyStatsMapper;
    private final EmployeeMapper employeeMapper;
    private final SkillInvocationMapper skillInvocationMapper;

    public DashboardSummaryResponse getSummary(TimeRangeRequest request) {
        var response = new DashboardSummaryResponse();
        
        // 从 hourly_stats 表查询数据
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(null, request.getStartTime(), request.getEndTime());
        
        // 聚合计算
        long consumedTokens = stats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum();
        int conversationTurns = stats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum();
        int skillCalls = stats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum();
        int activeUsers = (int) stats.stream().map(DashboardHourlyStats::getEmployeeId).distinct().count();
        
        response.setConsumedTokens(consumedTokens);
        response.setConversationTurns(conversationTurns);
        response.setSkillCalls(skillCalls);
        response.setActiveUsers(activeUsers);
        
        // 计算任务成功率
        if (conversationTurns > 0) {
            int completeTurns = stats.stream().mapToInt(DashboardHourlyStats::getCompleteTurns).sum();
            double successRate = ((double) completeTurns / conversationTurns) * 100;
            response.setTaskSuccessRate(successRate);
        } else {
            response.setTaskSuccessRate(0.0);
        }
        
        return response;
    }

    public GlobalStatsResponse getGlobalStats() {
        var response = new GlobalStatsResponse();
        
        // 从 hourly_stats 表查询所有数据
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(null, null, null);
        
        // 聚合计算
        long totalTokens = stats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum();
        int totalTurns = stats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum();
        int totalSkillCalls = stats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum();
        int totalUsers = (int) stats.stream().map(DashboardHourlyStats::getEmployeeId).distinct().count();
        
        response.setTotalTokens(totalTokens);
        response.setTotalTurns(totalTurns);
        response.setTotalSkillCalls(totalSkillCalls);
        response.setTotalUsers(totalUsers);
        
        return response;
    }

    // 北京时区
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");

    public List<TrendDataPoint> getTrend(TimeRangeRequest request) {
        // 从 hourly_stats 表查询数据
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(null, request.getStartTime(), request.getEndTime());
        
        // 按小时分组并转换为趋势数据点，限制最大返回720条
        return stats.stream()
                .collect(Collectors.groupingBy(DashboardHourlyStats::getStatHour))
                .entrySet().stream()
                .map(entry -> {
                    LocalDateTime hour = entry.getKey();
                    List<DashboardHourlyStats> hourStats = entry.getValue();
                    
                    var point = new TrendDataPoint();
                    // 格式化为 YYYY-MM-DD HH:00:00（北京时区）
                    point.setTimeLabel(hour.format(TIME_FORMATTER));
                    point.setTokens(hourStats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum());
                    point.setTurns(hourStats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum());
                    point.setSkills(hourStats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum());
                    
                    return point;
                })
                .limit(720)
                .collect(Collectors.toList());
    }

    public PageResult<UserSummaryItem> getUserSummaries(TimeRangeRequest request) {
        // 从 hourly_stats 表查询数据
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(null, request.getStartTime(), request.getEndTime());
        
        // 按员工分组并转换为用户明细
        var userItems = stats.stream()
                .collect(Collectors.groupingBy(DashboardHourlyStats::getEmployeeId))
                .entrySet().stream()
                .map(entry -> {
                    String employeeId = entry.getKey();
                    List<DashboardHourlyStats> userStats = entry.getValue();
                    
                    var item = new UserSummaryItem();
                    item.setUserId(employeeId);
                    item.setUserName(employeeId); // 暂时使用 employeeId 作为 userName
                    item.setOrgCode("18100000"); // 暂时使用默认机构号
                    item.setStatus("active"); // 暂时设置为 active
                    item.setLastHeartbeat(System.currentTimeMillis()); // 暂时使用当前时间
                    
                    // 计算 token 统计
                    var tokenStats = new UserSummaryItem.TokenStats();
                    tokenStats.setTotal(userStats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum());
                    tokenStats.setInput(userStats.stream().mapToLong(DashboardHourlyStats::getInputTokens).sum());
                    tokenStats.setOutput(userStats.stream().mapToLong(DashboardHourlyStats::getOutputTokens).sum());
                    item.setTokens(tokenStats);
                    
                    // 计算 turn 统计
                    var turnStats = new UserSummaryItem.TurnStats();
                    turnStats.setTotal(userStats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum());
                    turnStats.setComplete(userStats.stream().mapToInt(DashboardHourlyStats::getCompleteTurns).sum());
                    turnStats.setError(userStats.stream().mapToInt(DashboardHourlyStats::getErrorTurns).sum());
                    item.setTurns(turnStats);
                    
                    // 计算 skill 统计
                    var skillStats = new UserSummaryItem.SkillStats();
                    skillStats.setTotal(userStats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum());
                    skillStats.setSuccess(userStats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum() - 
                                         userStats.stream().mapToInt(DashboardHourlyStats::getSkillErrors).sum());
                    skillStats.setError(userStats.stream().mapToInt(DashboardHourlyStats::getSkillErrors).sum());
                    item.setSkillCalls(skillStats);
                    
                    // 计算 tool 统计
                    var toolStats = new UserSummaryItem.ToolStats();
                    toolStats.setTotal(userStats.stream().mapToInt(DashboardHourlyStats::getToolCalls).sum());
                    toolStats.setSuccess(userStats.stream().mapToInt(DashboardHourlyStats::getToolCalls).sum() - 
                                        userStats.stream().mapToInt(DashboardHourlyStats::getToolErrors).sum());
                    toolStats.setError(userStats.stream().mapToInt(DashboardHourlyStats::getToolErrors).sum());
                    item.setToolCalls(toolStats);
                    
                    // 暂时返回空列表
                    item.setTopSkills(List.of());
                    
                    return item;
                })
                .collect(Collectors.toList());
        
        return new PageResult<>(userItems.size(), request.getPageOrDefault(), request.getPageSizeOrDefault(), userItems);
    }

    public List<SkillOption> getSkillOptions() {
        var skills = skillInvocationMapper.selectDistinctSkills();
        return skills.stream().map(s -> {
            var opt = new SkillOption();
            opt.setSkillId(s);
            opt.setSkillName(s);
            return opt;
        }).toList();
    }
}
