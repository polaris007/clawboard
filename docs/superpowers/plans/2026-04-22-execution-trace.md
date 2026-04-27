# 执行链路追踪功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现执行链路追踪功能，支持按轮次查询完整的执行流程（用户输入→工具调用→工具结果→AI回复）

**Architecture:** 新增独立的 `dashboard_execution_trace` 表存储链路节点，在 transcript 解析阶段提取节点信息并插入数据库，提供 REST API 查询接口

**Tech Stack:** MySQL 5.7, MyBatis, Spring Boot, Java 17, Lombok

---

## 文件映射总览

### 新增文件
- `src/main/resources/db/migration/V5__create_execution_trace_table.sql` - 数据库迁移脚本
- `src/main/java/com/company/clawboard/entity/DashboardExecutionTrace.java` - Entity
- `src/main/java/com/company/clawboard/mapper/ExecutionTraceMapper.java` - Mapper 接口
- `src/main/resources/mapper/ExecutionTraceMapper.xml` - MyBatis XML
- `src/main/java/com/company/clawboard/dto/ExecutionTraceResponse.java` - 响应 DTO
- `src/main/java/com/company/clawboard/dto/TraceNodeVO.java` - 节点 VO
- `src/main/java/com/company/clawboard/service/ExecutionTraceService.java` - Service
- `src/main/java/com/company/clawboard/controller/ExecutionTraceController.java` - Controller
- `src/test/java/com/company/clawboard/parser/TranscriptParserTraceTest.java` - 单元测试
- `src/test/java/com/company/clawboard/service/ExecutionTraceServiceTest.java` - 单元测试
- `src/test/java/com/company/clawboard/controller/ExecutionTraceControllerTest.java` - 集成测试

### 修改文件
- `src/main/java/com/company/clawboard/parser/TranscriptParser.java` - 添加 `parseExecutionTrace()` 方法
- `src/main/java/com/company/clawboard/service/DataIngestionService.java` - 集成链路数据插入逻辑
- `scripts/reset-database.sql` - 添加清理 execution_trace 表的逻辑

---

## Task 1: 数据库迁移脚本

**Files:**
- Create: `src/main/resources/db/migration/V5__create_execution_trace_table.sql`

- [ ] **Step 1: 创建 Flyway 迁移脚本**

```sql
-- V5__create_execution_trace_table.sql
-- 创建执行链路追踪表

CREATE TABLE IF NOT EXISTS dashboard_execution_trace (
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

- [ ] **Step 2: 执行迁移验证表结构**

```bash
# 重启应用触发 Flyway 迁移
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或者手动执行 SQL
Get-Content src/main/resources/db/migration/V5__create_execution_trace_table.sql | mysql -u clawboard -pClqc@1234 -h 127.0.0.1 -P 3306 clawboard

# 验证表已创建
mysql -u clawboard -pClqc@1234 -h 127.0.0.1 -P 3306 clawboard -e "DESCRIBE dashboard_execution_trace;"
```

Expected: 显示表结构，包含所有字段和索引

- [ ] **Step 3: 更新 reset-database.sql 添加清理逻辑**

Modify: `scripts/reset-database.sql`

在现有清理逻辑中添加：

```sql
-- 清理执行链路追踪表
DELETE FROM dashboard_execution_trace;
ALTER TABLE dashboard_execution_trace AUTO_INCREMENT = 1;
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/db/migration/V5__create_execution_trace_table.sql scripts/reset-database.sql
git commit -m "feat: add execution trace table migration"
```

---

## Task 2: Entity 层

**Files:**
- Create: `src/main/java/com/company/clawboard/entity/DashboardExecutionTrace.java`

- [ ] **Step 1: 创建 Entity 类**

```java
package com.company.clawboard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 执行链路追踪实体
 */
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

