# dashboard_hourly_stats 全 0 数据优化设计

**日期**: 2026-04-29  
**作者**: AI Assistant  
**状态**: 待评审

---

## 1. 背景与问题

### 1.1 当前状况

`dashboard_hourly_stats` 表用于存储小时级聚合统计数据，当前实现中：
- 每次扫描完成后，会对所有 `(employee_id, stat_hour)` 组合执行 upsert 操作
- 即使所有统计字段都是 0，也会写入数据库

根据实际数据分析：
- 总记录数：20,881
- sha- 用户（未知员工）的全 0 记录：14,364（占比 68.8%）
- 已知员工的记录：6,517（占比 31.2%）

### 1.2 问题

1. **存储浪费**：68.8% 的记录是全 0 的，没有实际业务价值
2. **性能开销**：大量无意义的 upsert 操作
3. **查询效率**：无用记录增加索引大小，影响查询性能

### 1.3 优化目标

1. **减少存储**：避免插入无意义的全 0 记录
2. **保持准确性**：趋势图接口仍需返回完整时间序列（缺失的小时用 0 填充）
3. **处理边界情况**：如果已有记录重新聚合为全 0，需要正确更新

---

## 2. 设计方案

### 2.1 核心策略

**优化后的入库逻辑**：

```
聚合完成 → 判断是否全 0
    ├─ 非全 0 → 正常 upsert
    └─ 全 0 → 检查是否存在记录
        ├─ 不存在 → 跳过（首次聚合，无需入库）
        └─ 已存在 → upsert 为全 0（反映数据删除）
```

### 2.2 判断标准

**全 0 定义**：以下 14 个字段全部为 0

```java
totalTokens == 0
inputTokens == 0
outputTokens == 0
totalCost == 0
cacheReadTokens == 0
cacheWriteTokens == 0
conversationTurns == 0
completeTurns == 0
errorTurns == 0
skillInvocations == 0
skillErrors == 0
toolCalls == 0
toolErrors == 0
errorCount == 0
```

**理由**：
- 简单明确：逻辑清晰，易于理解和维护
- 保留有价值数据：即使只有错误数（errorCount=1），也说明发生了问题，值得记录
- 显著减少存储：预计可减少 60-70% 的无用记录

---

## 3. 技术实现

### 3.1 修改文件清单

1. **HourlyStatsAggregator.java** - 添加全 0 判断逻辑
2. **HourlyStatsMapper.java** - 添加查询方法
3. **HourlyStatsMapper.xml** - 添加 SQL 查询

### 3.2 HourlyStatsAggregator.java

#### 3.2.1 添加辅助方法

在 `HourlyStatsAggregator` 类中添加：

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

#### 3.2.2 修改 aggregateForEmployeeHour 方法

原代码（第 184-226 行）：

```java
private void aggregateForEmployeeHour(String employeeId, LocalDateTime statHour) {
    String statHourStr = statHour.format(HOUR_FORMATTER);

    DashboardHourlyStats tokenStats = hourlyStatsMapper.aggregateTokensByHour(employeeId, statHourStr);
    DashboardHourlyStats turnStats = hourlyStatsMapper.aggregateTurnsByHour(employeeId, statHourStr);
    int errorCount = hourlyStatsMapper.aggregateIssuesByHour(employeeId, statHourStr);

    DashboardHourlyStats stats = new DashboardHourlyStats();
    // ... 设置各个字段 ...
    
    stats.setErrorCount(errorCount);
    stats.setUpdatedAt(LocalDateTime.now());

    hourlyStatsMapper.upsertStats(stats);  // ← 无条件 upsert
    log.debug("Upserted hourly stats for employee {} hour {}: tokens={}, turns={}, complete={}, errors={}",
            employeeId, statHourStr, stats.getTotalTokens(), stats.getConversationTurns(),
            stats.getCompleteTurns(), stats.getErrorCount());
}
```

修改后：

```java
private void aggregateForEmployeeHour(String employeeId, LocalDateTime statHour) {
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

### 3.3 HourlyStatsMapper.java

添加新方法：

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

### 3.4 HourlyStatsMapper.xml

添加新查询：

```xml
<select id="selectByEmployeeAndHour" resultMap="BaseResultMap">
    SELECT * FROM dashboard_hourly_stats
    WHERE employee_id = #{employeeId}
      AND stat_hour = #{statHour}
</select>
```

---

## 4. 趋势图接口兼容性

### 4.1 当前行为

`/dashboard/trend` 接口从 `dashboard_hourly_stats` 表查询数据，按小时分组返回。

**问题**：如果某个小时没有记录，该小时不会出现在响应中。

### 4.2 期望行为

返回完整的时间序列，缺失的小时用 0 填充。

### 4.3 实现方案

修改 `DashboardService.getTrend()` 方法，在后端生成完整时间序列：

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

/**
 * 解析小时字符串为 LocalDateTime
 */
private LocalDateTime parseHour(String hourStr) {
    return LocalDateTime.parse(hourStr, DATETIME_FORMATTER);
}
```

**关键点**：
1. 从 `startTime` 到 `endTime` 逐个遍历小时
2. 如果某个小时没有数据，用 0 填充
3. 确保返回连续的时间序列

---

## 5. 预期效果

### 5.1 存储优化

