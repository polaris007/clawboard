# 智能混合聚合策略实施计划

**日期**: 2026-04-23  
**目标**: 优化 dashboard_hourly_stats 聚合性能，采用文件增量 + 时间窗口兜底的混合策略

---

## 📋 概述

### 问题背景
- 当前聚合逻辑：每次扫描后对该用户的所有历史小时数据重新聚合
- 性能瓶颈：随着用户使用时间增长（如半年后有 4320 小时），聚合时间线性增长
- 实际需求：大多数情况下只扫描新增文件，不需要重算所有历史数据

### 解决方案
采用**智能混合聚合策略**：
1. **文件增量**：从本次扫描的数据中提取有变化的小时（精确）
2. **时间窗口兜底**：最近 N 小时作为安全网（容错）
3. **合并去重**：两个集合合并后去重，只聚合一次

### 预期效果
- 性能提升：约 170 倍（4320 小时 → 25 小时）
- 准确性：100%（从入库数据提取时间）
- 容错性：三层保障（文件增量 → 时间窗口 → 全量回退）

---

## 🎯 实施步骤

### Step 1: 添加配置类

**文件**: `src/main/java/com/company/clawboard/config/ClawboardProperties.java`

**修改内容**:
```java
@Data
@ConfigurationProperties(prefix = "clawboard")
public class ClawboardProperties {

    private NasConfig nas = new NasConfig();
    private ScanConfig scan = new ScanConfig();
    private ParserConfig parser = new ParserConfig();
    private SkillConfig skill = new SkillConfig();
    private ReportsConfig reports = new ReportsConfig();
    private AggregationConfig aggregation = new AggregationConfig();  // ✅ 新增

    // ... 其他配置类保持不变

    @Data
    public static class AggregationConfig {
        // 是否启用智能混合聚合策略
        private boolean enableSmartAggregation = true;
        
        // 时间窗口大小（小时）
        private int timeWindowHours = 24;
        
        // 是否启用基于文件的增量聚合
        private boolean enableFileBasedIncremental = true;
        
        // 是否启用时间窗口兜底
        private boolean enableTimeWindow = true;
    }
}
```

**验证**:
- [ ] 编译通过
- [ ] 应用启动无错误
- [ ] 配置项可在 application.yml 中覆盖

---

### Step 2: 更新实体类

**文件**: `src/main/java/com/company/clawboard/entity/DashboardScanHistory.java`

**修改内容**:
```java
@Data
public class DashboardScanHistory {
    // ... 现有字段
    
    private Integer aggHoursFromFiles;      // ✅ 新增：从文件提取的小时数
    private Integer aggHoursFromWindow;     // ✅ 新增：从时间窗口获取的小时数
    private Integer aggHoursTotal;          // ✅ 新增：聚合的总小时数（去重后）
    private Long aggDurationMs;             // ✅ 新增：聚合耗时(ms)
}
```

**验证**:
- [ ] 编译通过
- [ ] Getter/Setter 方法生成正确

---

### Step 3: 修改 DataIngestionService

**文件**: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

**修改内容**:

1. **添加 ThreadLocal 存储**:
```java
// Thread-local 存储当前扫描收集的小时集合
private static final ThreadLocal<Set<LocalDateTime>> currentScanHours = 
    ThreadLocal.withInitial(ConcurrentHashMap::newKeySet);
```

