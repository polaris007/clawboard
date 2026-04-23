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
}
