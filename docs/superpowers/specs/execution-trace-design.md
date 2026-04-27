# 执行链路追踪功能设计文档

## 1. 概述

### 1.1 背景

当前 ClawBoard 系统已实现会话轮次（turn）级别的统计和查询，但缺乏对单个轮次内部执行链路的详细追踪能力。前端需要展示每个轮次的完整执行流程，包括：
- 用户输入
- 工具调用（exec、memory_search 等）
- 工具执行结果
- AI 回复内容

### 1.2 目标

提供执行链路追踪功能，支持：
1. **数据层**：新增独立表存储执行链路节点
2. **解析层**：在 transcript 解析阶段提取链路信息
3. **API 层**：提供按 turn_id 查询完整链路的接口
4. **展示层**：支持前端可视化展示执行流程

### 1.3 设计原则

- **非侵入式**：不修改任何现有表结构
- **独立性**：新功能完全独立，不影响现有业务
- **可扩展性**：支持未来添加性能分析、链路追踪等功能

---

## 2. 数据库设计

### 2.1 新增表：`dashboard_execution_trace`

```sql
CREATE TABLE dashboard_execution_trace (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '链路节点ID',
    scan_id         BIGINT        COMMENT '关联扫描ID（可选，用于追溯数据来源）',
    turn_id         BIGINT        NOT NULL COMMENT '关联轮次ID（外键指向 dashboard_conversation_turn.id）',
    node_index      INT           NOT NULL COMMENT '节点序号（从0开始，表示在链路中的顺序）',
    node_type       VARCHAR(20)   NOT NULL COMMENT '节点类型：user_input/tool_call/tool_result/reply',
    tool_name       VARCHAR(100)  COMMENT '工具名称（仅 tool_call 和 tool_result 节点有值）',
    tool_call_id    VARCHAR(100)  COMMENT '工具调用ID（仅 tool_call 和 tool_result 节点有值）',
    content         TEXT          COMMENT '内容（user_input/reply 的文本内容，tool_result 的执行结果）',
    timestamp_ms    BIGINT        COMMENT '执行时间戳（毫秒，来自 transcript 的 timestamp 字段）',
    duration_ms     INT           COMMENT '耗时（毫秒，计算相邻节点的时间差）',
    success         TINYINT(4)    COMMENT '是否成功（仅 tool_result 节点有值，1=成功，0=失败）',
    error_message   TEXT          COMMENT '错误信息（仅 tool_result 节点且失败时有值）',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_turn_id (turn_id) COMMENT '按轮次查询索引',
    INDEX idx_scan_id (scan_id) COMMENT '按扫描ID查询索引',
    INDEX idx_node_type (node_type) COMMENT '按节点类型统计索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行链路追踪表';
```

### 2.2 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | BIGINT | ✅ | 主键，自增 |
| `scan_id` | BIGINT | ❌ | 关联扫描ID，用于追溯数据来源 |
| `turn_id` | BIGINT | ✅ | 关联轮次ID，外键指向 `dashboard_conversation_turn.id` |
| `node_index` | INT | ✅ | 节点序号，从 0 开始递增 |
| `node_type` | VARCHAR(20) | ✅ | 节点类型枚举：`user_input`、`tool_call`、`tool_result`、`reply` |
| `tool_name` | VARCHAR(100) | ❌ | 工具名称，如 `exec`、`memory_search` |
| `tool_call_id` | VARCHAR(100) | ❌ | 工具调用ID，用于匹配 call 和 result |
| `content` | TEXT | ❌ | 内容字段，根据节点类型存储不同内容 |
| `timestamp_ms` | BIGINT | ✅ | 原始时间戳（毫秒） |
| `duration_ms` | INT | ❌ | 耗时（毫秒），通过计算相邻节点时间差得到 |
| `success` | TINYINT(4) | ❌ | 是否成功，1=成功，0=失败，NULL=不适用 |
| `error_message` | TEXT | ❌ | 错误信息，仅在 tool_result 且失败时填充 |
| `created_at` | DATETIME | ✅ | 记录创建时间 |

### 2.3 节点类型枚举

