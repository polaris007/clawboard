# Fix Turn ID and Error Tracking Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 ClawBoard 系统中三个数据质量问题：为 `dashboard_message` 添加 `turn_id` 字段并正确填充，修复 `is_error` 字段的判断逻辑，以及填充 `dashboard_transcript_issue.turn_id` 字段。

**Architecture:** 采用分阶段入库策略：先插入对话轮次获取 ID，构建消息到轮次的映射关系，然后在插入消息和问题时使用正确的 `turn_id`。通过增强错误检测逻辑（检查多个错误指标）确保 `is_error` 准确性。使用 `INSERT IGNORE` + Session 存在性检查避免重复扫描。

**Tech Stack:** Java 17, Spring Boot 3.2.5, MyBatis 3.0.3, MySQL 5.7+, JUnit 5, Mockito

---

## File Structure Overview

### Files to Create
- `src/main/resources/db/migration/V3__add_turn_id_to_message.sql` - 数据库迁移脚本
- `src/test/java/com/company/clawboard/service/DataIngestionServiceTest.java` - 单元测试（新增测试方法）

### Files to Modify
- `src/main/java/com/company/clawboard/entity/DashboardMessage.java` - 添加 turnId 字段
- `src/main/java/com/company/clawboard/mapper/MessageMapper.java` - 添加新方法
- `src/main/resources/mapper/MessageMapper.xml` - 更新 SQL 语句
- `src/main/java/com/company/clawboard/parser/TranscriptParser.java` - 增强 ParsedTranscript
- `src/main/java/com/company/clawboard/service/DataIngestionService.java` - 核心重构

---

## Task 1: Database Migration - Add turn_id Column

**Files:**
- Create: `src/main/resources/db/migration/V3__add_turn_id_to_message.sql`

- [ ] **Step 1: Create migration script**

```sql
-- V3__add_turn_id_to_message.sql
-- Add turn_id column to dashboard_message table for turn association

ALTER TABLE dashboard_message 
ADD COLUMN turn_id BIGINT COMMENT '关联轮次ID' AFTER session_id;

-- Add index for query performance
ALTER TABLE dashboard_message 
ADD INDEX idx_turn_id (turn_id);
```

- [ ] **Step 2: Verify migration file location**

Check that the file is in the correct Flyway/Liquibase migration directory:
```bash
ls src/main/resources/db/migration/
```
Expected output should show V1, V2, and now V3 migration files.

- [ ] **Step 3: Test migration on clean database**

Run database reset and verify schema:
```bash
mysql -u clawboard -p'Clqc@1234' -h 127.0.0.1 clawboard < scripts/reset-database.sql
mysql -u clawboard -p'Clqc@1234' -h 127.0.0.1 clawboard -e "DESCRIBE dashboard_message;"
```
Expected: Output shows `turn_id` column as BIGINT, nullable, with index.

- [ ] **Step 4: Commit migration script**

```bash
git add src/main/resources/db/migration/V3__add_turn_id_to_message.sql
git commit -m "db: add turn_id column to dashboard_message table"
```

---

## Task 2: Update DashboardMessage Entity

**Files:**
- Modify: `src/main/java/com/company/clawboard/entity/DashboardMessage.java`

- [ ] **Step 1: Read current entity structure**

```bash
cat src/main/java/com/company/clawboard/entity/DashboardMessage.java
```
Note the existing fields and Lombok annotations.

- [ ] **Step 2: Add turnId field**

Add the following field after `sessionId` field (around line 20-25):

```java
/**
 * 关联轮次ID
 */
private Long turnId;
```

The complete field section should look like:
```java
@Data
public class DashboardMessage {
    private Long id;
    private Long scanId;
    private String sessionId;
    private Long turnId;  // NEW FIELD
    private String messageId;
    // ... rest of fields
}
```

- [ ] **Step 3: Verify Lombok generates getter/setter**

Since the class uses `@Data`, Lombok will automatically generate:
- `getTurnId()` returning `Long`
- `setTurnId(Long turnId)`

No manual getter/setter needed.

- [ ] **Step 4: Compile to check for errors**

```bash
mvn compile -DskipTests
```
Expected: BUILD SUCCESS with no compilation errors.

- [ ] **Step 5: Commit entity change**