- [ ] **Step 2: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/entity/DashboardExecutionTrace.java
git commit -m "feat: add DashboardExecutionTrace entity"
```

---

## Task 3: Mapper 层

**Files:**
- Create: `src/main/java/com/company/clawboard/mapper/ExecutionTraceMapper.java`
- Create: `src/main/resources/mapper/ExecutionTraceMapper.xml`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardExecutionTrace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执行链路追踪 Mapper
 */
@Mapper
public interface ExecutionTraceMapper {
    
    /**
     * 批量插入执行链路节点（忽略重复）
     * @param nodes 节点列表
     * @return 插入的记录数
     */
    int batchInsertIgnore(@Param("nodes") List<DashboardExecutionTrace> nodes);
    
    /**
     * 根据 turnId 查询执行链路
     * @param turnId 轮次ID
     * @return 节点列表（按 node_index 排序）
     */
    List<DashboardExecutionTrace> selectByTurnId(@Param("turnId") Long turnId);
    
    /**
     * 根据 scanId 删除执行链路（用于重置扫描）
     * @param scanId 扫描ID
     * @return 删除的记录数
     */
    int deleteByScanId(@Param("scanId") Long scanId);
}
```

- [ ] **Step 2: 创建 MyBatis XML**

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

- [ ] **Step 3: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/mapper/ExecutionTraceMapper.java src/main/resources/mapper/ExecutionTraceMapper.xml
git commit -m "feat: add ExecutionTraceMapper interface and XML"
```

---

## Task 4: DTO/VO 层

**Files:**
- Create: `src/main/java/com/company/clawboard/dto/ExecutionTraceResponse.java`
- Create: `src/main/java/com/company/clawboard/dto/TraceNodeVO.java`

- [ ] **Step 1: 创建响应 DTO**

```java
package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行链路响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionTraceResponse {
    private Long turnId;
    private List<TraceNodeVO> nodes;
}
```

- [ ] **Step 2: 创建节点 VO**

```java
package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 链路节点 VO
 */
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

- [ ] **Step 3: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/dto/ExecutionTraceResponse.java src/main/java/com/company/clawboard/dto/TraceNodeVO.java
git commit -m "feat: add ExecutionTraceResponse and TraceNodeVO DTOs"
```

---

## Task 5: TranscriptParser 解析逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/parser/TranscriptParser.java`

- [ ] **Step 1: 查看现有代码结构**

Read: `src/main/java/com/company/clawboard/parser/TranscriptParser.java` (完整文件)

确认现有的 ParsedTranscript record 结构和辅助方法位置

- [ ] **Step 2: 添加 parseExecutionTrace 公共方法**

在 TranscriptParser 类中添加以下方法（放在类的末尾，私有辅助方法之前）：

```java
    /**
     * 解析单个轮次的执行链路
     * @param messages 该轮次的消息列表
     * @param turnId 轮次ID
     * @param scanId 扫描ID
     * @return 执行链路节点列表
     */
    public List<com.company.clawboard.entity.DashboardExecutionTrace> parseExecutionTrace(
        List<MessageRecord> messages, 
        Long turnId,
        Long scanId
    ) {
        List<com.company.clawboard.entity.DashboardExecutionTrace> nodes = new ArrayList<>();
        int nodeIndex = 0;
        
        for (MessageRecord msg : messages) {
            com.company.clawboard.entity.DashboardExecutionTrace node = null;
            
            switch (msg.role()) {
                case "user":
                    // 用户输入节点
                    String userInput = extractTextContent(msg);
                    node = com.company.clawboard.entity.DashboardExecutionTrace.builder()
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
                        node = com.company.clawboard.entity.DashboardExecutionTrace.builder()
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
                        node = com.company.clawboard.entity.DashboardExecutionTrace.builder()
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
                    node = com.company.clawboard.entity.DashboardExecutionTrace.builder()
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
```

- [ ] **Step 3: 添加辅助方法**

在 `parseExecutionTrace` 方法之后添加以下私有辅助方法：