2. **在 ingestTranscript 方法中收集小时**:
```java
public void ingestTranscript(...) {
    // ... 现有逻辑
    
    // ✅ 在处理消息和轮次时收集小时
    collectHoursFromMessages(messages);
    collectHoursFromTurns(turns);
}

private void collectHoursFromMessages(List<MessageRecord> messages) {
    for (MessageRecord msg : messages) {
        if (msg.epochMs() > 0) {
            LocalDateTime hour = truncateToHour(
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(msg.epochMs()), 
                    BEIJING_ZONE
                )
            );
            currentScanHours.get().add(hour);
        }
    }
}

private void collectHoursFromTurns(List<TurnAssembler.AssembledTurn> turns) {
    for (TurnAssembler.AssembledTurn turn : turns) {
        if (turn.startTime() > 0) {
            LocalDateTime hour = truncateToHour(
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(turn.startTime()), 
                    BEIJING_ZONE
                )
            );
            currentScanHours.get().add(hour);
        }
    }
}

/**
 * 获取并清除当前扫描收集的小时集合
 * @return 收集到的小时集合
 */
public Set<LocalDateTime> getAndClearCurrentScanHours() {
    Set<LocalDateTime> hours = new HashSet<>(currentScanHours.get());
    currentScanHours.remove();
    log.debug("Retrieved {} changed hours from current scan", hours.size());
    return hours;
}

private LocalDateTime truncateToHour(LocalDateTime dt) {
    return dt.withMinute(0).withSecond(0).withNano(0);
}
```

**验证**:
- [ ] 编译通过
- [ ] 单元测试通过（如有）
- [ ] 日志输出正确的小时数

---

### Step 4: 添加 Mapper 方法

**文件**: `src/main/java/com/company/clawboard/mapper/HourlyStatsMapper.java`

**修改内容**:
```java
@Mapper
public interface HourlyStatsMapper {
    // ... 现有方法
    
    /**
     * 根据员工ID列表和时间范围查询需要聚合的小时
     * @param employeeIds 员工ID列表
     * @param cutoffTime 时间窗口截止时间
     * @return (employee_id, stat_hour) 组合列表
     */
    List<EmployeeHourKey> selectDistinctEmployeeHoursByEmployeesAndTimeRange(
        @Param("employeeIds") List<String> employeeIds,
        @Param("cutoffTime") LocalDateTime cutoffTime
    );
}
```

**文件**: `src/main/resources/mapper/HourlyStatsMapper.xml`

**修改内容**:
```xml
<select id="selectDistinctEmployeeHoursByEmployeesAndTimeRange" 
        resultType="com.company.clawboard.mapper.HourlyStatsMapper$EmployeeHourKey">
    SELECT DISTINCT employee_id AS employeeId,
           DATE_FORMAT(stat_hour, '%Y-%m-%d %H:00:00') AS statHour
    FROM (
        SELECT employee_id, message_timestamp AS stat_hour
        FROM dashboard_message 
        WHERE message_timestamp IS NOT NULL 
          AND is_system = 0 
          AND message_timestamp &gt;= #{cutoffTime}
          AND employee_id IN
        <foreach collection="employeeIds" item="employeeId" open="(" separator="," close=")">
            #{employeeId}
        </foreach>
        
        UNION ALL
        
        SELECT employee_id, COALESCE(start_time, created_at) AS stat_hour
        FROM dashboard_conversation_turn 
        WHERE (start_time IS NOT NULL OR created_at IS NOT NULL) 
          AND system_turn = 0 
          AND COALESCE(start_time, created_at) &gt;= #{cutoffTime}
          AND employee_id IN
        <foreach collection="employeeIds" item="employeeId" open="(" separator="," close=")">
            #{employeeId}
        </foreach>
        
        UNION ALL
        
        SELECT employee_id, COALESCE(occurred_at, created_at) AS stat_hour
        FROM dashboard_transcript_issue 
        WHERE (occurred_at IS NOT NULL OR created_at IS NOT NULL) 
          AND COALESCE(occurred_at, created_at) &gt;= #{cutoffTime}
          AND employee_id IN
        <foreach collection="employeeIds" item="employeeId" open="(" separator="," close=")">
            #{employeeId}
        </foreach>
          AND turn_id IN (SELECT id FROM dashboard_conversation_turn WHERE system_turn = 0)
    ) AS all_hours
    WHERE stat_hour IS NOT NULL
</select>
```

