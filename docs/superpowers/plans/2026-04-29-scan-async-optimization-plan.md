# Session Transcript 扫描异步化与状态管理优化实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将扫描系统从同步阻塞改为异步执行，实现立即响应、完整的 duration_ms 计算、数据库驱动的并发控制和友好的状态查询接口。

**Architecture:** 
1. 新增 `ScanService` 层协调扫描触发和状态查询逻辑
2. 拆分 `ScanOrchestrator.executeScan()` 为 `createScanRecord()`（同步）和 `executeScanAsync()`（异步 @Async）
3. 调整 `finishScan()` 调用时机到所有操作完成后，使 `duration_ms` 包含完整执行时间
4. 新增 Mapper 方法支持基于数据库状态的并发控制
5. 增强 `/scan/status` 接口返回完整的当前和历史扫描信息

**Tech Stack:** Spring Boot, MyBatis, MySQL, Spring @Async, Lombok

---

## 文件结构映射

### 新增文件

1. **DTO 层**（4个文件）
   - `src/main/java/com/company/clawboard/dto/ScanTriggerResponse.java` - 触发响应
   - `src/main/java/com/company/clawboard/dto/ScanConflictResponse.java` - 冲突响应
   - `src/main/java/com/company/clawboard/dto/ScanStatusResponse.java` - 状态响应
   - `src/main/java/com/company/clawboard/dto/ScanDetail.java` - 扫描详情

2. **Service 层**（1个文件）
   - `src/main/java/com/company/clawboard/service/ScanService.java` - 扫描服务

3. **测试文件**（2个文件）
   - `src/test/java/com/company/clawboard/service/ScanServiceTest.java` - Service 单元测试
   - `src/test/java/com/company/clawboard/scanner/ScanOrchestratorAsyncTest.java` - Orchestrator 异步测试

### 修改文件

1. **Controller 层**
   - `src/main/java/com/company/clawboard/controller/ScanController.java` - 简化逻辑，委托给 ScanService

2. **Scanner 层**
   - `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java` - 拆分方法、添加 @Async、移除 AtomicBoolean

3. **Mapper 层**
   - `src/main/java/com/company/clawboard/mapper/ScanHistoryMapper.java` - 新增查询方法
   - `src/main/resources/mapper/ScanHistoryMapper.xml` - 新增 SQL

4. **现有测试**
   - `src/test/java/com/company/clawboard/controller/ScanControllerIntegrationTest.java` - 更新集成测试

---

## 前置检查

在开始实施前，确认以下信息：

- ✅ 设计文档：`docs/superpowers/specs/2026-04-29-scan-async-optimization-design.md`
- ✅ 数据库表：`dashboard_scan_history` 已存在且结构满足需求
- ✅ 线程池配置：`scanExecutor` 已在配置中定义
- ⚠️ **重要**：实施过程中不要使用 git 提交，由用户自行提交

---

## 实施任务

### Task 1: 创建 DTO 类

**Files:**
- Create: `src/main/java/com/company/clawboard/dto/ScanTriggerResponse.java`
- Create: `src/main/java/com/company/clawboard/dto/ScanConflictResponse.java`
- Create: `src/main/java/com/company/clawboard/dto/ScanStatusResponse.java`
- Create: `src/main/java/com/company/clawboard/dto/ScanDetail.java`

#### Step 1.1: 创建 ScanTriggerResponse

```java
package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanTriggerResponse {
    private Long scanId;
    private String triggerType;
    private String status;  // "triggered"
    private String message; // "Scan triggered successfully"
    private LocalDateTime startedAt;
}
```

#### Step 1.2: 创建 ScanConflictResponse

