# Session Transcript Skill 调用链解析设计

**日期**: 2026-04-23  
**状态**: Approved  
**作者**: Clawboard Team

---

## 1. 概述

### 1.1 背景

OpenClaw 的 session transcript 文件记录了用户与 AI 助手的完整对话过程。在对话中，AI 可能会调用各种 skill（技能）来执行特定任务。我们需要从这些 transcript 文件中提取 skill 调用的详细信息，包括：

- Skill 名称
- 调用时间
- 执行结果（成功/失败）
- 执行时长
- 调用顺序（同一轮次中的多个 skill）

### 1.2 目标

设计并实现一个完整的 skill 调用链检测机制，能够：

1. **识别 skill 调用起点**：通过 "正在使用 xxx Skill" 文本匹配
2. **确定调用链范围**：从起点到下一个非 toolCall 的 assistant 消息或 user 消息
3. **处理多 skill 场景**：同一 turn 中存在多个 skill 调用时，正确分段检测
4. **计算执行时长**：基于消息时间戳计算 duration
5. **记录调用轨迹**：通过 sequence_order 字段记录调用先后顺序

---

## 2. 数据结构分析

### 2.1 Transcript 消息结构

Transcript 文件是 JSONL 格式，每条消息包含以下关键字段：

```json
{
  "type": "message",
  "id": "msg_123",
  "timestamp": "2026-04-14T07:07:00.549Z",
  "message": {
    "role": "assistant",
    "content": [
      {"type": "text", "text": "## 🛠️ 正在使用 docx Skill\n..."},
      {"type": "toolCall", "id": "call_xxx", "name": "exec", "arguments": {...}}
    ]
  }
}
```

### 2.2 消息角色类型

| Role | 说明 | 示例 |
|------|------|------|
| `user` | 用户消息 | `"role":"user"` |
| `assistant` | AI 助手消息（可能包含 toolCall） | `"role":"assistant"` |
| `toolResult` | 工具执行结果 | `"role":"toolResult"`, `"toolCallId":"call_xxx"` |

**重要澄清**：不存在 `role="tool"` 的消息类型，工具结果使用的是 `role="toolResult"`。

### 2.3 Skill 调用特征

Skill 调用起点的识别条件：
- `role == "assistant"`
- `content` 数组中包含 text 类型的内容
- text 内容匹配正则：`正在使用\s+(.+?)\s+Skill`

---

## 3. 核心算法设计

### 3.1 整体流程

```
TranscriptParser.parse()
    ↓ 解析 JSONL → List<MessageRecord>
    ↓
SkillChainDetector.detectSkillChains()
    ↓ 分析消息列表 → List<SkillInvocation>
    ↓
DataIngestionService.convertToSkillInvocations()
    ↓ 转换为实体 → List<DashboardSkillInvocation>
    ↓
DashboardSkillInvocationMapper.batchInsert()
    ↓ 批量插入数据库
```

### 3.2 SkillChainDetector 算法

#### 步骤 1: 识别所有 skill 起点

```java
List<Integer> skillStarts = new ArrayList<>();
for (int i = 0; i < messages.size(); i++) {
    MessageRecord msg = messages.get(i);
    if (isSkillStart(msg)) {
        skillStarts.add(i);
    }
}

boolean isSkillStart(MessageRecord msg) {
    return msg.role().equals("assistant") 
        && msg.content() != null 
        && containsText(msg.content(), "正在使用.*Skill");
}
```

#### 步骤 2: 对每个 skill 起点确定终点

```java
for (int idx = 0; idx < skillStarts.size(); idx++) {
    int skillIndex = skillStarts.get(idx);
    int nextSkillIndex = (idx + 1 < skillStarts.size()) ? skillStarts.get(idx + 1) : -1;
    
    SkillInvocation skill;
    
    if (nextSkillIndex != -1) {
        // === 情况 1: 中间 skill ===
        skill = detectIntermediateSkill(messages, skillIndex, nextSkillIndex);
    } else {
        // === 情况 2: 最后一个 skill ===
        skill = detectLastSkill(messages, skillIndex);
    }
    
    skillInvocations.add(skill);
}
```

