# Dashboard Conversation Turn Token和ToolCall统计实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在扫描 transcript 文件过程中，一次性统计每个对话轮次的 token 使用量和 toolCall 执行情况，并将结果存入 dashboard_conversation_turn 表

**Architecture:** 
1. 扩展 `TurnAssembler.AssembledTurn` record，增加 token 和 toolCall 统计字段
2. 在 `TurnAssembler.assembleTurn()` 方法中遍历消息列表，累加所有 message 的 usage 信息并统计 toolCall
3. 修改 `DataIngestionService.saveConversationTurns()` 方法，从 AssembledTurn 读取统计数据并设置到实体对象
4. 添加单元测试验证统计逻辑的正确性

**Tech Stack:** Java 17, Spring Boot 3.2.5, MyBatis, JUnit 5, Lombok

---

## 文件结构概览

### 需要修改的文件

1. **TurnAssembler.java** - 核心修改
   - 扩展 `AssembledTurn` record 增加统计字段
   - 在 `assembleTurn()` 中添加统计逻辑

2. **DataIngestionService.java** - 数据持久化
   - 修改 `saveConversationTurns()` 方法，使用新的统计字段

3. **DashboardConversationTurn.java** - 实体类（已有字段，无需修改）
   - 确认字段存在：totalInputTokens, totalOutputTokens, totalTokens, toolCallsCount, toolCallsSuccess, toolCallsError

### 需要创建的文件

4. **TurnAssemblerTest.java** - 单元测试
   - 测试基础场景：无工具调用、单个工具调用成功
   - 测试异常场景：缺少 toolResult、toolResult 有错误、stopReason 为 error

---

## 关键设计决策

### 1. ToolCall 错误判定规则

一个 toolCall 被标记为错误，当满足以下任一条件：
- **缺少对应的 toolResult**：assistant message 中有 toolCall，但没有找到对应 callId 的 toolResult message
- **toolResult 有错误**：toolResult.isError() == true 或 toolResult.errorMessage() 不为空
- **assistant stopReason 异常**：stopReason 为 "error" 或 "aborted"

### 2. 孤立 toolResult 处理

如果 toolResult 找不到对应的 toolCall（孤立 toolResult）：
- ❌ **不计入 tool_calls_error**（保持 tool_calls_error ≤ tool_calls_count 约束）
- ✅ 可选：记录日志或添加到 issue 表（本次不实现）

### 3. Token 统计规则

- 累加该 turn 中所有 message 的 `usage.input/output/totalTokens`
- NULL 值当作 0 处理
- 只统计 assistant message 的 usage（user 和 toolResult 没有 usage 字段）

### 4. 实际数据结构确认

通过检查 transcript 文件确认：
- ✅ 每个 assistant message 最多只有 **1 个 toolCall**
- ✅ toolCall 信息存储在 `content` 数组中，类型为 `"toolCall"` 的 block
- ✅ `MessageRecord.toolCalls()` 返回的是 List，但实际长度最多为 1

---

## 任务分解

### Task 1: 扩展 AssembledTurn Record 增加统计字段

**Files:**
- Modify: `src/main/java/com/company/clawboard/parser/TurnAssembler.java:22-33`

- [ ] **Step 1: 查看当前 AssembledTurn 定义**

当前定义：
```java
public record AssembledTurn(
    String userInput,
    List<ChainStep> chainSteps,
    boolean isComplete,
    boolean hasError,
    String status,
    String startMessageId,
    String endMessageId,
    long startTime,
    long endTime,
    boolean isSystemTurn
) {}
```

- [ ] **Step 2: 添加统计字段到 AssembledTurn**

修改为：
```java
public record AssembledTurn(
    String userInput,
    List<ChainStep> chainSteps,
    boolean isComplete,
    boolean hasError,
    String status,
    String startMessageId,
    String endMessageId,
    long startTime,
    long endTime,
    boolean isSystemTurn,
    // 新增统计字段
    long totalInputTokens,
    long totalOutputTokens,
    long totalTokens,
    int toolCallsCount,
    int toolCallsSuccess,
    int toolCallsError
) {}
```

- [ ] **Step 3: 更新 assembleTurn() 的空返回值**

修改第 37 行：
```java
// 原来
return new AssembledTurn(null, List.of(), false, false, "incomplete", null, null, 0, 0, false);

// 修改为
return new AssembledTurn(null, List.of(), false, false, "incomplete", null, null, 0, 0, false, 0, 0, 0, 0, 0, 0);
```