```java
package com.company.clawboard.dto;

import com.company.clawboard.entity.DashboardScanHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanConflictResponse {
    private Long scanId;
    private String status;  // "running"
    private String triggerType;
    private LocalDateTime startedAt;
    private Long durationMs;  // 当前已运行时间
    private String message;   // "A scan is already running"
    
    public static ScanConflictResponse from(DashboardScanHistory history) {
        ScanConflictResponse response = new ScanConflictResponse();
        response.setScanId(history.getId());
        response.setStatus(history.getStatus());
        response.setTriggerType(history.getTriggerType());
        response.setStartedAt(history.getStartedAt());
        
        // 计算当前已运行时间
        if (history.getStartedAt() != null) {
            response.setDurationMs(
                java.time.Duration.between(history.getStartedAt(), LocalDateTime.now()).toMillis()
            );
        }
        
        response.setMessage("A scan is already running. Please wait for it to complete.");
        return response;
    }
}
```

#### Step 1.3: 创建 ScanDetail

```java
package com.company.clawboard.dto;

import com.company.clawboard.entity.DashboardScanHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanDetail {
    private Long scanId;
    private String triggerType;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationMs;
    private Integer usersScanned;
    private Integer filesProcessed;
    private Integer filesTotal;
    private Integer newMessages;
    private Integer newTurns;
    private Integer newIssues;
    
    public static ScanDetail from(DashboardScanHistory history) {
        if (history == null) {
            return null;
        }
        
        ScanDetail detail = new ScanDetail();
        detail.setScanId(history.getId());
        detail.setTriggerType(history.getTriggerType());
        detail.setStatus(history.getStatus());
        detail.setStartedAt(history.getStartedAt());
        detail.setFinishedAt(history.getFinishedAt());
        detail.setDurationMs(history.getDurationMs());
        detail.setUsersScanned(history.getUsersScanned());
        detail.setFilesProcessed(history.getFilesProcessed());
        detail.setFilesTotal(history.getFilesTotal());
        detail.setNewMessages(history.getNewMessages());
        detail.setNewTurns(history.getNewTurns());
        detail.setNewIssues(history.getNewIssues());
        return detail;
    }
}
```

#### Step 1.4: 创建 ScanStatusResponse

```java
package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanStatusResponse {
    private Boolean scanning;
    private ScanDetail currentScan;
    private ScanDetail lastCompletedScan;
    private LocalDateTime nextScheduledAt;
}
```

#### Step 1.5: 验证编译

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

---

### Task 2: 新增 Mapper 方法

**Files:**
- Modify: `src/main/java/com/company/clawboard/mapper/ScanHistoryMapper.java`
- Modify: `src/main/resources/mapper/ScanHistoryMapper.xml`

#### Step 2.1: 在 ScanHistoryMapper.java 中添加方法

打开 `ScanHistoryMapper.java`，在类末尾添加：

```java
/**
 * 查询指定状态的扫描记录（最新的）
 */
DashboardScanHistory selectByStatus(@Param("status") String status);

/**
 * 查询最近一次完成的扫描记录（completed 或 failed）
 */
DashboardScanHistory selectLatestCompleted();
```

#### Step 2.2: 在 ScanHistoryMapper.xml 中添加 SQL

打开 `ScanHistoryMapper.xml`，在 `<mapper>` 标签内添加：

```xml
<select id="selectByStatus" resultMap="BaseResultMap">
    SELECT * FROM dashboard_scan_history
    WHERE status = #{status}
    ORDER BY started_at DESC
    LIMIT 1
</select>

<select id="selectLatestCompleted" resultMap="BaseResultMap">
    SELECT * FROM dashboard_scan_history
    WHERE status IN ('completed', 'failed')
    ORDER BY finished_at DESC
    LIMIT 1
</select>
```

#### Step 2.3: 验证编译

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

---

### Task 3: 创建 ScanService

**Files:**
- Create: `src/main/java/com/company/clawboard/service/ScanService.java`

#### Step 3.1: 创建 ScanService 类