#### 步骤 3: 中间 skill 检测逻辑

```java
SkillInvocation detectIntermediateSkill(
    List<MessageRecord> messages, 
    int skillIndex, 
    int nextSkillIndex
) {
    // 在 [skillIndex+1, nextSkillIndex-1] 范围内查找最后一条 toolResult
    int lastToolResultIndex = -1;
    for (int j = skillIndex + 1; j < nextSkillIndex; j++) {
        if (messages.get(j).role().equals("toolResult")) {
            lastToolResultIndex = j;
        }
    }
    
    String resultMessageId;
    boolean isError;
    long durationMs;
    
    if (lastToolResultIndex != -1) {
        // ✅ 找到 toolResult，skill 成功
        resultMessageId = messages.get(lastToolResultIndex).id();
        isError = false;
        durationMs = messages.get(lastToolResultIndex).epochMs() - messages.get(skillIndex).epochMs();
    } else {
        // ❌ 没有 toolResult，skill 失败
        // 选项 B：记录最后一个消息的位置（即使是 assistant toolCall）
        int lastMessageIndex = nextSkillIndex - 1;
        resultMessageId = messages.get(lastMessageIndex).id();
        isError = true;
        durationMs = messages.get(lastMessageIndex).epochMs() - messages.get(skillIndex).epochMs();
    }
    
    return new SkillInvocation(
        extractSkillName(messages.get(skillIndex)),
        messages.get(skillIndex).id(),
        messageIdToTurnIndex.get(messages.get(skillIndex).id()),
        messages.get(skillIndex).epochMs(),
        resultMessageId,
        isError,
        (int) durationMs,
        idx + 1  // sequenceOrder
    );
}
```

#### 步骤 4: 最后一个 skill 检测逻辑

```java
SkillInvocation detectLastSkill(List<MessageRecord> messages, int skillIndex) {
    int index = skillIndex + 1;
    String resultMessageId = null;
    boolean isError = false;
    long durationMs = 0;
    
    while (index < messages.size()) {
        MessageRecord nextMsg = messages.get(index);
        
        if (nextMsg.role().equals("user")) {
            // User 打断，skill 失败
            resultMessageId = messages.get(index - 1).id();
            isError = true;
            durationMs = messages.get(index - 1).epochMs() - messages.get(skillIndex).epochMs();
            break;
        }
        
        if (nextMsg.role().equals("assistant") && !hasToolCall(nextMsg)) {
            // 正常结束，skill 成功
            resultMessageId = nextMsg.id();
            isError = false;
            durationMs = nextMsg.epochMs() - messages.get(skillIndex).epochMs();
            break;
        }
        
        index++;
    }
    
    if (index >= messages.size()) {
        // 文件结束都没找到终点
        resultMessageId = messages.get(messages.size() - 1).id();
        isError = true;
        durationMs = messages.get(messages.size() - 1).epochMs() - messages.get(skillIndex).epochMs();
    }
    
    return new SkillInvocation(
        extractSkillName(messages.get(skillIndex)),
        messages.get(skillIndex).id(),
        messageIdToTurnIndex.get(messages.get(skillIndex).id()),
        messages.get(skillIndex).epochMs(),
        resultMessageId,
        isError,
        (int) durationMs,
        idx + 1  // sequenceOrder
    );
}
```

---

## 4. 数据模型设计

### 4.1 SkillInvocation Record（增强版）

```java
public record SkillInvocation(
    String skillName,           // skill 名称（从 "正在使用 xxx Skill" 提取）
    String readMessageId,       // skill 调用起点消息 ID
    Long turnId,                // 所属 turn 的 ID（可为 null）
    long invokedAt,             // 调用时间戳 (epoch ms)
    String resultMessageId,     // skill 调用终点消息 ID
    boolean isError,            // 是否执行失败
    int durationMs,             // 执行时长 (毫秒)
    int sequenceOrder           // 在同一 turn 中的调用顺序 (1, 2, 3...)
) {}
```

