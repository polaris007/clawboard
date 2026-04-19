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
}
