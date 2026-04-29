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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private final ExecutionTraceMapper executionTraceMapper;  // NEW
    private final TranscriptParser transcriptParser;  // NEW
    private final ScanProgressMapper scanProgressMapper;  // For mtime check
    private final ScanProgressService scanProgressService;  // For updating progress

    // ✅ 线程安全的小时收集器：employeeId -> Set<hour>
    private final ConcurrentHashMap<String, Set<LocalDateTime>> scannedHoursByEmployee = new ConcurrentHashMap<>();

    /**
     * Ingest parsed transcript data into database
     * @param scanId Current scan ID
     * @param employeeId Employee ID extracted from file path
     * @param parsed Parsed transcript data
     * @return IngestionResult with processing status and statistics
     */
    @Transactional
    public IngestionResult ingestParsedTranscript(Long scanId, String employeeId, TranscriptParser.ParsedTranscript parsed) {
        if (parsed == null || parsed.sessionId() == null) {
            log.warn("Cannot ingest null or invalid parsed transcript");
            return IngestionResult.skipped(IngestionStatus.SKIPPED_EMPTY, "Invalid parsed transcript");
        }

        String sessionId = parsed.sessionId();
        LocalDateTime now = LocalDateTime.now(BEIJING_ZONE);
        
        // ✅ 检查文件是否有变化（基于文件修改时间）- 作为兜底检查
        if (parsed.filePath() != null) {
            try {
                Path filePath = Path.of(parsed.filePath());
                if (Files.exists(filePath)) {
                    long fileMtime = Files.getLastModifiedTime(filePath).toMillis();
                    
                    // 查询上次扫描的文件修改时间
                    var progress = scanProgressMapper.selectByEmployeeAndFile(employeeId, parsed.filePath());
                    
                    if (progress != null && fileMtime <= progress.getFileMtime()) {
                        log.debug("File not modified (mtime: {}), skipping session {}", fileMtime, sessionId);
                        return IngestionResult.skipped(IngestionStatus.SKIPPED_MTIME, null);
                    }
                    
                    log.info("File modified (old mtime: {}, new mtime: {}), reprocessing session {}", 
                        progress != null ? progress.getFileMtime() : 0, fileMtime, sessionId);
                }
            } catch (Exception e) {
                log.warn("Failed to check file modification time for {}, proceeding with scan", parsed.filePath(), e);
            }
        }
        
        // Check if session already scanned (fallback check)
        int existingCount = messageMapper.countBySessionId(sessionId);
        if (existingCount > 0) {
            log.info("Session {} already scanned ({} messages), deleting old data and reprocessing", 
                sessionId, existingCount);
            // ✅ 删除旧数据，然后重新处理
            deleteExistingSessionData(sessionId);
        }

        // Phase 1: Insert conversation turns first
        List<DashboardConversationTurn> turns = convertToTurns(scanId, parsed.turns(), sessionId, employeeId, now, parsed.filePath());
        if (!turns.isEmpty()) {
            int inserted = turnMapper.batchInsertIgnore(turns);
            log.debug("Inserted {} conversation turns for session {}", inserted, sessionId);
            
            // After insertion, load the generated IDs by querying back
            loadTurnIds(sessionId, turns);
        }

        // Phase 1.5: 解析并插入执行链路（新增）
        for (int i = 0; i < turns.size(); i++) {
            DashboardConversationTurn turn = turns.get(i);
            
            // 获取该轮次对应的消息列表
            List<MessageRecord> turnMessages = getMessagesForTurn(parsed.messages(), parsed.messageIdToTurnIndex(), i);
            
            if (!turnMessages.isEmpty()) {
                List<DashboardExecutionTrace> traces = transcriptParser.parseExecutionTrace(
                    turnMessages, 
                    turn.getId(), 
                    scanId
                );
                
                if (!traces.isEmpty()) {
                    executionTraceMapper.batchInsertIgnore(traces);
                    log.debug("Inserted {} trace nodes for turn {}", traces.size(), turn.getId());
                }
            }
        }

        // Phase 2: Convert and insert messages with turn_id
        List<DashboardMessage> messages = new ArrayList<>();
        Map<String, Integer> messageIdToTurnIndex = parsed.messageIdToTurnIndex();
        for (MessageRecord msg : parsed.messages()) {
            DashboardMessage entity = convertSingleMessage(scanId, msg, sessionId, employeeId, now);
            
            // Set turn_id based on mapping
            Integer turnIndex = messageIdToTurnIndex.get(msg.id());
            if (turnIndex != null && turnIndex >= 0 && turnIndex < turns.size()) {
                DashboardConversationTurn turn = turns.get(turnIndex);
                entity.setTurnId(turn.getId());
            }
            
            messages.add(entity);
        }
        
        if (!messages.isEmpty()) {
            int inserted = messageMapper.batchInsertIgnore(messages);
            log.debug("Inserted {} messages for session {}", inserted, sessionId);
        }

        // Phase 3: Convert and insert skill invocations (turn_id not populated yet)
        List<DashboardSkillInvocation> skills = convertToSkillInvocations(
                scanId, parsed.skillInvocations(), sessionId, employeeId, now);
        if (!skills.isEmpty()) {
            int inserted = skillMapper.batchInsertIgnore(skills);
            log.debug("Inserted {} skill invocations for session {}", inserted, sessionId);
        }

        // Phase 4: Convert and insert issues with turn_id
        // Reuse the messageIdToTurnIndex mapping from Phase 2
        List<DashboardTranscriptIssue> issues = new ArrayList<>();
        for (IssueDetector.DetectedIssue issue : parsed.issues()) {
            DashboardTranscriptIssue entity = convertSingleIssue(scanId, issue, sessionId, employeeId, now);
            
            // Set turn_id based on message ID to turn index mapping
            if (issue.messageId() != null && !issue.messageId().isEmpty()) {
                Integer turnIndex = messageIdToTurnIndex.get(issue.messageId());
                if (turnIndex != null && turnIndex >= 0 && turnIndex < turns.size()) {
                    DashboardConversationTurn turn = turns.get(turnIndex);
                    entity.setTurnId(turn.getId());
                }
            }
            
            issues.add(entity);
        }
        
        if (!issues.isEmpty()) {
            int inserted = issueMapper.batchInsertIgnore(issues);
            log.debug("Inserted {} transcript issues for session {}", inserted, sessionId);
        }

        // Phase 5: Update session summary
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
            parsed.messages(), conversationTurns, skills.size(), issues.size(), now);

        // ✅ 更新扫描进度
        if (parsed.filePath() != null) {
            try {
                java.nio.file.Path filePath = java.nio.file.Path.of(parsed.filePath());
                if (java.nio.file.Files.exists(filePath)) {
                    long fileMtime = java.nio.file.Files.getLastModifiedTime(filePath).toMillis();
                    long fileSize = java.nio.file.Files.size(filePath);
                    
                    // 获取最后一条消息的 ID 和时间戳
                    String lastMessageId = null;
                    LocalDateTime lastMessageTs = null;
                    if (!parsed.messages().isEmpty()) {
                        MessageRecord lastMsg = parsed.messages().get(parsed.messages().size() - 1);
                        lastMessageId = lastMsg.id();
                        if (lastMsg.epochMs() > 0) {
                            lastMessageTs = LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(lastMsg.epochMs()), BEIJING_ZONE);
                        }
                    }
                    
                    DashboardScanProgress progress = new DashboardScanProgress();
                    progress.setEmployeeId(employeeId);
                    progress.setFilePath(parsed.filePath());
                    progress.setLastOffset(0L);  // 初始偏移量为 0
                    progress.setFileMtime(fileMtime);
                    progress.setFileSize(fileSize);
                    progress.setSessionId(sessionId);
                    progress.setLastMessageId(lastMessageId);
                    progress.setLastMessageTs(lastMessageTs);
                    progress.setUpdatedAt(now);
                    
                    scanProgressService.updateProgress(progress);
                    log.debug("Updated scan progress for session {}", sessionId);
                }
            } catch (Exception e) {
                log.warn("Failed to update scan progress for session {}", sessionId, e);
            }
        }

        log.info("Ingested session {}: {} messages, {} turns, {} skills, {} issues",
                sessionId, messages.size(), conversationTurns, skills.size(), issues.size());
        
        // ✅ 返回成功结果
        return IngestionResult.success(
            parsed.messages().size(),
            parsed.turns().size(),
            parsed.issues().size(),
            parsed.skillInvocations().size()
        );
    }

    /**
     * Convert a single MessageRecord to DashboardMessage entity
     */
    private DashboardMessage convertSingleMessage(Long scanId, MessageRecord msg, 
                                                   String sessionId, String employeeId,
                                                   LocalDateTime now) {
        DashboardMessage entity = new DashboardMessage();
        entity.setScanId(scanId);
        entity.setSessionId(sessionId);
        entity.setMessageId(msg.id());
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

        return entity;
    }

    /**
     * Convert AssembledTurn list to DashboardConversationTurn entities
     */
    private List<DashboardConversationTurn> convertToTurns(Long scanId,
                                                            List<TurnAssembler.AssembledTurn> turns,
                                                            String sessionId,
                                                            String employeeId,
                                                            LocalDateTime now,
                                                            String filePath) {
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
            // 从 AssembledTurn 获取统计值
            entity.setTotalInputTokens((int) turn.totalInputTokens());
            entity.setTotalOutputTokens((int) turn.totalOutputTokens());
            entity.setTotalTokens((int) turn.totalTokens());
            entity.setTotalCost(BigDecimal.ZERO);  // TODO: 后续从 usage.costTotal 计算
            entity.setToolCallsCount(turn.toolCallsCount());
            entity.setToolCallsSuccess(turn.toolCallsSuccess());
            entity.setToolCallsError(turn.toolCallsError());
            entity.setSkillCallsCount(0);
            entity.setSkillCallsSuccess(0);
            entity.setSkillCallsError(0);
            entity.setTotalDurationMs((int) turn.totalDurationMs());  // 轮次总耗时
            entity.setToolDurationMs(0);
            entity.setModelDurationMs(0);
            entity.setChainSummary("");
            // Set log file path from parameter
            entity.setLogFilePath(filePath != null ? filePath : "");
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

    /**
     * Convert a single DetectedIssue to DashboardTranscriptIssue entity
     */
    private DashboardTranscriptIssue convertSingleIssue(Long scanId, IssueDetector.DetectedIssue issue,
                                                         String sessionId, String employeeId,
                                                         LocalDateTime now) {
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
        // Set occurred_at from message timestamp, fallback to current time if not available
        if (issue.timestamp() != null && issue.timestamp() > 0) {
            entity.setOccurredAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(issue.timestamp()), BEIJING_ZONE));
        } else {
            entity.setOccurredAt(now);
        }
        entity.setCreatedAt(now);

        return entity;
    }

    /**
     * Load generated turn IDs after batch insert by querying back
     */
    private void loadTurnIds(String sessionId, List<DashboardConversationTurn> turns) {
        // Query all turns for this session (ordered by turn_index)
        List<DashboardConversationTurn> existingTurns = turnMapper.selectBySessionId(sessionId);
        
        // Create a map from turn_index to turn object
        Map<Integer, DashboardConversationTurn> turnByIndex = new HashMap<>();
        for (DashboardConversationTurn turn : existingTurns) {
            turnByIndex.put(turn.getTurnIndex(), turn);
        }
        
        // Set the IDs on our turn objects
        for (DashboardConversationTurn turn : turns) {
            DashboardConversationTurn existing = turnByIndex.get(turn.getTurnIndex());
            if (existing != null) {
                turn.setId(existing.getId());
            }
        }
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
     * 删除指定 session 的所有相关详细数据
     * 按照依赖关系逆序删除，避免外键约束问题
     * 
     * @param sessionId Session ID
     */
    private void deleteExistingSessionData(String sessionId) {
        log.info("Deleting existing detail data for session: {}", sessionId);
        
        // 1. 删除执行追踪（依赖 turn_id）
        int traceCount = executionTraceMapper.deleteBySessionId(sessionId);
        log.debug("Deleted {} execution traces", traceCount);
        
        // 2. 删除问题记录（依赖 turn_id）
        int issueCount = issueMapper.deleteBySessionId(sessionId);
        log.debug("Deleted {} transcript issues", issueCount);
        
        // 3. 删除技能调用（依赖 session_id）
        int skillCount = skillMapper.deleteBySessionId(sessionId);
        log.debug("Deleted {} skill invocations", skillCount);
        
        // 4. 删除消息（依赖 turn_id, session_id）
        int messageCount = messageMapper.deleteBySessionId(sessionId);
        log.debug("Deleted {} messages", messageCount);
        
        // 5. 删除对话轮次（核心表，被其他表依赖）
        int turnCount = turnMapper.deleteBySessionId(sessionId);
        log.debug("Deleted {} conversation turns", turnCount);
        
        // ❌ 不删除 session_summary，让 upsert 自动更新
        
        log.info("Successfully deleted detail data for session {}: " +
                 "traces={}, issues={}, skills={}, messages={}, turns={}",
                 sessionId, traceCount, issueCount, skillCount, 
                 messageCount, turnCount);
    }

    /**
     * Update session summary with incremental data
     * Uses upsert to handle both new and existing sessions
     */
    private void updateSessionSummary(Long scanId, String sessionId, String employeeId,
                                      List<MessageRecord> messages, int turnCount, 
                                      int skillCount, int issueCount,
                                      LocalDateTime now) {
        try {
            // ✅ 从解析的消息中提取真实的时间戳范围
            LocalDateTime firstMessageAt = null;
            LocalDateTime lastMessageAt = null;
            
            if (messages != null && !messages.isEmpty()) {
                long minEpochMs = Long.MAX_VALUE;
                long maxEpochMs = Long.MIN_VALUE;
                
                for (MessageRecord msg : messages) {
                    if (msg.epochMs() > 0) {
                        minEpochMs = Math.min(minEpochMs, msg.epochMs());
                        maxEpochMs = Math.max(maxEpochMs, msg.epochMs());
                    }
                }
                
                if (minEpochMs != Long.MAX_VALUE) {
                    firstMessageAt = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(minEpochMs), BEIJING_ZONE);
                }
                if (maxEpochMs != Long.MIN_VALUE) {
                    lastMessageAt = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(maxEpochMs), BEIJING_ZONE);
                }
            }
            
            // 如果没有有效的时间戳，使用当前时间作为后备
            if (firstMessageAt == null) firstMessageAt = now;
            if (lastMessageAt == null) lastMessageAt = now;
            
            DashboardSessionSummary summary = new DashboardSessionSummary();
            summary.setSessionId(sessionId);
            summary.setEmployeeId(employeeId);
            summary.setAgentName("main");
            summary.setTotalMessages(messages != null ? messages.size() : 0);
            summary.setTotalTurns(turnCount);
            summary.setTotalIssues(issueCount);
            summary.setTotalSkills(skillCount);
            summary.setFirstMessageAt(firstMessageAt);   // ✅ 使用真实时间戳
            summary.setLastMessageAt(lastMessageAt);     // ✅ 使用真实时间戳
            summary.setLastScanId(scanId);
            summary.setLastUpdatedAt(now);
            
            sessionSummaryMapper.upsert(summary);
            log.debug("Updated session summary for {} (first: {}, last: {})", 
                sessionId, firstMessageAt, lastMessageAt);
        } catch (Exception e) {
            log.error("Failed to update session summary for {}", sessionId, e);
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

    /**
     * 获取指定轮次的消息列表
     */
    private List<MessageRecord> getMessagesForTurn(
        List<MessageRecord> allMessages,
        Map<String, Integer> messageIdToTurnIndex,
        int turnIndex
    ) {
        List<MessageRecord> turnMessages = new ArrayList<>();
        for (MessageRecord msg : allMessages) {
            Integer index = messageIdToTurnIndex.get(msg.id());
            if (index != null && index == turnIndex) {
                turnMessages.add(msg);
            }
        }
        return turnMessages;
    }

    /**
     * 从消息中提取小时信息并收集到 scannedHoursByEmployee
     * @param employeeId 员工ID
     * @param messages 消息列表
     */
    private void collectHoursFromMessages(String employeeId, List<DashboardMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        
        // 获取或创建该员工的小时集合
        Set<LocalDateTime> hours = scannedHoursByEmployee.computeIfAbsent(
            employeeId, k -> Collections.synchronizedSet(new HashSet<>()));
        
        // 提取每条消息的小时（截断到整点）
        for (DashboardMessage msg : messages) {
            if (msg.getMessageTimestamp() != null) {
                LocalDateTime hour = msg.getMessageTimestamp()
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
                hours.add(hour);
            }
        }
    }
    
    /**
     * 获取并清除收集的小时信息
     * @return Map<employeeId, Set<hour>>
     */
    public Map<String, Set<LocalDateTime>> getAndClearScannedHours() {
        Map<String, Set<LocalDateTime>> result = new HashMap<>(scannedHoursByEmployee);
        scannedHoursByEmployee.clear();
        return result;
    }
}
