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
    @DisplayName("Should return empty error summary")
    void testGetErrorSummary_EmptyResponse() {
        // When
        TimeRangeRequest request = new TimeRangeRequest();
        ErrorSummaryResponse response = turnErrorService.getErrorSummary(request);

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
    @DisplayName("Should use fuzzy matching when only userIdLike is provided")
    void testSearchTurns_WithUserIdLikeOnly() {
        // Given
        TurnSearchRequest request = new TurnSearchRequest();
        request.setUserIdLike("181");
        request.setPage(1);
        request.setPageSize(10);
        
        when(turnMapper.selectTurnsWithFilters(null, "181", null, null))
            .thenReturn(Collections.emptyList());
        
        // When
        PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
        
        // Then
        verify(turnMapper).selectTurnsWithFilters(null, "181", null, null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should prefer exact match when both userId and userIdLike are provided")
    void testSearchTurns_UserIdTakesPriorityOverUserIdLike() {
        // Given
        TurnSearchRequest request = new TurnSearchRequest();
        request.setUserId("18101142");
        request.setUserIdLike("181");
        request.setPage(1);
        request.setPageSize(10);
        
        when(turnMapper.selectTurnsWithFilters("18101142", "181", null, null))
            .thenReturn(Collections.emptyList());
        
        // When
        PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
        
        // Then
        // userId 应该被使用,userIdLike 被忽略(但会传递到 Mapper)
        verify(turnMapper).selectTurnsWithFilters("18101142", "181", null, null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should not apply filter when userIdLike is empty string")
    void testSearchTurns_EmptyUserIdLike() {
        // Given
        TurnSearchRequest request = new TurnSearchRequest();
        request.setUserIdLike("");
        request.setPage(1);
        request.setPageSize(10);
        
        when(turnMapper.selectTurnsWithFilters(null, "", null, null))
            .thenReturn(Collections.emptyList());
        
        // When
        PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
        
        // Then
        verify(turnMapper).selectTurnsWithFilters(null, "", null, null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should combine userIdLike with other filters")
    void testSearchTurns_UserIdLikeWithTimeRangeAndSkill() {
        // Given
        TurnSearchRequest request = new TurnSearchRequest();
        request.setUserIdLike("181");
        request.setStartTime("2026-04-21 00:00:00");
        request.setEndTime("2026-04-21 23:59:59");
        request.setSkillId("pptx");
        request.setPage(1);
        request.setPageSize(10);
        
        when(turnMapper.selectTurnsWithFilters(null, "181", "2026-04-21 00:00:00", "2026-04-21 23:59:59"))
            .thenReturn(Collections.emptyList());
        
        // When
        PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
        
        // Then
        verify(turnMapper).selectTurnsWithFilters(null, "181", "2026-04-21 00:00:00", "2026-04-21 23:59:59");
        assertThat(result).isNotNull();
    }
}
