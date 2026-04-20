package com.company.clawboard.service;

import com.company.clawboard.dto.*;
import com.company.clawboard.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HourlyStatsMapper hourlyStatsMapper;
    private final EmployeeMapper employeeMapper;
    private final SkillInvocationMapper skillInvocationMapper;

    public DashboardSummaryResponse getSummary(TimeRangeRequest request) {
        return new DashboardSummaryResponse();
    }

    public GlobalStatsResponse getGlobalStats() {
        return new GlobalStatsResponse();
    }

    public List<TrendDataPoint> getTrend(TimeRangeRequest request) {
        return List.of();
    }

    public PageResult<UserSummaryItem> getUserSummaries(TimeRangeRequest request) {
        return new PageResult<>(0, request.getPageOrDefault(), request.getPageSizeOrDefault(), List.of());
    }

    public List<SkillOption> getSkillOptions() {
        var skills = skillInvocationMapper.selectDistinctSkills();
        return skills.stream().map(s -> {
            var opt = new SkillOption();
            opt.setSkillId(s);
            opt.setSkillName(s);
            return opt;
        }).toList();
    }
}