```java
package com.company.clawboard.service;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.dto.ScanConflictResponse;
import com.company.clawboard.dto.ScanDetail;
import com.company.clawboard.dto.ScanStatusResponse;
import com.company.clawboard.dto.ScanTriggerResponse;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.ScanHistoryMapper;
import com.company.clawboard.scanner.ScanOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {
    
    private final ScanOrchestrator scanOrchestrator;
    private final ScanHistoryMapper scanHistoryMapper;
    private final ClawboardProperties properties;
    
    /**
     * 检查并触发扫描
     * - 如果有 running 记录 → 返回 409 Conflict
     * - 如果没有 → 创建记录，异步执行，返回 200 OK
     */
    public Object checkAndTriggerScan(String triggerType) {
        // 1. 检查是否有 running 状态的记录
        DashboardScanHistory running = scanHistoryMapper.selectByStatus("running");
        if (running != null) {
            log.warn("Scan skipped: previous scan still running (scanId={})", running.getId());
            return ScanConflictResponse.from(running);
        }
        
        // 2. 创建新的扫描记录（status='running'）
        Long scanId = scanOrchestrator.createScanRecord(triggerType);
        
        // 3. 异步执行扫描
        scanOrchestrator.executeScanAsync(scanId, triggerType);
        
        // 4. 立即返回
        ScanTriggerResponse response = new ScanTriggerResponse();
        response.setScanId(scanId);
        response.setTriggerType(triggerType);
        response.setStatus("triggered");
        response.setMessage("Scan triggered successfully");
        response.setStartedAt(LocalDateTime.now());
        
        return response;
    }
    
    /**
     * 获取扫描状态
     */
    public ScanStatusResponse getScanStatus() {
        // 1. 查询当前运行的扫描
        DashboardScanHistory currentScan = scanHistoryMapper.selectByStatus("running");
        
        // 2. 查询最近一次完成的扫描
        DashboardScanHistory lastCompleted = scanHistoryMapper.selectLatestCompleted();
        
        // 3. 计算下次定时任务执行时间
        LocalDateTime nextScheduledAt = calculateNextScheduledTime();
        
        ScanStatusResponse response = new ScanStatusResponse();
        response.setScanning(currentScan != null);
        response.setCurrentScan(ScanDetail.from(currentScan));
        response.setLastCompletedScan(ScanDetail.from(lastCompleted));
        response.setNextScheduledAt(nextScheduledAt);
        
        return response;
    }
    
    /**
     * 根据 cron 表达式计算下次执行时间
     */
    private LocalDateTime calculateNextScheduledTime() {
        try {
            String cron = properties.getScan().getCron();
            if (cron == null || cron.isEmpty()) {
                return null;
            }
            
            CronSequenceGenerator generator = new CronSequenceGenerator(cron);
            Date nextDate = generator.next(new Date());
            return LocalDateTime.ofInstant(nextDate.toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            log.error("Failed to calculate next scheduled time", e);
            return null;
        }
    }
}
```

#### Step 3.2: 验证编译

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

---

### Task 4: 修改 ScanOrchestrator

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

#### Step 4.1: 添加必要的导入

在文件顶部添加：

```java
import org.springframework.scheduling.annotation.Async;
```

#### Step 4.2: 新增 createScanRecord 方法

在 `executeScan()` 方法之前添加：

```java
/**
 * 创建扫描记录（同步）
 * 返回 scanId
 */
public Long createScanRecord(String triggerType) {
    DashboardScanHistory history = new DashboardScanHistory();
    history.setTriggerType(triggerType);
    history.setStatus("running");
    history.setStartedAt(LocalDateTime.now());
    
    // 初始化其他字段为 0
    history.setUsersScanned(0);
    history.setFilesTotal(0);
    history.setFilesProcessed(0);
    history.setNewMessages(0);
    history.setNewTurns(0);
    history.setNewIssues(0);
    history.setNewSkillCalls(0);
    
    scanHistoryMapper.insertAndGetId(history);
    
    log.info("Created scan record: scanId={}, triggerType={}", history.getId(), triggerType);
    return history.getId();
}
```

#### Step 4.3: 修改 executeScan 为 executeScanAsync

找到现有的 `executeScan()` 方法，将其重命名为 `executeScanAsync()`，并添加 `@Async` 注解：