- [ ] **Step 4: 编译验证**

运行：
```bash
mvn clean compile -DskipTests
```

预期：编译成功（暂时会有其他地方的编译错误，因为 DataIngestionService 还在使用旧的构造函数）

---

### Task 2: 实现 Token 和 ToolCall 统计逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/parser/TurnAssembler.java:35-74`

- [ ] **Step 1: 在 assembleTurn() 开头添加统计变量初始化**

在第 40 行后添加：
```java
// 初始化统计变量
long totalInputTokens = 0;
long totalOutputTokens = 0;
long totalTokens = 0;
int toolCallsCount = 0;
int toolCallsError = 0;

// 构建 toolCall -> toolResult 映射
Map<String, MessageRecord> toolResultMap = new HashMap<>();
for (MessageRecord msg : messages) {
    if ("toolResult".equals(msg.role()) && msg.toolCallId() != null) {
        toolResultMap.put(msg.toolCallId(), msg);
    }
}
```

需要添加 import：
```java
import java.util.HashMap;
import java.util.Map;
```

- [ ] **Step 2: 在 buildChainSteps() 之前添加统计循环**

在第 48 行后添加：
```java
// 统计 tokens 和 toolCalls
for (MessageRecord msg : messages) {
    // 只统计 assistant message 的 usage
    if ("assistant".equals(msg.role())) {
        // 统计 tokens
        if (msg.usage() != null) {
            totalInputTokens += msg.usage().inputTokens();
            totalOutputTokens += msg.usage().outputTokens();
            totalTokens += msg.usage().totalTokens();
        }
        
        // 统计 toolCalls
        if (msg.toolCalls() != null && !msg.toolCalls().isEmpty()) {
            for (MessageRecord.ToolCallInfo tc : msg.toolCalls()) {
                toolCallsCount++;
                
                boolean hasError = false;
                
                // 检查是否有对应的 toolResult
                MessageRecord toolResult = toolResultMap.get(tc.id());
                if (toolResult == null) {
                    hasError = true;  // 缺少 toolResult
                } else if (toolResult.isError() || 
                           (toolResult.errorMessage() != null && !toolResult.errorMessage().isEmpty())) {
                    hasError = true;  // toolResult 有错误
                }
                
                // 检查 stopReason
                String stopReason = msg.stopReason();
                if ("error".equals(stopReason) || "aborted".equals(stopReason)) {
                    hasError = true;
                }
                
                if (hasError) {
                    toolCallsError++;
                }
            }
        }
    }
}

int toolCallsSuccess = toolCallsCount - toolCallsError;
```

- [ ] **Step 3: 更新 return 语句使用统计值**

修改第 73 行的 return 语句：
```java
// 原来
return new AssembledTurn(userInput, chainSteps, isComplete, hasError, status, startMessageId, endMessageId, startTime, endTime, isSystemTurn);

// 修改为
return new AssembledTurn(
    userInput, chainSteps, isComplete, hasError, status, 
    startMessageId, endMessageId, startTime, endTime, isSystemTurn,
    totalInputTokens, totalOutputTokens, totalTokens,
    toolCallsCount, toolCallsSuccess, toolCallsError
);
```

- [ ] **Step 4: 编译验证**

运行：
```bash
mvn clean compile -DskipTests
```

预期：编译成功（DataIngestionService 仍有错误，待下一步修复）

---

