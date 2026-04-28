package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardSessionSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SessionSummaryMapper {
    /**
     * Insert or update session summary (upsert)
     * Uses INSERT ... ON DUPLICATE KEY UPDATE for atomic operation
     */
    int upsert(DashboardSessionSummary summary);
    
    /**
     * Batch upsert session summaries
     */
    int batchUpsert(java.util.List<DashboardSessionSummary> summaries);
    
    /**
     * Select session summary by session_id
     */
    DashboardSessionSummary selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * Get total conversation turns across all sessions
     */
    int selectTotalTurns();
    
    /**
     * Get total conversation turns for specific scan
     */
    int selectTotalTurnsByScanId(@Param("scanId") Long scanId);
    
    /**
     * 删除指定 session 的汇总记录
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
