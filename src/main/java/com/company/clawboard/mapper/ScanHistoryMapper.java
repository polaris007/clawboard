package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardScanHistory;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ScanHistoryMapper {
    long insertAndGetId(DashboardScanHistory history);
    int updateStatus(Long id, String status, Long durationMs, String errorMessage,
                     Integer usersScanned, Integer dirsScanned,
                     Integer filesTotal, Integer filesProcessed, Integer filesSkipped, Integer filesError,
                     Integer newMessages, Integer newTurns, Integer newIssues, Integer newSkillCalls);
    DashboardScanHistory selectById(Long id);
    List<DashboardScanHistory> selectRecent(int limit);
    DashboardScanHistory selectLatest();
}