### Task 3: 修改 DataIngestionService 使用新的统计字段

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java:240-280`

- [ ] **Step 1: 定位需要修改的代码段**

当前代码（第 271-277 行）：
```java
entity.setTotalInputTokens(0);
entity.setTotalOutputTokens(0);
entity.setTotalTokens(0);
entity.setTotalCost(BigDecimal.ZERO);
entity.setToolCallsCount(0);
entity.setToolCallsSuccess(0);
entity.setToolCallsError(0);
```

- [ ] **Step 2: 替换为从 AssembledTurn 读取统计值**

修改为：
```java
// 从 AssembledTurn 获取统计值
entity.setTotalInputTokens((int) turn.totalInputTokens());
entity.setTotalOutputTokens((int) turn.totalOutputTokens());
entity.setTotalTokens((int) turn.totalTokens());
entity.setTotalCost(BigDecimal.ZERO);  // TODO: 后续从 usage.costTotal 计算
entity.setToolCallsCount(turn.toolCallsCount());
entity.setToolCallsSuccess(turn.toolCallsSuccess());
entity.setToolCallsError(turn.toolCallsError());
```

- [ ] **Step 3: 编译验证**

运行：
```bash
mvn clean compile -DskipTests
```

预期：**编译成功，无错误**

- [ ] **Step 4: 运行现有测试确保未破坏功能**

运行：
```bash
mvn test -Dtest=TranscriptParserTest
```

预期：测试通过

---

### Task 4: 编写单元测试 - 基础场景

**Files:**
- Create: `src/test/java/com/company/clawboard/parser/TurnAssemblerTest.java`

- [ ] **Step 1: 创建测试类和基础 setup**

```java
package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;
import com.company.clawboard.parser.model.UsageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnAssemblerTest {
    
    private TurnAssembler turnAssembler;
    
    @BeforeEach
    void setUp() {
        // SystemMessageFilter 是必需的依赖
        turnAssembler = new TurnAssembler(new SystemMessageFilter());
    }
    
    private MessageRecord createUserMessage(String id, String content) {
        return new MessageRecord(
            id, null, "2026-04-22T10:00:00Z", "user", content,
            null, List.of(), null, null, false,
            null, null, null, null, 0, 1713780000000L, 1
        );
    }
    
    private MessageRecord createAssistantMessage(String id, UsageInfo usage, List<MessageRecord.ToolCallInfo> toolCalls, String stopReason) {
        return new MessageRecord(
            id, null, "2026-04-22T10:00:01Z", "assistant", null,
            null, toolCalls, null, null, false,
            "custom-provider", "Qwen3.5", stopReason, usage, 100, 1713780001000L, 2
        );
    }
    
    private MessageRecord createToolResultMessage(String id, String toolCallId, String toolName, boolean isError, String errorMessage) {
        return new MessageRecord(
            id, null, "2026-04-22T10:00:02Z", "toolResult", null,
            errorMessage, List.of(), toolCallId, toolName, isError,
            null, null, null, null, 0, 1713780002000L, 3
        );
    }
    
    private UsageInfo createUsage(int input, int output, int total) {
        return new UsageInfo(input, output, 0, 0, total, BigDecimal.ZERO);
    }
}
```

- [ ] **Step 2: 测试无工具调用的对话轮次**

```java
@Test
void testTurnWithoutToolCalls() {
    // 准备：user → assistant（纯文本回复）
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "帮我写报告"),
        createAssistantMessage("msg2", createUsage(100, 200, 300), List.of(), null)
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证
    assertEquals(100, turn.totalInputTokens());
    assertEquals(200, turn.totalOutputTokens());
    assertEquals(300, turn.totalTokens());
    assertEquals(0, turn.toolCallsCount());
    assertEquals(0, turn.toolCallsSuccess());
    assertEquals(0, turn.toolCallsError());
    assertTrue(turn.isComplete());
    assertFalse(turn.hasError());
}
```

- [ ] **Step 3: 测试单个工具调用成功**

```java
@Test
void testSingleToolCallSuccess() {
    // 准备：user → assistant(toolCall) → toolResult → assistant(reply)
    MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
        "call-1", "exec", "{\"command\":\"date\"}"
    );
    
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "执行命令"),
        createAssistantMessage("msg2", createUsage(50, 30, 80), List.of(toolCall), "toolUse"),
        createToolResultMessage("msg3", "call-1", "exec", false, null),
        createAssistantMessage("msg4", createUsage(20, 40, 60), List.of(), null)
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证
    assertEquals(70, turn.totalInputTokens());   // 50 + 20
    assertEquals(70, turn.totalOutputTokens());  // 30 + 40
    assertEquals(140, turn.totalTokens());       // 80 + 60
    assertEquals(1, turn.toolCallsCount());
    assertEquals(1, turn.toolCallsSuccess());
    assertEquals(0, turn.toolCallsError());
    assertTrue(turn.isComplete());
}
```

- [ ] **Step 4: 运行测试验证**

运行：
```bash
mvn test -Dtest=TurnAssemblerTest#testTurnWithoutToolCalls,TurnAssemblerTest#testSingleToolCallSuccess
```

预期：两个测试都通过

---

### Task 5: 编写单元测试 - 异常场景

**Files:**
- Modify: `src/test/java/com/company/clawboard/parser/TurnAssemblerTest.java`

- [ ] **Step 1: 测试缺少 toolResult**

```java
@Test
void testMissingToolResult() {
    // 准备：user → assistant(toolCall) → (文件结束，缺少 toolResult)
    MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
        "call-1", "read_file", "{\"path\":\"/etc/hosts\"}"
    );
    
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "读取文件"),
        createAssistantMessage("msg2", createUsage(60, 20, 80), List.of(toolCall), "toolUse")
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证
    assertEquals(60, turn.totalInputTokens());
    assertEquals(20, turn.totalOutputTokens());
    assertEquals(80, turn.totalTokens());
    assertEquals(1, turn.toolCallsCount());
    assertEquals(0, turn.toolCallsSuccess());
    assertEquals(1, turn.toolCallsError());  // 缺少 toolResult，计为错误
    assertFalse(turn.isComplete());
}
```

- [ ] **Step 2: 测试 toolResult 有错误**

```java
@Test
void testToolResultWithError() {
    // 准备：user → assistant(toolCall) → toolResult(error)
    MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
        "call-1", "exec", "{\"command\":\"invalid_command\"}"
    );
    
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "执行命令"),
        createAssistantMessage("msg2", createUsage(50, 30, 80), List.of(toolCall), "toolUse"),
        createToolResultMessage("msg3", "call-1", "exec", true, "Command not found")
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证
    assertEquals(1, turn.toolCallsCount());
    assertEquals(0, turn.toolCallsSuccess());
    assertEquals(1, turn.toolCallsError());  // toolResult 有错误
}
```

- [ ] **Step 3: 测试 assistant stopReason 为 error**

```java
@Test
void testAssistantStopReasonError() {
    // 准备：user → assistant(toolCall, stopReason="error")
    MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
        "call-1", "exec", "{\"command\":\"date\"}"
    );
    
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "执行命令"),
        createAssistantMessage("msg2", createUsage(50, 0, 50), List.of(toolCall), "error")
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证
    assertEquals(1, turn.toolCallsCount());
    assertEquals(0, turn.toolCallsSuccess());
    assertEquals(1, turn.toolCallsError());  // stopReason 为 error
    assertTrue(turn.hasError());
}
```

- [ ] **Step 4: 测试多个 toolCall 部分成功**

```java
@Test
void testMultipleToolCallsPartialSuccess() {
    // 准备：user → assistant(2个toolCall) → toolResult(success) → toolResult(error)
    MessageRecord.ToolCallInfo toolCall1 = new MessageRecord.ToolCallInfo(
        "call-1", "read_file", "{\"path\":\"/etc/hosts\"}"
    );
    MessageRecord.ToolCallInfo toolCall2 = new MessageRecord.ToolCallInfo(
        "call-2", "exec", "{\"command\":\"invalid\"}"
    );
    
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "读取并执行"),
        createAssistantMessage("msg2", createUsage(80, 40, 120), List.of(toolCall1, toolCall2), "toolUse"),
        createToolResultMessage("msg3", "call-1", "read_file", false, null),
        createToolResultMessage("msg4", "call-2", "exec", true, "Command failed")
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证
    assertEquals(2, turn.toolCallsCount());
    assertEquals(1, turn.toolCallsSuccess());  // call-1 成功
    assertEquals(1, turn.toolCallsError());    // call-2 失败
}
```

- [ ] **Step 5: 测试 usage 为 null 的情况**

```java
@Test
void testNullUsage() {
    // 准备：assistant message 的 usage 为 null
    List<MessageRecord> messages = List.of(
        createUserMessage("msg1", "测试"),
        createAssistantMessage("msg2", null, List.of(), null)
    );
    
    // 执行
    TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
    
    // 验证：应该当作 0 处理，不抛出异常
    assertEquals(0, turn.totalInputTokens());
    assertEquals(0, turn.totalOutputTokens());
    assertEquals(0, turn.totalTokens());
}
```

- [ ] **Step 6: 运行所有测试**

运行：
```bash
mvn test -Dtest=TurnAssemblerTest
```

预期：所有 7 个测试都通过

---

### Task 6: 集成测试 - 验证数据库写入

**Files:**
- Modify: `src/test/java/com/company/clawboard/service/DataIngestionServiceTest.java` (如果存在)
- 或 Create: `src/test/java/com/company/clawboard/integration/TokenStatisticsIntegrationTest.java`

- [ ] **Step 1: 准备测试数据**

创建一个简单的 transcript 文件用于测试：
```jsonl
{"type":"message","id":"msg1","timestamp":"2026-04-22T10:00:00Z","message":{"role":"user","content":[{"type":"text","text":"执行命令"}]}}
{"type":"message","id":"msg2","timestamp":"2026-04-22T10:00:01Z","message":{"role":"assistant","content":[{"type":"toolCall","id":"call-1","name":"exec","arguments":{"command":"date"}}],"usage":{"input":50,"output":30,"totalTokens":80},"stopReason":"toolUse"}}
{"type":"message","id":"msg3","timestamp":"2026-04-22T10:00:02Z","message":{"role":"toolResult","toolCallId":"call-1","toolName":"exec","isError":false,"content":[{"type":"text","text":"2026-04-22"}]}}
{"type":"message","id":"msg4","timestamp":"2026-04-22T10:00:03Z","message":{"role":"assistant","content":[],"usage":{"input":20,"output":40,"totalTokens":60}}}
```

- [ ] **Step 2: 编写集成测试**

```java
@Test
void testTokenAndToolCallStatisticsPersistedToDatabase() {
    // 准备：解析 transcript 文件
    Path transcriptFile = Paths.get("test/session-transcript/test-token-stats/test.jsonl");
    
    // 执行：触发扫描和数据导入
    Long scanId = dataIngestionService.scanAndIngest(transcriptFile, "TEST_EMPLOYEE");
    
    // 验证：查询数据库中的 conversation turn
    DashboardConversationTurn turn = turnMapper.selectByScanIdAndIndex(scanId, 1);
    
    assertNotNull(turn);
    assertEquals(70, turn.getTotalInputTokens());   // 50 + 20
    assertEquals(70, turn.getTotalOutputTokens());  // 30 + 40
    assertEquals(140, turn.getTotalTokens());       // 80 + 60
    assertEquals(1, turn.getToolCallsCount());
    assertEquals(1, turn.getToolCallsSuccess());
    assertEquals(0, turn.getToolCallsError());
}
```

- [ ] **Step 3: 运行集成测试**

运行：
```bash
mvn test -Dtest=TokenStatisticsIntegrationTest
```

预期：测试通过，数据库中正确保存了统计值

---

### Task 7: 清理临时文件并提交

**Files:**
- Delete: `check_toolcall.py`, `check_multiple_toolcalls.py`, `simple_check.py`, `check_single_file.py`

- [ ] **Step 1: 删除临时 Python 脚本**

```bash
rm check_toolcall.py check_multiple_toolcalls.py simple_check.py check_single_file.py
```

- [ ] **Step 2: 运行完整测试套件**

```bash
mvn clean test
```

预期：所有测试通过

- [ ] **Step 3: 提交代码**

```bash
git add src/main/java/com/company/clawboard/parser/TurnAssembler.java
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git add src/test/java/com/company/clawboard/parser/TurnAssemblerTest.java
git commit -m "feat: 实现对话轮次 token 和 toolCall 统计