**验证**:
- [ ] SQL 语法正确
- [ ] 执行测试查询返回正确结果
- [ ] 时间过滤条件生效

---

### Step 5: 添加 ScanHistoryMapper 更新方法

**文件**: `src/main/java/com/company/clawboard/mapper/ScanHistoryMapper.java`

**修改内容**:
```java
@Mapper
public interface ScanHistoryMapper {
    // ... 现有方法
    
    /**
     * 更新扫描历史中的聚合统计信息
     */
    void updateAggregationStats(DashboardScanHistory history);
    
    /**
     * 更新扫描状态和错误信息
     */
    void updateStatusAndError(DashboardScanHistory history);
}
```

**文件**: `src/main/resources/mapper/ScanHistoryMapper.xml`

**修改内容**:
```xml
<update id="updateAggregationStats">
    UPDATE dashboard_scan_history
    SET agg_hours_from_files = #{aggHoursFromFiles},
        agg_hours_from_window = #{aggHoursFromWindow},
        agg_hours_total = #{aggHoursTotal},
        agg_duration_ms = #{aggDurationMs}
    WHERE id = #{id}
</update>

<update id="updateStatusAndError">
    UPDATE dashboard_scan_history
    SET status = #{status},
        error_message = #{errorMessage}
    WHERE id = #{id}
</update>
```

**验证**:
- [ ] SQL 语法正确
- [ ] 更新操作成功执行

---

### Step 6: 重构 HourlyStatsAggregator

**文件**: `src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java`

**修改内容**:

完全重写该类，实现智能混合聚合逻辑：

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyStatsAggregator {

    private final HourlyStatsMapper hourlyStatsMapper;
    private final ClawboardProperties properties;
    private final ScanHistoryMapper scanHistoryMapper;
    
    private static final DateTimeFormatter HOUR_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");

    /**
     * 智能混合聚合
     * @param employeeIds 需要聚合的员工ID列表
     * @param changedHoursFromScan 从扫描中收集的小时集合
     * @param scanId 扫描ID，用于记录统计信息
     */
    public void aggregateForEmployeesWithStats(
        List<String> employeeIds, 
        Set<LocalDateTime> changedHoursFromScan,
        Long scanId
    ) {
        log.info("Starting smart hourly stats aggregation for {} employees", employeeIds.size());
        long start = System.currentTimeMillis();

        AggregationConfig config = properties.getAggregation();
        
        if (!config.isEnableSmartAggregation()) {
            log.info("Smart aggregation disabled, using legacy full aggregation");
            aggregateForEmployeesLegacy(employeeIds);
            return;
        }
        
        Set<LocalDateTime> hoursToAggregate = new HashSet<>();
        int hoursFromFiles = 0;
        int hoursFromWindow = 0;
        
        // 1. 添加从扫描文件中提取的小时
        if (config.isEnableFileBasedIncremental() && changedHoursFromScan != null && !changedHoursFromScan.isEmpty()) {
            hoursToAggregate.addAll(changedHoursFromScan);
            hoursFromFiles = changedHoursFromScan.size();
            log.info("Added {} hours from scanned files", hoursFromFiles);
        }
        
        // 2. 添加时间窗口内的小时（兜底）
        if (config.isEnableTimeWindow()) {
            int windowHours = config.getTimeWindowHours();
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(windowHours);
            
            List<HourlyStatsMapper.EmployeeHourKey> recentHours = 
                hourlyStatsMapper.selectDistinctEmployeeHoursByEmployeesAndTimeRange(
                    employeeIds, cutoffTime);
            
            recentHours.forEach(eh -> hoursToAggregate.add(eh.statHour()));
            hoursFromWindow = recentHours.size();
            log.info("Added {} hours from time window (last {} hours)", 
                hoursFromWindow, windowHours);
        }
        
        // 3. 如果没有收集到任何小时，抛出异常
        if (hoursToAggregate.isEmpty()) {
            String errorMsg = String.format(
                "Smart aggregation failed: no hours collected. Files=%d, Window=%d",
                hoursFromFiles, hoursFromWindow
            );
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        
        int totalHours = hoursToAggregate.size();
        log.info("Total hours to aggregate: {} (files={}, window={}, overlap={})", 
            totalHours, hoursFromFiles, hoursFromWindow, 
            (hoursFromFiles + hoursFromWindow - totalHours));
        
        // 4. 转换为 EmployeeHourKey 列表
        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = 
            hoursToAggregate.stream()
                .flatMap(hour -> employeeIds.stream()
                    .map(empId -> new HourlyStatsMapper.EmployeeHourKey(empId, hour)))
                .distinct()
                .collect(Collectors.toList());
        
        // 5. 执行聚合
        processEmployeeHours(employeeHours, start);
        
        // 6. 记录聚合统计到 scan_history
        long duration = System.currentTimeMillis() - start;
        updateScanHistoryWithAggStats(scanId, hoursFromFiles, hoursFromWindow, totalHours, duration);
        
        log.info("Smart aggregation completed in {}ms: total={} hours", duration, totalHours);
    }
    
    /**
     * 更新扫描历史中的聚合统计
     */
    private void updateScanHistoryWithAggStats(
        Long scanId, 
        int hoursFromFiles, 
        int hoursFromWindow, 
        int totalHours,
        long durationMs
    ) {
        try {
            DashboardScanHistory history = new DashboardScanHistory();
            history.setId(scanId);
            history.setAggHoursFromFiles(hoursFromFiles);
            history.setAggHoursFromWindow(hoursFromWindow);
            history.setAggHoursTotal(totalHours);
            history.setAggDurationMs(durationMs);
            
            scanHistoryMapper.updateAggregationStats(history);
            log.debug("Updated scan {} with aggregation stats", scanId);
        } catch (Exception e) {
            log.error("Failed to update aggregation stats for scan {}", scanId, e);
            // 不抛出异常，避免影响主流程
        }
    }
    
    /**
     * 传统的全量聚合（向后兼容）
     */
    private void aggregateForEmployeesLegacy(List<String> employeeIds) {
        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = 
            hourlyStatsMapper.selectDistinctEmployeeHoursByEmployees(employeeIds);
        log.info("Found {} distinct (employee, hour) combinations to aggregate", employeeHours.size());
        processEmployeeHours(employeeHours, System.currentTimeMillis());
    }
    
    /**
     * 原有方法保留（向后兼容）
     */
    public void aggregateForEmployees(List<String> employeeIds) {
        aggregateForEmployeesWithStats(employeeIds, Collections.emptySet(), null);
    }
    
    private void processEmployeeHours(List<HourlyStatsMapper.EmployeeHourKey> employeeHours, long start) {
        int successCount = 0;
        int errorCount = 0;

        for (HourlyStatsMapper.EmployeeHourKey key : employeeHours) {
            try {
                aggregateForEmployeeHour(key.employeeId(), key.statHour());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to aggregate stats for employee {} hour {}", 
                    key.employeeId(), key.statHour(), e);
                errorCount++;
            }
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Hourly stats aggregation completed in {}ms: {} success, {} errors", 
            duration, successCount, errorCount);
    }

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

        hourlyStatsMapper.upsertStats(stats);
        log.debug("Upserted hourly stats for employee {} hour {}: tokens={}, turns={}, complete={}, errors={}",
                employeeId, statHourStr, stats.getTotalTokens(), stats.getConversationTurns(),
                stats.getCompleteTurns(), stats.getErrorCount());
    }
}
```

**验证**:
- [ ] 编译通过
- [ ] 单元测试通过（如有）
- [ ] 日志输出符合预期

---

### Step 7: 修改 ScanOrchestrator

**文件**: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

**修改内容**:

找到聚合调用的位置（大约在第 199-208 行），修改为：

```java
// Aggregate hourly stats from the scanned data
try {
    if (!scannedEmployeeIds.isEmpty()) {
        // ✅ 获取从扫描中收集的小时
        Set<LocalDateTime> changedHours = dataIngestionService.getAndClearCurrentScanHours();
        log.info("Collected {} changed hours from this scan", changedHours.size());
        
        // ✅ 调用新的智能聚合方法
        hourlyStatsAggregator.aggregateForEmployeesWithStats(
            new ArrayList<>(scannedEmployeeIds),
            changedHours,
            scanId  // ✅ 传递 scanId 用于记录统计
        );
    } else {
        log.info("No employees scanned, skipping hourly stats aggregation");
    }
} catch (Exception e) {
    log.error("Failed to aggregate hourly stats for scan {}", scanId, e);
    
    // ✅ 更新扫描历史的错误信息
    try {
        DashboardScanHistory errorHistory = new DashboardScanHistory();
        errorHistory.setId(scanId);
        errorHistory.setStatus("completed_with_agg_error");
        errorHistory.setErrorMessage("Aggregation failed: " + e.getMessage());
        scanHistoryMapper.updateStatusAndError(errorHistory);
    } catch (Exception ex) {
        log.error("Failed to update scan error status", ex);
    }
    
    // ✅ 抛出异常，让上层处理
    throw e;
}
```

**验证**:
- [ ] 编译通过
- [ ] 扫描流程正常执行
- [ ] 错误处理逻辑生效

---

### Step 8: 添加配置文件

**文件**: `src/main/resources/application.yml`

**修改内容**:

在 `clawboard` 配置块中添加：

```yaml
clawboard:
  # ... 现有配置
  
  aggregation:
    # 是否启用智能混合聚合策略
    enable-smart-aggregation: true
    # 时间窗口大小（小时），默认 24 小时
    time-window-hours: 24
    # 是否启用基于文件的增量聚合
    enable-file-based-incremental: true
    # 是否启用时间窗口兜底
    enable-time-window: true
