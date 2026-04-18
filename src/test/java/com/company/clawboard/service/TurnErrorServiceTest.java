package com.company.clawboard.service;

import com.company.clawboard.dto.*;
import com.company.clawboard.mapper.ConversationTurnMapper;
import com.company.clawboard.mapper.TranscriptIssueMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TurnErrorService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TurnErrorService Unit Tests")
class TurnErrorServiceTest {

    @Mock
    private ConversationTurnMapper turnMapper;

    @Mock
    private TranscriptIssueMapper issueMapper;

    private TurnErrorService turnErrorService;

    @BeforeEach
    void setUp() {
        turnErrorService = new TurnErrorService(turnMapper, issueMapper);
    }

    @Test
    @DisplayName("Should return empty page when searching turns with no data")
    void testSearchTurns_NoData() {
        // Given
        TurnSearchRequest request = new TurnSearchRequest();
        request.setPage(1);
        request.setPageSize(10);

        // When
        PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getList()).isEmpty();
    }

    @Test
    @DisplayName("Should return empty trace response")
    void testGetTrace_EmptyResponse() {
        // When
        TraceResponse response = turnErrorService.getTrace(1L);

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should return empty error summary")
    void testGetErrorSummary_EmptyResponse() {
        // When
        ErrorSummaryResponse response = turnErrorService.getErrorSummary();

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should return empty page when searching errors with no data")
    void testSearchErrors_NoData() {
        // Given
        ErrorSearchRequest request = new ErrorSearchRequest();
        request.setPage(1);
        request.setPageSize(20);

        // When
        PageResult<ErrorSearchItem> result = turnErrorService.searchErrors(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(20);
        assertThat(result.getList()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void testSearchTurns_NullRequest() {
        // When & Then - should throw NullPointerException or handle gracefully
        // Current implementation will throw NPE, which is expected behavior
        org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> turnErrorService.searchTurns(null)
        );
    }
}
