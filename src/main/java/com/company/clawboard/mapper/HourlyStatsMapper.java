package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardHourlyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HourlyStatsMapper {
    int upsertStats(DashboardHourlyStats stats);
    
    /**
     * 根据时间范围、团队和工号筛选小时统计数据
     * @param teamName 团队名称（可选）
     * @param userId 工号（employee_id，可选，精确匹配）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 小时统计数据列表
     */
    List<DashboardHourlyStats> selectByTimeRange(
        @Param("teamName") String teamName,
        @Param("userId") String userId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );

    record EmployeeHourKey(String employeeId, LocalDateTime statHour) {}

    List<EmployeeHourKey> selectDistinctEmployeeHours();
    List<EmployeeHourKey> selectDistinctEmployeeHoursByEmployees(@Param("employeeIds") List<String> employeeIds);
    
    /**
     * 根据员工ID列表和时间范围查询需要聚合的小时
     * @param employeeIds 员工ID列表
     * @param cutoffTime 时间窗口截止时间
     * @return (employee_id, stat_hour) 组合列表
     */
    List<EmployeeHourKey> selectDistinctEmployeeHoursByEmployeesAndTimeRange(
        @Param("employeeIds") List<String> employeeIds,
        @Param("cutoffTime") LocalDateTime cutoffTime
    );

    DashboardHourlyStats aggregateTokensByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    DashboardHourlyStats aggregateTurnsByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    int aggregateIssuesByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    /**
     * 查询指定员工和小时的统计数据
     * @param employeeId 员工ID
     * @param statHour 统计小时
     * @return 统计数据，如果不存在返回 null
     */
    DashboardHourlyStats selectByEmployeeAndHour(
        @Param("employeeId") String employeeId, 
        @Param("statHour") LocalDateTime statHour
    );
}