```

同样更新 `application-dev.yml`、`application-dev-work.yml`、`application-dev-home.yml`。

**验证**:
- [ ] 配置文件语法正确
- [ ] 应用启动时配置加载成功

---

### Step 9: 数据库迁移脚本

**文件**: `src/main/resources/db/migration/V6__add_aggregation_stats_to_scan_history.sql`

**创建新文件**:

```sql
-- 添加聚合统计字段到 dashboard_scan_history 表
ALTER TABLE dashboard_scan_history 
ADD COLUMN agg_hours_from_files INT NOT NULL DEFAULT 0 COMMENT '从文件提取的小时数',
ADD COLUMN agg_hours_from_window INT NOT NULL DEFAULT 0 COMMENT '从时间窗口获取的小时数',
ADD COLUMN agg_hours_total INT NOT NULL DEFAULT 0 COMMENT '聚合的总小时数（去重后）',
ADD COLUMN agg_duration_ms BIGINT NOT NULL DEFAULT 0 COMMENT '聚合耗时(ms)';
```

**验证**:
- [ ] SQL 语法正确
- [ ] 执行迁移脚本成功
- [ ] 表结构更新正确

---

### Step 10: 编写单元测试

**文件**: `src/test/java/com/company/clawboard/scanner/HourlyStatsAggregatorTest.java`

**测试用例**:

1. **测试文件增量聚合**
2. **测试时间窗口聚合**
3. **测试混合策略（重叠去重）**
4. **测试异常情况（无小时收集）**
5. **测试统计信息记录**

**验证**:
- [ ] 所有测试用例通过
- [ ] 代码覆盖率达标

---

## ✅ 验收标准

### 功能验收
- [ ] 智能聚合策略正常工作
- [ ] 从文件提取的小时数正确
- [ ] 时间窗口兜底生效
- [ ] 重叠小时自动去重
- [ ] 聚合统计信息正确记录到 dashboard_scan_history
- [ ] 聚合失败时错误信息正确记录

### 性能验收
- [ ] 聚合时间显著减少（预期 100+ 倍提升）
- [ ] 内存占用合理
- [ ] 数据库查询次数合理

### 兼容性验收
- [ ] 向后兼容：旧接口仍然可用
- [ ] 配置可关闭智能聚合，回退到旧逻辑
- [ ] 不影响现有扫描流程

### 监控验收
- [ ] 日志输出清晰，包含关键指标
- [ ] 可通过 SQL 查询聚合统计历史
- [ ] 异常情况可追溯

---

## 🚀 部署步骤

1. **备份数据库**
   ```bash
   mysqldump -u clawboard -p clawboard dashboard_scan_history > backup_scan_history.sql
   ```

2. **执行迁移脚本**
   ```bash
   mysql -u clawboard -p clawboard < V6__add_aggregation_stats_to_scan_history.sql
   ```

3. **部署新代码**
   ```bash
   mvn clean package -DskipTests
   # 重启应用
   ```

4. **验证配置**
   - 检查 application.yml 中的 aggregation 配置
   - 确认应用启动日志无错误

5. **触发测试扫描**
   ```bash
   curl -X POST http://localhost:8080/api/v1/scan/trigger
   ```

6. **检查结果**
   ```sql
   -- 查看最新扫描的聚合统计
   SELECT 
       id,
       started_at,
       status,
       agg_hours_from_files,
       agg_hours_from_window,
       agg_hours_total,
       agg_duration_ms,
       error_message
   FROM dashboard_scan_history
   ORDER BY started_at DESC
   LIMIT 5;
   ```

---

## ⚠️ 风险与应对

### 风险 1: 时间收集失败
**现象**: `changedHours` 为空  
**应对**: 时间窗口兜底，确保至少聚合最近 N 小时

### 风险 2: 聚合失败
**现象**: 抛出异常  
**应对**: 记录错误到 dashboard_scan_history，不影响扫描主流程

### 风险 3: 性能未达预期
**现象**: 聚合时间仍然很长  
**应对**: 调整时间窗口大小，或检查是否有大量重叠

### 风险 4: 数据不一致
**现象**: 统计数据与实际不符  
**应对**: 提供手动全量重算接口，定期校验

---

## 📊 监控指标

### 关键指标
- `agg_hours_from_files`: 从文件提取的小时数
- `agg_hours_from_window`: 从时间窗口获取的小时数
- `agg_hours_total`: 聚合的总小时数（去重后）
- `agg_duration_ms`: 聚合耗时

### 查询示例
```sql
-- 查看最近 10 次扫描的聚合效果
SELECT 
    id,
    started_at,
    agg_hours_from_files,
    agg_hours_from_window,
    agg_hours_total,
    ROUND(agg_duration_ms / 1000.0, 2) as duration_seconds
FROM dashboard_scan_history
WHERE agg_hours_total > 0
ORDER BY started_at DESC
LIMIT 10;

-- 统计分析效果（最近 7 天）
SELECT 
    AVG(agg_hours_total) as avg_hours,
    MAX(agg_hours_total) as max_hours,
    MIN(agg_hours_total) as min_hours,
    AVG(agg_duration_ms) as avg_duration_ms
FROM dashboard_scan_history
WHERE started_at >= DATE_SUB(NOW(), INTERVAL 7 DAY);
```

---

## 📝 后续优化方向

1. **动态时间窗口**: 根据扫描频率自动调整窗口大小
2. **并行聚合**: 对多个员工的小时数据并行聚合
3. **缓存优化**: 缓存已聚合的小时，避免重复计算
4. **异步聚合**: 将聚合操作异步化，不阻塞扫描流程

---

**预计工作量**: 2-3 天  
**优先级**: 高（性能优化）  
**负责人**: TBD