| node_type | 说明 | content 字段含义 | 其他字段 |
|-----------|------|------------------|----------|
| `user_input` | 用户输入 | 用户输入的文本内容 | - |
| `tool_call` | 工具调用 | NULL | tool_name, tool_call_id |
| `tool_result` | 工具执行结果 | 工具返回的结果内容 | tool_name, tool_call_id, success, error_message |
| `reply` | AI 回复 | AI 回复用户的文本内容 | - |

---

## 3. 解析逻辑设计

### 3.1 会话轮次识别规则

根据 transcript 文件的消息序列，按以下规则划分轮次：

```
轮次边界：以 [user] 消息为起点，到下一个 [user] 消息之前或文件结束为终点

示例：
[user] → 轮次1开始
  [assistant + toolCall] → exec
  [toolResult] → exec 结果
  [assistant + toolCall] → memory_search
  [toolResult] → memory_search 结果
  [assistant + text] → 回复
[user] → 轮次2开始
  ...
```

### 3.2 解析流程

#### 3.2.1 整体流程

```java
// 在 DataIngestionService.ingestParsedTranscript() 中
// Phase 1: 插入 conversation_turn
List<ConversationTurn> turns = insertConversationTurns(parsed.turns(), sessionId, scanId);

// Phase 1.5: 解析并插入执行链路（新增）
for (int i = 0; i < turns.size(); i++) {
    ConversationTurn turn = turns.get(i);
    List<MessageRecord> messages = parsed.messagesForTurn(i);
    
    List<ExecutionTraceNode> traces = parseExecutionTrace(
        messages, 
        turn.getId(), 
        scanId
    );
    
    if (!traces.isEmpty()) {
        executionTraceMapper.batchInsertIgnore(traces);
        log.debug("Inserted {} trace nodes for turn {}", traces.size(), turn.getId());
    }
}

// Phase 2: 插入 message
insertMessages(parsed.messages(), turns, scanId);
```

#### 3.2.2 节点解析逻辑

```java
/**
 * 解析单个轮次的执行链路
 */
public List<ExecutionTraceNode> parseExecutionTrace(
    List<MessageRecord> messages, 
    Long turnId,
    Long scanId
) {
    List<ExecutionTraceNode> nodes = new ArrayList<>();
    int nodeIndex = 0;
    
    for (MessageRecord msg : messages) {
        ExecutionTraceNode node = null;
        
        switch (msg.role()) {
            case "user":
                // 用户输入节点
                String userInput = extractTextContent(msg);
                node = ExecutionTraceNode.builder()
                    .scanId(scanId)
                    .turnId(turnId)
                    .nodeIndex(nodeIndex++)
                    .nodeType("user_input")
                    .content(userInput)
                    .timestampMs(msg.timestamp())
                    .build();
                break;
                
            case "assistant":
                if (isToolCall(msg)) {
                    // 工具调用节点
                    node = ExecutionTraceNode.builder()
                        .scanId(scanId)
                        .turnId(turnId)
                        .nodeIndex(nodeIndex++)
                        .nodeType("tool_call")
                        .toolName(msg.toolName())
                        .toolCallId(msg.toolCallId())
                        .timestampMs(msg.timestamp())
                        .build();
                } else if (isTextReply(msg)) {
                    // AI 回复节点
                    String replyContent = extractTextContent(msg);
                    node = ExecutionTraceNode.builder()
                        .scanId(scanId)
                        .turnId(turnId)
                        .nodeIndex(nodeIndex++)
                        .nodeType("reply")
                        .content(replyContent)
                        .timestampMs(msg.timestamp())
                        .build();
                }
                break;
                
            case "toolResult":
                // 工具结果节点
                String resultContent = extractTextContent(msg);
                boolean success = !msg.isError();
                node = ExecutionTraceNode.builder()
                    .scanId(scanId)
                    .turnId(turnId)
                    .nodeIndex(nodeIndex++)
                    .nodeType("tool_result")
                    .toolName(msg.toolName())
                    .toolCallId(msg.toolCallId())
                    .content(resultContent)
                    .timestampMs(msg.timestamp())
                    .success(success)
                    .errorMessage(success ? null : msg.errorMessage())
                    .build();
                break;
        }
        
        if (node != null) {
            nodes.add(node);
        }
    }
    
    // 计算耗时（相邻节点时间差）
    calculateDurations(nodes);
    
    return nodes;
}

/**
 * 计算节点耗时
 */
private void calculateDurations(List<ExecutionTraceNode> nodes) {
    for (int i = 0; i < nodes.size() - 1; i++) {
        ExecutionTraceNode current = nodes.get(i);
        ExecutionTraceNode next = nodes.get(i + 1);
        
        long duration = next.getTimestampMs() - current.getTimestampMs();
        current.setDurationMs((int) duration);
    }
    // 最后一个节点的 duration 为 null
}
```

