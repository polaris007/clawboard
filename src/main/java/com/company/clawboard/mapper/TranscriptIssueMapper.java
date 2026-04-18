package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardTranscriptIssue;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TranscriptIssueMapper {
    int batchInsertIgnore(List<DashboardTranscriptIssue> issues);
    List<DashboardTranscriptIssue> selectByScanId(Long scanId);
}