```bash
git add src/main/java/com/company/clawboard/entity/DashboardMessage.java
git commit -m "feat: add turnId field to DashboardMessage entity"
```

---

## Task 3: Update MessageMapper Interface and XML

**Files:**
- Modify: `src/main/java/com/company/clawboard/mapper/MessageMapper.java`
- Modify: `src/main/resources/mapper/MessageMapper.xml`

- [ ] **Step 1: Add new methods to MessageMapper interface**

Read current interface:
```bash
cat src/main/java/com/company/clawboard/mapper/MessageMapper.java
```

Add these two methods before the closing brace:

```java
/**
 * 根据 Session ID 统计消息数量（用于检查是否已扫描）
 */
int countBySessionId(@Param("sessionId") String sessionId);

/**
 * 根据轮次 ID 查询消息列表
 */
List<DashboardMessage> selectByTurnId(@Param("turnId") Long turnId);
```

- [ ] **Step 2: Update INSERT statement in MessageMapper.xml**

Read current XML:
```bash
cat src/main/resources/mapper/MessageMapper.xml
```

Find the `<insert id="batchInsertIgnore">` section and update it to include `turn_id`:

```xml
<insert id="batchInsertIgnore">
    INSERT IGNORE INTO dashboard_message (
        scan_id, session_id, turn_id, message_id, employee_id, role,
        message_timestamp, input_tokens, output_tokens, cache_read_tokens,
        cache_write_tokens, total_tokens, cost_total, provider, model,
        stop_reason, duration_ms, is_error, error_message, tool_name,
        tool_call_id, is_system, parent_id, created_at
    ) VALUES
    <foreach collection="list" item="msg" separator=",">
        (
            #{msg.scanId}, #{msg.sessionId}, #{msg.turnId}, #{msg.messageId},
            #{msg.employeeId}, #{msg.role}, #{msg.messageTimestamp},
            #{msg.inputTokens}, #{msg.outputTokens}, #{msg.cacheReadTokens},
            #{msg.cacheWriteTokens}, #{msg.totalTokens}, #{msg.costTotal},
            #{msg.provider}, #{msg.model}, #{msg.stopReason}, #{msg.durationMs},
            #{msg.isError}, #{msg.errorMessage}, #{msg.toolName},
            #{msg.toolCallId}, #{msg.isSystem}, #{msg.parentId}, #{msg.createdAt}
        )
    </foreach>
</insert>
```

- [ ] **Step 3: Add new SELECT queries**

Add these two queries at the end of the XML file (before `</mapper>`):

```xml
<!-- Count messages by session ID -->
<select id="countBySessionId" resultType="int">
    SELECT COUNT(*) FROM dashboard_message
    WHERE session_id = #{sessionId}
</select>

<!-- Select messages by turn ID -->
<select id="selectByTurnId" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM dashboard_message
    WHERE turn_id = #{turnId}
    ORDER BY message_timestamp ASC
</select>
```

Also ensure `Base_Column_List` includes `turn_id`:
```xml
<sql id="Base_Column_List">
    id, scan_id, session_id, turn_id, message_id, employee_id, role,
    message_timestamp, input_tokens, output_tokens, cache_read_tokens,
    cache_write_tokens, total_tokens, cost_total, provider, model,
    stop_reason, duration_ms, is_error, error_message, tool_name,
    tool_call_id, is_system, parent_id, created_at
</sql>
```

And `BaseResultMap` includes turnId mapping:
```xml
<resultMap id="BaseResultMap" type="com.company.clawboard.entity.DashboardMessage">
    <id column="id" property="id"/>
    <result column="scan_id" property="scanId"/>
    <result column="session_id" property="sessionId"/>
    <result column="turn_id" property="turnId"/>
    <!-- ... rest of mappings -->
</resultMap>
```

- [ ] **Step 4: Compile and verify**