### 3.3 辅助方法

```java
/**
 * 判断是否为工具调用消息
 */
private boolean isToolCall(MessageRecord msg) {
    return msg.role().equals("assistant") && msg.toolName() != null;
}

/**
 * 判断是否为文本回复消息
 */
private boolean isTextReply(MessageRecord msg) {
    return msg.role().equals("assistant") && msg.toolName() == null && hasTextContent(msg);
}

/**
 * 提取文本内容
 */
private String extractTextContent(MessageRecord msg) {
    // 从 JSON content 中提取 text 字段
    // 如果 content 是字符串，直接返回
    // 如果 content 是对象，提取 text 字段
    // 如果无法提取，返回空字符串
}

/**
 * 判断是否有文本内容
 */
private boolean hasTextContent(MessageRecord msg) {
    String content = extractTextContent(msg);
    return content != null && !content.trim().isEmpty();
}
```

---

## 4. API 接口设计

### 4.1 查询执行链路

**Endpoint:** `GET /api/v1/turns/{turnId}/trace`

**Path Parameters:**
- `turnId` (Long, required): 轮次ID

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "turnId": 12345,
    "nodes": [
      {
        "nodeIndex": 0,
        "nodeType": "user_input",
        "content": "帮我生成一个国寿龙虾介绍文档",
        "timestampMs": 1775715498443,
        "durationMs": 2,
        "toolName": null,
        "toolCallId": null,
        "success": null,
        "errorMessage": null
      },
      {
        "nodeIndex": 1,
        "nodeType": "tool_call",
        "content": null,
        "timestampMs": 1775715498445,
        "durationMs": 3408,
        "toolName": "exec",
        "toolCallId": "call_99e0c55790b94b00940082b6",
        "success": null,
        "errorMessage": null
      },
      {
        "nodeIndex": 2,
        "nodeType": "tool_result",
        "content": "WARNING: Retrying (Retry...)",
        "timestampMs": 1775715501853,
        "durationMs": 12,
        "toolName": "exec",
        "toolCallId": "call_99e0c55790b94b00940082b6",
        "success": true,
        "errorMessage": null
      },
      {
        "nodeIndex": 3,
        "nodeType": "reply",
        "content": "很抱歉，当前环境无法直接生成docx文件...",
        "timestampMs": 1775715428734,
        "durationMs": null,
        "toolName": null,
        "toolCallId": null,
        "success": null,
        "errorMessage": null
      }
    ]
  }
}
```

**Response Fields:**
- `turnId`: 轮次ID
- `nodes`: 链路节点列表，按 `nodeIndex` 排序
  - `nodeIndex`: 节点序号
  - `nodeType`: 节点类型
  - `content`: 内容
  - `timestampMs`: 时间戳（毫秒）
  - `durationMs`: 耗时（毫秒）
  - `toolName`: 工具名称
  - `toolCallId`: 工具调用ID
  - `success`: 是否成功
  - `errorMessage`: 错误信息

**Error Responses:**
- `404 Not Found`: turnId 不存在
- `500 Internal Server Error`: 服务器内部错误

---

## 5. 代码结构设计

### 5.1 Entity 层

**文件位置:** `src/main/java/com/company/clawboard/entity/DashboardExecutionTrace.java`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardExecutionTrace {
    private Long id;
    private Long scanId;
    private Long turnId;
    private Integer nodeIndex;
    private String nodeType;
    private String toolName;
    private String toolCallId;
    private String content;
    private Long timestampMs;
    private Integer durationMs;
    private Boolean success;
    private String errorMessage;
    private LocalDateTime createdAt;
}
```

