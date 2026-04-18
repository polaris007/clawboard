package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardHourlyStats;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HourlyStatsMapper {
    int upsertStats(DashboardHourlyStats stats);
    List<DashboardHourlyStats> selectByTimeRange(String employeeId, Long startTime, Long endTime);
}
