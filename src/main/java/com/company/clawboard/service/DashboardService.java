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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HourlyStatsMapper hourlyStatsMapper;
    private final EmployeeMapper employeeMapper;
    private final SkillInvocationMapper skillInvocationMapper;
    private final ConversationTurnMapper conversationTurnMapper;
    private final VInstanceDetailMapper vInstanceDetailMapper;

    public DashboardSummaryResponse getSummary(TimeRangeRequest request) {
        var response = new DashboardSummaryResponse();
        
        // 标准化时间参数
        String normalizedStartTime = normalizeStartTime(request.getStartTime());
        String normalizedEndTime = normalizeEndTime(request.getEndTime());
        
        // 从 hourly_stats 表查询数据，支持团队和姓名筛选
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            normalizedStartTime,
            normalizedEndTime
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
        
        // 计算任务成功率：没有错误的完成轮次数 / 总对话轮次数（都排除系统轮次）
        Integer totalNonSystemTurns = conversationTurnMapper.countNonSystemTurns(
            request.getTeamName(),
            request.getUserId(),
            normalizedStartTime,
            normalizedEndTime
        );
        Integer noErrorCompleteTurns = conversationTurnMapper.countNoErrorCompleteTurns(
            request.getTeamName(),
            request.getUserId(),
            normalizedStartTime,
            normalizedEndTime
        );
        
        int total = (totalNonSystemTurns != null) ? totalNonSystemTurns : 0;
        int complete = (noErrorCompleteTurns != null) ? noErrorCompleteTurns : 0;
        
        if (total > 0) {
            double successRate = ((double) complete / total) * 100;
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
        
        // 新增：统计注册用户数（从 v_instance_detail 视图查询）
        Integer registeredUsers = vInstanceDetailMapper.countRegisteredUsers();
        response.setRegisteredUsers(registeredUsers != null ? registeredUsers : 0);
        
        // 新增：统计 OpenClaw 实例总数量（排除 deleted）
        Integer instanceTotalCount = vInstanceDetailMapper.countInstanceTotal();
        response.setInstanceTotalCount(instanceTotalCount != null ? instanceTotalCount : 0);
        
        // 新增：统计 OpenClaw 异常实例数量（排除 deleted 和 running）
        Integer instanceAbnormalCount = vInstanceDetailMapper.countInstanceAbnormal();
        response.setInstanceAbnormalCount(instanceAbnormalCount != null ? instanceAbnormalCount : 0);
        
        return response;
    }

    // 北京时区
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 将 startTime 向下取整到下一个小时的开始
     * 例如："2026-04-22 00:59:03" -> "2026-04-22 01:00:00"
     *      "2026-04-22 00:00:00" -> "2026-04-22 00:00:00"（已经是整点）
     */
    private String normalizeStartTime(String startTime) {
        if (startTime == null || startTime.isEmpty()) {
            return startTime;
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(startTime, DATETIME_FORMATTER);
            // 如果分钟或秒不为0，则向上取整到下一小时
            if (dt.getMinute() > 0 || dt.getSecond() > 0) {
                dt = dt.plusHours(1).withMinute(0).withSecond(0).withNano(0);
            }
            return dt.format(DATETIME_FORMATTER);
        } catch (Exception e) {
            // 解析失败，返回原值
            return startTime;
        }
    }

    /**
     * 将 endTime 保持不变（因为查询条件是 <=，所以不需要调整）
     * 但为了语义清晰，保留此方法以备将来扩展
     */
    private String normalizeEndTime(String endTime) {
        return endTime;
    }

    /**
     * 解析小时字符串为 LocalDateTime
     */
    private LocalDateTime parseHour(String hourStr) {
        return LocalDateTime.parse(hourStr, DATETIME_FORMATTER);
    }

    public List<TrendDataPoint> getTrend(TimeRangeRequest request) {
        // 标准化时间参数
        String normalizedStartTime = normalizeStartTime(request.getStartTime());
        String normalizedEndTime = normalizeEndTime(request.getEndTime());
        
        // 从 hourly_stats 表查询数据
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            normalizedStartTime,
            normalizedEndTime
        );
        
        if (stats == null || stats.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 按小时分组
        Map<LocalDateTime, List<DashboardHourlyStats>> statsByHour = stats.stream()
            .collect(Collectors.groupingBy(DashboardHourlyStats::getStatHour));
        
        // ✅ 生成完整时间序列
        LocalDateTime startHour = parseHour(normalizedStartTime);
        LocalDateTime endHour = parseHour(normalizedEndTime);
        
        List<TrendDataPoint> trendPoints = new ArrayList<>();
        LocalDateTime currentHour = startHour;
        
        while (!currentHour.isAfter(endHour)) {
            TrendDataPoint point = new TrendDataPoint();
            point.setTimeLabel(currentHour.format(TIME_FORMATTER));
            
            List<DashboardHourlyStats> hourStats = statsByHour.getOrDefault(currentHour, Collections.emptyList());
            
            if (hourStats.isEmpty()) {
                // 缺失的小时 → 填充 0
                point.setTokens(0L);
                point.setTurns(0);
                point.setSkills(0);
            } else {
                // 有数据 → 聚合
                point.setTokens(hourStats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum());
                point.setTurns(hourStats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum());
                point.setSkills(hourStats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum());
            }
            
            trendPoints.add(point);
            currentHour = currentHour.plusHours(1);
        }
        
        return trendPoints.stream()
            .limit(720)  // 最多返回720条（30天）
            .collect(Collectors.toList());
    }

    public PageResult<UserSummaryItem> getUserSummaries(TimeRangeRequest request) {
        // 标准化时间参数
        String normalizedStartTime = normalizeStartTime(request.getStartTime());
        String normalizedEndTime = normalizeEndTime(request.getEndTime());
        
        // 从 hourly_stats 表查询数据，支持团队和姓名筛选
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            normalizedStartTime,
            normalizedEndTime
        );
        
        // 新增：收集所有需要查询状态的 employeeId
        List<String> employeeIds = stats.stream()
            .map(DashboardHourlyStats::getEmployeeId)
            .distinct()
            .toList();
        
        // 新增：批量查询用户实例状态
        Map<String, String> userStatusMap = new java.util.HashMap<>();
        if (!employeeIds.isEmpty()) {
            List<Map<String, Object>> statuses = vInstanceDetailMapper.selectStatusByUids(employeeIds);
            for (Map<String, Object> status : statuses) {
                String uid = (String) status.get("uid");
                String instanceStatus = (String) status.get("status");
                userStatusMap.put(uid, instanceStatus);
            }
        }
        
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
                    
                    // 修改：根据 v_instance_detail 的 status 字段判断
                    String instanceStatus = userStatusMap.get(employeeId);
                    item.setStatus("running".equals(instanceStatus));
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
