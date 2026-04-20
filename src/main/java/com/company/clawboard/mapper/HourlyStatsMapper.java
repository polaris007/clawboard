package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardHourlyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HourlyStatsMapper {
    int upsertStats(DashboardHourlyStats stats);
    List<DashboardHourlyStats> selectByTimeRange(String employeeId, Long startTime, Long endTime);

    record EmployeeHourKey(String employeeId, LocalDateTime statHour) {}

    List<EmployeeHourKey> selectDistinctEmployeeHours();
    List<EmployeeHourKey> selectDistinctEmployeeHoursByEmployees(@Param("employeeIds") List<String> employeeIds);

    DashboardHourlyStats aggregateTokensByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    DashboardHourlyStats aggregateTurnsByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);

    int aggregateIssuesByHour(@Param("employeeId") String employeeId, @Param("statHour") String statHour);
}
