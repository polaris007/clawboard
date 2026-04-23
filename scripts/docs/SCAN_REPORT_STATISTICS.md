# 扫描报告统计指标计算逻辑

## 概述

当扫描完 transcript 文件生成报告时，三个核心统计指标的计算来源和逻辑如下：

---

## 1. 总错误数 (Total Errors)

### 数据来源
**表**: `dashboard_transcript_issue`

### 查询方式
```java
// ReportGenerator.java 第44行
var issues = issueMapper.selectByScanId(scanId);
```

```xml
<!-- TranscriptIssueMapper.xml -->
<select id="selectByScanId" resultType="com.company.clawboard.entity.DashboardTranscriptIssue">
    SELECT * FROM dashboard_transcript_issue 
    WHERE scan_id = #{scanId} 
    ORDER BY occurred_at
</select>
```

### 计算方法
```java
// ReportGenerator.java 第154行
sb.append("- **总错误数**: ").append(issues.size()).append("\n");
```

**逻辑**: 直接统计查询到的 issues 列表的大小（记录数量）

### 关键点
- ✅ 包括所有 error_type 的问题
- ✅ 包括 turn_id 为 NULL 的 issues
- ✅ 只统计当前 scan_id 的数据
- ✅ 按 occurred_at 时间排序

---

## 2. 总对话轮数 (Total Conversation Turns)

### 数据来源
**表**: `dashboard_conversation_turn`

### 查询方式
```java
// ReportGenerator.java 第47行
Integer totalTurns = conversationTurnMapper.countNonSystemTurnsByScanId(scanId);
```

```xml
<!-- ConversationTurnMapper.xml -->
<select id="countNonSystemTurnsByScanId" resultType="java.lang.Integer">
    SELECT COUNT(*) FROM dashboard_conversation_turn
    WHERE scan_id = #{scanId}
      AND (system_turn = 0 OR system_turn IS NULL)
</select>
```

### 计算方法
```java
// ReportGenerator.java 第144行
int totalConversationTurns = (totalTurns != null) ? totalTurns : 0;
sb.append("- **总对话轮数**: ").append(totalConversationTurns).append(" （排除系统消息）\n");
```

**逻辑**: 直接数据库 COUNT 查询，排除系统轮次

### 关键点
- ✅ 排除系统轮次（system_turn = 1）
- ✅ 只统计当前 scan_id 的数据
- ✅ 使用数据库聚合函数，性能更好
- ✅ 如果结果为 NULL，默认为 0

---

## 3. 有错误轮数 (Problematic Turns)

### 数据来源
**表**: `dashboard_conversation_turn`

### 查询方式
```java
// ReportGenerator.java 第50-54行
Integer problematicTurns = conversationTurnMapper.countProblematicTurnsByScanId(scanId);
if (problematicTurns == null) {
    problematicTurns = 0;
}
```

```xml
<!-- ConversationTurnMapper.xml -->
<select id="countProblematicTurnsByScanId" resultType="java.lang.Integer">
    SELECT COUNT(*) FROM dashboard_conversation_turn
    WHERE scan_id = #{scanId}
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
- 总错误数: 464
- 总对话轮数: 581
- 有错误轮数: 160
- 错误率: 160 / 581 × 100% = 27.54%

---

## 数据流程图

```
扫描完成 (scanId)
    ↓
┌─────────────────────────────────────────┐
│ 1. 查询该 scan 的所有 issues             │
│    SELECT * FROM dashboard_transcript_   │
│    issue WHERE scan_id = ?              │
│                                         │
│    → issues 列表 (包含所有字段)          │
└─────────────────────────────────────────┘
    ↓                                    ↓
┌──────────────────────┐    ┌──────────────────────────┐
│ 2. 统计总错误数       │    │ 3. 计算有错误轮数         │
│                      │    │                          │
│ issues.size()        │    │ issues.stream()          │
│                      │    │   .map(getTurnId)        │
│ 例如: 464            │    │   .filter(not null)      │
└──────────────────────┘    │   .distinct()            │
                            │   .count()               │
                            │                          │
                            │ 例如: 160                │
                            └──────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 4. 查询总对话轮数                        │
