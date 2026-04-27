package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.ErrorSearchRequest;
import com.company.clawboard.dto.TimeRangeRequest;
import com.company.clawboard.dto.TurnSearchRequest;
import com.company.clawboard.service.TurnErrorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TurnErrorController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TurnErrorController Unit Tests")
class TurnErrorControllerTest {

    @Mock
    private TurnErrorService turnErrorService;

    private TurnErrorController turnErrorController;

    @BeforeEach
    void setUp() {
        turnErrorController = new TurnErrorController(turnErrorService);
    }

    @Test
    @DisplayName("Should search turns with valid request")
    void testSearchTurns() {
        // Given
        TurnSearchRequest request = new TurnSearchRequest();
        request.setPage(1);
        request.setPageSize(10);

        // When
        ApiResponse<?> response = turnErrorController.searchTurns(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        verify(turnErrorService, times(1)).searchTurns(request);
    }

    @Test
    @DisplayName("Should return error summary from service")
    void testGetErrorSummary() {
        // When
        TimeRangeRequest request = new TimeRangeRequest();
        ApiResponse<?> response = turnErrorController.getErrorSummary(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        verify(turnErrorService, times(1)).getErrorSummary(request);
    }

    @Test
    @DisplayName("Should search errors with valid request")
    void testSearchErrors() {
        // Given
        ErrorSearchRequest request = new ErrorSearchRequest();
        request.setPage(1);
        request.setPageSize(20);

        // When
        ApiResponse<?> response = turnErrorController.searchErrors(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        verify(turnErrorService, times(1)).searchErrors(request);
    }
}
