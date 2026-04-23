# 时间范围报告数据筛选修复

## 问题描述

### 问题1：数据不一致（已修复）

使用 `generate-by-time-range` API 生成的报告与扫描时生成的报告数据不一致：

**时间范围报告**（涵盖所有数据）：
- 总错误数: 242
- 总对话轮数: 581
- 有错误轮数: 97
- 错误率: 16.70%

**扫描报告**（同一批数据）：
- 总错误数: 464
- 总对话轮数: 581
- 有错误轮数: 160
- 错误率: 27.54%

**原因**：通过 `turn_id IN (...)` 查询时，无法查到 turn_id 为 NULL 的 issues。

### 问题2：未按时间范围筛选（当前修复）

用户请求时间范围 `2026-04-14` 到 `2026-04-23`，但生成的报告显示：
- 总错误数: 464（数据库中所有的数据）
- 总对话轮数: 237（正确，已按时间筛选）
- 有错误轮数: 160（数据库中所有的数据）
- 错误率: 67.51%

**问题**：总错误数和有错误轮数没有按请求的时间范围筛选，而是返回了这些 scan 的所有数据。

**期望**：所有统计数据都应该严格限制在请求的时间范围内。

## 根本原因

### 第一次修复（问题1）：通过 scan_ids 查询所有 issues

**原实现逻辑的问题**：

```java
// 旧代码：通过 turn_ids 查询 issues
List<Long> turnIds = turns.stream()
    .map(DashboardConversationTurn::getId)
    .collect(Collectors.toList());

var issues = issueMapper.selectByTurnIds(turnIds);
```

**问题**：
1. `selectByTurnIds` 使用 `WHERE turn_id IN (...)` 查询
2. **如果 issue 的 `turn_id` 为 NULL，则不会被查到**
3. 导致大量 issue 丢失（222个）

**第一次修复**：改为通过 scan_ids 查询

```java
List<Long> scanIds = turns.stream()
    .map(DashboardConversationTurn::getScanId)
    .distinct()
    .collect(Collectors.toList());

var issues = issueMapper.selectByScanIds(scanIds);
```

但这引入了新问题：**查询了这些 scan 的所有 issues，而不考虑时间范围**。

### 第二次修复（问题2）：按时间范围筛选

**正确的逻辑应该是**：
1. 通过时间范围查询 turns（已排除系统轮次）
2. 提取这些 turns 的 IDs
3. 查询 issues 时，同时满足以下条件之一：
   - `turn_id IN (turn_ids)` - 关联到时间范围内的 turns
   - **或者** `occurred_at BETWEEN startTime AND endTime AND turn_id IS NULL` - 时间在范围内但 turn_id 为 NULL

这样可以确保：
- ✅ 只统计时间范围内的数据
- ✅ 包括 turn_id 为 NULL 但在时间范围内的 issues

## 修复方案

### 核心思路

**时间范围报告应该严格筛选指定时间范围内的数据**，包括：
1. 关联到时间范围内 turns 的 issues（通过 turn_id）
2. occurred_at 在时间范围内但 turn_id 为 NULL 的 issues

### 修改内容

#### 1. 添加新的 Mapper 方法

**文件**: [TranscriptIssueMapper.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java)

```java
/**
 * 根据时间范围和轮次ID列表查询问题
 * 包括：1) turn_id 在指定列表中的问题  2) occurred_at 在时间范围内但 turn_id 为 NULL 的问题
 * @param turnIds 轮次ID列表
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 问题列表，按 occurred_at 升序排列
 */
List<DashboardTranscriptIssue> selectByTimeRangeAndTurnIds(
    @Param("turnIds") List<Long> turnIds,
    @Param("startTime") java.time.LocalDateTime startTime,
    @Param("endTime") java.time.LocalDateTime endTime);
```

#### 2. 添加对应的 SQL 查询

**文件**: [TranscriptIssueMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/TranscriptIssueMapper.xml)

```xml
<!-- 根据时间范围和轮次ID列表查询问题 -->
<select id="selectByTimeRangeAndTurnIds" resultType="com.company.clawboard.entity.DashboardTranscriptIssue">
    SELECT * FROM dashboard_transcript_issue
    WHERE (
        -- 条件1: turn_id 在指定列表中
        turn_id IN
        <foreach collection="turnIds" item="turnId" open="(" separator="," close=")">
            #{turnId}
        </foreach>
        OR
        -- 条件2: occurred_at 在时间范围内但 turn_id 为 NULL
        (turn_id IS NULL AND occurred_at &gt;= #{startTime} AND occurred_at &lt;= #{endTime})
    )
    ORDER BY occurred_at ASC
</select>
```

#### 3. 修改 ReportGenerator.generateReportByTimeRange

