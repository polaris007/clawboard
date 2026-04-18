package com.company.clawboard.service;

import com.company.clawboard.dto.*;
import com.company.clawboard.mapper.ConversationTurnMapper;
import com.company.clawboard.mapper.TranscriptIssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TurnErrorService {

    private final ConversationTurnMapper turnMapper;
    private final TranscriptIssueMapper issueMapper;

    public PageResult<TurnSearchItem> searchTurns(TurnSearchRequest request) {
        return new PageResult<>(0, request.getPageOrDefault(), request.getPageSizeOrDefault(), java.util.List.of());
    }

    public TraceResponse getTrace(Long turnId) {
        return new TraceResponse();
    }

    public ErrorSummaryResponse getErrorSummary() {
        return new ErrorSummaryResponse();
    }

    public PageResult<ErrorSearchItem> searchErrors(ErrorSearchRequest request) {
        return new PageResult<>(0, request.getPageOrDefault(), request.getPageSizeOrDefault(), java.util.List.of());
    }
}
