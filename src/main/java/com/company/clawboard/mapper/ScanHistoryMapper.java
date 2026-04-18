package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardScanHistory;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ScanHistoryMapper {
    long insertAndGetId(DashboardScanHistory history);
    int updateStatus(Long id, String status, Long durationMs, String errorMessage);
    DashboardScanHistory selectById(Long id);
    List<DashboardScanHistory> selectRecent(int limit);
}
