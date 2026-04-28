package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardTranscriptIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TranscriptIssueMapper {
    int batchInsertIgnore(List<DashboardTranscriptIssue> issues);
    List<DashboardTranscriptIssue> selectByScanId(Long scanId);
    List<DashboardTranscriptIssue> selectAll();
    
    /**
     * 根据轮次ID列表查询问题
     * @param turnIds 轮次ID列表
     * @return 问题列表，按 occurred_at 升序排列
     */
    List<DashboardTranscriptIssue> selectByTurnIds(@Param("turnIds") List<Long> turnIds);
    
    /**
     * 根据扫描ID列表查询问题（包括 turn_id 为 NULL 的问题）
     * @param scanIds 扫描ID列表
     * @return 问题列表，按 occurred_at 升序排列
     */
    List<DashboardTranscriptIssue> selectByScanIds(@Param("scanIds") List<Long> scanIds);
    
    /**
     * 根据时间范围和轮次ID列表查询问题
     * 包括：1) turn_id 在指定列表中的问题  2) occurred_at 在时间范围内但 turn_id 为 NULL 的问题
     * @param turnIds 轮次ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 问题列表，按 occurred_at 升序排列
     */
    List<DashboardTranscriptIssue> selectByTimeRangeAndTurnIds(
        @Param("turnIds") List<Long> turnIds,
        @Param("startTime") java.time.LocalDateTime startTime,
        @Param("endTime") java.time.LocalDateTime endTime);
    
    /**
     * 根据时间范围查询问题（简单直接，只检查 occurred_at）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 问题列表，按 occurred_at 升序排列
     */
    List<DashboardTranscriptIssue> selectByTimeRange(
        @Param("startTime") java.time.LocalDateTime startTime,
        @Param("endTime") java.time.LocalDateTime endTime);
}