│    SELECT COUNT(*) FROM                  │
│    dashboard_conversation_turn           │
│    WHERE scan_id = ?                     │
│      AND system_turn != 1                │
│                                         │
│    → totalTurns                         │
│    例如: 581                             │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 5. 计算错误率                            │
│    错误率 = 160 / 581 × 100% = 27.54%  │
└─────────────────────────────────────────┘
    ↓
生成 Markdown 报告
```

---

## 关键注意事项

### 1. turn_id 为 NULL 的影响

**问题**: 如果 issues 中有 turn_id 为 NULL 的记录：
- ✅ 会计入"总错误数"
- ❌ **不会**计入"有错误轮数"

**影响**: 
- 导致"有错误轮数"偏低
- 可能导致错误率计算不准确

**解决方案**: 
- 确保 `DataIngestionService` 正确设置 turn_id
- 使用 `messageIdToTurnIndex` 映射关联 message 和 turn

### 2. 一个 turn 多个 issues

**场景**: 同一个对话轮次可能有多个不同类型的错误

**处理**: 
- "总错误数"会统计所有 issues（例如 5 个）
- "有错误轮数"只计数 1 次（通过 distinct 去重）

**示例**:
```
Turn #10:
  - Issue 1: flow_interrupted
  - Issue 2: missing_tool_call
  - Issue 3: incomplete_response

统计结果:
  - 总错误数: +3
  - 有错误轮数: +1
```

### 3. 系统轮次排除

**规则**: 
- `system_turn = 1` 的轮次不计入"总对话轮数"
- 但系统轮次中的 issues 仍会计入"总错误数"

**原因**: 
- 系统轮次是自动生成的，不应该参与对话质量评估
- 但系统轮次中的错误仍需记录和报告

---

## 相关代码文件

1. **[ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)**
   - 第41-71行: `generateReport()` 主方法
   - 第113-160行: `buildReportContent()` 统计计算逻辑

2. **[TranscriptIssueMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/TranscriptIssueMapper.xml)**
   - 第20-22行: `selectByScanId` 查询

3. **[ConversationTurnMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/ConversationTurnMapper.xml)**
   - 第177-181行: `countNonSystemTurnsByScanId` 查询

---

## 验证 SQL

可以通过以下 SQL 验证统计数据的准确性：

```sql
-- 假设 scan_id = 1

-- 1. 验证总错误数
SELECT COUNT(*) as total_errors
FROM dashboard_transcript_issue
WHERE scan_id = 1;

-- 2. 验证总对话轮数（排除系统轮次）
SELECT COUNT(*) as total_turns
FROM dashboard_conversation_turn
WHERE scan_id = 1
  AND (system_turn = 0 OR system_turn IS NULL);

-- 3. 验证有错误轮数
SELECT COUNT(DISTINCT turn_id) as problematic_turns
FROM dashboard_transcript_issue
WHERE scan_id = 1
  AND turn_id IS NOT NULL;

-- 4. 检查 turn_id 为 NULL 的 issues
SELECT COUNT(*) as issues_without_turn_id
FROM dashboard_transcript_issue
WHERE scan_id = 1
  AND turn_id IS NULL;
```

---

## 总结

| 指标 | 数据源 | 计算方法 | 是否排除 NULL | 是否去重 |
|------|--------|----------|--------------|---------|
| 总错误数 | `dashboard_transcript_issue` | `COUNT(*)` | ❌ 否 | ❌ 否 |
| 总对话轮数 | `dashboard_conversation_turn` | `COUNT(*) WHERE system_turn != 1` | N/A | N/A |
| 有错误轮数 | `dashboard_transcript_issue.turn_id` | `COUNT(DISTINCT turn_id)` | ✅ 是 | ✅ 是 |
| 错误率 | 计算得出 | `有错误轮数 / 总对话轮数` | N/A | N/A |

**核心原则**:
- 总错误数 = 该 scan 的所有 issues 数量
- 总对话轮数 = 该 scan 的非系统轮次数量
- 有错误轮数 = 该 scan 中有至少一个 issue 的唯一 turn_id 数量
- 错误率 = 有错误轮数 / 总对话轮数
