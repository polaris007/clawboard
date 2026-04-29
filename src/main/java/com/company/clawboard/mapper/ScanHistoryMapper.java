package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardScanHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
    
    /**
     * 更新扫描历史中的聚合统计信息
     */
    void updateAggregationStats(DashboardScanHistory history);
    
    /**
     * 更新扫描状态和错误信息
     */
    void updateStatusAndError(DashboardScanHistory history);
    
    /**
     * 查询指定状态的扫描记录（最新的）
     */
    DashboardScanHistory selectByStatus(@Param("status") String status);
    
    /**
     * 查询最近一次完成的扫描记录（completed 或 failed）
     */
    DashboardScanHistory selectLatestCompleted();
}