```java
    /**
     * 判断是否为工具调用消息
     */
    private boolean isToolCall(MessageRecord msg) {
        return "assistant".equals(msg.role()) && msg.toolName() != null;
    }

    /**
     * 判断是否为文本回复消息
     */
    private boolean isTextReply(MessageRecord msg) {
        return "assistant".equals(msg.role()) && msg.toolName() == null && hasTextContent(msg);
    }

    /**
     * 提取文本内容
     */
    private String extractTextContent(MessageRecord msg) {
        // 从 JSON content 中提取 text 字段
        // 如果 content 是字符串，直接返回
        // 如果 content 是对象，提取 text 字段
        // 如果无法提取，返回空字符串
        if (msg.content() == null) {
            return "";
        }
        
        // 如果 content 已经是字符串，直接返回
        if (msg.content() instanceof String) {
            return (String) msg.content();
        }
        
        // 如果 content 是 Map，尝试提取 text 字段
        if (msg.content() instanceof Map) {
            Object text = ((Map<?, ?>) msg.content()).get("text");
            return text != null ? text.toString() : "";
        }
        
        return msg.content().toString();
    }

    /**
     * 判断是否有文本内容
     */
    private boolean hasTextContent(MessageRecord msg) {
        String content = extractTextContent(msg);
        return content != null && !content.trim().isEmpty();
    }

    /**
     * 计算节点耗时
     */
    private void calculateDurations(List<com.company.clawboard.entity.DashboardExecutionTrace> nodes) {
        for (int i = 0; i < nodes.size() - 1; i++) {
            com.company.clawboard.entity.DashboardExecutionTrace current = nodes.get(i);
            com.company.clawboard.entity.DashboardExecutionTrace next = nodes.get(i + 1);
            
            long duration = next.getTimestampMs() - current.getTimestampMs();
            current.setDurationMs((int) duration);
        }
        // 最后一个节点的 duration 为 null
    }
```

- [ ] **Step 4: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/parser/TranscriptParser.java
git commit -m "feat: add parseExecutionTrace method to TranscriptParser"
```

---

## Task 6: DataIngestionService 集成

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 查看现有代码结构**

Read: `src/main/java/com/company/clawboard/service/DataIngestionService.java` (lines 48-120)

找到 Phase 1 和 Phase 2 之间的位置

- [ ] **Step 2: 注入 ExecutionTraceMapper**

在 DataIngestionService 类的字段声明部分添加：

```java
    private final MessageMapper messageMapper;
    private final ConversationTurnMapper turnMapper;
    private final SkillInvocationMapper skillMapper;
    private final TranscriptIssueMapper issueMapper;
    private final SessionSummaryMapper sessionSummaryMapper;
    private final SystemMessageFilter systemMessageFilter;
    private final com.company.clawboard.mapper.ExecutionTraceMapper executionTraceMapper;  // NEW
    private final com.company.clawboard.parser.TranscriptParser transcriptParser;  // NEW
