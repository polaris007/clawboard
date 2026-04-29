# dashboard_hourly_stats 全 0 数据优化实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 优化 `dashboard_hourly_stats` 表的数据入库逻辑，跳过无意义的全 0 记录，同时确保趋势图接口返回完整时间序列。

**Architecture:** 
1. 在 `HourlyStatsAggregator` 中添加全 0 判断逻辑，首次聚合的全 0 记录跳过入库，已有记录重新聚合为全 0 时更新
2. 修改 `DashboardService.getTrend()` 方法，在后端生成完整时间序列，缺失的小时用 0 填充

**Tech Stack:** Java 17, Spring Boot, MyBatis, MySQL 5.7

---

## 文件结构映射

### 需要修改的文件

1. **HourlyStatsAggregator.java** (`src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java`)
   - 添加 `isAllFieldsZero()` 辅助方法
   - 修改 `aggregateForEmployeeHour()` 方法，添加全 0 判断逻辑

2. **HourlyStatsMapper.java** (`src/main/java/com/company/clawboard/mapper/HourlyStatsMapper.java`)
   - 添加 `selectByEmployeeAndHour()` 方法声明

3. **HourlyStatsMapper.xml** (`src/main/resources/mapper/HourlyStatsMapper.xml`)
   - 添加 `selectByEmployeeAndHour` SQL 查询

4. **DashboardService.java** (`src/main/java/com/company/clawboard/service/DashboardService.java`)
   - 修改 `getTrend()` 方法，生成完整时间序列
   - 添加 `parseHour()` 辅助方法

### 需要创建的测试文件

5. **HourlyStatsAggregatorTest.java** (`src/test/java/com/company/clawboard/scanner/HourlyStatsAggregatorTest.java`)
   - 单元测试：`isAllFieldsZero()` 方法
   - 单元测试：`aggregateForEmployeeHour()` 方法的三种场景

---

## Task 1: 添加 isAllFieldsZero 辅助方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java`

- [ ] **Step 1: 在 HourlyStatsAggregator 类末尾添加辅助方法**

在 `HourlyStatsAggregator.java` 的第 226 行（类的右括号之前）添加：

```java
    /**
     * 检查所有统计字段是否都为 0
     * @param stats 统计数据
     * @return true 如果所有字段都为 0
     */
    private boolean isAllFieldsZero(DashboardHourlyStats stats) {
        return stats.getTotalTokens() == 0L
            && stats.getInputTokens() == 0L
            && stats.getOutputTokens() == 0L
            && stats.getTotalCost().compareTo(BigDecimal.ZERO) == 0
            && stats.getCacheReadTokens() == 0L
            && stats.getCacheWriteTokens() == 0L
            && stats.getConversationTurns() == 0
            && stats.getCompleteTurns() == 0
            && stats.getErrorTurns() == 0
            && stats.getSkillInvocations() == 0
            && stats.getSkillErrors() == 0
            && stats.getToolCalls() == 0
            && stats.getToolErrors() == 0
            && stats.getErrorCount() == 0;
    }
```

- [ ] **Step 2: 验证代码编译**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java
git commit -m "feat: add isAllFieldsZero helper method"
```

---

## Task 2: 修改 aggregateForEmployeeHour 方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java:184-226`

- [ ] **Step 1: 替换 aggregateForEmployeeHour 方法**

将第 184-226 行的整个方法替换为：

