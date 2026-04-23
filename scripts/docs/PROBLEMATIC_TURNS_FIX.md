# 有错误轮数统计修复 - 排除系统轮次

## 问题描述

之前的实现中，"有错误轮数"的计算存在**未排除系统轮次**的问题。

### 原实现逻辑

```java
// 从 issues 列表中提取 turn_id，然后去重统计
int totalProblematicTurns = (int) issues.stream()
    .map(DashboardTranscriptIssue::getTurnId)
    .filter(turnId -> turnId != null)
    .distinct()
    .count();
```

### 问题分析

1. **数据来源**: 从 `dashboard_transcript_issue` 表中查询的 issues
2. **筛选方式**: 通过 `turn_id` 关联到对话轮次
3. **缺陷**: 
   - ❌ **没有排除系统轮次**（system_turn = 1）
   - 如果系统轮次有 issues，会被错误地计入"有错误轮数"
   - 导致"有错误轮数"偏高，错误率计算不准确

### 示例场景

假设数据库中有以下数据：
- Turn #1: system_turn = 0, has_error = 1（用户轮次，有错误）
- Turn #2: system_turn = 1, has_error = 1（系统轮次，有错误）
- Turn #3: system_turn = 0, has_error = 0（用户轮次，无错误）

**原实现结果**:
- 总对话轮数: 2（已排除系统轮次）✅
- 有错误轮数: 2（包括系统轮次）❌
- 错误率: 2 / 2 = 100% ❌

**正确结果**:
- 总对话轮数: 2（已排除系统轮次）✅
- 有错误轮数: 1（只统计非系统轮次）✅
- 错误率: 1 / 2 = 50% ✅

---

## 修复方案

### 核心思路

**直接从 `dashboard_conversation_turn` 表查询**，使用 `has_error = 1 AND system_turn = 0` 条件。

### 优势

1. ✅ **准确性**: 直接查询 `has_error` 字段，确保排除系统轮次
2. ✅ **性能**: 单次 COUNT 查询，比从 issues 中提取更高效
3. ✅ **一致性**: 与"总对话轮数"的查询方式保持一致（都从 conversation_turn 表）
4. ✅ **简洁**: SQL 逻辑清晰，易于理解和维护

---

## 修改内容

### 1. 添加 Mapper 方法

**文件**: [ConversationTurnMapper.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java)

```java
/**
 * 统计指定扫描ID的有错误非系统轮次数
 * @param scanId 扫描ID
 * @return 有错误非系统轮次数（has_error = 1 AND system_turn = 0）
 */
Integer countProblematicTurnsByScanId(@Param("scanId") Long scanId);

/**
 * 统计时间范围内的有错误非系统轮次数
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 有错误非系统轮次数（has_error = 1 AND system_turn = 0）
 */
Integer countProblematicTurnsByTimeRange(
    @Param("startTime") LocalDateTime startTime,
    @Param("endTime") LocalDateTime endTime);
```

### 2. 添加 SQL 查询

**文件**: [ConversationTurnMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/ConversationTurnMapper.xml)

```xml
<!-- 统计指定扫描ID的有错误非系统轮次数 -->
<select id="countProblematicTurnsByScanId" resultType="java.lang.Integer">
    SELECT COUNT(*) FROM dashboard_conversation_turn
    WHERE scan_id = #{scanId}
      AND has_error = 1
      AND (system_turn = 0 OR system_turn IS NULL)
</select>

<!-- 统计时间范围内的有错误非系统轮次数 -->
<select id="countProblematicTurnsByTimeRange" resultType="java.lang.Integer">
    SELECT COUNT(*) FROM dashboard_conversation_turn
    WHERE start_time >= #{startTime} AND start_time <= #{endTime}
      AND has_error = 1
      AND (system_turn = 0 OR system_turn IS NULL)
</select>
```

### 3. 修改 ReportGenerator

**文件**: [ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)

#### 扫描报告 (`generateReport`)

```java
// Get problematic turns count directly from conversation_turn table
Integer problematicTurns = conversationTurnMapper.countProblematicTurnsByScanId(scanId);
if (problematicTurns == null) {
    problematicTurns = 0;
}

String markdown = buildReportContent(issues, timeRangeLabel, totalTurns, problematicTurns);
```