```

- [ ] **Step 3: 在 Phase 1 后添加链路数据插入逻辑**

在 `ingestParsedTranscript` 方法中，Phase 1 结束后（loadTurnIds 之后），Phase 2 开始前添加：

```java
        // Phase 1.5: 解析并插入执行链路（新增）
        for (int i = 0; i < turns.size(); i++) {
            DashboardConversationTurn turn = turns.get(i);
            
            // 获取该轮次对应的消息列表
            List<MessageRecord> turnMessages = getMessagesForTurn(parsed.messages(), parsed.messageIdToTurnIndex(), i);
            
            if (!turnMessages.isEmpty()) {
                List<com.company.clawboard.entity.DashboardExecutionTrace> traces = transcriptParser.parseExecutionTrace(
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
```

- [ ] **Step 4: 添加辅助方法 getMessagesForTurn**

在 DataIngestionService 类的末尾添加：

```java
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
```

- [ ] **Step 5: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "feat: integrate execution trace parsing in DataIngestionService"
```

---

## Task 7: Service 层

**Files:**
- Create: `src/main/java/com/company/clawboard/service/ExecutionTraceService.java`

- [ ] **Step 1: 创建 Service 类**

```java
package com.company.clawboard.service;

import com.company.clawboard.dto.ExecutionTraceResponse;
import com.company.clawboard.dto.TraceNodeVO;
import com.company.clawboard.entity.DashboardExecutionTrace;
import com.company.clawboard.mapper.ExecutionTraceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行链路追踪服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExecutionTraceService {
    
    private final ExecutionTraceMapper executionTraceMapper;
    
    /**
     * 获取指定轮次的执行链路
     * @param turnId 轮次ID
     * @return 执行链路响应
     */
    public ExecutionTraceResponse getTraceByTurnId(Long turnId) {
        List<DashboardExecutionTrace> nodes = executionTraceMapper.selectByTurnId(turnId);
        
        if (nodes == null || nodes.isEmpty()) {
            throw new RuntimeException("未找到轮次ID为 " + turnId + " 的执行链路");
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

- [ ] **Step 2: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/service/ExecutionTraceService.java
git commit -m "feat: add ExecutionTraceService"
```

---

## Task 8: Controller 层

**Files:**
- Create: `src/main/java/com/company/clawboard/controller/ExecutionTraceController.java`

- [ ] **Step 1: 查看现有 Controller 风格**

Read: `src/main/java/com/company/clawboard/controller/DashboardController.java` (前50行)

确认 ApiResponse 的使用方式和注解风格

- [ ] **Step 2: 创建 Controller 类**

```java
package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.ExecutionTraceResponse;
import com.company.clawboard.service.ExecutionTraceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 执行链路追踪控制器
 */
@RestController
@RequestMapping("/api/v1/turns")
@RequiredArgsConstructor
@Slf4j
public class ExecutionTraceController {
    
    private final ExecutionTraceService executionTraceService;
    
    /**
     * 查询指定轮次的执行链路
     * @param turnId 轮次ID
     * @return 执行链路响应
     */
    @GetMapping("/{turnId}/trace")
    public ApiResponse<ExecutionTraceResponse> getTrace(@PathVariable Long turnId) {
        log.info("查询执行链路: turnId={}", turnId);
        ExecutionTraceResponse response = executionTraceService.getTraceByTurnId(turnId);
        return ApiResponse.success(response);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/controller/ExecutionTraceController.java
git commit -m "feat: add ExecutionTraceController with GET /{turnId}/trace endpoint"
```

---

## Task 9: 单元测试 - TranscriptParser

**Files:**
- Create: `src/test/java/com/company/clawboard/parser/TranscriptParserTraceTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.company.clawboard.parser;

import com.company.clawboard.entity.DashboardExecutionTrace;
import com.company.clawboard.parser.model.MessageRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TranscriptParserTraceTest {

    @Autowired
    private TranscriptParser transcriptParser;

    private List<MessageRecord> testMessages;

    @BeforeEach
    void setUp() {
        testMessages = new ArrayList<>();
        
        // 模拟一个完整的轮次
        // 1. 用户输入
        testMessages.add(createUserMessage("msg1", "帮我生成文档", 1000L));
        
        // 2. 工具调用
        testMessages.add(createAssistantToolCall("msg2", "exec", "call_123", 1002L));
        
        // 3. 工具结果
        testMessages.add(createToolResult("msg3", "exec", "call_123", "执行成功", false, 1010L));
        
        // 4. AI 回复
        testMessages.add(createAssistantReply("msg4", "文档已生成", 1015L));
    }

    @Test
    void testParseExecutionTrace_BasicFlow() {
        List<DashboardExecutionTrace> traces = transcriptParser.parseExecutionTrace(
            testMessages, 1L, 100L
        );
        
        assertEquals(4, traces.size(), "应该有4个节点");
        
        // 验证节点类型
        assertEquals("user_input", traces.get(0).getNodeType());
        assertEquals("tool_call", traces.get(1).getNodeType());
        assertEquals("tool_result", traces.get(2).getNodeType());
        assertEquals("reply", traces.get(3).getNodeType());
        
        // 验证节点索引
        assertEquals(0, traces.get(0).getNodeIndex());
        assertEquals(1, traces.get(1).getNodeIndex());
        assertEquals(2, traces.get(2).getNodeIndex());
        assertEquals(3, traces.get(3).getNodeIndex());
        
        // 验证内容
        assertEquals("帮我生成文档", traces.get(0).getContent());
        assertEquals("exec", traces.get(1).getToolName());
        assertEquals("执行成功", traces.get(2).getContent());
        assertEquals("文档已生成", traces.get(3).getContent());
    }

    @Test
    void testParseExecutionTrace_DurationCalculation() {
        List<DashboardExecutionTrace> traces = transcriptParser.parseExecutionTrace(
            testMessages, 1L, 100L
        );
        
        // 验证耗时计算
        assertEquals(2, traces.get(0).getDurationMs());  // 1002 - 1000
        assertEquals(8, traces.get(1).getDurationMs());  // 1010 - 1002
        assertEquals(5, traces.get(2).getDurationMs());  // 1015 - 1010
        assertNull(traces.get(3).getDurationMs());  // 最后一个节点为 null
    }

    @Test
    void testParseExecutionTrace_ToolResultWithError() {
        List<MessageRecord> messagesWithError = new ArrayList<>();
        messagesWithError.add(createUserMessage("msg1", "test", 1000L));
        messagesWithError.add(createAssistantToolCall("msg2", "exec", "call_456", 1002L));
        messagesWithError.add(createToolResult("msg3", "exec", "call_456", "执行失败", true, 1010L));
        
        List<DashboardExecutionTrace> traces = transcriptParser.parseExecutionTrace(
            messagesWithError, 1L, 100L
        );
        
        assertEquals(3, traces.size());
        assertFalse(traces.get(2).getSuccess());
        assertNotNull(traces.get(2).getErrorMessage());
    }

    // Helper methods
    private MessageRecord createUserMessage(String id, String content, long timestamp) {
        return new MessageRecord(
            id, "user", null, null, null, content, timestamp, null, null, null, null, null
        );
    }

    private MessageRecord createAssistantToolCall(String id, String toolName, String toolCallId, long timestamp) {
        return new MessageRecord(
            id, "assistant", null, null, toolName, null, timestamp, toolCallId, null, null, null, null
        );
    }

    private MessageRecord createToolResult(String id, String toolName, String toolCallId, String content, boolean isError, long timestamp) {
        return new MessageRecord(
            id, "toolResult", null, null, toolName, content, timestamp, toolCallId, null, null, null, isError ? "Error occurred" : null
        );
    }

    private MessageRecord createAssistantReply(String id, String content, long timestamp) {
        return new MessageRecord(
            id, "assistant", null, null, null, content, timestamp, null, null, null, null, null
        );
    }
}
```

- [ ] **Step 2: 运行测试**

```bash
mvn test -Dtest=TranscriptParserTraceTest
```

Expected: All tests PASS

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/company/clawboard/parser/TranscriptParserTraceTest.java
git commit -m "test: add unit tests for TranscriptParser.parseExecutionTrace"
```

---

## Task 10: 单元测试 - ExecutionTraceService

**Files:**
- Create: `src/test/java/com/company/clawboard/service/ExecutionTraceServiceTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.company.clawboard.service;

import com.company.clawboard.dto.ExecutionTraceResponse;
import com.company.clawboard.dto.TraceNodeVO;
import com.company.clawboard.entity.DashboardExecutionTrace;
import com.company.clawboard.mapper.ExecutionTraceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionTraceServiceTest {

    @Mock
    private ExecutionTraceMapper executionTraceMapper;

    @InjectMocks
    private ExecutionTraceService executionTraceService;

    @Test
    void testGetTraceByTurnId_Success() {
        // Arrange
        List<DashboardExecutionTrace> mockTraces = new ArrayList<>();
        mockTraces.add(DashboardExecutionTrace.builder()
            .nodeIndex(0)
            .nodeType("user_input")
            .content("test input")
            .timestampMs(1000L)
            .build());
        
        when(executionTraceMapper.selectByTurnId(1L)).thenReturn(mockTraces);

        // Act
        ExecutionTraceResponse response = executionTraceService.getTraceByTurnId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getTurnId());
        assertEquals(1, response.getNodes().size());
        
        TraceNodeVO node = response.getNodes().get(0);
        assertEquals(0, node.getNodeIndex());
        assertEquals("user_input", node.getNodeType());
        assertEquals("test input", node.getContent());
    }

    @Test
    void testGetTraceByTurnId_NotFound() {
        // Arrange
        when(executionTraceMapper.selectByTurnId(999L)).thenReturn(new ArrayList<>());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            executionTraceService.getTraceByTurnId(999L);
        });
        
        assertTrue(exception.getMessage().contains("999"));
    }
}
```

- [ ] **Step 2: 运行测试**

```bash
mvn test -Dtest=ExecutionTraceServiceTest
```

Expected: All tests PASS

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/company/clawboard/service/ExecutionTraceServiceTest.java
git commit -m "test: add unit tests for ExecutionTraceService"
```