```java
    public void aggregateForEmployeeHour(String employeeId, LocalDateTime statHour) {
        String statHourStr = statHour.format(HOUR_FORMATTER);

        DashboardHourlyStats tokenStats = hourlyStatsMapper.aggregateTokensByHour(employeeId, statHourStr);
        DashboardHourlyStats turnStats = hourlyStatsMapper.aggregateTurnsByHour(employeeId, statHourStr);
        int errorCount = hourlyStatsMapper.aggregateIssuesByHour(employeeId, statHourStr);

        DashboardHourlyStats stats = new DashboardHourlyStats();
        stats.setEmployeeId(employeeId);
        stats.setStatHour(statHour);
        stats.setTotalTokens(tokenStats != null ? tokenStats.getTotalTokens() : 0L);
        stats.setInputTokens(tokenStats != null ? tokenStats.getInputTokens() : 0L);
        stats.setOutputTokens(tokenStats != null ? tokenStats.getOutputTokens() : 0L);
        stats.setTotalCost(tokenStats != null ? tokenStats.getTotalCost() : BigDecimal.ZERO);
        stats.setCacheReadTokens(tokenStats != null ? tokenStats.getCacheReadTokens() : 0L);
        stats.setCacheWriteTokens(tokenStats != null ? tokenStats.getCacheWriteTokens() : 0L);

        if (turnStats != null) {
            stats.setConversationTurns(turnStats.getConversationTurns());
            stats.setCompleteTurns(turnStats.getCompleteTurns());
            stats.setErrorTurns(turnStats.getErrorTurns());
            stats.setToolCalls(turnStats.getToolCalls());
            stats.setToolErrors(turnStats.getToolErrors());
            stats.setSkillInvocations(turnStats.getSkillInvocations());
            stats.setSkillErrors(turnStats.getSkillErrors());
        } else {
            stats.setConversationTurns(0);
            stats.setCompleteTurns(0);
            stats.setErrorTurns(0);
            stats.setToolCalls(0);
            stats.setToolErrors(0);
            stats.setSkillInvocations(0);
            stats.setSkillErrors(0);
        }

        stats.setErrorCount(errorCount);
        stats.setUpdatedAt(LocalDateTime.now());

        // ✅ 优化：检查是否全 0
        if (isAllFieldsZero(stats)) {
            // 只有全 0 时才查询是否存在记录
            DashboardHourlyStats existing = hourlyStatsMapper.selectByEmployeeAndHour(employeeId, statHour);
            
            if (existing == null) {
                // 首次聚合且全 0 → 跳过
                log.debug("Skipping initial upsert for all-zero stats: employee={}, hour={}", 
                    employeeId, statHourStr);
                return;
            } else {
                // 已有记录但重新聚合为全 0 → 执行 upsert（可能意味着数据被删除）
                log.info("Updating existing record to all-zero (data may have been deleted): employee={}, hour={}", 
                    employeeId, statHourStr);
                hourlyStatsMapper.upsertStats(stats);
                return;
            }
        }

        // 非全 0 数据 → 正常 upsert
        hourlyStatsMapper.upsertStats(stats);
        log.debug("Upserted hourly stats for employee {} hour {}: tokens={}, turns={}, complete={}, errors={}",
                employeeId, statHourStr, stats.getTotalTokens(), stats.getConversationTurns(),
                stats.getCompleteTurns(), stats.getErrorCount());
    }
```

- [ ] **Step 2: 验证代码编译**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS (可能会有 IDE 报错说 selectByEmployeeAndHour 方法不存在，这是正常的，下一步会添加)

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java
git commit -m "feat: optimize aggregateForEmployeeHour to skip all-zero records"
```

---

## Task 3: 添加 Mapper 方法声明

**Files:**
- Modify: `src/main/java/com/company/clawboard/mapper/HourlyStatsMapper.java`

- [ ] **Step 1: 查看当前 HourlyStatsMapper.java 的内容**

Run: `cat src/main/java/com/company/clawboard/mapper/HourlyStatsMapper.java`

确认文件结构和现有方法。

- [ ] **Step 2: 在 HourlyStatsMapper 接口中添加新方法**

在 `HourlyStatsMapper.java` 的最后一个方法之后（第 48 行之后，第 49 行的右括号之前）添加：

```java
    /**
     * 查询指定员工和小时的统计数据
     * @param employeeId 员工ID
     * @param statHour 统计小时
     * @return 统计数据，如果不存在返回 null
     */
    DashboardHourlyStats selectByEmployeeAndHour(
        @Param("employeeId") String employeeId, 
        @Param("statHour") LocalDateTime statHour
    );
```

- [ ] **Step 3: 验证代码编译**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS (可能会有 IDE 报错说 XML 中缺少对应的 SQL，这是正常的，下一步会添加)

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/mapper/HourlyStatsMapper.java
git commit -m "feat: add selectByEmployeeAndHour mapper method"
```

---

## Task 4: 添加 SQL 查询

**Files:**
- Modify: `src/main/resources/mapper/HourlyStatsMapper.xml`

- [ ] **Step 1: 查看当前 HourlyStatsMapper.xml 的结构**

Run: `cat src/main/resources/mapper/HourlyStatsMapper.xml`

找到 `<mapper>` 标签的结束位置。

- [ ] **Step 2: 在 XML 中添加新的 select 语句**

在 `HourlyStatsMapper.xml` 的最后一个 `</select>` 或 `</insert>` 之后，`</mapper>` 之前添加：

```xml
    <select id="selectByEmployeeAndHour" resultMap="BaseResultMap">
        SELECT * FROM dashboard_hourly_stats
        WHERE employee_id = #{employeeId}
          AND stat_hour = #{statHour}
    </select>
```

- [ ] **Step 3: 验证代码编译**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/mapper/HourlyStatsMapper.xml
git commit -m "feat: add selectByEmployeeAndHour SQL query"
```

---

## Task 5: 修改 getTrend 方法生成完整时间序列

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DashboardService.java:149-191`

