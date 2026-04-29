# 小时级统计聚合完整修复实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复小时级统计聚合的笛卡尔积 Bug，并实现从扫描文件提取小时的逻辑，确保所有扫描到的数据（无论时间远近）都被正确聚合到 dashboard_hourly_stats

**Architecture:** 
1. DataIngestionService 在入库时收集每个员工涉及的小时信息
2. ScanOrchestrator 在扫描完成后获取收集的小时信息并传递给聚合器
3. HourlyStatsAggregator 修复笛卡尔积 Bug，使用完整的 (employee_id, hour) 组合而非重新生成
4. 采用"从文件提取的小时 + 时间窗口兜底"的混合策略，确保数据完整性

**Tech Stack:** Java 17, Spring Boot, MyBatis, ConcurrentHashMap, LocalDateTime

---

## 文件结构映射

### 修改文件
- `src/main/java/com/company/clawboard/service/DataIngestionService.java`
  - 添加字段：scannedHoursByEmployee（线程安全的小时收集器）
  - 添加方法：collectHoursFromMessages()、getAndClearScannedHours()
  - 修改 ingestParsedTranscript()：调用收集逻辑
  
- `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`
  - 修改扫描完成后的聚合调用：获取小时信息并传递
  
- `src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java`
  - 修复 aggregateForEmployeesWithStats() 方法：使用 Set<EmployeeHourKey> 替代 Set<LocalDateTime>
  - 删除笛卡尔积生成逻辑（第 94-100 行）
  - 直接使用数据库查询结果和文件提取的组合

---

## Phase 1: DataIngestionService - 小时收集功能

### Task 1: 添加小时收集字段和方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 添加 import**

在第 24 行后添加：
```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
```

- [ ] **Step 2: 添加字段**

在第 46 行后添加：
```java
    // ✅ 线程安全的小时收集器：employeeId -> Set<hour>
    private final ConcurrentHashMap<String, Set<LocalDateTime>> scannedHoursByEmployee = new ConcurrentHashMap<>();
```

- [ ] **Step 3: 添加 collectHoursFromMessages 方法**

在第 249 行后（ingestParsedTranscript 方法结束后）添加：
```java
    /**
     * 从消息中提取小时信息并收集到 scannedHoursByEmployee
     * @param employeeId 员工ID
     * @param messages 消息列表
     */
    private void collectHoursFromMessages(String employeeId, List<DashboardMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        
        // 获取或创建该员工的小时集合
        Set<LocalDateTime> hours = scannedHoursByEmployee.computeIfAbsent(
            employeeId, k -> Collections.synchronizedSet(new HashSet<>()));
        
        // 提取每条消息的小时（截断到整点）
        for (DashboardMessage msg : messages) {
            if (msg.getMessageTimestamp() != null) {
                LocalDateTime hour = msg.getMessageTimestamp()
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
                hours.add(hour);
            }
        }
    }
    
    /**
     * 获取并清除收集的小时信息
     * @return Map<employeeId, Set<hour>>
     */
    public Map<String, Set<LocalDateTime>> getAndClearScannedHours() {
        Map<String, Set<LocalDateTime>> result = new HashMap<>(scannedHoursByEmployee);
        scannedHoursByEmployee.clear();
        return result;
    }
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "feat: add hour collection logic in DataIngestionService"
```

---

### Task 2: 在 ingestParsedTranscript 中调用收集逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 找到返回成功结果的位置**

定位到第 239-248 行：
```java
log.info("Ingested session {}: {} messages, {} turns, {} skills, {} issues",
        sessionId, messages.size(), conversationTurns, skills.size(), issues.size());

// ✅ 返回成功结果
return IngestionResult.success(...);
```

- [ ] **Step 2: 在日志后、返回前添加收集调用**

修改为：
```java
log.info("Ingested session {}: {} messages, {} turns, {} skills, {} issues",
        sessionId, messages.size(), conversationTurns, skills.size(), issues.size());

// ✅ 收集本次扫描的小时信息（用于增量聚合）
collectHoursFromMessages(employeeId, messages);

// ✅ 返回成功结果
return IngestionResult.success(
    parsed.messages().size(),
    parsed.turns().size(),
    parsed.issues().size(),
    parsed.skillInvocations().size()
);
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "feat: call collectHoursFromMessages after successful ingestion"
```

---

## Phase 2: ScanOrchestrator - 传递小时信息

### Task 3: 修改扫描完成后的聚合调用

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

- [ ] **Step 1: 找到聚合调用位置**

定位到第 223-232 行：
```java
// Aggregate hourly stats from the scanned data
try {
    if (!scannedEmployeeIds.isEmpty()) {
        hourlyStatsAggregator.aggregateForEmployees(new ArrayList<>(scannedEmployeeIds));
    } else {
        log.info("No employees scanned, skipping hourly stats aggregation");
    }
} catch (Exception e) {
    log.error("Failed to aggregate hourly stats for scan {}", scanId, e);
}
```

