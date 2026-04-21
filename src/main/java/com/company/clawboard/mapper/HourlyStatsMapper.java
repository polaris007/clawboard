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
     * 根据时间范围、团队和姓名筛选小时统计数据
     * @param teamName 团队名称（可选）
     * @param userName 姓名（可选，模糊匹配）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 小时统计数据列表
     */
    List<DashboardHourlyStats> selectByTimeRange(
        @Param("teamName") String teamName,
        @Param("userName") String userName,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );

    record EmployeeHourKey(String employeeId, LocalDateTime statHour) {}

    List<EmployeeHourKey> selectDistinctEmployeeHours();
    List<EmployeeHourKey> selectDistinctEmployeeHoursByEmployees(@Param("employeeIds") List<String> employeeIds);

    DashboardHourlyStats aggregateTokensByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    DashboardHourlyStats aggregateTurnsByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    int aggregateIssuesByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);
}