- [ ] **Step 1: 替换 getTrend 方法**

将第 149-191 行的整个 `getTrend()` 方法替换为：

```java
    public List<TrendDataPoint> getTrend(TimeRangeRequest request) {
        // 标准化时间参数
        String normalizedStartTime = normalizeStartTime(request.getStartTime());
        String normalizedEndTime = normalizeEndTime(request.getEndTime());
        
        // 从 hourly_stats 表查询数据
        List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
            request.getTeamName(),
            request.getUserId(),
            normalizedStartTime,
            normalizedEndTime
        );
        
        if (stats == null || stats.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 按小时分组
        Map<LocalDateTime, List<DashboardHourlyStats>> statsByHour = stats.stream()
            .collect(Collectors.groupingBy(DashboardHourlyStats::getStatHour));
        
        // ✅ 生成完整时间序列
        LocalDateTime startHour = parseHour(normalizedStartTime);
        LocalDateTime endHour = parseHour(normalizedEndTime);
        
        List<TrendDataPoint> trendPoints = new ArrayList<>();
        LocalDateTime currentHour = startHour;
        
        while (!currentHour.isAfter(endHour)) {
            TrendDataPoint point = new TrendDataPoint();
            point.setTimeLabel(currentHour.format(TIME_FORMATTER));
            
            List<DashboardHourlyStats> hourStats = statsByHour.getOrDefault(currentHour, Collections.emptyList());
            
            if (hourStats.isEmpty()) {
                // 缺失的小时 → 填充 0
                point.setTokens(0L);
                point.setTurns(0);
                point.setSkills(0);
            } else {
                // 有数据 → 聚合
                point.setTokens(hourStats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum());
                point.setTurns(hourStats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum());
                point.setSkills(hourStats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum());
            }
            
            trendPoints.add(point);
            currentHour = currentHour.plusHours(1);
        }
        
        return trendPoints.stream()
            .limit(720)  // 最多返回720条（30天）
            .collect(Collectors.toList());
    }
```

- [ ] **Step 2: 验证代码编译**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS (可能会有 IDE 报错说 parseHour 方法不存在，这是正常的，下一步会添加)

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DashboardService.java
git commit -m "feat: modify getTrend to generate complete time series"
```

---

## Task 6: 添加 parseHour 辅助方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DashboardService.java`

- [ ] **Step 1: 在 DashboardService 类中添加 parseHour 方法**

在 `DashboardService.java` 的 `normalizeEndTime()` 方法之后（大约第 147 行之后），`getTrend()` 方法之前添加：

```java
    /**
     * 解析小时字符串为 LocalDateTime
     */
    private LocalDateTime parseHour(String hourStr) {
        return LocalDateTime.parse(hourStr, DATETIME_FORMATTER);
    }
```

- [ ] **Step 2: 验证代码编译**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DashboardService.java
git commit -m "feat: add parseHour helper method"
```

---

## Task 7: 编写单元测试

**Files:**
- Create: `src/test/java/com/company/clawboard/scanner/HourlyStatsAggregatorTest.java`

- [ ] **Step 1: 创建测试类文件**

创建 `src/test/java/com/company/clawboard/scanner/HourlyStatsAggregatorTest.java`，内容如下：

```java
package com.company.clawboard.scanner;

