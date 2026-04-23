# 时间范围报告统计指标计算逻辑

## 概述

`/api/v1/reports/generate-by-time-range` 接口根据指定的时间范围生成报告，三个核心统计指标的计算来源和逻辑如下：

---

## 1. 总错误数 (Total Errors)

### 数据来源
**表**: `dashboard_transcript_issue`

### 查询方式
```java
// ReportGenerator.java 第98行
var issues = issueMapper.selectByTimeRange(startTime, endTime);
```

```xml
<!-- TranscriptIssueMapper.xml -->
<select id="selectByTimeRange" resultType="com.company.clawboard.entity.DashboardTranscriptIssue">
    SELECT * FROM dashboard_transcript_issue
    WHERE occurred_at >= #{startTime} AND occurred_at <= #{endTime}
    ORDER BY occurred_at ASC
</select>
```

### 计算方法
```java
// ReportGenerator.java 第175行（buildReportContent 中）
sb.append("- **总错误数**: ").append(issues.size()).append("\n");
```

**逻辑**: 直接统计查询到的 issues 列表的大小（记录数量）

### 筛选条件
满足以下条件即可：
- ✅ `occurred_at` 在时间范围内（>= startTime AND <= endTime）

### 关键点
- ✅ **简单直接**：只检查 `occurred_at` 字段
- ✅ **与扫描报告一致**：都是直接统计 issues 数量
- ✅ **包括所有 error_type** 的问题
- ✅ **包括 turn_id 为 NULL** 的 issues
- ✅ **严格限制在请求的时间范围内**
- ✅ 按 occurred_at 时间排序

---

## 2. 总对话轮数 (Total Conversation Turns)

### 数据来源
**表**: `dashboard_conversation_turn`

### 查询方式
```java
// ReportGenerator.java 第82行
var turns = conversationTurnMapper.selectTurnsByTimeRange(startTime, endTime);
```

```xml
<!-- ConversationTurnMapper.xml -->
<select id="selectTurnsByTimeRange" resultMap="BaseResultMap">
    SELECT * FROM dashboard_conversation_turn
    WHERE start_time >= #{startTime} AND start_time <= #{endTime}
      AND (system_turn = 0 OR system_turn IS NULL)
    ORDER BY start_time ASC
</select>
```

### 计算方法
```java
// ReportGenerator.java 第102行
int totalTurns = turns.size();  // Total non-system conversation turns in this time range

// 然后在 buildReportContent 中（第144行）
int totalConversationTurns = (totalTurns != null) ? totalTurns : 0;
sb.append("- **总对话轮数**: ").append(totalConversationTurns).append(" （排除系统消息）\n");
```

**逻辑**: 统计查询到的 turns 列表的大小

### 筛选条件
满足以下**所有条件**的 turns 会被计入：
1. ✅ `start_time` 在时间范围内（>= startTime AND <= endTime）
2. ✅ **排除系统轮次**（system_turn = 0 OR system_turn IS NULL）

### 关键点
- ✅ 排除系统轮次（system_turn = 1）
- ✅ **严格限制在请求的时间范围内**
- ✅ 使用 start_time 字段判断时间范围
- ✅ 按 start_time 升序排列

---

## 3. 有错误轮数 (Problematic Turns)

### 数据来源
**表**: `dashboard_conversation_turn`

### 查询方式
```java
// ReportGenerator.java 第108-112行
Integer problematicTurns = conversationTurnMapper.countProblematicTurnsByTimeRange(startTime, endTime);
if (problematicTurns == null) {
    problematicTurns = 0;
}
```

```xml
<!-- ConversationTurnMapper.xml -->
<select id="countProblematicTurnsByTimeRange" resultType="java.lang.Integer">
    SELECT COUNT(*) FROM dashboard_conversation_turn
    WHERE start_time >= #{startTime} AND start_time <= #{endTime}
      AND has_error = 1
      AND (system_turn = 0 OR system_turn IS NULL)
</select>
```