```bash
mvn compile -DskipTests
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit mapper changes**

```bash
git add src/main/java/com/company/clawboard/mapper/MessageMapper.java
git add src/main/resources/mapper/MessageMapper.xml
git commit -m "feat: add turn_id support to MessageMapper"
```

---

## Task 4: Enhance TranscriptParser with Message-to-Turn Mapping

**Files:**
- Modify: `src/main/java/com/company/clawboard/parser/TranscriptParser.java`

- [ ] **Step 1: Locate ParsedTranscript record definition**

Search for the record definition:
```bash
grep -n "record ParsedTranscript" src/main/java/com/company/clawboard/parser/TranscriptParser.java
```

- [ ] **Step 2: Add messageIdToTurnIndex field to ParsedTranscript**

Modify the record to include the new field:

```java
public record ParsedTranscript(
    String sessionId,
    List<MessageRecord> messages,
    List<TurnAssembler.AssembledTurn> turns,
    List<IssueDetector.DetectedIssue> issues,
    List<SkillInvocation> skillInvocations,
    Map<String, Integer> messageIdToTurnIndex  // NEW: maps message ID to turn index (0-based)
) {}
```

- [ ] **Step 3: Build mapping during parseFile()**

Find the `parseFile()` method and locate where it returns `ParsedTranscript`. Before the return statement, add:

```java
// Build message-to-turn mapping
Map<String, Integer> messageIdToTurnIndex = buildMessageToTurnMapping(messages, turns);

return new ParsedTranscript(
    sessionId,
    messages,
    turns,
    issues,
    skillInvocations,
    messageIdToTurnIndex  // NEW
);
```

- [ ] **Step 4: Implement buildMessageToTurnMapping helper method**

Add this private method to the `TranscriptParser` class:

```java
/**
 * Build a mapping from message IDs to their turn indices.
 * Each turn contains a range of messages, and we map each message ID to its turn index.
 *
 * @param messages All messages in the session
 * @param turns Assembled conversation turns
 * @return Map from message ID to turn index (0-based)
 */
private Map<String, Integer> buildMessageToTurnMapping(
        List<MessageRecord> messages,
        List<TurnAssembler.AssembledTurn> turns) {
    
    Map<String, Integer> mapping = new HashMap<>();
    
    // Create a map from message ID to its index in the messages list
    Map<String, Integer> messageIdToIndex = new HashMap<>();
    for (int i = 0; i < messages.size(); i++) {
        messageIdToIndex.put(messages.get(i).id(), i);
    }
    
    // For each turn, map all its messages to the turn index
    for (int turnIndex = 0; turnIndex < turns.size(); turnIndex++) {
        TurnAssembler.AssembledTurn turn = turns.get(turnIndex);
        
        // Find start and end message indices
        Integer startIndex = messageIdToIndex.get(turn.startMessageId());
        Integer endIndex = turn.endMessageId() != null ? 
            messageIdToIndex.get(turn.endMessageId()) : startIndex;
        
        if (startIndex != null && endIndex != null) {
            // Map all messages in this turn's range
            for (int i = startIndex; i <= endIndex && i < messages.size(); i++) {
                mapping.put(messages.get(i).id(), turnIndex);
            }
        }
    }
    
    return mapping;
}
```

Add necessary import at the top:
```java
import java.util.HashMap;
import java.util.Map;
```

- [ ] **Step 5: Compile and test basic parsing**

```bash
mvn compile -DskipTests
```
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit parser enhancement**

```bash
git add src/main/java/com/company/clawboard/parser/TranscriptParser.java
git commit -m "feat: add message-to-turn mapping in TranscriptParser"
```

---

## Task 5: Implement Comprehensive Error Detection in DataIngestionService

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: Read current convertToMessages method**

```bash
grep -A 50 "private List<DashboardMessage> convertToMessages" src/main/java/com/company/clawboard/service/DataIngestionService.java
```

Note the current `is_error` logic at line ~150:
```java
entity.setIsError(msg.isError() ? 1 : 0);
```

- [ ] **Step 2: Implement hasMessageError helper method**

Add this private method to `DataIngestionService`:

```java
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
    
    // 3. Tool call errors
    if (msg.toolCalls() != null) {
        for (var toolCall : msg.toolCalls()) {
            if (toolCall.isError() || 
                (toolCall.error() != null && !toolCall.error().isEmpty())) {
                return true;
            }
        }
    }
    
    // 4. Stop reason indicates error
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
```

- [ ] **Step 3: Update convertToMessages to use new error detection**

Find the line:
```java
entity.setIsError(msg.isError() ? 1 : 0);
```

Replace with:
```java
entity.setIsError(hasMessageError(msg) ? 1 : 0);
```

- [ ] **Step 4: Write unit test for hasMessageError**

Create test file if it doesn't exist:
```bash
mkdir -p src/test/java/com/company/clawboard/service
```

Add these test methods to `DataIngestionServiceTest`:

```java
@Test
void should_detect_direct_error_flag() {
    MessageRecord msg = mock(MessageRecord.class);
    when(msg.isError()).thenReturn(true);
    
    boolean result = service.hasMessageError(msg);  // May need to make method package-private for testing
    
    assertTrue(result);
}