---

## Task 11: 集成测试 - Controller

**Files:**
- Create: `src/test/java/com/company/clawboard/controller/ExecutionTraceControllerTest.java`

- [ ] **Step 1: 查看现有 Controller 测试风格**

Read: `src/test/java/com/company/clawboard/controller/DashboardControllerTest.java` (前80行)

确认 MockMvc 的使用方式

- [ ] **Step 2: 创建集成测试类**

```java
package com.company.clawboard.controller;

import com.company.clawboard.entity.DashboardExecutionTrace;
import com.company.clawboard.mapper.ExecutionTraceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExecutionTraceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExecutionTraceMapper executionTraceMapper;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        List<DashboardExecutionTrace> traces = new ArrayList<>();
        traces.add(DashboardExecutionTrace.builder()
            .turnId(1L)
            .nodeIndex(0)
            .nodeType("user_input")
            .content("测试输入")
            .timestampMs(1000L)
            .durationMs(2)
            .build());
        traces.add(DashboardExecutionTrace.builder()
            .turnId(1L)
            .nodeIndex(1)
            .nodeType("reply")
            .content("测试回复")
            .timestampMs(1002L)
            .build());
        
        executionTraceMapper.batchInsertIgnore(traces);
    }

    @Test
    void testGetTrace_Success() throws Exception {
        mockMvc.perform(get("/api/v1/turns/1/trace")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.turnId").value(1))
            .andExpect(jsonPath("$.data.nodes").isArray())
            .andExpect(jsonPath("$.data.nodes.length()").value(2))
            .andExpect(jsonPath("$.data.nodes[0].nodeType").value("user_input"))
            .andExpect(jsonPath("$.data.nodes[0].content").value("测试输入"))
            .andExpect(jsonPath("$.data.nodes[1].nodeType").value("reply"))
            .andExpect(jsonPath("$.data.nodes[1].content").value("测试回复"));
    }

    @Test
    void testGetTrace_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/turns/999/trace")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }
}
```