```java
/**
 * 异步执行扫描
 */
@Async("scanExecutor")
public void executeScanAsync(Long scanId, String triggerType) {
    long startTime = System.currentTimeMillis();
    
    log.info("Starting async scan {} (triggerType={})", scanId, triggerType);
    
    try {
        // 1. 加载 accounts
        List<AccountConfig> accounts = accountLoader.loadAccounts();
        if (accounts.isEmpty()) {
            log.warn("No accounts configured, skipping scan");
            finishScan(scanId, null, "completed", startTime, "No accounts configured");
            return;
        }
        
        // 2. 扫描用户目录
        Set<String> scannedEmployeeIds = new HashSet<>();
        int totalFiles = 0;
        int processedFiles = 0;
        int totalMessages = 0;
        int totalTurns = 0;
        int totalIssues = 0;
        int totalSkills = 0;
        
        for (AccountConfig account : accounts) {
            // ... 保持原有的扫描逻辑不变 ...
            // （这里省略详细代码，保持原有逻辑）
        }
        
        // 3. 创建历史记录
        DashboardScanHistory history = scanHistoryMapper.selectById(scanId);
        if (history == null) {
            log.error("Scan history not found for scanId={}", scanId);
            return;
        }
        
        history.setUsersScanned(scannedEmployeeIds.size());
        history.setFilesTotal(totalFiles);
        history.setFilesProcessed(processedFiles);
        history.setNewMessages(totalMessages);
        history.setNewTurns(totalTurns);
        history.setNewIssues(totalIssues);
        history.setNewSkillCalls(totalSkills);
        
        // 4. 小时统计聚合
        try {
            if (!scannedEmployeeIds.isEmpty()) {
                Map<String, Set<LocalDateTime>> hoursByEmployee = dataIngestionService.getAndClearScannedHours();
                
                Set<LocalDateTime> allChangedHours = hoursByEmployee.values().stream()
                    .flatMap(Set::stream)
                    .collect(java.util.stream.Collectors.toSet());
                
                log.info("Collected {} distinct hours from {} employees", 
                    allChangedHours.size(), hoursByEmployee.size());
                
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
        
        // 5. 报告生成
        try {
            LocalDateTime scanStartTime = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(startTime), 
                java.time.ZoneId.systemDefault());
            reportGenerator.generateReport(scanId, scanStartTime);
        } catch (Exception e) {
            log.error("Failed to generate report for scan {}", scanId, e);
        }
        
        // 6. 保存文件列表
        try {
            saveScannedFilesList(scanId);
            saveSkippedFilesList(scanId);
        } catch (Exception e) {
            log.error("Failed to save files lists", e);
        }
        
        // 7. 完成扫描（包括 duration_ms）
        finishScan(scanId, history, "completed", startTime, null);
        
        log.info("Scan {} completed in {}ms - Users: {}, Files: {}/{}, Messages: {}, Turns: {}, Issues: {}, Skills: {}",
                scanId, history.getDurationMs(),
                history.getUsersScanned(),
                processedFiles, totalFiles,
                totalMessages, totalTurns, totalIssues, totalSkills);
        
    } catch (Exception e) {
        log.error("Scan {} failed", scanId, e);
        finishScan(scanId, null, "failed", startTime, e.getMessage());
    }
}
```

**注意**: 保持原有的扫描逻辑不变，只是将 `executeScan()` 改名为 `executeScanAsync()` 并添加 `@Async` 注解。

#### Step 4.4: 移除 AtomicBoolean 相关代码

找到以下代码并删除：

```java
private final AtomicBoolean scanning = new AtomicBoolean(false);
private volatile Long currentScanId = null;
```

以及在 `executeScan()` 开头的并发控制代码：

```java
// Concurrency control: prevent overlapping scans
if (!scanning.compareAndSet(false, true)) {
    log.warn("Scan skipped: previous scan still running (scanId={})", currentScanId);
    return null;
}
```

以及在 `finally` 块中的重置代码：

```java
scanning.set(false);
currentScanId = null;
```

#### Step 4.5: 调整 finishScan 调用时机

确保 `finishScan()` 在所有操作完成后调用（包括小时聚合、报告生成、文件列表保存）。

查看当前的 `executeScanAsync()` 方法，确认 `finishScan()` 在最后调用。

