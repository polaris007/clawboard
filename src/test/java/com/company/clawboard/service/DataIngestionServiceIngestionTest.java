package com.company.clawboard.service;

import com.company.clawboard.mapper.*;
import com.company.clawboard.parser.SystemMessageFilter;
import com.company.clawboard.parser.TranscriptParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DataIngestionService ingestParsedTranscript 方法单元测试
 * 重点测试 IngestionResult 返回值的正确性
 */
@ExtendWith(MockitoExtension.class)
class DataIngestionServiceIngestionTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private ConversationTurnMapper turnMapper;

    @Mock
    private SkillInvocationMapper skillMapper;

    @Mock
    private TranscriptIssueMapper issueMapper;

    @Mock
    private SessionSummaryMapper sessionSummaryMapper;

    @Mock
    private SystemMessageFilter systemMessageFilter;

    @Mock
    private ExecutionTraceMapper executionTraceMapper;

    @Mock
    private TranscriptParser transcriptParser;

    @Mock
    private ScanProgressMapper scanProgressMapper;

    @Mock
    private ScanProgressService scanProgressService;

    private DataIngestionService dataIngestionService;

    @BeforeEach
    void setUp() {
        dataIngestionService = new DataIngestionService(
            messageMapper,
            turnMapper,
            skillMapper,
            issueMapper,
            sessionSummaryMapper,
            systemMessageFilter,
            executionTraceMapper,
            transcriptParser,
            scanProgressMapper,
            scanProgressService
        );
    }

    /**
     * 测试 1: null parsed transcript 应该返回 SKIPPED_EMPTY
     */
    @Test
    void testIngestWithNullParsedTranscript() {
        IngestionResult result = dataIngestionService.ingestParsedTranscript(1L, "user1", null);

        assertNotNull(result);
        assertEquals(IngestionStatus.SKIPPED_EMPTY, result.status());
        assertEquals("Invalid parsed transcript", result.errorMessage());
        assertEquals(0, result.messageCount());
        assertEquals(0, result.turnCount());
        assertEquals(0, result.issueCount());
        assertEquals(0, result.skillCount());
    }

    /**
     * 测试 2: sessionId 为 null 应该返回 SKIPPED_EMPTY
     */
    @Test
    void testIngestWithNullSessionId() {
        TranscriptParser.ParsedTranscript parsed = mock(TranscriptParser.ParsedTranscript.class);
        when(parsed.sessionId()).thenReturn(null);

        IngestionResult result = dataIngestionService.ingestParsedTranscript(1L, "user1", parsed);

        assertNotNull(result);
        assertEquals(IngestionStatus.SKIPPED_EMPTY, result.status());
        verify(messageMapper, never()).countBySessionId(anyString());
    }

    /**
     * 测试 3: 文件 mtime 未变化应该返回 SKIPPED_MTIME
     */
    @Test
    void testIngestWithMtimeUnchanged() throws Exception {
        String sessionId = "session-123";
        String filePath = "/test/path/session.jsonl";
        long fileMtime = 1000L;
        
        TranscriptParser.ParsedTranscript parsed = mock(TranscriptParser.ParsedTranscript.class);
        when(parsed.sessionId()).thenReturn(sessionId);
        when(parsed.filePath()).thenReturn(filePath);

        try (var mockedStatic = org.mockito.Mockito.mockStatic(Files.class)) {
            mockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            mockedStatic.when(() -> Files.getLastModifiedTime(any(Path.class)))
                .thenReturn(java.nio.file.attribute.FileTime.fromMillis(fileMtime));

            var progress = new com.company.clawboard.entity.DashboardScanProgress();
            progress.setFileMtime(fileMtime);
            when(scanProgressMapper.selectByEmployeeAndFile(anyString(), anyString()))
                .thenReturn(progress);

            IngestionResult result = dataIngestionService.ingestParsedTranscript(1L, "user1", parsed);

            assertNotNull(result);
            assertEquals(IngestionStatus.SKIPPED_MTIME, result.status());
            assertNull(result.errorMessage());
            assertEquals(0, result.messageCount());
            
            verify(messageMapper, never()).countBySessionId(anyString());
            verify(turnMapper, never()).batchInsertIgnore(any());
        }
    }

    /**
     * 测试 4: 文件 mtime 已变化应该继续处理（不返回 SKIPPED_MTIME）
     */
    @Test
    void testIngestWithMtimeChanged() throws Exception {
        String sessionId = "session-456";
        String filePath = "/test/path/session2.jsonl";
        long oldMtime = 1000L;
        long newMtime = 2000L;
        
        TranscriptParser.ParsedTranscript parsed = mock(TranscriptParser.ParsedTranscript.class);
        when(parsed.sessionId()).thenReturn(sessionId);
        when(parsed.filePath()).thenReturn(filePath);

        try (var mockedStatic = org.mockito.Mockito.mockStatic(Files.class)) {
            mockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            mockedStatic.when(() -> Files.getLastModifiedTime(any(Path.class)))
                .thenReturn(java.nio.file.attribute.FileTime.fromMillis(newMtime));

            var progress = new com.company.clawboard.entity.DashboardScanProgress();
            progress.setFileMtime(oldMtime);
            when(scanProgressMapper.selectByEmployeeAndFile(anyString(), anyString()))
                .thenReturn(progress);

            // Mock session not existing
            when(messageMapper.countBySessionId(sessionId)).thenReturn(0);
            
            // Mock successful inserts (minimal setup to avoid complex mocking)
            lenient().when(turnMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(messageMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(skillMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(issueMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(systemMessageFilter.isSystemGeneratedUserMessage(anyString())).thenReturn(false);

            // Execute - 由于 mock 了所有依赖，应该不会抛出异常
            IngestionResult result = dataIngestionService.ingestParsedTranscript(1L, "user1", parsed);

            // Verify - 不应该返回 SKIPPED_MTIME
            assertNotEquals(IngestionStatus.SKIPPED_MTIME, result.status());
            
            // Verify mtime check was performed
            verify(scanProgressMapper).selectByEmployeeAndFile(anyString(), anyString());
            verify(messageMapper).countBySessionId(sessionId);
        }
    }

    /**
     * 测试 5: filePath 为 null 时跳过 mtime 检查
     */
    @Test
    void testIngestWithNullFilePath() {
        String sessionId = "session-no-path";
        
        TranscriptParser.ParsedTranscript parsed = mock(TranscriptParser.ParsedTranscript.class);
        when(parsed.sessionId()).thenReturn(sessionId);
        when(parsed.filePath()).thenReturn(null);

        when(messageMapper.countBySessionId(sessionId)).thenReturn(0);
        lenient().when(turnMapper.batchInsertIgnore(any())).thenReturn(0);
        lenient().when(messageMapper.batchInsertIgnore(any())).thenReturn(0);
        lenient().when(skillMapper.batchInsertIgnore(any())).thenReturn(0);
        lenient().when(issueMapper.batchInsertIgnore(any())).thenReturn(0);
        lenient().when(systemMessageFilter.isSystemGeneratedUserMessage(anyString())).thenReturn(false);

        IngestionResult result = dataIngestionService.ingestParsedTranscript(1L, "user1", parsed);

        // Should not return SKIPPED_MTIME since no file path
        assertNotEquals(IngestionStatus.SKIPPED_MTIME, result.status());
        
        // Verify mtime check was NOT performed
        verify(scanProgressMapper, never()).selectByEmployeeAndFile(anyString(), anyString());
    }

    /**
     * 测试 6: mtime 检查异常时应该继续处理
     */
    @Test
    void testIngestWithMtimeCheckException() throws Exception {
        String sessionId = "session-exception";
        String filePath = "/test/path/error.jsonl";
        
        TranscriptParser.ParsedTranscript parsed = mock(TranscriptParser.ParsedTranscript.class);
        when(parsed.sessionId()).thenReturn(sessionId);
        when(parsed.filePath()).thenReturn(filePath);

        try (var mockedStatic = org.mockito.Mockito.mockStatic(Files.class)) {
            // Simulate exception during file access
            mockedStatic.when(() -> Files.exists(any(Path.class)))
                .thenThrow(new RuntimeException("File system error"));

            lenient().when(messageMapper.countBySessionId(sessionId)).thenReturn(0);
            lenient().when(turnMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(messageMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(skillMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(issueMapper.batchInsertIgnore(any())).thenReturn(0);
            lenient().when(systemMessageFilter.isSystemGeneratedUserMessage(anyString())).thenReturn(false);

            IngestionResult result = dataIngestionService.ingestParsedTranscript(1L, "user1", parsed);

            // Should continue processing despite mtime check failure
            assertNotEquals(IngestionStatus.SKIPPED_MTIME, result.status());
        }
    }
}
