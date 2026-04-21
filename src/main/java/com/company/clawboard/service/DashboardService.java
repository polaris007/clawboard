package com.company.clawboard.service;

import com.company.clawboard.dto.*;
import com.company.clawboard.entity.DashboardEmployee;
import com.company.clawboard.entity.DashboardHourlyStats;
import com.company.clawboard.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
        
        // 从 hourly_stats 表查询数据，支持团队和姓名筛选
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            request.getStartTime(),
            request.getEndTime()
        );
        
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
        
        // 从 hourly_stats 表查询所有数据（不受筛选条件影响）
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(null, null, null, null);
        
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
        // 从 hourly_stats 表查询数据，支持团队和姓名筛选
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            request.getStartTime(),
            request.getEndTime()
        );
        
        // 按小时分组并转换为趋势数据点
        // API文档要求：后端始终以小时为最小粒度返回，不做聚合
        // 从startTime所在的小时开始，到endTime所在的小时结束
        List<TrendDataPoint> trendPoints = new java.util.ArrayList<>();
        
        // 如果查询结果为空，直接返回空列表
        if (stats == null || stats.isEmpty()) {
            return trendPoints;
        }
        
        // 按小时分组，每个小时一个数据点
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
                .limit(720)  // 最多返回720条（30天）
                .collect(Collectors.toList());
    }

    public PageResult<UserSummaryItem> getUserSummaries(TimeRangeRequest request) {
        // 从 hourly_stats 表查询数据，支持团队和姓名筛选
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            request.getStartTime(),
            request.getEndTime()
        );
        
        // 按员工分组并转换为用户明细
        var userItems = stats.stream()
                .collect(Collectors.groupingBy(DashboardHourlyStats::getEmployeeId))
                .entrySet().stream()
                .map(entry -> {
                    String employeeId = entry.getKey();
                    List<DashboardHourlyStats> userStats = entry.getValue();
                    
                    var item = new UserSummaryItem();
                    item.setUserId(employeeId);
                    
                    // 从 employee 表获取员工信息
                    DashboardEmployee employee = employeeMapper.selectByEmployeeId(employeeId);
                    if (employee != null) {
                        item.setUserName(employee.getEmployeeName());
                        item.setOrgCode(employee.getTeamName());
                    } else {
                        item.setUserName(employeeId);
                        item.setOrgCode("未知");
                    }
                    
                    // 暂时设置为 active（实际应根据心跳时间判断）
                    item.setStatus("active");
                    item.setLastHeartbeat(System.currentTimeMillis());
                    
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
        
        // 由于需要先分组再分页，采用内存分页方式
        int total = userItems.size();
        int page = request.getPageOrDefault();
        int pageSize = request.getPageSizeOrDefault();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<UserSummaryItem> pagedItems = fromIndex < total ? 
            userItems.subList(fromIndex, toIndex) : List.of();
        
        return new PageResult<>(total, page, pageSize, pagedItems);
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