- [ ] **Step 2: 修改为获取并传递小时信息**

替换为：
```java
// Aggregate hourly stats from the scanned data
try {
    if (!scannedEmployeeIds.isEmpty()) {
        // ✅ 获取从扫描中收集的小时信息
        Map<String, Set<LocalDateTime>> hoursByEmployee = dataIngestionService.getAndClearScannedHours();
        
        // ✅ 合并所有员工的小时（去重）
        Set<LocalDateTime> allChangedHours = hoursByEmployee.values().stream()
            .flatMap(Set::stream)
            .collect(java.util.stream.Collectors.toSet());
        
        log.info("Collected {} distinct hours from {} employees", 
            allChangedHours.size(), hoursByEmployee.size());
        
        // ✅ 传递小时信息给聚合器
        hourlyStatsAggregator.aggregateForEmployeesWithStats(
            new ArrayList<>(scannedEmployeeIds),
            allChangedHours,
            scanId
        );
    } else {
        log.info("No employees scanned, skipping hourly stats aggregation");
    }
} catch (Exception e) {
    log.error("Failed to aggregate hourly stats for scan {}", scanId, e);
}
```

- [ ] **Step 3: 添加必要的 import**

在文件顶部确认已有：
```java
import java.util.Set;
import java.util.stream.Collectors;
```

如果没有，添加它们。

- [ ] **Step 4: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java
git commit -m "feat: pass collected hours to HourlyStatsAggregator"
```

---

## Phase 3: HourlyStatsAggregator - 修复笛卡尔积 Bug

### Task 4: 重写 aggregateForEmployeesWithStats 方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java`

- [ ] **Step 1: 找到需要修改的方法**

定位到第 35-110 行的 `aggregateForEmployeesWithStats` 方法

- [ ] **Step 2: 替换整个方法体**

将第 51-109 行替换为：
```java
        // ✅ 收集实际的 (employee_id, hour) 组合
        Set<HourlyStatsMapper.EmployeeHourKey> employeeHoursSet = new HashSet<>();
        int hoursFromFiles = 0;
        int hoursFromWindow = 0;
        
        // 1. 从扫描文件提取的小时（与所有员工组合）
        if (config.isEnableFileBasedIncremental() && changedHoursFromScan != null && !changedHoursFromScan.isEmpty()) {
            // 为每个小时与每个员工生成组合
            for (LocalDateTime hour : changedHoursFromScan) {
                for (String empId : employeeIds) {
                    employeeHoursSet.add(new HourlyStatsMapper.EmployeeHourKey(empId, hour));
                }
            }
            hoursFromFiles = changedHoursFromScan.size();
            log.info("Added {} hours from scanned files ({} employee-hour combinations)", 
                hoursFromFiles, changedHoursFromScan.size() * employeeIds.size());
        }
        
        // 2. 从时间窗口获取的组合（直接使用完整组合，不再生成笛卡尔积）
        if (config.isEnableTimeWindow()) {
            int windowHours = config.getTimeWindowHours();
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(windowHours);
            
            List<HourlyStatsMapper.EmployeeHourKey> recentHours = 
                hourlyStatsMapper.selectDistinctEmployeeHoursByEmployeesAndTimeRange(
                    employeeIds, cutoffTime);
            
            employeeHoursSet.addAll(recentHours);  // ← 直接添加完整组合
            hoursFromWindow = (int) recentHours.stream()
                .map(HourlyStatsMapper.EmployeeHourKey::statHour)
                .distinct()
                .count();
            log.info("Added {} hours from time window (last {} hours)", 
                hoursFromWindow, windowHours);
        }
        
        // 3. 如果没有收集到任何组合，回退到全量聚合
        if (employeeHoursSet.isEmpty()) {
            log.warn("No hours collected from files or window, falling back to full aggregation for {} employees", 
                employeeIds.size());
            aggregateForEmployeesLegacy(employeeIds);
            
            // 记录统计信息（全量聚合无法提供细分统计）
            long duration = System.currentTimeMillis() - start;
            updateScanHistoryWithAggStats(scanId, 0, 0, 0, duration);
            return;
        }
        
        int totalCombinations = employeeHoursSet.size();
        int uniqueHours = (int) employeeHoursSet.stream()
            .map(HourlyStatsMapper.EmployeeHourKey::statHour)
            .distinct()
            .count();
        log.info("Total (employee, hour) combinations to aggregate: {} (files={}, window={}, unique_hours={})", 
            totalCombinations, hoursFromFiles, hoursFromWindow, uniqueHours);
        
        // 4. 转换为列表并执行聚合（不再重新生成笛卡尔积）
        List<HourlyStatsMapper.EmployeeHourKey> employeeHours = new ArrayList<>(employeeHoursSet);
        processEmployeeHours(employeeHours, start);
        
        // 5. 记录聚合统计到 scan_history
        long duration = System.currentTimeMillis() - start;
        updateScanHistoryWithAggStats(scanId, hoursFromFiles, hoursFromWindow, uniqueHours, duration);
        
        log.info("Smart aggregation completed in {}ms: total={} combinations", duration, totalCombinations);
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java
git commit -m "fix: eliminate cartesian product bug and support file-based hour extraction"
```

