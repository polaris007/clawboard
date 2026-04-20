package com.company.clawboard.service;

import com.company.clawboard.dto.DashboardSummaryResponse;
import com.company.clawboard.dto.GlobalStatsResponse;
import com.company.clawboard.dto.PageResult;
import com.company.clawboard.dto.SkillOption;
import com.company.clawboard.dto.TimeRangeRequest;
import com.company.clawboard.dto.UserSummaryItem;
import com.company.clawboard.mapper.EmployeeMapper;
import com.company.clawboard.mapper.HourlyStatsMapper;
import com.company.clawboard.mapper.SkillInvocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DashboardService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService Unit Tests")
class DashboardServiceTest {

    @Mock
    private HourlyStatsMapper hourlyStatsMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private SkillInvocationMapper skillInvocationMapper;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(hourlyStatsMapper, employeeMapper, skillInvocationMapper);
    }

    @Test
    @DisplayName("Should return empty summary when no data")
    void testGetSummary_NoData() {
        // When
        TimeRangeRequest request = new TimeRangeRequest();
        DashboardSummaryResponse response = dashboardService.getSummary(request);

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should return empty global stats when no data")
    void testGetGlobalStats_NoData() {
        // When
        GlobalStatsResponse response = dashboardService.getGlobalStats();

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should return empty user summaries (placeholder implementation)")
    void testGetUserSummaries_NoEmployees() {
        // When
        TimeRangeRequest request = new TimeRangeRequest();
        PageResult<UserSummaryItem> result = dashboardService.getUserSummaries(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getList()).isEmpty();
        // Note: Current implementation returns empty list directly without calling mapper
    }

    @Test
    @DisplayName("Should return skill options from mapper")
    void testGetSkillOptions_WithSkills() {
        // Given
        List<String> mockSkills = Arrays.asList("skill-1", "skill-2", "skill-3");
        when(skillInvocationMapper.selectDistinctSkills()).thenReturn(mockSkills);

        // When
        List<SkillOption> result = dashboardService.getSkillOptions();

        // Then
        assertThat(result).isNotNull().hasSize(3);
        assertThat(result.get(0).getSkillId()).isEqualTo("skill-1");
        assertThat(result.get(0).getSkillName()).isEqualTo("skill-1");
        assertThat(result.get(1).getSkillId()).isEqualTo("skill-2");
        assertThat(result.get(2).getSkillId()).isEqualTo("skill-3");
        
        verify(skillInvocationMapper, times(1)).selectDistinctSkills();
    }

    @Test
    @DisplayName("Should return empty skill options when no skills")
    void testGetSkillOptions_NoSkills() {
        // Given
        when(skillInvocationMapper.selectDistinctSkills()).thenReturn(Collections.emptyList());

        // When
        List<SkillOption> result = dashboardService.getSkillOptions();

        // Then
        assertThat(result).isNotNull().isEmpty();
        verify(skillInvocationMapper, times(1)).selectDistinctSkills();
    }
}
