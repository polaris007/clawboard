package com.company.clawboard.service;

import com.company.clawboard.entity.*;
import com.company.clawboard.mapper.*;
import com.company.clawboard.parser.IssueDetector;
import com.company.clawboard.parser.TranscriptParser;
import com.company.clawboard.parser.TurnAssembler;
import com.company.clawboard.parser.SystemMessageFilter;
import com.company.clawboard.parser.model.MessageRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to convert parsed transcript data to entities and batch insert into database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataIngestionService {

    // 北京时区
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    private final MessageMapper messageMapper;
    private final ConversationTurnMapper turnMapper;
    private final SkillInvocationMapper skillMapper;
    private final TranscriptIssueMapper issueMapper;
    private final SessionSummaryMapper sessionSummaryMapper;
    private final SystemMessageFilter systemMessageFilter;

    /**
     * Ingest parsed transcript data into database
     * @param scanId Current scan ID
     * @param employeeId Employee ID extracted from file path
     * @param parsed Parsed transcript data
     */
    @Transactional
    public void ingestParsedTranscript(Long scanId, String employeeId, TranscriptParser.ParsedTranscript parsed) {
        if (parsed == null || parsed.sessionId() == null) {
            log.warn("Cannot ingest null or invalid parsed transcript");
            return;
        }

        String sessionId = parsed.sessionId();
        LocalDateTime now = LocalDateTime.now();

        // Step 1: Convert and insert messages
        List<DashboardMessage> messages = convertToMessages(scanId, parsed.messages(), sessionId, employeeId, now);
        if (!messages.isEmpty()) {
            int inserted = messageMapper.batchInsertIgnore(messages);
            log.debug("Inserted {} messages for session {}", inserted, sessionId);
        }

        // Step 2: Convert and insert conversation turns
        List<DashboardConversationTurn> turns = convertToTurns(scanId, parsed.turns(), sessionId, employeeId, now);
        if (!turns.isEmpty()) {
            int inserted = turnMapper.batchInsertIgnore(turns);
            log.debug("Inserted {} turns for session {}", inserted, sessionId);
        }

        // Step 3: Convert and insert skill invocations
        List<DashboardSkillInvocation> skills = convertToSkillInvocations(
                scanId, parsed.skillInvocations(), sessionId, employeeId, now);
        if (!skills.isEmpty()) {
            int inserted = skillMapper.batchInsertIgnore(skills);
            log.debug("Inserted {} skill invocations for session {}", inserted, sessionId);
        }

        // Step 4: Convert and insert issues
        List<DashboardTranscriptIssue> issues = convertToIssues(
                scanId, parsed.issues(), sessionId, employeeId, now);
        if (!issues.isEmpty()) {
            int inserted = issueMapper.batchInsertIgnore(issues);
            log.debug("Inserted {} issues for session {}", inserted, sessionId);
        }

        // Step 5: Update session summary
        // Calculate conversation turns like Python does: count non-system user messages
        int conversationTurns = 0;
        for (MessageRecord msg : parsed.messages()) {
            if ("user".equals(msg.role())) {
                // Check if this is a system-generated user message
                String content = msg.textContent();
                if (content != null && !content.isEmpty()) {
                    // Use systemMessageFilter to check if this is a system-generated user message
                    boolean isSystemGenerated = systemMessageFilter.isSystemGeneratedUserMessage(content);
                    if (!isSystemGenerated) {
                        conversationTurns++;
                    }
                }
            }
        }

        updateSessionSummary(scanId, sessionId, employeeId, 
            messages.size(), conversationTurns, skills.size(), issues.size(), now);

        log.info("Ingested session {}: {} messages, {} turns, {} skills, {} issues",
                sessionId, messages.size(), conversationTurns, skills.size(), issues.size());
    }

    /**
     * Convert MessageRecord list to DashboardMessage entities
     */
    private List<DashboardMessage> convertToMessages(Long scanId,
                                                      List<MessageRecord> messages, 
                                                      String sessionId, 
                                                      String employeeId,
                                                      LocalDateTime now) {
        List<DashboardMessage> result = new ArrayList<>();

        for (MessageRecord msg : messages) {
            DashboardMessage entity = new DashboardMessage();
            entity.setScanId(scanId);
            entity.setSessionId(sessionId);
            entity.setMessageId(msg.id());  // Changed from messageId() to id()
            entity.setEmployeeId(employeeId);
            entity.setRole(msg.role());
            
            // Convert epoch milliseconds to LocalDateTime
            if (msg.epochMs() > 0) {
                entity.setMessageTimestamp(
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(msg.epochMs()), BEIJING_ZONE));
            }
            
            // Extract token info from UsageInfo
            if (msg.usage() != null) {
                entity.setInputTokens(msg.usage().inputTokens());
                entity.setOutputTokens(msg.usage().outputTokens());
                entity.setCacheReadTokens(msg.usage().cacheReadTokens());
                entity.setCacheWriteTokens(msg.usage().cacheWriteTokens());
                entity.setTotalTokens(msg.usage().totalTokens());
                entity.setCostTotal(msg.usage().costTotal());
            }
            
            entity.setProvider(msg.provider());
            entity.setModel(msg.model());
            entity.setStopReason(msg.stopReason());
            entity.setDurationMs(msg.durationMs());
            entity.setIsError(hasMessageError(msg) ? 1 : 0);  // Use enhanced error detection
            entity.setErrorMessage(msg.errorMessage());  // Added for storing error messages
            
            // Extract tool information if present
            if (msg.toolCalls() != null && !msg.toolCalls().isEmpty()) {
                entity.setToolName(msg.toolCalls().get(0).name());
                entity.setToolCallId(msg.toolCalls().get(0).id());
            }
            
            // 标记系统消息
            if ("user".equals(msg.role())) {
                boolean isSystem = systemMessageFilter.isSystemGeneratedUserMessage(msg.textContent());
                entity.setIsSystem(isSystem ? 1 : 0);
            } else {
                entity.setIsSystem(0);
            }
            entity.setParentId(msg.parentId());
            entity.setCreatedAt(now);

            result.add(entity);
        }

        return result;
    }

    /**
     * Convert AssembledTurn list to DashboardConversationTurn entities
     */
    private List<DashboardConversationTurn> convertToTurns(Long scanId,
                                                            List<TurnAssembler.AssembledTurn> turns,
                                                            String sessionId,
                                                            String employeeId,
                                                            LocalDateTime now) {
        List<DashboardConversationTurn> result = new ArrayList<>();

        for (int i = 0; i < turns.size(); i++) {
            TurnAssembler.AssembledTurn turn = turns.get(i);
            DashboardConversationTurn entity = new DashboardConversationTurn();
            
            entity.setScanId(scanId);
            entity.setSessionId(sessionId);
            entity.setEmployeeId(employeeId);
            entity.setTurnIndex(i + 1); // 1-based index
            
            // Extract user input
            entity.setUserInput(turn.userInput());
            
            // Set status
            entity.setStatus(turn.status());
            entity.setIsComplete(turn.isComplete() ? 1 : 0);
            entity.setHasError(turn.hasError() ? 1 : 0);
            
            // Set start and end message IDs from AssembledTurn
            entity.setStartMessageId(turn.startMessageId() != null ? turn.startMessageId() : "");
            entity.setEndMessageId(turn.endMessageId() != null ? turn.endMessageId() : "");
            
            // Set start and end times from AssembledTurn
            if (turn.startTime() > 0) {
                entity.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(turn.startTime()), BEIJING_ZONE));
            } else {
                entity.setStartTime(now);
            }
            if (turn.endTime() > 0) {
                entity.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(turn.endTime()), BEIJING_ZONE));
            } else {
                entity.setEndTime(now);
            }
            entity.setTotalInputTokens(0);
            entity.setTotalOutputTokens(0);
            entity.setTotalTokens(0);
            entity.setTotalCost(BigDecimal.ZERO);
            entity.setToolCallsCount(0);
            entity.setToolCallsSuccess(0);
            entity.setToolCallsError(0);
            entity.setSkillCallsCount(0);
            entity.setSkillCallsSuccess(0);
            entity.setSkillCallsError(0);
            entity.setTotalDurationMs(0);
            entity.setToolDurationMs(0);
            entity.setModelDurationMs(0);
            entity.setChainSummary("");
            entity.setLogFilePath("");
            entity.setQualityStatus(0);
            // 标记系统轮次：使用AssembledTurn中的isSystemTurn字段
            entity.setSystemTurn(turn.isSystemTurn() ? 1 : 0);
            // 调试日志
            if (turn.isSystemTurn()) {
                log.debug("System turn detected: sessionId={}, userInput={}", sessionId, turn.userInput());
            }
            entity.setCreatedAt(now);

            result.add(entity);
        }

        return result;
    }

    /**
     * Convert SkillInvocation list to DashboardSkillInvocation entities
     */
    private List<DashboardSkillInvocation> convertToSkillInvocations(
            Long scanId,
            List<TranscriptParser.SkillInvocation> skills,
            String sessionId,
            String employeeId,
            LocalDateTime now) {
        List<DashboardSkillInvocation> result = new ArrayList<>();

        for (TranscriptParser.SkillInvocation skill : skills) {
            DashboardSkillInvocation entity = new DashboardSkillInvocation();
            entity.setScanId(scanId);
            entity.setSessionId(sessionId);
            entity.setEmployeeId(employeeId);
            entity.setSkillName(skill.skillName());
            entity.setInvokedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(skill.invokedAt()), BEIJING_ZONE));
            entity.setReadMessageId(null); // TODO: Extract from context if needed
            entity.setResultMessageId(null); // TODO: Extract from context if needed
            entity.setIsError(0); // Default to no error
            entity.setTriggerType("model_read"); // Default trigger type
            entity.setCreatedAt(now);

            result.add(entity);
        }

        return result;
    }

    private List<DashboardTranscriptIssue> convertToIssues(
            Long scanId,
            List<IssueDetector.DetectedIssue> issues,
            String sessionId,
            String employeeId,
            LocalDateTime now) {
        List<DashboardTranscriptIssue> result = new ArrayList<>();

        for (IssueDetector.DetectedIssue issue : issues) {
            DashboardTranscriptIssue entity = new DashboardTranscriptIssue();
            entity.setScanId(scanId);
            entity.setSessionId(sessionId);
            entity.setMessageId(issue.messageId() != null ? issue.messageId() : "");
            entity.setEmployeeId(employeeId);
            entity.setErrorType(issue.errorType());
            entity.setSeverity(issue.severity());
            entity.setDescription(issue.description());
            entity.setErrorMessage(issue.errorMessage());
            entity.setUserInput(issue.userInput());
            entity.setCauseAnalysis(issue.causeAnalysis());
            entity.setFilePath(issue.filePath());
            entity.setErrorLineContent(issue.errorLineContent());
            entity.setNextLineContent(issue.nextLineContent());
            entity.setLineNumber(issue.lineNumber());
            entity.setEventType(issue.eventType() != null ? issue.eventType() : "message");
            entity.setRunId(issue.runId());
            entity.setProvider(issue.provider());
            entity.setModel(issue.model());
            entity.setOccurredAt(now);
            entity.setCreatedAt(now);

            result.add(entity);
        }

        return result;
    }

    /**
     * Extract user input from turn messages
     */
    private String extractUserInput(List<MessageRecord> messages) {
        for (MessageRecord msg : messages) {
            if ("user".equals(msg.role()) && msg.textContent() != null) {
                // Truncate to max length
                String content = msg.textContent();
                return content.length() > 200 ? content.substring(0, 200) : content;
            }
        }
        return null;
    }

    /**
     * Calculate aggregated metrics for a turn
     * Note: This is a simplified version since AssembledTurn doesn't expose messages
     */
    private void calculateTurnMetrics(DashboardConversationTurn entity, List<MessageRecord> messages) {
        // Simplified - just set defaults
        // TODO: Implement proper metric calculation when AssembledTurn exposes messages
        entity.setTotalInputTokens(0);
        entity.setTotalOutputTokens(0);
        entity.setTotalTokens(0);
        entity.setTotalCost(java.math.BigDecimal.ZERO);
        entity.setToolCallsCount(0);
        entity.setToolCallsSuccess(0);
        entity.setToolCallsError(0);
        entity.setSkillCallsCount(0);
        entity.setSkillCallsSuccess(0);
        entity.setSkillCallsError(0);
    }

    /**
     * Update session summary with incremental data
     * Uses upsert to handle both new and existing sessions
     */
    private void updateSessionSummary(Long scanId, String sessionId, String employeeId,
                                      int messageCount, int turnCount, 
                                      int skillCount, int issueCount,
                                      LocalDateTime now) {
        try {
            // Find first and last message timestamps from the messages list
            // For now, use current time as approximation
            // TODO: Extract actual timestamps from parsed messages
            
            DashboardSessionSummary summary = new DashboardSessionSummary();
            summary.setSessionId(sessionId);
            summary.setEmployeeId(employeeId);
            summary.setAgentName("main"); // Default agent name, can be extracted from path
            summary.setTotalMessages(messageCount);
            summary.setTotalTurns(turnCount);
            summary.setTotalIssues(issueCount);
            summary.setTotalSkills(skillCount);
            summary.setFirstMessageAt(now);
            summary.setLastMessageAt(now);
            summary.setLastScanId(scanId);
            summary.setLastUpdatedAt(now);
            
            // Upsert will incrementally update counts
            sessionSummaryMapper.upsert(summary);
            log.debug("Updated session summary for {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to update session summary for {}", sessionId, e);
            // Don't fail the ingestion if summary update fails
        }
    }

    /**
     * Determine if a message has any type of error by checking multiple indicators.
     *
     * @param msg The message record to check
     * @return true if any error indicator is present
     */
    private boolean hasMessageError(MessageRecord msg) {
        // 1. Direct error flag
        if (msg.isError()) {
            return true;
        }
        
        // 2. Error message content
        if (msg.errorMessage() != null && !msg.errorMessage().isEmpty()) {
            return true;
        }
        
        // 3. Stop reason indicates error
        if (msg.stopReason() != null) {
            String reason = msg.stopReason().toLowerCase();
            if (reason.contains("error") || 
                reason.contains("timeout") ||
                reason.contains("failure")) {
                return true;
            }
        }
        
        return false;
    }
}