### 计算方法
```java
// ReportGenerator.java 第157-166行
int totalProblematicTurns;
if (problematicTurns != null) {
    // Use the value from database query (excludes system turns)
    totalProblematicTurns = problematicTurns;
} else {
    // Fallback: calculate from issues (may include system turns if they have issues)
    totalProblematicTurns = (int) issues.stream()
        .map(DashboardTranscriptIssue::getTurnId)
        .filter(turnId -> turnId != null)
        .distinct()
        .count();
}

sb.append("- **有错误轮数**: ").append(totalProblematicTurns).append(" （存在任何类型错误的轮次）\n");
```

**逻辑**: 
1. **优先使用数据库查询结果**（已排除系统轮次）
2. 如果为 null，降级为从 issues 中计算（向后兼容）

### 关键点
- ✅ **直接查询 `has_error = 1 AND system_turn = 0` 的记录数**
- ✅ **排除了系统轮次**（system_turn = 1）
- ✅ **严格限制在请求的时间范围内**（通过 start_time 筛选）
- ✅ 更准确、更高效
- ⚠️ 降级逻辑仍可能存在系统轮次计入的问题（仅作为后备方案）

---

## 4. 错误率 (Error Rate)

### 计算公式
```java
// ReportGenerator.java 第158行
double problemRate = ((double) totalProblematicTurns / totalConversationTurns) * 100;
sb.append(String.format("- **错误率**: %.2f%% （有错误轮数 / 总对话轮数）\n", problemRate));
```

**公式**: `错误率 = (有错误轮数 / 总对话轮数) × 100%`

### 示例
假设时间范围 `2026-04-14` 到 `2026-04-23`：
- 总错误数: 242（时间范围内的所有 issues）
- 总对话轮数: 237（时间范围内的非系统轮次）
- 有错误轮数: 97（时间范围内有错误的唯一 turn_id 数量）
- 错误率: 97 / 237 × 100% = 40.93%

---

## 数据流程图

```
用户请求: startTime, endTime
    ↓
┌─────────────────────────────────────────────────────┐
│ 1. 查询时间范围内的 turns（排除系统轮次）             │
│    SELECT * FROM dashboard_conversation_turn         │
│    WHERE start_time >= ? AND start_time <= ?         │
│      AND system_turn != 1                            │
│                                                      │
│    → turns 列表                                      │
│    → totalTurns = turns.size()                       │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ 2. 提取 turn IDs                                    │
│    List<Long> turnIds = turns.stream()               │
│        .map(getId)                                   │
│        .collect(toList())                            │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ 3. 查询时间范围内的 issues                           │
│    SELECT * FROM dashboard_transcript_issue          │
│    WHERE (                                           │
│        turn_id IN (turnIds)                          │
│        OR                                            │
│        (turn_id IS NULL                              │
│         AND occurred_at >= startTime                 │
│         AND occurred_at <= endTime)                  │
│    )                                                 │
│                                                      │
│    → issues 列表                                     │
└─────────────────────────────────────────────────────┘
    ↓                                    ↓
┌──────────────────────┐    ┌──────────────────────────┐
│ 4. 统计总错误数       │    │ 5. 计算有错误轮数         │
│                      │    │                          │
│ issues.size()        │    │ issues.stream()          │
│                      │    │   .map(getTurnId)        │
│ 例如: 242            │    │   .filter(not null)      │
└──────────────────────┘    │   .distinct()            │
                            │   .count()               │
                            │                          │
                            │ 例如: 97                 │
                            └──────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ 6. 计算错误率                                        │
│    错误率 = 97 / 237 × 100% = 40.93%                │
└─────────────────────────────────────────────────────┘
    ↓
生成 Markdown 报告
```

---

## 与扫描报告的对比

