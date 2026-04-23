# 时间范围报告总错误数简化

## 问题描述

之前的实现过于复杂，通过 `turn_id` 关联来筛选 issues，逻辑复杂且容易出错。

### 原实现逻辑

```java
// 1. 提取 turn IDs
List<Long> turnIds = turns.stream()
    .map(DashboardConversationTurn::getId)
    .collect(Collectors.toList());

// 2. 查询 issues（复杂的 OR 条件）
var issues = issueMapper.selectByTimeRangeAndTurnIds(turnIds, startTime, endTime);
```

```sql
SELECT * FROM dashboard_transcript_issue
WHERE (
    (turn_id IN (...) AND occurred_at BETWEEN ...)
    OR
    (turn_id IS NULL AND occurred_at BETWEEN ...)
)
```

### 问题分析

1. **逻辑复杂**: 需要同时检查 `turn_id` 和 `occurred_at`
2. **性能开销**: 需要先提取 turn IDs，再执行复杂的 SQL 查询
3. **不必要**: 实际上只需要检查 `occurred_at` 是否在时间范围内

---

## 简化方案

### 核心思路

**与扫描报告保持一致**：直接根据时间范围查询 issues，只检查 `occurred_at` 字段。

### 优势

1. ✅ **简单**: SQL 查询简洁明了
2. ✅ **高效**: 单次查询，无需额外处理
3. ✅ **一致**: 与扫描报告的逻辑保持一致（都是直接统计 issues）
4. ✅ **准确**: 只检查 `occurred_at`，避免复杂的关联逻辑

---

## 修改内容

### 1. 添加新的 Mapper 方法

**文件**: [TranscriptIssueMapper.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java)

```java
/**
 * 根据时间范围查询问题（简单直接，只检查 occurred_at）
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 问题列表，按 occurred_at 升序排列
 */
List<DashboardTranscriptIssue> selectByTimeRange(
    @Param("startTime") LocalDateTime startTime,
    @Param("endTime") LocalDateTime endTime);
```

### 2. 添加对应的 SQL 查询

**文件**: [TranscriptIssueMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/TranscriptIssueMapper.xml)

```xml
<!-- 根据时间范围查询问题（简单直接，只检查 occurred_at） -->
<select id="selectByTimeRange" resultType="com.company.clawboard.entity.DashboardTranscriptIssue">
    SELECT * FROM dashboard_transcript_issue
    WHERE occurred_at >= #{startTime} AND occurred_at <= #{endTime}
    ORDER BY occurred_at ASC
</select>
```

### 3. 修改 ReportGenerator.generateReportByTimeRange

**文件**: [ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)

**修改前**：
```java
// Collect turn IDs from these turns
List<Long> turnIds = turns.stream()
    .map(DashboardConversationTurn::getId)
    .collect(Collectors.toList());

// Query issues within the time range (complex logic)
var issues = issueMapper.selectByTimeRangeAndTurnIds(turnIds, startTime, endTime);
```

**修改后**：
```java
// Query issues within the time range (simple: just check occurred_at)
var issues = issueMapper.selectByTimeRange(startTime, endTime);
```

---

## 对比分析

### 修复前 vs 修复后

| 维度 | 修复前 | 修复后 |
|------|--------|--------|
| **查询方式** | 通过 turn_id 关联 + occurred_at | 只检查 occurred_at |
| **SQL 复杂度** | 复杂（OR 条件 + IN 子句） | 简单（单个 WHERE 条件） |
| **代码行数** | ~10 行 | ~3 行 |
| **性能** | 需要提取 turnIds + 复杂查询 | 单次简单查询 |
| **可维护性** | ⚠️ 复杂，易出错 | ✅ 简单，清晰 |
| **与扫描报告一致性** | ❌ 不一致 | ✅ 一致 |

### SQL 对比

**修复前**：
```sql
SELECT * FROM dashboard_transcript_issue
WHERE (
    (turn_id IN (1, 2, 3, ...) AND occurred_at BETWEEN ? AND ?)
    OR
    (turn_id IS NULL AND occurred_at BETWEEN ? AND ?)
)
ORDER BY occurred_at ASC
```

**修复后**：
```sql
SELECT * FROM dashboard_transcript_issue
WHERE occurred_at >= ? AND occurred_at <= ?
ORDER BY occurred_at ASC
```