import com.company.clawboard.entity.DashboardHourlyStats;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HourlyStatsAggregatorTest {

    private final HourlyStatsAggregator aggregator = new HourlyStatsAggregator(null, null, null);

    @Test
    void testIsAllFieldsZero_AllZeros_ReturnsTrue() {
        DashboardHourlyStats stats = createAllZeroStats();
        
        // 使用反射调用私有方法
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertTrue(result, "All fields are zero, should return true");
    }

    @Test
    void testIsAllFieldsZero_TotalTokensNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setTotalTokens(1000L);
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "totalTokens is non-zero, should return false");
    }

    @Test
    void testIsAllFieldsZero_ConversationTurnsNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setConversationTurns(5);
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "conversationTurns is non-zero, should return false");
    }

    @Test
    void testIsAllFieldsZero_ErrorCountNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setErrorCount(1);
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "errorCount is non-zero, should return false");
    }

    @Test
    void testIsAllFieldsZero_TotalCostNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setTotalCost(new BigDecimal("0.01"));
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "totalCost is non-zero, should return false");
    }

    /**
     * 创建全 0 的统计数据对象
     */
    private DashboardHourlyStats createAllZeroStats() {
        DashboardHourlyStats stats = new DashboardHourlyStats();
        stats.setEmployeeId("test-user");
        stats.setStatHour(LocalDateTime.now());
        stats.setTotalTokens(0L);
        stats.setInputTokens(0L);
        stats.setOutputTokens(0L);
        stats.setTotalCost(BigDecimal.ZERO);
        stats.setCacheReadTokens(0L);
        stats.setCacheWriteTokens(0L);
        stats.setConversationTurns(0);
        stats.setCompleteTurns(0);
        stats.setErrorTurns(0);
        stats.setSkillInvocations(0);
        stats.setSkillErrors(0);
        stats.setToolCalls(0);
        stats.setToolErrors(0);
        stats.setErrorCount(0);
        stats.setUpdatedAt(LocalDateTime.now());
        return stats;
    }

    /**
     * 使用反射调用私有的 isAllFieldsZero 方法
     */
    private boolean invokeIsAllFieldsZero(DashboardHourlyStats stats) {
        try {
            java.lang.reflect.Method method = HourlyStatsAggregator.class.getDeclaredMethod("isAllFieldsZero", DashboardHourlyStats.class);
            method.setAccessible(true);
            return (boolean) method.invoke(aggregator, stats);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke isAllFieldsZero", e);
        }
    }
}
```

- [ ] **Step 2: 运行单元测试**

Run: `mvn test -Dtest=HourlyStatsAggregatorTest`
Expected: All 5 tests PASS

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/company/clawboard/scanner/HourlyStatsAggregatorTest.java
git commit -m "test: add unit tests for isAllFieldsZero method"
```

---

## Task 8: 集成测试 - 验证扫描优化

**Files:**
- No file changes (manual testing)

- [ ] **Step 1: 清空数据库**

Run: `.\scripts\reset-database.ps1`
Expected: Database tables cleared

- [ ] **Step 2: 启动应用**

Run: `.\run-app-optimized.bat`
Expected: Application starts on port 8080

- [ ] **Step 3: 触发扫描**

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing`
Expected: Scan triggered successfully

- [ ] **Step 4: 等待扫描完成**

Wait for scan to complete (check logs or wait ~2-5 minutes)

- [ ] **Step 5: 验证 sha- 用户的全 0 记录未入库**

Run: 
```sql
SELECT COUNT(*) as sha_zero_count 
FROM dashboard_hourly_stats 
WHERE employee_id LIKE 'sha-%'
  AND total_tokens = 0
  AND conversation_turns = 0;
```
Expected: `sha_zero_count = 0`

- [ ] **Step 6: 验证已知用户的记录正常入库**

Run: 
```sql
SELECT COUNT(*) as known_user_count 
FROM dashboard_hourly_stats 
WHERE employee_id NOT LIKE 'sha-%';
```
Expected: `known_user_count > 0` (应该有几千条记录)

- [ ] **Step 7: 验证总记录数减少**

Run: 
```sql
SELECT COUNT(*) as total_count FROM dashboard_hourly_stats;
```
Expected: `total_count` 应该在 6000-7000 左右（相比之前的 20881 减少了约 68.8%）

---

## Task 9: 集成测试 - 验证趋势图完整时间序列

**Files:**
- No file changes (manual testing)

- [ ] **Step 1: 查询趋势图接口**

Run:
```powershell
$body = @{
    startTime = "2026-04-22 00:00:00"
    endTime = "2026-04-22 23:00:00"
} | ConvertTo-Json

Invoke-WebRequest -Uri http://localhost:8080/api/v1/dashboard/trend -Method POST -Body $body -ContentType "application/json" -UseBasicParsing | Select-Object -ExpandProperty Content
```
Expected: 返回 24 个小时的数据点（00:00 到 23:00）

- [ ] **Step 2: 验证返回的时间序列连续性**

检查响应中的 `timeLabel` 字段，应该包含：
- "2026-04-22 00:00:00"
- "2026-04-22 01:00:00"
- ...
- "2026-04-22 23:00:00"

共 24 条记录，即使某些小时没有数据也应该返回（tokens=0, turns=0, skills=0）

- [ ] **Step 3: 验证缺失的小时用 0 填充**

找到某个没有数据的小时（例如凌晨时段），验证其值为：
```json
{
  "timeLabel": "2026-04-22 03:00:00",
  "tokens": 0,
  "turns": 0,
  "skills": 0
}
```

---

## Task 10: 边界情况测试 - 数据删除场景

**Files:**
- No file changes (manual testing)

- [ ] **Step 1: 选择一个有数据的小时记录**

Run:
```sql
SELECT employee_id, stat_hour, total_tokens, conversation_turns
FROM dashboard_hourly_stats
WHERE total_tokens > 0
LIMIT 1;
```
记录返回的 `employee_id` 和 `stat_hour`。

- [ ] **Step 2: 手动将该记录更新为全 0（模拟数据删除）**

Run:
```sql
UPDATE dashboard_hourly_stats
SET total_tokens = 0,
    input_tokens = 0,
    output_tokens = 0,
    total_cost = 0,
    cache_read_tokens = 0,
    cache_write_tokens = 0,
    conversation_turns = 0,
    complete_turns = 0,
    error_turns = 0,
    skill_invocations = 0,
    skill_errors = 0,
    tool_calls = 0,
    tool_errors = 0,
    error_count = 0