#### Step 4.6: 验证编译

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

---

### Task 5: 修改 ScanController

**Files:**
- Modify: `src/main/java/com/company/clawboard/controller/ScanController.java`

#### Step 5.1: 注入 ScanService

在 `ScanController` 类中添加：

```java
private final ScanService scanService;

public ScanController(ScanService scanService) {
    this.scanService = scanService;
}
```

或者如果使用 `@RequiredArgsConstructor`：

```java
private final ScanService scanService;
```

#### Step 5.2: 修改 triggerScan 方法

找到 `triggerScan()` 方法，替换为：

```java
@PostMapping("/trigger")
public ApiResponse<?> triggerScan() {
    Object result = scanService.checkAndTriggerScan("manual");
    
    // 判断是成功还是冲突
    if (result instanceof com.company.clawboard.dto.ScanConflictResponse) {
        return ApiResponse.error(409, "A scan is already running", result);
    } else {
        return ApiResponse.ok(result);
    }
}
```

#### Step 5.3: 修改 getStatus 方法

找到 `getStatus()` 方法，替换为：

```java
@GetMapping("/status")
public ApiResponse<?> getStatus() {
    return ApiResponse.ok(scanService.getScanStatus());
}
```

#### Step 5.4: 移除旧的依赖

如果 `ScanController` 中有直接引用 `ScanOrchestrator` 的代码，可以保留（因为其他接口可能还需要），但触发和状态查询现在通过 `ScanService`。

#### Step 5.5: 验证编译

```bash
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

---

### Task 6: 编写单元测试

**Files:**
- Create: `src/test/java/com/company/clawboard/service/ScanServiceTest.java`

#### Step 6.1: 创建 ScanServiceTest

```java
package com.company.clawboard.service;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.dto.ScanConflictResponse;
import com.company.clawboard.dto.ScanStatusResponse;
import com.company.clawboard.dto.ScanTriggerResponse;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.ScanHistoryMapper;
import com.company.clawboard.scanner.ScanOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScanServiceTest {
    
    @Mock
    private ScanOrchestrator scanOrchestrator;
    
    @Mock
    private ScanHistoryMapper scanHistoryMapper;
    
    @Mock
    private ClawboardProperties properties;
    
    @InjectMocks
    private ScanService scanService;
    
    private DashboardScanHistory runningScan;
    
    @BeforeEach
    void setUp() {
        runningScan = new DashboardScanHistory();
        runningScan.setId(123L);
        runningScan.setStatus("running");
        runningScan.setTriggerType("manual");
        runningScan.setStartedAt(LocalDateTime.now().minusMinutes(5));
    }
    
    @Test
    void testCheckAndTriggerScan_NoRunning_Succeeds() {
        // Arrange
        when(scanHistoryMapper.selectByStatus("running")).thenReturn(null);
        when(scanOrchestrator.createScanRecord("manual")).thenReturn(456L);
        
        // Act
        Object result = scanService.checkAndTriggerScan("manual");
        
        // Assert
        assertTrue(result instanceof ScanTriggerResponse);
        ScanTriggerResponse response = (ScanTriggerResponse) result;
        assertEquals(456L, response.getScanId());
        assertEquals("manual", response.getTriggerType());
        assertEquals("triggered", response.getStatus());
        assertEquals("Scan triggered successfully", response.getMessage());
        
        verify(scanOrchestrator).executeScanAsync(456L, "manual");
    }
    
    @Test
    void testCheckAndTriggerScan_Running_ReturnsConflict() {
        // Arrange
        when(scanHistoryMapper.selectByStatus("running")).thenReturn(runningScan);
        
        // Act
        Object result = scanService.checkAndTriggerScan("manual");
        
        // Assert
        assertTrue(result instanceof ScanConflictResponse);
        ScanConflictResponse response = (ScanConflictResponse) result;
        assertEquals(123L, response.getScanId());
        assertEquals("running", response.getStatus());
        assertNotNull(response.getDurationMs());
        assertTrue(response.getDurationMs() > 0);
        
        verify(scanOrchestrator, never()).createScanRecord(anyString());
        verify(scanOrchestrator, never()).executeScanAsync(anyLong(), anyString());
    }
    
    @Test
    void testGetScanStatus_WithRunningScan() {
        // Arrange
        DashboardScanHistory completedScan = new DashboardScanHistory();
        completedScan.setId(122L);
        completedScan.setStatus("completed");
        completedScan.setStartedAt(LocalDateTime.now().minusHours(1));
        completedScan.setFinishedAt(LocalDateTime.now().minusMinutes(30));
        completedScan.setDurationMs(1800000L);
        
        when(scanHistoryMapper.selectByStatus("running")).thenReturn(runningScan);
        when(scanHistoryMapper.selectLatestCompleted()).thenReturn(completedScan);
        when(properties.getScan()).thenReturn(new ClawboardProperties.ScanConfig());
        when(properties.getScan().getCron()).thenReturn("0 0 * * * *");
        
        // Act
        ScanStatusResponse response = scanService.getScanStatus();
        
        // Assert
        assertTrue(response.getScanning());
        assertNotNull(response.getCurrentScan());
        assertEquals(123L, response.getCurrentScan().getScanId());
        assertEquals("running", response.getCurrentScan().getStatus());
        
        assertNotNull(response.getLastCompletedScan());
        assertEquals(122L, response.getLastCompletedScan().getScanId());
        assertEquals("completed", response.getLastCompletedScan().getStatus());
        
        assertNotNull(response.getNextScheduledAt());
    }
    
    @Test
    void testGetScanStatus_NoRunningScan() {
        // Arrange
        when(scanHistoryMapper.selectByStatus("running")).thenReturn(null);
        when(scanHistoryMapper.selectLatestCompleted()).thenReturn(null);
        when(properties.getScan()).thenReturn(new ClawboardProperties.ScanConfig());
        when(properties.getScan().getCron()).thenReturn("0 0 * * * *");
        
        // Act
        ScanStatusResponse response = scanService.getScanStatus();
        
        // Assert
        assertFalse(response.getScanning());
        assertNull(response.getCurrentScan());
        assertNull(response.getLastCompletedScan());
    }
}
```

#### Step 6.2: 运行单元测试

```bash
mvn test -Dtest=ScanServiceTest
```

Expected: All tests PASS

---

### Task 7: 编写 Orchestrator 异步测试

**Files:**
- Create: `src/test/java/com/company/clawboard/scanner/ScanOrchestratorAsyncTest.java`

#### Step 7.1: 创建 ScanOrchestratorAsyncTest

```java
package com.company.clawboard.scanner;