@Test
void should_detect_error_message_content() {
    MessageRecord msg = mock(MessageRecord.class);
    when(msg.isError()).thenReturn(false);
    when(msg.errorMessage()).thenReturn("Connection timeout");
    
    boolean result = service.hasMessageError(msg);
    
    assertTrue(result);
}

@Test
void should_detect_tool_call_errors() {
    MessageRecord msg = mock(MessageRecord.class);
    when(msg.isError()).thenReturn(false);
    when(msg.errorMessage()).thenReturn(null);
    
    var toolCall = mock(ToolCall.class);
    when(toolCall.isError()).thenReturn(true);
    when(msg.toolCalls()).thenReturn(List.of(toolCall));
    
    boolean result = service.hasMessageError(msg);
    
    assertTrue(result);
}

@Test
void should_detect_error_stop_reason() {
    MessageRecord msg = mock(MessageRecord.class);
    when(msg.isError()).thenReturn(false);
    when(msg.errorMessage()).thenReturn(null);
    when(msg.toolCalls()).thenReturn(null);
    when(msg.stopReason()).thenReturn("model_timeout");
    
    boolean result = service.hasMessageError(msg);
    
    assertTrue(result);
}

@Test
void should_return_false_for_valid_message() {
    MessageRecord msg = mock(MessageRecord.class);
    when(msg.isError()).thenReturn(false);
    when(msg.errorMessage()).thenReturn(null);
    when(msg.toolCalls()).thenReturn(null);
    when(msg.stopReason()).thenReturn("stop");
    
    boolean result = service.hasMessageError(msg);
    
    assertFalse(result);
}
```

- [ ] **Step 5: Run unit tests**

```bash
mvn test -Dtest=DataIngestionServiceTest#should_detect_*
```
Expected: All 5 tests PASS

- [ ] **Step 6: Commit error detection enhancement**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git add src/test/java/com/company/clawboard/service/DataIngestionServiceTest.java
git commit -m "feat: enhance error detection with multiple indicators"
```

---

## Task 6: Refactor DataIngestionService Ingestion Flow

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: Read current ingestParsedTranscript method**

```bash
grep -A 80 "public void ingestParsedTranscript" src/main/java/com/company/clawboard/service/DataIngestionService.java
```

Note the current order: Messages → Turns → Skills → Issues

- [ ] **Step 2: Add session existence check at the beginning**

At the very start of `ingestParsedTranscript()`, add:

```java
@Transactional
public void ingestParsedTranscript(Long scanId, String employeeId, TranscriptParser.ParsedTranscript parsed) {
    String sessionId = parsed.sessionId();
    LocalDateTime now = LocalDateTime.now(BEIJING_ZONE);
    
    // Check if session already scanned
    int existingCount = messageMapper.countBySessionId(sessionId);
    if (existingCount > 0) {
        log.info("Session {} already scanned ({} messages), skipping", sessionId, existingCount);
        return;
    }
    
    // ... rest of method
}
```

- [ ] **Step 3: Implement buildTurnMapping helper method**

Add this private method:

```java
/**
 * Build a mapping from turn startMessageId to database turn ID.
 * This is used to look up turn IDs when inserting messages.
 *
 * @param turns List of inserted conversation turns
 * @return Map from startMessageId to turn database ID
 */
private Map<String, Long> buildTurnMapping(List<DashboardConversationTurn> turns) {
    Map<String, Long> mapping = new HashMap<>();
    for (DashboardConversationTurn turn : turns) {
        mapping.put(turn.getStartMessageId(), turn.getId());
    }
    return mapping;
}
```

- [ ] **Step 4: Implement buildTurnLineNumberMapping helper method**

Add this private method:

```java
/**
 * Build a mapping from JSONL line numbers to turn IDs.
 * Used for associating issues with their corresponding turns.
 *
 * @param messages All messages in the session
 * @param turns Assembled conversation turns
 * @return Map from line number to turn ID
 */
private Map<Integer, Long> buildTurnLineNumberMapping(
        List<MessageRecord> messages,
        List<DashboardConversationTurn> turns) {
    
    Map<Integer, Long> mapping = new HashMap<>();
    
    // Create message ID to line number mapping
    Map<String, Integer> messageIdToLine = new HashMap<>();
    for (int i = 0; i < messages.size(); i++) {
        messageIdToLine.put(messages.get(i).id(), i + 1); // Line numbers are 1-based
    }
    
    // For each turn, map all line numbers in its range to the turn ID
    for (DashboardConversationTurn turn : turns) {
        Integer startLine = messageIdToLine.get(turn.getStartMessageId());
        Integer endLine = turn.getEndMessageId() != null ? 
            messageIdToLine.get(turn.getEndMessageId()) : startLine;
        
        if (startLine != null && endLine != null) {
            for (int line = startLine; line <= endLine; line++) {
                mapping.put(line, turn.getId());
            }
        }
    }
    
    return mapping;
}
```

- [ ] **Step 5: Implement convertToMessagesWithTurnId method**

Replace the existing `convertToMessages` calls with a new method. Add:

```java
/**
 * Convert messages to entities with turn_id and correct is_error.
 *
 * @param scanId Current scan ID
 * @param messages Parsed message records
 * @param sessionId Session identifier
 * @param employeeId Employee identifier
 * @param now Current timestamp
 * @param turnIdByStartMessageId Mapping from start message ID to turn ID
 * @param messageIdToTurnIndex Mapping from message ID to turn index
 * @return List of DashboardMessage entities with turn_id populated
 */
private List<DashboardMessage> convertToMessagesWithTurnId(
        Long scanId,
        List<MessageRecord> messages,
        String sessionId,
        String employeeId,
        LocalDateTime now,
        Map<String, Long> turnIdByStartMessageId,
        Map<String, Integer> messageIdToTurnIndex) {
    
    List<DashboardMessage> result = new ArrayList<>();
    
    for (MessageRecord msg : messages) {
        DashboardMessage entity = new DashboardMessage();
        entity.setScanId(scanId);
        entity.setSessionId(sessionId);
        entity.setMessageId(msg.id());
        entity.setEmployeeId(employeeId);
        entity.setRole(msg.role());
        
        // Set turn_id based on mapping
        Integer turnIndex = messageIdToTurnIndex.get(msg.id());
        if (turnIndex != null && turnIndex >= 0 && turnIndex < turns.size()) {
            // Find the turn by index and get its database ID
            // Note: This requires access to turns list, so we'll adjust the signature
            // For now, we'll set it in the calling method
        }
        
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
        entity.setErrorMessage(msg.errorMessage());
        
        // Extract tool information if present
        if (msg.toolCalls() != null && !msg.toolCalls().isEmpty()) {
            entity.setToolName(msg.toolCalls().get(0).name());
            entity.setToolCallId(msg.toolCalls().get(0).id());
        }
        
        // Mark system messages
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
```

Actually, let me simplify this. We'll set turn_id directly in the ingestion method. Let me revise the approach.

- [ ] **Step 6: Refactor ingestParsedTranscript with new flow**

Replace the entire method body with:

```java
@Transactional
public void ingestParsedTranscript(Long scanId, String employeeId, TranscriptParser.ParsedTranscript parsed) {
    String sessionId = parsed.sessionId();
    LocalDateTime now = LocalDateTime.now(BEIJING_ZONE);
    
    // Check if session already scanned
    int existingCount = messageMapper.countBySessionId(sessionId);
    if (existingCount > 0) {
        log.info("Session {} already scanned ({} messages), skipping", sessionId, existingCount);
        return;
    }
    
    // Phase 1: Insert conversation turns first
    List<DashboardConversationTurn> turns = convertToTurns(scanId, parsed.turns(), sessionId, employeeId, now);
    if (!turns.isEmpty()) {
        int inserted = turnMapper.batchInsertIgnore(turns);
        log.debug("Inserted {} conversation turns for session {}", inserted, sessionId);
    }
    
    // Phase 2: Build mappings
    Map<String, Long> turnIdByStartMessageId = buildTurnMapping(turns);
    Map<String, Integer> messageIdToTurnIndex = parsed.messageIdToTurnIndex();
    
    // Phase 3: Convert and insert messages with turn_id
    List<DashboardMessage> messages = new ArrayList<>();
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
    
    // Phase 4: Convert and insert skill invocations (turn_id not populated yet)
    List<DashboardSkillInvocation> skills = convertToSkillInvocations(scanId, parsed.skillInvocations(), sessionId, employeeId, now);
    if (!skills.isEmpty()) {
        int inserted = skillMapper.batchInsertIgnore(skills);
        log.debug("Inserted {} skill invocations for session {}", inserted, sessionId);
    }
    
    // Phase 5: Convert and insert issues with turn_id
    Map<Integer, Long> turnIdByLineNumber = buildTurnLineNumberMapping(parsed.messages(), turns);
    List<DashboardTranscriptIssue> issues = new ArrayList<>();
    for (IssueDetector.DetectedIssue issue : parsed.issues()) {
        DashboardTranscriptIssue entity = convertSingleIssue(scanId, issue, sessionId, employeeId, now);
        
        // Set turn_id based on line number mapping
        if (issue.lineNumber() != null) {
            Long turnId = turnIdByLineNumber.get(issue.lineNumber());
            if (turnId != null) {
                entity.setTurnId(turnId);
            }
        }
        
        issues.add(entity);
    }
    
    if (!issues.isEmpty()) {
        int inserted = issueMapper.batchInsertIgnore(issues);
        log.debug("Inserted {} transcript issues for session {}", inserted, sessionId);
    }
    
    // Phase 6: Update session summary
    updateSessionSummary(scanId, sessionId, employeeId, messages, turns, skills, issues, now);
}
```

- [ ] **Step 7: Add helper methods for single entity conversion**

Add these two private methods:

```java
/**
 * Convert a single MessageRecord to DashboardMessage entity.
 */
private DashboardMessage convertSingleMessage(
        Long scanId,
        MessageRecord msg,
        String sessionId,
        String employeeId,
        LocalDateTime now) {
    
    DashboardMessage entity = new DashboardMessage();
    entity.setScanId(scanId);
    entity.setSessionId(sessionId);
    entity.setMessageId(msg.id());
    entity.setEmployeeId(employeeId);
    entity.setRole(msg.role());
    
    if (msg.epochMs() > 0) {
        entity.setMessageTimestamp(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(msg.epochMs()), BEIJING_ZONE));
    }
    
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
    entity.setIsError(hasMessageError(msg) ? 1 : 0);
    entity.setErrorMessage(msg.errorMessage());
    
    if (msg.toolCalls() != null && !msg.toolCalls().isEmpty()) {
        entity.setToolName(msg.toolCalls().get(0).name());
        entity.setToolCallId(msg.toolCalls().get(0).id());
    }
    
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
 * Convert a single DetectedIssue to DashboardTranscriptIssue entity.
 */
private DashboardTranscriptIssue convertSingleIssue(
        Long scanId,
        IssueDetector.DetectedIssue issue,
        String sessionId,
        String employeeId,
        LocalDateTime now) {
    
    DashboardTranscriptIssue entity = new DashboardTranscriptIssue();
    entity.setScanId(scanId);
    entity.setSessionId(sessionId);
    entity.setMessageId(issue.messageId());
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
    entity.setEventType(issue.eventType());
    entity.setRunId(issue.runId());
    entity.setProvider(issue.provider());
    entity.setModel(issue.model());
    entity.setLineNumber(issue.lineNumber());
    entity.setOccurredAt(issue.occurredAt());
    // turn_id will be set separately
    entity.setCreatedAt(now);
    
    return entity;
}
```

- [ ] **Step 8: Remove old convertToMessages and convertToIssues methods**

Since we're now using `convertSingleMessage` and `convertSingleIssue`, you can either:
- Keep the old methods for backward compatibility (if used elsewhere)
- Or delete them if they're only used internally

Check usage:
```bash
grep -r "convertToMessages\|convertToIssues" src/main/java --include="*.java"
```

If only used in `ingestParsedTranscript`, safe to remove.

- [ ] **Step 9: Compile and fix any errors**

```bash
mvn compile -DskipTests
```
Fix any compilation errors (likely missing imports or method signatures).