- 扩展 AssembledTurn 增加统计字段
- 在 TurnAssembler 中实现统计逻辑
- 修改 DataIngestionService 使用统计值
- 添加完整的单元测试覆盖基础和异常场景

统计规则：
- Token: 累加所有 assistant message 的 usage 字段
- ToolCall 错误判定：缺少 toolResult / toolResult 有错误 / stopReason 异常
- 孤立 toolResult 不计入错误（保持数量约束）"
```

---

## 验证清单

完成所有任务后，验证以下内容：

- [ ] 编译成功：`mvn clean compile`
- [ ] 单元测试全部通过：`mvn test -Dtest=TurnAssemblerTest`
- [ ] 集成测试通过（如果有）
- [ ] 手动测试：扫描一个实际的 transcript 文件，检查数据库中 dashboard_conversation_turn 表的统计字段是否正确
- [ ] 代码审查：确认统计逻辑符合设计规范
- [ ] 文档更新：如有必要，更新相关技术文档

---

## 注意事项

1. **数据类型转换**：AssembledTurn 使用 `long` 存储 token 数，但数据库实体使用 `Integer`，需要注意溢出问题（虽然实际场景中不太可能超过 Integer.MAX_VALUE）

2. **NULL 值处理**：确保所有 usage 相关的字段都有 NULL 检查，避免 NullPointerException

3. **性能考虑**：统计逻辑在内存中完成，不涉及额外的数据库查询，性能良好

4. **向后兼容**：如果未来需要支持一个 assistant message 包含多个 toolCall，当前代码已经支持（使用循环处理 toolCalls 列表）

5. **扩展性**：skill_calls 的统计逻辑类似，可以在后续任务中按照相同模式实现