import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.ScanHistoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScanOrchestratorAsyncTest {
    
    @Mock
    private ScanHistoryMapper scanHistoryMapper;
    
    @InjectMocks
    private ScanOrchestrator scanOrchestrator;
    
    @Test
    void testCreateScanRecord_InsertsRecord() {
        // Arrange
        DashboardScanHistory history = new DashboardScanHistory();
        history.setId(789L);
        when(scanHistoryMapper.insertAndGetId(any(DashboardScanHistory.class))).thenAnswer(invocation -> {
            DashboardScanHistory h = invocation.getArgument(0);
            h.setId(789L);
            return 789L;
        });
        
        // Act
        Long scanId = scanOrchestrator.createScanRecord("manual");
        
        // Assert
        assertEquals(789L, scanId);
        verify(scanHistoryMapper).insertAndGetId(any(DashboardScanHistory.class));
    }
}
```

#### Step 7.2: 运行单元测试

```bash
mvn test -Dtest=ScanOrchestratorAsyncTest
```

Expected: All tests PASS

---

### Task 8: 更新集成测试

**Files:**
- Modify: `src/test/java/com/company/clawboard/controller/ScanControllerIntegrationTest.java`

#### Step 8.1: 查找现有的 trigger 测试

打开 `ScanControllerIntegrationTest.java`，找到测试 `POST /api/v1/scan/trigger` 的方法。

#### Step 8.2: 更新测试以验证异步行为

修改测试，验证：
1. 立即返回（不等待扫描完成）
2. 返回 200 OK 和 scanId
3. 可以通过 `/scan/status` 查询状态

示例：

```java
@Test
void testTriggerScan_Async_ReturnsImmediately() throws Exception {
    mockMvc.perform(post("/api/v1/scan/trigger")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.scanId").exists())
        .andExpect(jsonPath("$.data.status").value("triggered"));
}

@Test
void testTriggerScan_Conflict_Returns409() throws Exception {
    // 先触发一个扫描
    mockMvc.perform(post("/api/v1/scan/trigger"))
        .andExpect(status().isOk());
    
    // 再次触发，应该返回 409
    mockMvc.perform(post("/api/v1/scan/trigger")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(409))
        .andExpect(jsonPath("$.data.scanId").exists())
        .andExpect(jsonPath("$.data.status").value("running"));
}

@Test
void testGetStatus_ReturnsCompleteInfo() throws Exception {
    mockMvc.perform(get("/api/v1/scan/status")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.scanning").exists())
        .andExpect(jsonPath("$.data.currentScan").exists())
        .andExpect(jsonPath("$.data.lastCompletedScan").exists())
        .andExpect(jsonPath("$.data.nextScheduledAt").exists());
}
```

#### Step 8.3: 运行集成测试

```bash
mvn test -Dtest=ScanControllerIntegrationTest
```

Expected: All tests PASS

---

### Task 9: 完整编译和测试

#### Step 9.1: 完整编译

```bash
mvn clean compile
```

Expected: BUILD SUCCESS

#### Step 9.2: 运行所有测试

```bash
mvn test
```

Expected: All tests PASS

#### Step 9.3: 打包验证

```bash
mvn package -DskipTests
```

Expected: BUILD SUCCESS

---

### Task 10: 手动测试准备

#### Step 10.1: 清空数据库

```powershell
Get-Content scripts\reset-database.sql | & "D:\Program\Database\mysql-5.7.44-winx64\bin\mysql.exe" -uclawboard -pClqc@1234 clawboard
```

#### Step 10.2: 启动应用

```powershell
.\run-app-optimized.bat
```

#### Step 10.3: 测试触发扫描

```powershell
Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing
```

Expected: 立即返回 200 OK，包含 scanId

#### Step 10.4: 测试状态查询

```powershell
Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/status -Method GET -UseBasicParsing
```

Expected: 返回完整的状态信息

#### Step 10.5: 测试冲突场景

```powershell
# 第一次触发
Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing

# 第二次触发（应该返回 409）
Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing
```

Expected: 第二次返回 409 Conflict

---

## 验收标准

实施完成后，验证以下功能：

- ✅ `/scan/trigger` 立即返回（< 100ms）
- ✅ 返回的响应包含 scanId、status="triggered"
- ✅ 后台异步执行扫描
- ✅ `/scan/status` 返回完整的当前和历史扫描信息
- ✅ 当有运行中的扫描时，再次触发返回 409 Conflict
- ✅ 409 响应包含当前运行扫描的详细信息（scanId、startedAt、durationMs）
- ✅ `duration_ms` 包含所有操作时间（扫描 + 聚合 + 报告生成）
- ✅ 所有单元测试通过
- ✅ 所有集成测试通过
- ✅ 应用可以正常启动和运行

---

## 注意事项

1. **不要提交 git**：实施过程中不要使用 git 提交，由用户自行提交
2. **保持原有逻辑**：除了异步化和状态管理，保持原有的扫描逻辑不变
3. **异常处理**：确保异步方法中的异常被正确捕获并更新数据库状态
4. **线程池配置**：确认 `scanExecutor` 线程池大小为 1
5. **日志记录**：添加足够的日志以便调试和监控

---

## 回滚方案

如果出现问题，可以：

1. 恢复 `ScanController.java` 到之前的版本
2. 恢复 `ScanOrchestrator.java` 到之前的版本
3. 删除 `ScanService.java`
4. 删除新增的 DTO 类
5. 删除新增的 Mapper 方法

---

**计划完成**。准备好开始实施了吗？