### 4.2 数据库 Schema 调整

需要在 `dashboard_skill_invocation` 表中添加 `sequence_order` 字段：

```sql
ALTER TABLE dashboard_skill_invocation 
ADD COLUMN sequence_order INT NOT NULL DEFAULT 1 
COMMENT '同一turn中的skill调用顺序';
```

### 4.3 DashboardSkillInvocation 实体类

```java
@Data
@TableName("dashboard_skill_invocation")
public class DashboardSkillInvocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long scanId;
    private String sessionId;
    private String employeeId;
    private Long turnId;
    private String skillName;
    private String skillPath;
    private LocalDateTime invokedAt;
    private String readMessageId;
    private String resultMessageId;
    private Integer isError;  // 0=成功, 1=失败
    private String triggerType;
    private Integer durationMs;
    private Integer sequenceOrder;  // ⭐ 新增字段
    private LocalDateTime createdAt;
}
```

---

## 5. 边界情况处理

### 5.1 连续两个 skill 起点之间没有 toolResult

**场景**：
```
索引 0: {role: "assistant", content: "正在使用 Skill A"}
索引 1: {role: "assistant", content: "正在使用 Skill B"}
```

**处理**：
- Skill A: `lastToolResultIndex = -1` → `isError = true`, `resultMessageId = msg_0.id`, `durationMs = 0`
- Skill B: 按正常逻辑继续检测

### 5.2 最后一个 skill 后直接跟 user 消息

**场景**：
```
索引 0: {role: "assistant", content: "正在使用 Skill A"}
索引 1: {role: "toolResult", ...}
索引 2: {role: "user", content: "停止"}
```

**处理**：
- Skill A: `resultMessageId = msg_1.id`, `isError = true` (被 user 打断)

### 5.3 三个及以上 skill 连续调用

算法同样适用，每个 skill 的终点都是下一个 skill 起点前的最后一条 toolResult。

### 5.4 TurnId 映射缺失

如果 `messageIdToTurnIndex` 中没有对应的 turnId，则 `turnId = null`，后续可通过 messageId 查询补充。

---

## 6. 集成点设计

### 6.1 TranscriptParser 修改

在 `TranscriptParser.parse()` 方法中，替换现有的简单 skill 检测逻辑：

```java
// ❌ 旧逻辑（删除）
List<SkillInvocation> skillInvocations = new ArrayList<>();
for (MessageRecord msg : messages) {
    if (msg.toolCalls() != null) {
        for (var tc : msg.toolCalls()) {
            if (skillDetector.isSkillToolCall(tc.name())) {
                String skillName = skillDetector.extractSkillName(tc.name());
                skillInvocations.add(new SkillInvocation(skillName, tc.id(), msg.epochMs()));
            }
        }
    }
}

// ✅ 新逻辑（替换为）
SkillChainDetector chainDetector = new SkillChainDetector();
List<SkillInvocation> skillInvocations = chainDetector.detectSkillChains(
    messages, 
    messageIdToTurnIndex
);
```

### 6.2 DataIngestionService 修改

更新 `convertToSkillInvocations()` 方法以填充完整信息：

```java
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
        entity.setTurnId(skill.turnId());  // ✅ 现在可以填充了
        entity.setSkillName(skill.skillName());
        entity.setInvokedAt(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(skill.invokedAt()), BEIJING_ZONE));
        entity.setReadMessageId(skill.readMessageId());  // ✅ 不再为 null
        entity.setResultMessageId(skill.resultMessageId());  // ✅ 不再为 null
        entity.setIsError(skill.isError() ? 1 : 0);  // ✅ 正确的错误状态
        entity.setTriggerType("model_read");
        entity.setDurationMs(skill.durationMs());  // ✅ 计算出的时长
        entity.setSequenceOrder(skill.sequenceOrder());  // ⭐ 新增
        entity.setCreatedAt(now);

        result.add(entity);
    }

    return result;
}
```

