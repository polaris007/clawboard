# Session 数据更新机制实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现 session 数据更新机制，确保重新扫描时所有数据反映 transcript 文件的最新状态，特别是修复 conversation turn 属性不更新的问题。

**Architecture:** 采用"删除后重新插入"策略，在事务中执行删除和重新插入操作，保证数据一致性和原子性。

**Tech Stack:** Java 17, Spring Boot, MyBatis, MySQL 5.7

**Design Doc:** [2026-04-28-session-data-update-design.md](./specs/2026-04-28-session-data-update-design.md)

---

## 文件结构映射

### 新增/修改文件

#### Mapper 层
- `src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java` - 添加 deleteBySessionId 方法
- `src/main/resources/mapper/ConversationTurnMapper.xml` - 实现删除 SQL
- `src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java` - 添加 deleteBySessionId 方法
- `src/main/resources/mapper/TranscriptIssueMapper.xml` - 实现删除 SQL

#### Service 层
- `src/main/java/com/company/clawboard/service/DataIngestionService.java`
  - 添加 `deleteExistingSessionData` 方法
  - 修改 `updateSessionSummary` 方法签名和实现
  - 在 `ingestParsedTranscript` 中调用删除逻辑

### 测试文件（可选）
- `src/test/java/com/company/clawboard/service/DataIngestionServiceUpdateTest.java` - 单元测试

---

## Phase 1: 添加 Mapper 方法

### Task 1: 实现 ConversationTurnMapper.deleteBySessionId

**Files:**
- Modify: `src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java`
- Modify: `src/main/resources/mapper/ConversationTurnMapper.xml`

- [ ] **Step 1: 添加接口方法**

在 `ConversationTurnMapper.java` 末尾（第 102 行之前）添加：

```java
/**
 * 删除指定 session 的所有对话轮次
 * @param sessionId Session ID
 * @return 删除的记录数
 */
int deleteBySessionId(@Param("sessionId") String sessionId);
```

- [ ] **Step 2: 实现 XML SQL**

在 `ConversationTurnMapper.xml` 末尾（`</mapper>` 之前）添加：

```xml
<!-- Delete conversation turns by session ID -->
<delete id="deleteBySessionId">
    DELETE FROM dashboard_conversation_turn WHERE session_id = #{sessionId}
</delete>
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java
git add src/main/resources/mapper/ConversationTurnMapper.xml
git commit -m "feat: add deleteBySessionId method to ConversationTurnMapper"
```

---

### Task 2: 实现 TranscriptIssueMapper.deleteBySessionId

**Files:**
- Modify: `src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java`
- Modify: `src/main/resources/mapper/TranscriptIssueMapper.xml`

- [ ] **Step 1: 添加接口方法**

在 `TranscriptIssueMapper.java` 末尾（第 51 行之前）添加：

```java
/**
 * 删除指定 session 的所有问题记录
 * @param sessionId Session ID
 * @return 删除的记录数
 */
int deleteBySessionId(@Param("sessionId") String sessionId);
```

- [ ] **Step 2: 实现 XML SQL**

在 `TranscriptIssueMapper.xml` 末尾（`</mapper>` 之前）添加：

```xml
<!-- Delete transcript issues by session ID -->
<delete id="deleteBySessionId">
    DELETE FROM dashboard_transcript_issue WHERE session_id = #{sessionId}
</delete>
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java
git add src/main/resources/mapper/TranscriptIssueMapper.xml
git commit -m "feat: add deleteBySessionId method to TranscriptIssueMapper"
```

---

## Phase 2: 实现删除逻辑

### Task 3: 实现 deleteExistingSessionData 方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 找到合适的位置**

在 `DataIngestionService.java` 中找到 `updateSessionSummary` 方法（约第 503 行），在其之前添加新方法。

- [ ] **Step 2: 添加删除方法**

在 `updateSessionSummary` 方法之前添加：

```java
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
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "feat: implement deleteExistingSessionData method"
```

---

### Task 4: 在 ingestParsedTranscript 中调用删除逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 找到检测逻辑**

定位到第 89-93 行：

```java
int existingCount = messageMapper.countBySessionId(sessionId);
if (existingCount > 0) {
    log.info("Session {} already scanned ({} messages), will be reprocessed", 
        sessionId, existingCount);
}
```

- [ ] **Step 2: 修改为调用删除**

修改为：

```java
int existingCount = messageMapper.countBySessionId(sessionId);
if (existingCount > 0) {
    log.info("Session {} already scanned ({} messages), deleting old data and reprocessing", 
        sessionId, existingCount);
    // ✅ 删除旧数据，然后重新处理
    deleteExistingSessionData(sessionId);
}
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "feat: call deleteExistingSessionData when session already exists"
```

---

## Phase 3: 改进时间戳提取

### Task 5: 修改 updateSessionSummary 方法签名

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 修改方法签名**

找到第 503 行的方法签名：

```java
// 当前
private void updateSessionSummary(Long scanId, String sessionId, String employeeId,
                                  int messageCount, int turnCount, 
                                  int skillCount, int issueCount,
                                  LocalDateTime now) {

// 修改为
private void updateSessionSummary(Long scanId, String sessionId, String employeeId,
                                  List<MessageRecord> messages, int turnCount, 
                                  int skillCount, int issueCount,
                                  LocalDateTime now) {
```

- [ ] **Step 2: 实现时间戳提取逻辑**