---

## Phase 4: 测试与验证

### Task 5: 运行应用并执行扫描测试

**Files:**
- No code changes

- [ ] **Step 1: 清空数据库**

Run: `.\scripts\reset-database.ps1`
Expected: Database tables truncated successfully

- [ ] **Step 2: 启动应用**

Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Expected: Application started on port 8080

- [ ] **Step 3: 触发第一次扫描**

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/scan/trigger -Method POST -UseBasicParsing`
Expected: HTTP 200 OK, scan started

- [ ] **Step 4: 等待扫描完成并检查日志**

查看日志输出，确认：
```
Collected X distinct hours from Y employees
Added Z hours from time window (last 24 hours)
Total (employee, hour) combinations to aggregate: ...
Smart aggregation completed in ...ms
```

Expected: 没有 "cartesian product" 相关的警告，聚合正常完成

- [ ] **Step 5: 查询数据库验证**

Run SQL:
```sql
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT employee_id) as unique_employees,
    COUNT(DISTINCT stat_hour) as unique_hours,
    MIN(stat_hour) as earliest_hour,
    MAX(stat_hour) as latest_hour
FROM dashboard_hourly_stats;
```

Expected: 
- total_records > 0
- 记录数合理（不应该有 24 × 206 = 4944 条这样的笛卡尔积数量）
- earliest_hour 可能很早（如果扫描到旧数据）

- [ ] **Step 6: 检查是否有全 0 的记录**

Run SQL:
```sql
SELECT COUNT(*) as zero_records
FROM dashboard_hourly_stats
WHERE total_tokens = 0 
  AND conversation_turns = 0 
  AND error_count = 0;
```

Expected: zero_records = 0 或非常少（说明没有大量无用的笛卡尔积记录）

---

### Task 6: 测试 24 小时前数据的聚合

**Files:**
- No code changes

- [ ] **Step 1: 准备测试数据**

找一个包含 24 小时前消息的 Session 文件（例如 4 天前的文件）

- [ ] **Step 2: 触发第二次扫描**

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/scan/trigger -Method POST -UseBasicParsing`

- [ ] **Step 3: 检查日志中的小时收集**

查看日志：
```
Collected X distinct hours from Y employees
```

Expected: X 应该包含 24 小时前的小时

- [ ] **Step 4: 验证数据库中是否有旧数据的聚合**

Run SQL:
```sql
SELECT 
    DATE(stat_hour) as date,
    COUNT(*) as records_per_day,
    SUM(total_tokens) as tokens_per_day
FROM dashboard_hourly_stats
GROUP BY DATE(stat_hour)
ORDER BY date DESC
LIMIT 10;
```

Expected: 应该看到多个日期的记录，包括 4 天前的日期

- [ ] **Step 5: 对比两次扫描的聚合统计**

查看 `dashboard_scan_history` 表：
```sql
SELECT 
    id,
    agg_hours_from_files,
    agg_hours_from_window,
    agg_hours_total,
    agg_duration_ms
FROM dashboard_scan_history
ORDER BY id DESC
LIMIT 2;
```

Expected: 
- agg_hours_from_files > 0（说明从文件中提取了小时）
- agg_hours_total 合理（不是笛卡尔积的数量）

---

## Phase 5: 性能验证（可选）

### Task 7: 性能对比测试

**Files:**
- No code changes

- [ ] **Step 1: 记录当前扫描时间**

从日志中获取：
```
Scan X completed in Y ms
```

- [ ] **Step 2: 检查聚合耗时**

从 `dashboard_scan_history` 查询：
```sql
SELECT agg_duration_ms FROM dashboard_scan_history ORDER BY id DESC LIMIT 1;
```

- [ ] **Step 3: 对比优化前后的性能**

如果有之前的扫描日志，对比：
- 扫描总时间
- 聚合耗时
- 处理的记录数

Expected: 聚合耗时应该显著减少（因为不再处理 4000+ 条无用记录）

---

## 完成检查清单

- [ ] 所有任务已完成
- [ ] 编译通过无错误
- [ ] 应用启动成功
- [ ] 扫描执行成功
- [ ] 日志显示正确的小时收集信息
- [ ] dashboard_hourly_stats 中没有大量全 0 记录
- [ ] 24 小时前的数据被正确聚合
- [ ] 聚合耗时合理（性能提升）
- [ ] 无回归问题

---

## 回滚方案

如果实施后出现问题：

1. **恢复 DataIngestionService**：
   ```bash
   git revert <commit-hash-of-task-1-2>
   ```

2. **恢复 ScanOrchestrator**：
   ```bash
   git revert <commit-hash-of-task-3>
   ```

3. **恢复 HourlyStatsAggregator**：
   ```bash
   git revert <commit-hash-of-task-4>
   ```

4. **重新编译和测试**
