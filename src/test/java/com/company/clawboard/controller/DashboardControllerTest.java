package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.TimeRangeRequest;
import com.company.clawboard.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DashboardController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController Unit Tests")
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        dashboardController = new DashboardController(dashboardService);
    }

    @Test
    @DisplayName("Should return summary from service")
    void testGetSummary() {
        // When
        TimeRangeRequest request = new TimeRangeRequest();
        ApiResponse<?> response = dashboardController.getSummary(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        verify(dashboardService, times(1)).getSummary(request);
    }

    @Test
    @DisplayName("Should return global stats from service")
    void testGetGlobalStats() {
        // When
        ApiResponse<?> response = dashboardController.getGlobalStats();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        verify(dashboardService, times(1)).getGlobalStats();
    }

    @Test
    @DisplayName("Should return user summaries from service")
    void testGetUserSummaries() {
        // When
        TimeRangeRequest request = new TimeRangeRequest();
        ApiResponse<?> response = dashboardController.getUserSummaries(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        verify(dashboardService, times(1)).getUserSummaries(request);
    }
}