---

## 7. 测试策略

### 7.1 单元测试

```java
@SpringBootTest
class SkillChainDetectorTest {
    
    @Test
    void testDetectSuccessfulSkillChain() {
        // 构造测试数据：assistant(toolCall+"正在使用 X Skill") → toolResult → assistant
        // 验证：isError=false, durationMs > 0
    }
    
    @Test
    void testDetectFailedSkillChain_UserInterrupted() {
        // 构造测试数据：assistant(toolCall+"正在使用 X Skill") → user
        // 验证：isError=true, resultMessageId=user 前一条消息
    }
    
    @Test
    void testDetectMultipleSkillsInOneTurn() {
        // 构造测试数据：Skill A → toolResult → Skill B → toolResult → assistant
        // 验证：两个 skill 都有正确的 resultMessageId 和 sequenceOrder
    }
    
    @Test
    void testDetectSkillWithoutToolResult() {
        // 构造测试数据：Skill A → assistant(toolCall) → Skill B
        // 验证：Skill A isError=true, resultMessageId=索引1的消息ID
    }
}
```

### 7.2 集成测试

- 使用真实的 transcript 文件（如 `test/session-transcript/a793d94b.../*.jsonl`）
- 验证数据库中的 `dashboard_skill_invocation` 记录完整性
- 对比手动分析的结果

---

## 8. 性能考虑

### 8.1 时间复杂度

- **单次遍历**: O(n)，n = messages 数量
- **内存占用**: O(k)，k = skill 调用数量

### 8.2 优化建议

1. 使用静态 Pattern 避免重复编译正则表达式
2. 批量插入数据库而非逐条插入
3. 复用 messageIdToTurnIndex 映射表

---

## 9. 增量扫描支持

当 `/scan/trigger` 触发新扫描时：

1. **删除旧数据**（已有逻辑）
   ```java
   skillMapper.deleteBySessionId(sessionId);
   ```

2. **插入新数据**（已有逻辑）
   ```java
   skillMapper.batchInsert(skills);
   ```

3. **自动处理变化**
   - 如果 skill 调用链发生变化，旧记录被删除，新记录插入
   - 如果 skill 调用未变化，新记录覆盖旧记录

---

## 10. 调用轨迹查询

如果需要查询某个 turn 中 skill 的调用轨迹：

```sql
SELECT 
    skill_name,
    sequence_order,
    invoked_at,
    duration_ms,
    is_error
FROM dashboard_skill_invocation
WHERE session_id = ? AND turn_id = ?
ORDER BY sequence_order ASC;
```

返回结果示例：
```
| skill_name | sequence_order | invoked_at          | duration_ms | is_error |
|------------|----------------|---------------------|-------------|----------|
| Skill A    | 1              | 2026-04-23 10:00:00 | 20          | 0        |
| Skill B    | 2              | 2026-04-23 10:00:01 | 15          | 0        |
| Skill C    | 3              | 2026-04-23 10:00:02 | 0           | 1        |
```

---

## 11. 实施清单

- [ ] 创建 `SkillChainDetector` 类
- [ ] 增强 `SkillInvocation` record（添加 sequenceOrder 等字段）
- [ ] 修改 `TranscriptParser.parse()` 调用新检测器
- [ ] 更新 `DataIngestionService.convertToSkillInvocations()`
- [ ] 执行数据库迁移（添加 sequence_order 字段）
- [ ] 编写单元测试
- [ ] 运行集成测试验证
- [ ] 更新 API 文档（如有需要）

---

## 12. 参考资料

- OpenClaw 源码路径：`G:\Workplace\github\openclaw`
- Transcript 示例文件：`test/session-transcript/a793d94b.../agents/main/sessions/*.jsonl`
- 现有解析器：`src/main/java/com/company/clawboard/parser/TranscriptParser.java`
- 数据摄入服务：`src/main/java/com/company/clawboard/service/DataIngestionService.java`