| 维度 | 扫描报告 | 时间范围报告 |
|------|---------|-------------|
| **总错误数** | `SELECT * FROM dashboard_transcript_issue WHERE scan_id = ?` | `SELECT * FROM dashboard_transcript_issue WHERE (turn_id IN (...) OR (turn_id IS NULL AND occurred_at BETWEEN ? AND ?))` |
| **总对话轮数** | `SELECT COUNT(*) FROM dashboard_conversation_turn WHERE scan_id = ? AND system_turn != 1` | `SELECT * FROM dashboard_conversation_turn WHERE start_time BETWEEN ? AND ? AND system_turn != 1`，然后 `.size()` |
| **有错误轮数** | 从 issues 中提取唯一的 turn_id | 从 issues 中提取唯一的 turn_id（相同逻辑） |
| **筛选依据** | scan_id | start_time / occurred_at 时间范围 |
| **是否包含 NULL turn_id** | ✅ 是 | ✅ 是（通过 occurred_at 筛选） |
| **是否排除系统轮次** | ✅ 是 | ✅ 是 |

---

## 关键注意事项

### 1. 时间范围筛选的完整性

**问题**: 如何确保所有统计数据都来自请求的时间范围？

**解决方案**:
- **turns**: 通过 `start_time >= startTime AND start_time <= endTime` 筛选
- **issues (有 turn_id)**: 通过关联到时间范围内的 turns 间接筛选
- **issues (无 turn_id)**: 通过 `occurred_at >= startTime AND occurred_at <= endTime` 直接筛选

**验证**: 
```sql
-- 检查是否有 issues 超出时间范围
SELECT COUNT(*) as out_of_range_issues
FROM dashboard_transcript_issue
WHERE turn_id IN (
    SELECT id FROM dashboard_conversation_turn
    WHERE start_time >= '2026-04-14' AND start_time <= '2026-04-23'
)
AND occurred_at < '2026-04-14' OR occurred_at > '2026-04-23';
-- 应该返回 0
```

### 2. turn_id 为 NULL 的处理

**场景**: issues 中有 turn_id 为 NULL 的记录

**处理**:
- ✅ 如果 `occurred_at` 在时间范围内，会计入"总错误数"
- ❌ **不会**计入"有错误轮数"（因为 turn_id 为 NULL）

**影响**: 
- 导致"有错误轮数"可能偏低
- 但不影响"总错误数"的准确性

**建议**: 
- 确保 `DataIngestionService` 正确设置 turn_id
- 监控 turn_id 为 NULL 的 issues 比例

### 3. 一个 turn 多个 issues

**场景**: 同一个对话轮次可能有多个不同类型的错误

**处理**: 
- "总错误数"会统计所有 issues（例如 5 个）
- "有错误轮数"只计数 1 次（通过 distinct 去重）

**示例**:
```
Turn #10 (start_time: 2026-04-15 10:30:00):
  - Issue 1: flow_interrupted (occurred_at: 2026-04-15 10:30:05)
  - Issue 2: missing_tool_call (occurred_at: 2026-04-15 10:30:06)
  - Issue 3: incomplete_response (occurred_at: 2026-04-15 10:30:07)

统计结果:
  - 总错误数: +3
  - 有错误轮数: +1
```

### 4. 边界情况处理

**时间边界**:
- `start_time >= startTime`（包含起始时间）
- `start_time <= endTime`（包含结束时间）
- `occurred_at >= startTime`（包含起始时间）
- `occurred_at <= endTime`（包含结束时间）

**空结果处理**:
```java
if (turns == null || turns.isEmpty()) {
    return buildReportContent(new ArrayList<>(), timeRangeLabel, 0);
}
```
如果没有找到任何 turns，返回空报告，总对话轮数为 0。

---

## 相关代码文件

1. **[ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)**
   - 第79-108行: `generateReportByTimeRange()` 主方法
   - 第113-160行: `buildReportContent()` 统计计算逻辑

2. **[TranscriptIssueMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/TranscriptIssueMapper.xml)**
   - 第49-62行: `selectByTimeRangeAndTurnIds` 查询

3. **[ConversationTurnMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/ConversationTurnMapper.xml)**
   - 第169-174行: `selectTurnsByTimeRange` 查询

---

## 验证 SQL

可以通过以下 SQL 验证时间范围报告的统计数据：