#### 时间范围报告 (`generateReportByTimeRange`)

```java
// Get problematic turns count directly from conversation_turn table
Integer problematicTurns = conversationTurnMapper.countProblematicTurnsByTimeRange(startTime, endTime);
if (problematicTurns == null) {
    problematicTurns = 0;
}

return buildReportContent(issues, timeRangeLabel, totalTurns, problematicTurns);
```

#### 构建报告内容 (`buildReportContent`)

```java
/**
 * 构建报告内容（可复用）
 * @param issues 问题列表
 * @param timeRangeLabel 时间范围标签
 * @param totalTurns 总对话轮数
 * @param problematicTurns 有错误轮数（可选，如果为 null 则从 issues 中计算）
 */
private String buildReportContent(
    List<DashboardTranscriptIssue> issues, 
    String timeRangeLabel, 
    Integer totalTurns, 
    Integer problematicTurns) {
    
    // ...
    
    // Get problematic turns count
    // If provided, use it directly; otherwise calculate from issues (fallback)
    int totalProblematicTurns;
    if (problematicTurns != null) {
        // Use the value from database query (excludes system turns)
        totalProblematicTurns = problematicTurns;
    } else {
        // Fallback: calculate from issues (may include system turns)
        totalProblematicTurns = (int) issues.stream()
            .map(DashboardTranscriptIssue::getTurnId)
            .filter(turnId -> turnId != null)
            .distinct()
            .count();
    }
    
    // ...
}
```

---

## 对比分析

### 修复前 vs 修复后

| 维度 | 修复前 | 修复后 |
|------|--------|--------|
| **数据源** | `dashboard_transcript_issue` | `dashboard_conversation_turn` |
| **查询方式** | 从 issues 提取 turn_id 去重 | 直接 COUNT(has_error=1 AND system_turn=0) |
| **是否排除系统轮次** | ❌ 否 | ✅ 是 |
| **性能** | 需要遍历 issues 列表 | 单次 SQL COUNT 查询 |
| **准确性** | ⚠️ 可能包含系统轮次 | ✅ 准确 |
| **降级处理** | N/A | 保留旧逻辑作为后备方案 |

### 两种报告的实现对比

| 报告类型 | 总错误数 | 总对话轮数 | 有错误轮数 |
|---------|---------|-----------|-----------|
| **扫描报告** | `SELECT * FROM issue WHERE scan_id=?` | `COUNT(*) FROM turn WHERE scan_id=? AND system_turn!=1` | `COUNT(*) FROM turn WHERE scan_id=? AND has_error=1 AND system_turn!=1` |
| **时间范围报告** | `SELECT * FROM issue WHERE (turn_id IN (...) OR occurred_at BETWEEN)` | `SELECT * FROM turn WHERE start_time BETWEEN AND system_turn!=1` | `COUNT(*) FROM turn WHERE start_time BETWEEN AND has_error=1 AND system_turn!=1` |

---

## 验证步骤

### 1. 重新编译

```powershell
mvn clean compile -DskipTests
```

### 2. 重启应用并触发扫描

```powershell
# 停止当前应用
# 重新启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 触发扫描
curl -X POST http://localhost:8080/api/v1/scan/trigger -UseBasicParsing
```

### 3. 检查生成的报告

查看 `reports/2026-04-23/transcript-comprehensive-issues-scan-*.md` 文件，确认：

- **有错误轮数** 应该小于或等于 **总对话轮数**
- **错误率** 应该在 0-100% 之间
- 如果之前错误率超过 100%，修复后应该恢复正常

### 4. 验证 SQL