---

## 验证步骤

### 1. 重新编译

```powershell
mvn clean compile -DskipTests
```

### 2. 重启应用并触发报告生成

```powershell
curl -X POST http://localhost:8080/api/v1/reports/generate-by-time-range `
  -H "Content-Type: application/json" `
  --data '{"startTime": "2025-03-15 00:00:00", "endTime": "2027-04-23 23:59:59"}' `
  -UseBasicParsing
```

### 3. 验证 SQL

```sql
-- 验证简化后的查询逻辑
SELECT COUNT(*) as total_issues_in_range
FROM dashboard_transcript_issue
WHERE occurred_at >= '2025-03-15 00:00:00'
  AND occurred_at <= '2027-04-23 23:59:59';

-- 检查是否有 issues 的 occurred_at 超出时间范围
SELECT 
    id,
    occurred_at,
    CASE 
        WHEN occurred_at < '2025-03-15 00:00:00' THEN 'BEFORE range'
        WHEN occurred_at > '2027-04-23 23:59:59' THEN 'AFTER range'
        ELSE 'IN range'
    END as time_status
FROM dashboard_transcript_issue
WHERE occurred_at < '2025-03-15 00:00:00' OR occurred_at > '2027-04-23 23:59:59'
LIMIT 10;
-- 这些 issues 不应该出现在报告中
```

### 4. 检查生成的报告

查看 `reports/2026-04-23/time-range-report-*.md` 文件，确认：

- **总错误数** 应该等于 `SELECT COUNT(*) FROM dashboard_transcript_issue WHERE occurred_at BETWEEN ...`
- 所有 issues 的 occurred_at 都应该在请求的时间范围内
- 报告生成速度应该更快

---

## 影响范围

### 受影响的接口

- `/api/v1/reports/generate-by-time-range`

### 不受影响的接口

- `/api/v1/scan/trigger`（扫描报告）
  - 使用 `selectByScanId` 方法，不受影响

### 数据一致性

修复后，时间范围报告的统计数据更加简洁和准确：
- ✅ 总错误数：只统计 occurred_at 在时间范围内的 issues
- ✅ 总对话轮数：只统计时间范围内的非系统轮次
- ✅ 有错误轮数：只统计时间范围内的有错误非系统轮次
- ✅ 错误率：基于准确的数据计算

---

## 注意事项

### 1. occurred_at 字段的重要性

确保 `DataIngestionService` 在插入 issue 时正确设置 `occurred_at` 字段：

```java
entity.setOccurredAt(issue.occurredAt());
```

### 2. 时间范围的边界

- `occurred_at >= startTime`（包含起始时间）
- `occurred_at <= endTime`（包含结束时间）

### 3. 性能考虑

简化后的查询：
- ✅ 更简单：单个 WHERE 条件
- ✅ 更高效：可以利用 `occurred_at` 字段的索引
- ✅ 更易优化：MySQL 可以更好地执行计划

### 4. 历史数据

如果数据库中已有历史数据：
- 修复后会正确统计时间范围内的 issues
- 无需迁移或更新历史数据

---

## 相关文件

1. **[TranscriptIssueMapper.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java)**
   - 添加了 `selectByTimeRange` 方法

2. **[TranscriptIssueMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/TranscriptIssueMapper.xml)**
   - 添加了对应的 SQL 查询

3. **[ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)**
   - 简化了 `generateReportByTimeRange()` 方法

4. **[TIME_RANGE_REPORT_STATISTICS.md](file://g:/Workplace/github/clawboard/scripts/docs/TIME_RANGE_REPORT_STATISTICS.md)**
   - 更新了统计逻辑说明

---

## 总结

### 核心改进

从复杂的 `turn_id` 关联查询简化为直接的 `occurred_at` 时间范围查询。

### 关键优势

1. ✅ **简单**: SQL 查询从 10+ 行减少到 3 行
2. ✅ **高效**: 无需提取 turnIds，单次查询完成
3. ✅ **一致**: 与扫描报告的逻辑保持一致
4. ✅ **准确**: 避免了复杂的关联逻辑可能带来的错误

### 预期效果

- 总错误数准确反映时间范围内的 issues 数量
- 代码更易理解和维护
- 查询性能更好
- 与扫描报告的计算逻辑一致