- [ ] **Step 3: 运行测试**

```bash
mvn test -Dtest=ExecutionTraceControllerTest
```

Expected: All tests PASS

- [ ] **Step 4: Commit**

```bash
git add src/test/java/com/company/clawboard/controller/ExecutionTraceControllerTest.java
git commit -m "test: add integration tests for ExecutionTraceController"
```

---

## Task 12: 端到端测试

**Files:**
- No file changes (manual testing)

- [ ] **Step 1: 重置数据库**

```powershell
# 清空数据库
Get-Content scripts/reset-database.sql | mysql -u clawboard -pClqc@1234 -h 127.0.0.1 -P 3306 clawboard
```

Expected: 所有表被清空

- [ ] **Step 2: 启动应用**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Wait for application to start (look for "Started ClawboardApplication")

- [ ] **Step 3: 执行扫描**

使用现有的扫描命令扫描一个包含 transcript 文件的目录

```bash
# 假设使用现有的扫描接口
curl -X POST http://localhost:8080/api/v1/scans/trigger ^
  -H "Content-Type: application/json" ^
  -d "{\"directory\": \"test/session-transcript\"}"
```

Wait for scan to complete

- [ ] **Step 4: 查询某个 turn 的执行链路**

```bash
# 先查询一个存在的 turnId
curl http://localhost:8080/api/v1/dashboard/global-stats

# 然后查询该 turn 的执行链路（替换 12345 为实际的 turnId）
curl http://localhost:8080/api/v1/turns/12345/trace
```

Expected: 返回 JSON 格式的执行链路数据，包含 user_input、tool_call、tool_result、reply 等节点

- [ ] **Step 5: 验证数据正确性**

对比返回的链路数据与原始 transcript 文件，确认：
- 节点数量正确
- 节点顺序正确
- 内容匹配
- 耗时计算合理