### 5.2 Mapper 层

**接口文件:** `src/main/java/com/company/clawboard/mapper/ExecutionTraceMapper.java`

```java
@Mapper
public interface ExecutionTraceMapper {
    
    /**
     * 批量插入执行链路节点（忽略重复）
     */
    int batchInsertIgnore(@Param("nodes") List<DashboardExecutionTrace> nodes);
    
    /**
     * 根据 turnId 查询执行链路
     */
    List<DashboardExecutionTrace> selectByTurnId(@Param("turnId") Long turnId);
    
    /**
     * 根据 scanId 删除执行链路（用于重置扫描）
     */
    int deleteByScanId(@Param("scanId") Long scanId);
}
```

**XML 文件:** `src/main/resources/mapper/ExecutionTraceMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.company.clawboard.mapper.ExecutionTraceMapper">

    <!-- 批量插入（忽略重复） -->
    <insert id="batchInsertIgnore">
        INSERT IGNORE INTO dashboard_execution_trace 
        (scan_id, turn_id, node_index, node_type, tool_name, tool_call_id, 
         content, timestamp_ms, duration_ms, success, error_message)
        VALUES
        <foreach collection="nodes" item="node" separator=",">
            (#{node.scanId}, #{node.turnId}, #{node.nodeIndex}, #{node.nodeType},
             #{node.toolName}, #{node.toolCallId}, #{node.content},
             #{node.timestampMs}, #{node.durationMs}, #{node.success}, #{node.errorMessage})
        </foreach>
    </insert>

    <!-- 根据 turnId 查询 -->
    <select id="selectByTurnId" resultType="com.company.clawboard.entity.DashboardExecutionTrace">
        SELECT * FROM dashboard_execution_trace
        WHERE turn_id = #{turnId}
        ORDER BY node_index ASC
    </select>

    <!-- 根据 scanId 删除 -->
    <delete id="deleteByScanId">
        DELETE FROM dashboard_execution_trace
        WHERE scan_id = #{scanId}
    </delete>

</mapper>
```

### 5.3 Service 层

**文件位置:** `src/main/java/com/company/clawboard/service/ExecutionTraceService.java`

```java
@Service
@Slf4j
public class ExecutionTraceService {
    
    @Autowired
    private ExecutionTraceMapper executionTraceMapper;
    
    @Autowired
    private TranscriptParser transcriptParser;
    
    /**
     * 获取指定轮次的执行链路
     */
    public ExecutionTraceResponse getTraceByTurnId(Long turnId) {
        List<DashboardExecutionTrace> nodes = executionTraceMapper.selectByTurnId(turnId);
        
        if (nodes == null || nodes.isEmpty()) {
            throw new BusinessException("未找到轮次ID为 " + turnId + " 的执行链路");
        }
        
        return ExecutionTraceResponse.builder()
            .turnId(turnId)
            .nodes(nodes.stream()
                .map(this::convertToNodeVO)
                .collect(Collectors.toList()))
            .build();
    }
    
    /**
     * 转换 Entity 为 VO
     */
    private TraceNodeVO convertToNodeVO(DashboardExecutionTrace entity) {
        return TraceNodeVO.builder()
            .nodeIndex(entity.getNodeIndex())
            .nodeType(entity.getNodeType())
            .content(entity.getContent())
            .timestampMs(entity.getTimestampMs())
            .durationMs(entity.getDurationMs())
            .toolName(entity.getToolName())
            .toolCallId(entity.getToolCallId())
            .success(entity.getSuccess())
            .errorMessage(entity.getErrorMessage())
            .build();
    }
}
```

### 5.4 Controller 层

**文件位置:** `src/main/java/com/company/clawboard/controller/ExecutionTraceController.java`

```java
@RestController
@RequestMapping("/api/v1/turns")
@Slf4j
public class ExecutionTraceController {
    
    @Autowired
    private ExecutionTraceService executionTraceService;
    
    /**
     * 查询指定轮次的执行链路
     */
    @GetMapping("/{turnId}/trace")
    public ApiResponse<ExecutionTraceResponse> getTrace(@PathVariable Long turnId) {
        log.info("查询执行链路: turnId={}", turnId);
        ExecutionTraceResponse response = executionTraceService.getTraceByTurnId(turnId);
        return ApiResponse.success(response);
    }
}
```