WHERE employee_id = '<记录的employee_id>'
  AND stat_hour = '<记录的stat_hour>';
```

- [ ] **Step 3: 重新触发扫描**

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing`

- [ ] **Step 4: 验证该记录仍然存在于数据库中（虽然值为全 0）**

Run:
```sql
SELECT COUNT(*) as exists_count
FROM dashboard_hourly_stats
WHERE employee_id = '<记录的employee_id>'
  AND stat_hour = '<记录的stat_hour>';
```
Expected: `exists_count = 1`（记录仍然存在，说明已有记录重新聚合为全 0 时会更新而不是跳过）

---

## Task 11: 性能验证

**Files:**
- No file changes (manual testing)

- [ ] **Step 1: 查看扫描日志中的聚合耗时**

检查应用日志，找到类似这样的输出：
```
Hourly stats aggregation completed in XXXXms: YYYY success, ZZZZ errors
```

对比优化前后的耗时（如果有历史数据）。

- [ ] **Step 2: 验证跳过的全 0 记录数量**

检查日志中是否有类似这样的输出：
```
Skipping initial upsert for all-zero stats: employee=sha-xxx, hour=2026-04-22 10:00:00
```

统计这类日志的数量，应该接近 14000+ 条（对应之前 68.8% 的 sha- 用户记录）。

- [ ] **Step 3: 验证更新的已有记录数量**

检查日志中是否有类似这样的输出：
```
Updating existing record to all-zero (data may have been deleted): employee=18100919, hour=2026-04-22 10:00:00
```

这类日志应该很少（只有在数据被删除的情况下才会出现）。

---

## Task 12: 文档更新

**Files:**
- Modify: `docs/API接口文档.md` (如果需要)
- Create: `docs/OPTIMIZATION-REPORT-HOURLY-STATS.md`

- [ ] **Step 1: 创建优化报告文档**

创建 `docs/OPTIMIZATION-REPORT-HOURLY-STATS.md`，内容包括：
- 优化背景和目标
- 实施方案概述
- 预期效果（存储减少 68.8%，upsert 操作减少 68.8%）
- 测试结果 summary
- 后续优化建议

- [ ] **Step 2: Commit 文档**

```bash
git add docs/OPTIMIZATION-REPORT-HOURLY-STATS.md
git commit -m "docs: add hourly stats optimization report"
```

---

## 验收标准

✅ **所有任务完成后，以下标准必须满足：**

1. **代码编译通过**：`mvn compile -DskipTests` 无错误
2. **单元测试通过**：`mvn test -Dtest=HourlyStatsAggregatorTest` 全部通过
3. **sha- 用户的全 0 记录未入库**：数据库中 `employee_id LIKE 'sha-%'` 且所有字段为 0 的记录数为 0
4. **已知用户的记录正常入库**：数据库中 `employee_id NOT LIKE 'sha-%'` 的记录数 > 0
5. **总记录数减少约 68.8%**：从 20881 减少到约 6500
6. **趋势图返回完整时间序列**：查询任意时间范围，返回连续的小时数据点，缺失的小时用 0 填充
7. **已有记录重新聚合为全 0 时更新**：手动测试验证记录不会被删除

---

## 回滚方案

如果实施后发现问题，可以快速回滚：

1. **Git 回滚**：
   ```bash
   git revert HEAD~12  # 回滚最近 12 个 commit
   ```

2. **数据库清理**（可选）：
   ```sql
   -- 如果需要清理 sha- 用户的全 0 记录
   DELETE FROM dashboard_hourly_stats
   WHERE employee_id LIKE 'sha-%'
     AND total_tokens = 0
     AND conversation_turns = 0
     AND skill_invocations = 0;
   ```

3. **恢复原始代码**：手动恢复 `HourlyStatsAggregator.java`、`HourlyStatsMapper.java`、`HourlyStatsMapper.xml`、`DashboardService.java` 到优化前的版本

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-29-hourly-stats-zero-data-optimization.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