- [ ] **Step 10: Commit refactored ingestion service**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "refactor: restructure ingestion flow with turn_id support"
```

---

## Task 7: Integration Testing and Verification

**Files:**
- No new files, just run tests and verification queries

- [ ] **Step 1: Run full test suite**

```bash
mvn test
```
Expected: All tests PASS (including the new error detection tests)

- [ ] **Step 2: Reset database and perform test scan**

```bash
# Reset database
mysql -u clawboard -p'Clqc@1234' -h 127.0.0.1 clawboard < scripts/reset-database.sql

# Start application in dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

In another terminal, trigger a scan (adjust endpoint as needed):
```bash
curl -X POST http://localhost:8080/api/v1/scans/trigger `
  -H "Content-Type: application/json" `
  -UseBasicParsing
```

- [ ] **Step 3: Verify turn_id is populated in messages**

After scan completes, run SQL query:
```sql
SELECT 
    m.id,
    m.message_id,
    m.turn_id,
    t.turn_index,
    m.is_error,
    m.role
FROM dashboard_message m
LEFT JOIN dashboard_conversation_turn t ON m.turn_id = t.id
WHERE m.session_id = '<your_test_session_id>'
LIMIT 10;
```
Expected: All messages have non-null `turn_id` values matching their turns.

- [ ] **Step 4: Verify is_error accuracy**

Run diagnostic query:
```sql
SELECT 
    m.message_id,
    m.is_error AS message_is_error,
    CASE WHEN i.id IS NOT NULL THEN 1 ELSE 0 END AS has_issue
FROM dashboard_message m
LEFT JOIN dashboard_transcript_issue i ON m.message_id = i.message_id
WHERE m.session_id = '<your_test_session_id>'
ORDER BY m.message_timestamp
LIMIT 20;
```
Expected: When `has_issue = 1`, `message_is_error` should also be 1 (no mismatches).

- [ ] **Step 5: Verify issue turn_id population**

```sql
SELECT 
    i.id,
    i.message_id,
    i.turn_id,
    i.error_type,
    t.turn_index
FROM dashboard_transcript_issue i
LEFT JOIN dashboard_conversation_turn t ON i.turn_id = t.id
WHERE i.session_id = '<your_test_session_id>'
LIMIT 10;
```
Expected: All issues have non-null `turn_id` values.

- [ ] **Step 6: Test duplicate scan skipping**

Trigger the same scan again:
```bash
curl -X POST http://localhost:8080/api/v1/scans/trigger `
  -H "Content-Type: application/json" `
  -UseBasicParsing
```

Check logs for:
```
Session <session_id> already scanned (N messages), skipping
```

Verify no duplicate records:
```sql
SELECT session_id, COUNT(*) as msg_count
FROM dashboard_message
GROUP BY session_id
HAVING COUNT(*) > 50;  -- Adjust threshold based on your data
```
Expected: No sessions with unexpectedly high message counts.

- [ ] **Step 7: Document verification results**

Create a verification report in your notes:
```markdown
## Verification Results - 2026-04-23

- ✅ turn_id populated in messages: X/Y messages have turn_id
- ✅ is_error accuracy: Z mismatches found (should be 0)
- ✅ issue turn_id populated: A/B issues have turn_id
- ✅ Duplicate scan skipping: Working correctly
- ✅ All unit tests passing
```

- [ ] **Step 8: Final commit**

```bash
git add .
git commit -m "test: verify turn_id and error tracking implementation"
```

---

## Summary

This plan implements the fix for three data quality issues through 7 tasks:

1. **Database Migration** - Add `turn_id` column
2. **Entity Update** - Add field to `DashboardMessage`
3. **Mapper Update** - Support turn_id in SQL operations
4. **Parser Enhancement** - Build message-to-turn mapping
5. **Error Detection** - Implement comprehensive error checking
6. **Service Refactoring** - Restructure ingestion flow
7. **Testing & Verification** - Validate correctness

Each task produces working, testable code with frequent commits. Total estimated time: 3-4 hours.

**Next Step:** Choose execution mode:
- **Subagent-Driven** (recommended): I'll dispatch a fresh subagent per task with review checkpoints
- **Inline Execution**: Execute all tasks in this session using executing-plans skill

Which approach would you prefer?