### 5.5 DTO/VO 层

**请求 DTO:** 无（使用 Path Variable）

**响应 VO:** `src/main/java/com/company/clawboard/dto/ExecutionTraceResponse.java`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionTraceResponse {
    private Long turnId;
    private List<TraceNodeVO> nodes;
}
```

**节点 VO:** `src/main/java/com/company/clawboard/dto/TraceNodeVO.java`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceNodeVO {
    private Integer nodeIndex;
    private String nodeType;
    private String content;
    private Long timestampMs;
    private Integer durationMs;
    private String toolName;
    private String toolCallId;
    private Boolean success;
    private String errorMessage;
}
```

---

## 6. 数据写入时机

### 6.1 集成点

在 `DataIngestionService.ingestParsedTranscript()` 方法中，Phase 1 插入 conversation_turn 之后，Phase 2 插入 message 之前：

```java
@Transactional
public IngestionResult ingestParsedTranscript(ParsedTranscript parsed, Long scanId) {
    // Phase 1: 插入会话轮次
    List<ConversationTurn> turns = insertConversationTurns(parsed.turns(), sessionId, scanId);
    
    // Phase 1.5: 解析并插入执行链路（新增）
    for (int i = 0; i < turns.size(); i++) {
        ConversationTurn turn = turns.get(i);
        List<MessageRecord> messages = parsed.messagesForTurn(i);
        
        List<ExecutionTraceNode> traces = transcriptParser.parseExecutionTrace(
            messages, 
            turn.getId(), 
            scanId
        );
        
        if (!traces.isEmpty()) {
            executionTraceMapper.batchInsertIgnore(traces);
            log.debug("Inserted {} trace nodes for turn {}", traces.size(), turn.getId());
        }
    }
    
    // Phase 2: 插入消息
    insertMessages(parsed.messages(), turns, scanId);
    
    // Phase 3: 插入技能调用
    insertSkillInvocations(parsed.skillInvocations(), turns, scanId);
    
    // ...
}
```

### 6.2 幂等性保证

使用 `INSERT IGNORE` 语句，基于唯一约束避免重复插入。由于 `dashboard_execution_trace` 表没有显式唯一约束，需要在应用层确保：
- 每次扫描前清空该 scan_id 的数据
- 或者在 `reset-database.sql` 中添加清理逻辑

---

## 7. 前端展示建议

### 7.1 可视化布局

参考用户提供的设计图，可以采用横向流程图形式：

```
┌──────────────┐      ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│ ● 用户输入    │─────→│ ● exec 工具  │─────→│ ● 工具结果    │─────→│ ● 回复用户    │
│ 耗时: -      │ 2ms  │ 耗时: 3408ms │ 12ms │ 耗时: -      │      │              │
└──────────────┘      └──────────────┘      └──────────────┘      └──────────────┘
```

### 7.2 交互设计

1. **点击节点**：展开显示详细内容（content 字段）
2. **悬停提示**：显示时间戳、耗时等信息
3. **错误高亮**：如果 `success=false`，用红色边框标识
4. **工具名称标签**：在 tool_call 和 tool_result 节点上显示工具名称

### 7.3 颜色规范