替换方法体（第 507-531 行）为：

```java
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
```

- [ ] **Step 3: 编译验证（预期失败）**

Run: `mvn compile -DskipTests`
Expected: COMPILATION ERROR（因为调用处参数不匹配）

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "refactor: modify updateSessionSummary to accept messages list"
```

---

### Task 6: 修改 updateSessionSummary 调用处

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 找到调用处**

定位到约第 195 行：

```java
updateSessionSummary(scanId, sessionId, employeeId, 
    messages.size(), conversationTurns, skills.size(), issues.size(), now);
```

- [ ] **Step 2: 修改调用参数**

修改为：

```java
updateSessionSummary(scanId, sessionId, employeeId, 
    messages, conversationTurns, skills.size(), issues.size(), now);
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "fix: update call to updateSessionSummary with messages parameter"
```

---

## Phase 4: 测试与验证

### Task 7: 运行应用并执行测试

**Files:**
- No code changes

- [ ] **Step 1: 清空数据库**

Run: `.\scripts\reset-database.ps1`
Expected: Database tables truncated successfully

- [ ] **Step 2: 启动应用**

Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Expected: Application started on port 8080

- [ ] **Step 3: 第一次扫描**

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/scan/trigger -Method POST -UseBasicParsing`
Expected: HTTP 200 OK, scan started

等待扫描完成，记录日志输出。

- [ ] **Step 4: 验证第一次扫描结果**

查询数据库：

```sql
SELECT COUNT(*) as turn_count FROM dashboard_conversation_turn;
SELECT COUNT(*) as message_count FROM dashboard_message;
SELECT session_id, total_messages, total_turns, first_message_at, last_message_at 
FROM dashboard_session_summary LIMIT 5;
```

记录数据量。

- [ ] **Step 5: 触发第二次扫描**

立即再次触发扫描（文件未修改）：

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/scan/trigger -Method POST -UseBasicParsing`

观察日志，应该看到：
```
Session XXX already scanned (N messages), deleting old data and reprocessing
Deleting existing detail data for session: XXX
Deleted X execution traces
Deleted X transcript issues
...
```

- [ ] **Step 6: 验证第二次扫描结果**

再次查询数据库，确认：
- 数据量与第一次扫描相同
- session_summary 的时间戳正确
- 没有重复数据

- [ ] **Step 7: 检查统计等式**

```sql
SELECT files_total, files_processed, files_skipped, files_error,
       (files_processed + files_skipped + files_error) as calculated_total
FROM dashboard_scan_history 
ORDER BY id DESC LIMIT 2;
```

Expected: `files_total = calculated_total`

---

### Task 8: 编写单元测试（可选）

**Files:**
- Create: `src/test/java/com/company/clawboard/service/DataIngestionServiceUpdateTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.company.clawboard.service;

import com.company.clawboard.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DataIngestionServiceUpdateTest {
    
    @Autowired
    private DataIngestionService dataIngestionService;
    
    @Autowired
    private ConversationTurnMapper turnMapper;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private SessionSummaryMapper sessionSummaryMapper;
    
    @Test
    void testDeleteExistingSessionData() {
        // TODO: 实现测试逻辑
        // 1. 插入测试数据
        // 2. 调用删除方法
        // 3. 验证数据已删除
    }
}
```

- [ ] **Step 2: 运行测试**

Run: `mvn test "-Dtest=DataIngestionServiceUpdateTest"`
Expected: Tests run: X, Failures: 0, Errors: 0

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/company/clawboard/service/DataIngestionServiceUpdateTest.java
git commit -m "test: add unit tests for session data update"
```

---

## 完成检查清单

- [ ] 所有任务已完成
- [ ] 编译通过无错误
- [ ] 应用启动成功
- [ ] 第一次扫描成功
- [ ] 第二次扫描触发删除逻辑
- [ ] 日志输出正确（显示删除的记录数）
- [ ] 数据库中没有重复数据
- [ ] session_summary 的时间戳正确
- [ ] 统计等式成立：`processed + skipped + error = total`
- [ ] Turn 的属性（end_message_id, end_time）正确更新
- [ ] 单元测试通过（如果编写）

---

## 回滚方案

如果实施后出现问题：

1. **恢复所有修改**：
   ```bash
   git revert <commit-hash-of-task-1>
   git revert <commit-hash-of-task-2>
   git revert <commit-hash-of-task-3>
   git revert <commit-hash-of-task-4>
   git revert <commit-hash-of-task-5>
   git revert <commit-hash-of-task-6>
   ```

2. **删除测试文件**（如果有）：
   ```bash
   git rm src/test/java/com/company/clawboard/service/DataIngestionServiceUpdateTest.java
   ```

3. **重新编译和测试**

---

## 后续优化建议

1. **批量删除优化**：对于大量数据的 session，可以考虑分批删除
2. **异步删除**：将删除操作移到后台任务，减少扫描阻塞时间
3. **监控告警**：当删除失败或数据不一致时发送告警
4. **性能分析**：记录删除和重新插入的时间，分析瓶颈

---

## 参考资料

- [Session 数据更新机制设计](./specs/2026-04-28-session-data-update-design.md)
- [扫描统计优化设计](./specs/2026-04-28-scan-statistics-optimization-design.md)
- [Session汇总表及增量更新机制](../../memory/project_introduction.md#session汇总表及增量更新机制)