- [ ] **Step 6: 记录测试结果**

创建一个简单的测试报告，记录：
- 测试的 turnId
- 返回的节点数量
- 关键节点的内容摘要
- 是否符合预期

---

## Task 13: API 文档更新

**Files:**
- Modify: `docs/API接口文档v1.2.md` (或当前最新的 API 文档)

- [ ] **Step 1: 查看现有 API 文档格式**

Read: `docs/API接口文档v1.2.md` (搜索 `/turns/` 相关接口)

确认文档的组织方式和示例格式

- [ ] **Step 2: 添加新接口文档**

在文档中添加新的接口章节：

```markdown
## 执行链路追踪接口

### 查询执行链路

**Endpoint:** `GET /api/v1/turns/{turnId}/trace`

**描述:** 查询指定轮次的完整执行链路，包括用户输入、工具调用、工具结果和 AI 回复

**路径参数:**
- `turnId` (Long, required): 轮次ID

**请求示例:**
```bash
curl http://localhost:8080/api/v1/turns/12345/trace
```

**响应示例:**
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

**响应字段说明:**
- `turnId`: 轮次ID
- `nodes`: 链路节点列表，按 `nodeIndex` 排序
  - `nodeIndex`: 节点序号（从0开始）
  - `nodeType`: 节点类型（user_input/tool_call/tool_result/reply）
  - `content`: 内容（用户输入、AI 回复或工具结果）
  - `timestampMs`: 时间戳（毫秒）
  - `durationMs`: 耗时（毫秒），最后一个节点为 null
  - `toolName`: 工具名称（仅 tool_call 和 tool_result 节点）
  - `toolCallId`: 工具调用ID（仅 tool_call 和 tool_result 节点）
  - `success`: 是否成功（仅 tool_result 节点）
  - `errorMessage`: 错误信息（仅失败的 tool_result 节点）

**错误响应:**
- `500 Internal Server Error`: turnId 不存在或服务器内部错误
```

- [ ] **Step 3: Commit**

```bash
git add docs/API接口文档v1.2.md
git commit -m "docs: add execution trace API documentation"
```

---

## Task 14: 最终验证与提交

**Files:**
- No file changes (verification only)

- [ ] **Step 1: 运行完整测试套件**

```bash
mvn clean test
```

Expected: All tests PASS

- [ ] **Step 2: 检查代码质量**

```bash
mvn clean compile
```

Expected: BUILD SUCCESS, no warnings

- [ ] **Step 3: 查看所有变更**

```bash
git status
```

Expected: 所有新增和修改的文件都已 staged

- [ ] **Step 4: 创建最终提交**

```bash
git add .
git commit -m "feat: complete execution trace feature implementation

- Add dashboard_execution_trace table
- Implement parseExecutionTrace in TranscriptParser
- Integrate trace parsing in DataIngestionService
- Add ExecutionTraceService and Controller
- Add comprehensive unit and integration tests
- Update API documentation"
```

- [ ] **Step 5: 推送到远程仓库（如果需要）**

```bash
git push origin main
```

---

## 自检验清单

完成所有任务后，对照设计文档检查：

**1. 规格覆盖：**
- ✅ 数据库表 `dashboard_execution_trace` 已创建
- ✅ Entity、Mapper、Service、Controller 已实现
- ✅ 解析逻辑 `parseExecutionTrace()` 已实现
- ✅ API 接口 `GET /api/v1/turns/{turnId}/trace` 可用
- ✅ 单元测试、集成测试、端到端测试已通过
- ✅ API 文档已更新

**2. 占位符扫描：**
- ✅ 无 "TBD"、"TODO"、"implement later"
- ✅ 无 "Add appropriate error handling" 等模糊描述
- ✅ 所有步骤都有具体代码
- ✅ 无 "Similar to Task N" 引用

**3. 类型一致性：**
- ✅ Entity 字段类型与 DTO/VO 一致
- ✅ Mapper 方法签名与调用处一致
- ✅ API 路径与文档一致
- ✅ 节点类型枚举值在所有地方一致

---

Plan complete and saved to `docs/superpowers/plans/2026-04-22-execution-trace.md`. Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