- **用户输入**：蓝色 (#1890ff)
- **工具调用**：橙色 (#fa8c16)
- **工具结果（成功）**：绿色 (#52c41a)
- **工具结果（失败）**：红色 (#f5222d)
- **AI 回复**：紫色 (#722ed1)

---

## 8. 测试计划

### 8.1 单元测试

1. **TranscriptParserTest**
   - 测试 `parseExecutionTrace()` 方法
   - 验证节点类型识别正确
   - 验证耗时计算正确
   - 验证错误信息提取正确

2. **ExecutionTraceServiceTest**
   - 测试 `getTraceByTurnId()` 方法
   - 验证数据转换正确
   - 测试异常场景（turnId 不存在）

### 8.2 集成测试

1. **DataIngestionServiceIntegrationTest**
   - 执行完整扫描流程
   - 验证 `dashboard_execution_trace` 表数据正确插入
   - 验证节点数量与预期一致
   - 验证节点顺序正确

2. **ExecutionTraceControllerTest**
   - 测试 `GET /api/v1/turns/{turnId}/trace` 接口
   - 验证响应格式正确
   - 测试 404 场景

### 8.3 端到端测试

1. 重置数据库
2. 执行扫描
3. 查询某个 turn 的执行链路
4. 验证返回数据与 transcript 文件一致

---

## 9. 性能考虑

### 9.1 索引优化

已创建的索引：
- `idx_turn_id`: 支持按 turn_id 快速查询
- `idx_scan_id`: 支持按 scan_id 批量删除
- `idx_node_type`: 支持按节点类型统计

### 9.2 数据量评估

假设：
- 平均每个 turn 有 6 个节点
- 每天新增 1000 个 turn
- 数据保留 90 天

总数据量 ≈ 6 × 1000 × 90 = 540,000 条记录

这个数据量对于 MySQL 来说非常小，无需特殊优化。

### 9.3 查询性能

典型查询：
```sql
SELECT * FROM dashboard_execution_trace
WHERE turn_id = ?
ORDER BY node_index ASC;
```

由于有 `idx_turn_id` 索引，查询性能为 O(log n)，即使百万级数据也能在毫秒级返回。

---

## 10. 后续扩展

### 10.1 可能的增强方向

1. **性能分析**：统计各工具的平均耗时，识别慢工具
2. **错误追踪**：聚合所有失败的 tool_result，分析常见错误
3. **链路拓扑**：可视化工具调用关系图
4. **实时监控**：实时展示正在执行的链路

### 10.2 与现有功能的集成

1. **与 `chainSummary` 字段的关系**：
   - 当前 `dashboard_conversation_turn.chainSummary` 为 NULL
   - 可以考虑将链路摘要（如节点数量、总耗时）回填到此字段
   - 但这不是必须的，可以保持独立

2. **与问题检测的集成**：
   - 当前 `hasMessageError()` 检测消息级别错误
   - 可以扩展到检测链路级别问题（如工具调用超时）

---

## 11. 实施步骤

### Phase 1: 数据库迁移
1. 创建 Flyway 迁移脚本 `V4__create_execution_trace_table.sql`
2. 执行迁移，验证表结构

### Phase 2: 基础代码开发
1. 创建 Entity: `DashboardExecutionTrace.java`
2. 创建 Mapper: `ExecutionTraceMapper.java` + XML
3. 创建 DTO/VO: `ExecutionTraceResponse.java`, `TraceNodeVO.java`

### Phase 3: 解析逻辑实现
1. 在 `TranscriptParser` 中实现 `parseExecutionTrace()` 方法
2. 在 `DataIngestionService` 中集成调用

### Phase 4: API 开发
1. 创建 Service: `ExecutionTraceService.java`
2. 创建 Controller: `ExecutionTraceController.java`

### Phase 5: 测试与验证
1. 编写单元测试
2. 编写集成测试
3. 执行端到端测试
4. 验证数据正确性

### Phase 6: 文档更新
1. 更新 API 接口文档
2. 更新数据库设计文档

---

## 12. 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| transcript 格式变化 | 解析失败 | 添加容错逻辑，记录警告日志 |
| 数据量超预期增长 | 性能下降 | 监控数据增长，必要时添加分区 |
| 节点顺序错误 | 前端展示混乱 | 严格依赖 `node_index` 排序 |
| 时间戳不准确 | 耗时计算错误 | 使用 transcript 原始时间戳，不做修正 |

---

## 13. 总结

本设计方案通过新增独立的 `dashboard_execution_trace` 表，实现了执行链路追踪功能，具有以下特点：

✅ **非侵入式**：不修改任何现有表结构  
✅ **独立性**：新功能完全独立，易于维护  
✅ **可扩展性**：支持未来添加性能分析、链路追踪等功能  
✅ **高性能**：合理的索引设计，查询效率高  
✅ **易用性**：简洁的 API 接口，方便前端集成  

下一步将按照实施步骤逐步开发，确保功能质量和稳定性。