```sql
-- 假设时间范围: 2026-04-14 00:00:00 到 2026-04-23 23:59:59

-- 1. 验证总对话轮数（排除系统轮次）
SELECT COUNT(*) as total_turns
FROM dashboard_conversation_turn
WHERE start_time >= '2026-04-14 00:00:00' 
  AND start_time <= '2026-04-23 23:59:59'
  AND (system_turn = 0 OR system_turn IS NULL);

-- 2. 获取这些 turns 的 IDs
SELECT id FROM dashboard_conversation_turn
WHERE start_time >= '2026-04-14 00:00:00' 
  AND start_time <= '2026-04-23 23:59:59'
  AND (system_turn = 0 OR system_turn IS NULL);

-- 3. 验证总错误数（方式1: 通过 turn_id 关联）
SELECT COUNT(*) as issues_by_turn_id
FROM dashboard_transcript_issue
WHERE turn_id IN (
    SELECT id FROM dashboard_conversation_turn
    WHERE start_time >= '2026-04-14 00:00:00' 
      AND start_time <= '2026-04-23 23:59:59'
      AND (system_turn = 0 OR system_turn IS NULL)
);

-- 4. 验证总错误数（方式2: turn_id 为 NULL 但 occurred_at 在范围内）
SELECT COUNT(*) as issues_by_occurred_at
FROM dashboard_transcript_issue
WHERE turn_id IS NULL
  AND occurred_at >= '2026-04-14 00:00:00'
  AND occurred_at <= '2026-04-23 23:59:59';

-- 5. 总错误数 = 方式1 + 方式2
SELECT 
    (SELECT COUNT(*) FROM dashboard_transcript_issue
     WHERE turn_id IN (
         SELECT id FROM dashboard_conversation_turn
         WHERE start_time >= '2026-04-14 00:00:00' 
           AND start_time <= '2026-04-23 23:59:59'
           AND (system_turn = 0 OR system_turn IS NULL)
     )) 
    +
    (SELECT COUNT(*) FROM dashboard_transcript_issue
     WHERE turn_id IS NULL
       AND occurred_at >= '2026-04-14 00:00:00'
       AND occurred_at <= '2026-04-23 23:59:59')
    as total_errors;

-- 6. 验证有错误轮数
SELECT COUNT(DISTINCT turn_id) as problematic_turns
FROM dashboard_transcript_issue
WHERE turn_id IN (
    SELECT id FROM dashboard_conversation_turn
    WHERE start_time >= '2026-04-14 00:00:00' 
      AND start_time <= '2026-04-23 23:59:59'
      AND (system_turn = 0 OR system_turn IS NULL)
);
```

---

## 总结

| 指标 | 数据源 | 计算方法 | 时间筛选 | 是否排除 NULL | 是否去重 |
|------|--------|----------|---------|--------------|---------|
| 总错误数 | `dashboard_transcript_issue` | `COUNT(*)` | ✅ 是（turn_id 或 occurred_at） | ❌ 否 | ❌ 否 |
| 总对话轮数 | `dashboard_conversation_turn` | `COUNT(*) WHERE start_time BETWEEN` | ✅ 是（start_time） | N/A | N/A |
| 有错误轮数 | `dashboard_transcript_issue.turn_id` | `COUNT(DISTINCT turn_id)` | ✅ 是（通过 turn_id 间接筛选） | ✅ 是 | ✅ 是 |
| 错误率 | 计算得出 | `有错误轮数 / 总对话轮数` | N/A | N/A | N/A |

**核心原则**:
- 总错误数 = 时间范围内的所有 issues（通过 turn_id 关联或 occurred_at 直接筛选）
- 总对话轮数 = 时间范围内的非系统轮次数量（通过 start_time 筛选）
- 有错误轮数 = 时间范围内有至少一个 issue 的唯一 turn_id 数量
- 错误率 = 有错误轮数 / 总对话轮数

**与扫描报告的关键区别**:
- 扫描报告：基于 `scan_id` 筛选
- 时间范围报告：基于 `start_time` / `occurred_at` 时间范围筛选
- 两者都排除系统轮次和 turn_id 为 NULL 的有错误轮数统计