**文件**: [ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java#L79-L104)

**修改前**（第一次修复后）：
```java
// Collect unique scan IDs from these turns
List<Long> scanIds = turns.stream()
    .map(DashboardConversationTurn::getScanId)
    .distinct()
    .collect(Collectors.toList());

// Query ALL issues for these scans (including those with NULL turn_id)
var issues = issueMapper.selectByScanIds(scanIds);
```

**修改后**（第二次修复）：
```java
// Collect turn IDs from these turns
List<Long> turnIds = turns.stream()
    .map(DashboardConversationTurn::getId)
    .collect(Collectors.toList());

// Query issues within the time range:
// 1. Issues linked to turns in this time range (via turn_id)
// 2. Issues with NULL turn_id but occurred_at within the time range
var issues = issueMapper.selectByTimeRangeAndTurnIds(turnIds, startTime, endTime);
```

### 逻辑对比

| 维度 | 扫描报告 | 时间范围报告（第一次修复） | 时间范围报告（第二次修复） |
|------|---------|---------------------|---------------------|
| 查询方式 | 通过 scan_id | 通过 scan_ids | 通过 turn_ids + occurred_at |
| 是否包含 NULL turn_id | ✅ 是 | ✅ 是 | ✅ 是 |
| 是否按时间筛选 | ❌ 不适用 | ❌ 否 | ✅ 是 |
| 总错误数准确性 | ✅ 准确 | ❌ 偏高 | ✅ 准确 |
| 有错误轮数准确性 | ✅ 准确 | ❌ 偏高 | ✅ 准确 |

## 验证步骤

### 1. 重新编译

```powershell
mvn clean compile -DskipTests
```

### 2. 重启应用

```powershell
# 停止当前运行的应用
# 重新启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. 执行时间范围报告请求

```powershell
curl -X POST http://localhost:8080/api/v1/reports/generate-by-time-range `
  -H "Content-Type: application/json" `
  --data '{"startTime": "2026-04-14 00:00:00", "endTime": "2026-04-23 23:59:59"}' `
  -UseBasicParsing
```

### 4. 检查生成的报告

查看 `reports/2026-04-23/time-range-report-*.md` 文件，确认：

- **总错误数** 应该明显小于数据库中的总数（只统计时间范围内的）
- **总对话轮数** 应该是时间范围内的非系统轮次数
- **有错误轮数** 应该小于或等于总对话轮数
- **错误率** 应该在合理范围内（0-100%）

**示例期望结果**（假设时间范围内有 237 个 turns）：
```markdown
## 📊 统计概览

- **总错误数**: < 464 （只统计时间范围内的）
- **总对话轮数**: 237 （排除系统消息）
- **有错误轮数**: < 237 （存在任何类型错误的轮次）
- **错误率**: < 100% （有错误轮数 / 总对话轮数）
```

### 5. 诊断 SQL（可选）

运行诊断脚本验证数据完整性：

```powershell
mysql -u clawboard -p'Clqc@1234' -h 127.0.0.1 clawboard < scripts\sql\diagnose-time-range-report.sql
```

关键查询结果：
- Query 1: 检查整体 turn_id 填充率
- Query 3: 对比 by_scan_id vs by_turn_ids 的 issue 数量差异
- Query 4: 查看 turn_id 为 NULL 的 issues 详情

## 预期结果

修复后，时间范围报告的数据应该严格限制在请求的时间范围内：

**请求**: `2026-04-14 00:00:00` 到 `2026-04-23 23:59:59`

**期望结果**：
```markdown
## 📊 统计概览

- **总错误数**: < 数据库总数 （只统计时间范围内的）
- **总对话轮数**: 237 （排除系统消息，时间范围内的）
- **有错误轮数**: <= 237 （存在任何类型错误的轮次）
- **错误率**: 0-100% （有错误轮数 / 总对话轮数）
```

**关键验证点**：
1. ✅ 总错误数 < 数据库中的总 issues 数
2. ✅ 有错误轮数 <= 总对话轮数
3. ✅ 错误率在 0-100% 之间
4. ✅ 所有统计数据都来自请求的时间范围

## 注意事项

1. **时间范围筛选**：所有统计数据（总错误数、有错误轮数）都严格限制在请求的时间范围内
2. **NULL turn_id 处理**：通过 `occurred_at` 字段筛选时间在范围内但 turn_id 为 NULL 的 issues
3. **性能考虑**：查询使用 `turn_id IN (...)` 和 `occurred_at` 范围条件，应该有良好的性能
4. **历史数据**：如果数据库中仍有 turn_id 为 NULL 的历史数据，只要在时间范围内就会被正确统计

## 相关文件

- [TranscriptIssueMapper.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/mapper/TranscriptIssueMapper.java)
- [TranscriptIssueMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/TranscriptIssueMapper.xml)
- [ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)
- [diagnose-time-range-report.sql](file://g:/Workplace/github/clawboard/scripts/sql/diagnose-time-range-report.sql)