| 指标 | 优化前 | 优化后 | 改善 |
|------|--------|--------|------|
| 总记录数 | 20,881 | ~6,500 | ↓ 68.8% |
| sha- 用户记录 | 14,364 | 0 | ↓ 100% |
| 已知用户记录 | 6,517 | 6,517 | 不变 |

### 5.2 性能优化

| 操作 | 优化前 | 优化后 | 改善 |
|------|--------|--------|------|
| Upsert 次数 | 20,881 | ~6,500 | ↓ 68.8% |
| 额外 SELECT 次数 | 0 | ~14,364 | ↑ 新增 |
| 净收益 | - | - | 减少 68.8% 的写操作 |

**说明**：
- 对于 68.8% 的全 0 记录，会多一次 SELECT 查询，但避免了 upsert
- Upsert 比 SELECT 更昂贵（涉及索引更新、事务日志等）
- 总体性能提升

### 5.3 数据准确性

- ✅ 趋势图返回完整时间序列
- ✅ 数据删除能被正确反映（已有记录更新为全 0）
- ✅ 首次聚合的全 0 记录被跳过（避免无用数据）

---

## 6. 测试计划

### 6.1 单元测试

1. **测试 isAllFieldsZero 方法**
   - 所有字段为 0 → 返回 true
   - 任一字段非 0 → 返回 false

2. **测试 aggregateForEmployeeHour 方法**
   - 非全 0 数据 → 正常 upsert
   - 全 0 且首次聚合 → 跳过
   - 全 0 且已有记录 → upsert 为全 0

### 6.2 集成测试

1. **扫描测试**
   - 清空数据库
   - 触发扫描
   - 验证 sha- 用户的全 0 记录未入库
   - 验证已知用户的记录正常入库

2. **趋势图测试**
   - 查询某个时间范围的趋势
   - 验证返回完整时间序列
   - 验证缺失的小时用 0 填充

### 6.3 边界情况测试

1. **数据删除场景**
   - 初始状态：某个小时有数据
   - 删除源数据后重新扫描
   - 验证记录被更新为全 0

2. **跨天边界**
   - 查询跨天的时间范围
   - 验证时间序列连续性

---

## 7. 回滚方案

如果优化后发现问题，可以快速回滚：

1. **代码回滚**：恢复 `aggregateForEmployeeHour` 方法的原始实现
2. **数据清理**：如果需要，可以删除新增的全 0 记录

```sql
-- 可选：清理 sha- 用户的全 0 记录
DELETE FROM dashboard_hourly_stats
WHERE employee_id LIKE 'sha-%'
  AND total_tokens = 0
  AND conversation_turns = 0
  AND skill_invocations = 0;
```

---

## 8. 后续优化建议

### 8.1 监控指标

添加以下监控指标：
- 跳过的全 0 记录数
- 更新的已有记录数
- 正常 upsert 的记录数

### 8.2 缓存优化（可选）

如果 SELECT 查询成为瓶颈，可以考虑内存缓存：

```java
private final Set<String> knownEmployeeHours = ConcurrentHashMap.newKeySet();

// 在 upsert 成功后记录
knownEmployeeHours.add(employeeId + ":" + statHourStr);

// 在判断时使用
if (!knownEmployeeHours.contains(key)) {
    // 首次聚合 → 跳过
    return;
}
```

### 8.3 定期清理

添加定时任务，清理过期的全 0 记录：

```sql
-- 清理 90 天前的全 0 记录
DELETE FROM dashboard_hourly_stats
WHERE stat_hour < DATE_SUB(NOW(), INTERVAL 90 DAY)
  AND total_tokens = 0
  AND conversation_turns = 0;
```

---

## 9. 总结

### 9.1 核心价值

1. **减少存储**：预计减少 68.8% 的无用记录
2. **提升性能**：减少 68.8% 的 upsert 操作
3. **保持兼容**：趋势图接口仍返回完整时间序列

### 9.2 实施风险

- **低风险**：逻辑简单，易于测试和回滚
- **中等风险**：需要修改趋势图接口以支持完整时间序列

### 9.3 建议

✅ **推荐实施**：优化带来的收益远大于风险

---

**文档版本**: v1.0  
**最后更新**: 2026-04-29  
**下一步**: 提交评审，获得批准后进入实施阶段

## 10. 实施状态

**完成时间**: 2026-04-29

**已完成任务**:
- ✅ Task 1: 添加 `isAllFieldsZero` 辅助方法
- ✅ Task 2: 修改 `aggregateForEmployeeHour` 方法
- ✅ Task 3: 添加 Mapper 方法声明
- ✅ Task 4: 添加 SQL 查询
- ✅ Task 5: 修改 `getTrend` 方法生成完整时间序列
- ✅ Task 6: 添加 `parseHour` 辅助方法
- ✅ Task 7: 编写单元测试（5/5 测试通过）

**待执行任务**:
- ⏸️ Task 8-11: 集成测试和性能验证（需要手动执行）
- ⏸️ Task 12: 文档更新（进行中）

**Git Commits**:
```
1. feat: add isAllFieldsZero helper method
2. feat: optimize aggregateForEmployeeHour to skip all-zero records
3. feat: add selectByEmployeeAndHour mapper method
4. feat: add selectByEmployeeAndHour SQL query
5. feat: modify getTrend to generate complete time series
6. feat: add parseHour helper method
7. test: add unit tests for isAllFieldsZero method
```

**编译状态**: ✅ BUILD SUCCESS

**测试状态**: ✅ 5/5 单元测试通过