```sql
-- 假设 scan_id = 1

-- 1. 验证总对话轮数（排除系统轮次）
SELECT COUNT(*) as total_turns
FROM dashboard_conversation_turn
WHERE scan_id = 1
  AND (system_turn = 0 OR system_turn IS NULL);

-- 2. 验证有错误轮数（排除系统轮次）
SELECT COUNT(*) as problematic_turns
FROM dashboard_conversation_turn
WHERE scan_id = 1
  AND has_error = 1
  AND (system_turn = 0 OR system_turn IS NULL);

-- 3. 检查是否有系统轮次被错误计入
SELECT COUNT(*) as system_turns_with_errors
FROM dashboard_conversation_turn
WHERE scan_id = 1
  AND has_error = 1
  AND system_turn = 1;
-- 这个值不应该为 0（可能有系统轮次有错误），但不应计入"有错误轮数"

-- 4. 验证错误率
SELECT 
    (SELECT COUNT(*) FROM dashboard_conversation_turn
     WHERE scan_id = 1 AND has_error = 1 AND (system_turn = 0 OR system_turn IS NULL)) * 100.0 /
    (SELECT COUNT(*) FROM dashboard_conversation_turn
     WHERE scan_id = 1 AND (system_turn = 0 OR system_turn IS NULL))
    as error_rate_percent;
```

---

## 预期结果

### 修复前
```markdown
## 📊 统计概览

- **总错误数**: 464
- **总对话轮数**: 581 （排除系统消息）
- **有错误轮数**: 160 （可能存在系统轮次）
- **错误率**: 27.54% （可能偏高）
```

### 修复后
```markdown
## 📊 统计概览

- **总错误数**: 464
- **总对话轮数**: 581 （排除系统消息）
- **有错误轮数**: < 160 （严格排除系统轮次）
- **错误率**: < 27.54% （更准确）
```

**关键改进**:
- ✅ 有错误轮数 ≤ 总对话轮数
- ✅ 错误率 ≤ 100%
- ✅ 统计数据更加准确和一致

---

## 注意事项

### 1. 降级逻辑

为了向后兼容，保留了从 issues 中计算的降级逻辑：

```java
if (problematicTurns != null) {
    totalProblematicTurns = problematicTurns;  // 优先使用数据库查询
} else {
    // 降级：从 issues 中计算（可能包含系统轮次）
    totalProblematicTurns = issues.stream()...count();
}
```

**建议**: 
- 确保所有调用都传入 `problematicTurns` 参数
- 未来可以考虑移除降级逻辑

### 2. has_error 字段的更新

确保 `DataIngestionService` 在插入或更新 turn 时正确设置 `has_error` 字段：

```java
entity.setHasError(turn.hasError() ? 1 : 0);
```

### 3. 历史数据

如果数据库中已有历史数据：
- 新的查询会正确排除系统轮次
- 无需迁移或更新历史数据

---

## 相关文件

1. **[ConversationTurnMapper.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java)**
   - 添加了两个新方法

2. **[ConversationTurnMapper.xml](file://g:/Workplace/github/clawboard/src/main/resources/mapper/ConversationTurnMapper.xml)**
   - 添加了对应的 SQL 查询

3. **[ReportGenerator.java](file://g:/Workplace/github/clawboard/src/main/java/com/company/clawboard/service/ReportGenerator.java)**
   - 修改了 `generateReport()` 和 `generateReportByTimeRange()`
   - 修改了 `buildReportContent()` 方法签名和逻辑

4. **[SCAN_REPORT_STATISTICS.md](file://g:/Workplace/github/clawboard/scripts/docs/SCAN_REPORT_STATISTICS.md)**
   - 更新了扫描报告的统计逻辑说明

5. **[TIME_RANGE_REPORT_STATISTICS.md](file://g:/Workplace/github/clawboard/scripts/docs/TIME_RANGE_REPORT_STATISTICS.md)**
   - 更新了时间范围报告的统计逻辑说明

---

## 总结

### 修复要点

1. ✅ **数据源变更**: 从 `dashboard_transcript_issue` 改为 `dashboard_conversation_turn`
2. ✅ **查询优化**: 使用 `COUNT(*) WHERE has_error=1 AND system_turn=0`
3. ✅ **准确性提升**: 严格排除系统轮次
4. ✅ **性能优化**: 单次 SQL 查询替代 Stream 处理
5. ✅ **向后兼容**: 保留降级逻辑

### 影响范围

- **扫描报告**: 有错误轮数可能减少（如果之前包含了系统轮次）
- **时间范围报告**: 有错误轮数可能减少（如果之前包含了系统轮次）
- **错误率**: 可能降低（更准确）

### 验证重点

- 有错误轮数 ≤ 总对话轮数
- 错误率在 0-100% 之间
- 与 Python 脚本的报告数据对齐
