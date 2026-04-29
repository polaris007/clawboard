# Session Transcript 扫描异步化与状态管理优化设计

**日期**: 2026-04-29  
**版本**: v1.0  
**状态**: 待审阅

---

## 1. 背景与目标

### 1.1 当前问题

当前扫描系统存在以下问题：

1. **同步阻塞**：`/scan/trigger` 接口同步执行整个扫描流程，用户需要等待扫描完成才能收到响应
2. **duration_ms 不完整**：只计算到 `finishScan()`，不包括后续的小时统计聚合和报告生成
3. **并发控制不友好**：使用 `AtomicBoolean` 内存标志，应用重启后状态丢失
4. **状态查询信息不足**：`/scan/status` 返回硬编码的空数据，无法获取实际扫描状态
5. **重复触发无提示**：当有扫描正在运行时，再次触发只返回 `null`，没有明确提示

### 1.2 优化目标

1. **异步化**：`/scan/trigger` 立即返回，后台执行扫描
2. **完整 duration_ms**：包含所有操作时间（扫描 + 聚合 + 报告生成）
3. **数据库驱动的状态管理**：基于 `dashboard_scan_history` 表进行并发控制和状态查询
4. **友好的冲突提示**：当有运行中的扫描时，返回明确的冲突信息和当前扫描详情
5. **完整的状态查询**：`/scan/status` 返回当前和历史扫描的完整信息

---

## 2. 设计方案

### 2.1 整体架构

```
┌─────────────┐         ┌──────────────┐         ┌─────────────────┐
│   Client    │         │  Controller  │         │ ScanOrchestrator│
│             │         │              │         │                 │
│ POST /scan  │────────▶│ checkAnd     │────────▶│ executeScanAsync│
│ /trigger    │         │ triggerScan()│         │ (@Async)        │
│             │◀────────│ (immediate   │         │                 │
│ 200 OK      │         │  response)   │         │ 1. Create record│
│ scanId: 123 │         │              │         │ 2. Scan files   │
│ status:     │         │              │         │ 3. Aggregate    │
│ "triggered" │         │              │         │ 4. Generate rpt │
└─────────────┘         └──────────────┘         │ 5. Update status│
                                                  └─────────────────┘
                                                           │
                                                           ▼
                                                  ┌─────────────────┐
                                                  │   Database      │
                                                  │                 │
                                                  │ dashboard_scan_ │
                                                  │ history         │
                                                  │ status: running │
                                                  │       ↓         │
                                                  │ status: completed│
                                                  └─────────────────┘
```

### 2.2 核心改动

#### 2.2.1 Controller 层

**文件**: `ScanController.java`

**改动点**:

1. **`POST /api/v1/scan/trigger`** - 改为异步触发
   ```java
   @PostMapping("/trigger")
   public ApiResponse<?> triggerScan() {
       return scanService.checkAndTriggerScan("manual");
   }
   ```

2. **`GET /api/v1/scan/status`** - 返回完整状态
   ```java
   @GetMapping("/status")
   public ApiResponse<?> getStatus() {
       return ApiResponse.ok(scanService.getScanStatus());
   }
   ```

#### 2.2.2 Service 层（新增）

**文件**: `ScanService.java`（新建）

**职责**: 
- 协调扫描触发逻辑
- 提供状态查询
- 处理并发控制

**关键方法**:

```java
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
    public ApiResponse<?> checkAndTriggerScan(String triggerType) {
        // 1. 检查是否有 running 状态的记录
        DashboardScanHistory running = scanHistoryMapper.selectByStatus("running");
        if (running != null) {
            // 返回冲突响应
            return ApiResponse.error(409, "A scan is already running", 
                new ScanConflictResponse(running));
        }
        
        // 2. 创建新的扫描记录（status='running'）
        Long scanId = scanOrchestrator.createScanRecord(triggerType);
        
        // 3. 异步执行扫描
        scanOrchestrator.executeScanAsync(scanId, triggerType);
        
        // 4. 立即返回
        return ApiResponse.ok(new ScanTriggerResponse(scanId, triggerType));
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
        
        return new ScanStatusResponse(currentScan, lastCompleted, nextScheduledAt);
    }
    
    private LocalDateTime calculateNextScheduledTime() {
        // 根据 cron 表达式计算下次执行时间
        String cron = properties.getScan().getCron();
        // 使用 CronSequenceGenerator 或类似工具计算
        // ...
    }
}
```

#### 2.2.3 ScanOrchestrator 层

**文件**: `ScanOrchestrator.java`

**改动点**:

1. **拆分 `executeScan()` 方法**
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
       
       scanHistoryMapper.insertAndGetId(history);
       return history.getId();
   }
   
   /**
    * 异步执行扫描
    */
   @Async("scanExecutor")
   public void executeScanAsync(Long scanId, String triggerType) {
       long startTime = System.currentTimeMillis();
       
       try {
           // 1. 加载 accounts
           // 2. 扫描用户目录
           // 3. 并行处理文件
           // 4. 小时统计聚合
           // 5. 报告生成
           // 6. 保存文件列表
           
           // 7. 更新状态为 completed（包括 duration_ms）
           finishScan(scanId, history, "completed", startTime, null);
           
       } catch (Exception e) {
           // 更新状态为 failed
           finishScan(scanId, history, "failed", startTime, e.getMessage());
       }
   }
   ```

2. **调整 `finishScan()` 调用时机**
   - **之前**: 在小时聚合和报告生成之前调用
   - **之后**: 在所有操作完成后调用
   - **效果**: `duration_ms` 包含完整执行时间

3. **移除 `AtomicBoolean` 并发控制**
   - 不再需要内存级别的并发控制
   - 完全依赖数据库状态

#### 2.2.4 Mapper 层

**文件**: `ScanHistoryMapper.java`

**新增方法**:

```java
/**
 * 查询指定状态的扫描记录
 */
DashboardScanHistory selectByStatus(@Param("status") String status);

/**
 * 查询最近一次完成的扫描记录
 */
DashboardScanHistory selectLatestCompleted();
```

**文件**: `ScanHistoryMapper.xml`

**新增 SQL**:

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

#### 2.2.5 DTO 层（新增）

**文件**: `ScanTriggerResponse.java`

```java
public class ScanTriggerResponse {
    private Long scanId;
    private String triggerType;
    private String status;  // "triggered"
    private String message; // "Scan triggered successfully"
    private LocalDateTime startedAt;
}
```

**文件**: `ScanConflictResponse.java`

```java
public class ScanConflictResponse {
    private Long scanId;
    private String status;  // "running"
    private String triggerType;
    private LocalDateTime startedAt;
    private Long durationMs;  // 当前已运行时间
    private String message;   // "A scan is already running"
}
```

**文件**: `ScanStatusResponse.java`

```java
public class ScanStatusResponse {
    private Boolean scanning;
    private ScanDetail currentScan;
    private ScanDetail lastCompletedScan;
    private LocalDateTime nextScheduledAt;
}

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
}
```

---

## 3. 数据流

### 3.1 正常扫描流程

```
1. Client 发送 POST /api/v1/scan/trigger
   ↓
2. ScanService.checkAndTriggerScan()
   ├─ 查询数据库: SELECT * FROM dashboard_scan_history WHERE status='running'
   ├─ 结果: null（没有运行中的扫描）
   ↓
3. ScanOrchestrator.createScanRecord("manual")
   ├─ INSERT INTO dashboard_scan_history (status='running', ...)
   ├─ 返回: scanId = 123
   ↓
4. ScanOrchestrator.executeScanAsync(123, "manual") [@Async]
   ├─ 立即返回给 Controller
   └─ 后台线程继续执行
   ↓
5. Controller 返回 200 OK
   {
     "code": 200,
     "data": {
       "scanId": 123,
       "triggerType": "manual",
       "status": "triggered",
       "message": "Scan triggered successfully",
       "startedAt": "2026-04-29T15:00:00"
     }
   }
   ↓
6. 后台线程执行扫描
   ├─ 加载 accounts
   ├─ 扫描文件
   ├─ 解析入库
   ├─ 小时统计聚合
   ├─ 报告生成
   └─ 保存文件列表
   ↓
7. finishScan(123, "completed", startTime, null)
   ├─ UPDATE dashboard_scan_history 
   │   SET status='completed', 
   │       finished_at=NOW(), 
   │       duration_ms=(NOW() - startTime),
   │       users_scanned=..., 
   │       files_processed=...
   │   WHERE id=123
   └─ 扫描完成
```

### 3.2 冲突场景（有运行中的扫描）

```
1. Client 发送 POST /api/v1/scan/trigger
   ↓
2. ScanService.checkAndTriggerScan()
   ├─ 查询数据库: SELECT * FROM dashboard_scan_history WHERE status='running'
   ├─ 结果: { scanId: 122, status: 'running', startedAt: '2026-04-29T14:50:00' }
   ↓
3. 返回 409 Conflict
   {
     "code": 409,
     "message": "A scan is already running",
     "data": {
       "scanId": 122,
       "status": "running",
       "triggerType": "manual",
       "startedAt": "2026-04-29T14:50:00",
       "durationMs": 60000,
       "message": "A scan is already running. Please wait for it to complete."
     }
   }
```

### 3.3 状态查询流程

```
1. Client 发送 GET /api/v1/scan/status
   ↓
2. ScanService.getScanStatus()
   ├─ 查询当前运行: SELECT * FROM dashboard_scan_history WHERE status='running'
   ├─ 查询最近完成: SELECT * FROM dashboard_scan_history WHERE status IN ('completed','failed') ORDER BY finished_at DESC LIMIT 1
   ├─ 计算下次调度时间
   ↓
3. 返回 200 OK
   {
     "code": 200,
     "data": {
       "scanning": true,
       "currentScan": {
         "scanId": 123,
         "triggerType": "manual",
         "status": "running",
         "startedAt": "2026-04-29T15:00:00",
         "durationMs": 30000,
         "usersScanned": 5,
         "filesProcessed": 100,
         "filesTotal": 150
       },
       "lastCompletedScan": {
         "scanId": 122,
         "status": "completed",
         "startedAt": "2026-04-29T14:30:00",
         "finishedAt": "2026-04-29T14:45:00",
         "durationMs": 900000,
         "usersScanned": 10,
         "filesProcessed": 200,
         "newMessages": 5000
       },
       "nextScheduledAt": "2026-04-29T16:00:00"
     }
   }
```

---

## 4. 关键技术决策

### 4.1 为什么选择方案 B（数据库驱动的并发控制）？

**优点**:
1. **状态持久化**：应用重启后仍能检测到未完成的扫描
2. **单一事实来源**：数据库是扫描状态的唯一权威来源
3. **可追溯性**：所有扫描记录都保存在数据库中，便于审计和调试
4. **分布式友好**：如果未来需要多实例部署，数据库锁可以防止并发

**缺点**:
1. **性能开销**：每次触发都需要查询数据库
2. **复杂性**：比 `AtomicBoolean` 复杂

**权衡**: 
- 扫描是低频操作（几分钟到几十分钟一次）
- 数据库查询开销可以忽略不计
- 健壮性和可维护性更重要

### 4.2 为什么调整 duration_ms 计算时机？

**之前的问题**:
- `duration_ms` 只包含文件扫描和入库时间
- 不包括小时统计聚合（可能耗时几秒到几十秒）
- 不包括报告生成（可能耗时几秒）
- 导致用户看到的耗时与实际感受不一致

**调整后的好处**:
- `duration_ms` 反映从请求到所有操作完成的总时间
- 更准确地反映扫描的实际耗时
- 便于性能分析和优化

### 4.3 为什么使用 @Async 而不是手动创建线程？

**优点**:
1. **Spring 管理**：线程池由 Spring 管理，生命周期清晰
2. **配置统一**：复用现有的 `scanExecutor` 线程池配置
3. **异常处理**：Spring 提供统一的异常处理机制
4. **监控集成**：可以与 Spring Actuator 等监控工具集成

**注意事项**:
- 确保 `scanExecutor` 线程池大小为 1（单线程执行）
- 避免多个扫描同时执行

---

## 5. 边界情况处理

### 5.1 应用重启

**场景**: 扫描正在运行时应用重启

**处理**:
1. 重启后，数据库中的记录仍然是 `status='running'`
2. 下次触发扫描时，会检测到该记录并返回冲突
3. **手动干预**: 管理员需要将旧的 running 记录标记为 `failed`

**改进建议**（可选）:
- 启动时检查是否有 `status='running'` 且 `started_at < NOW() - 1 hour` 的记录
- 自动标记为 `failed`，错误消息："Application restarted during scan"

### 5.2 扫描异常终止

**场景**: 扫描过程中抛出未捕获的异常

**处理**:
```java
try {
    // 执行扫描
} catch (Exception e) {
    finishScan(scanId, history, "failed", startTime, e.getMessage());
}
```

- `finally` 块确保 `scanning` 标志被重置（如果保留的话）
- 数据库记录更新为 `status='failed'`

### 5.3 长时间运行的扫描

**场景**: 扫描需要很长时间（例如几小时）

**处理**:
- 前端可以通过 `/scan/status` 轮询查看进度
- 可以考虑添加进度更新机制（可选）

### 5.4 定时任务与手动触发冲突

**场景**: 定时任务触发时，手动触发的扫描仍在运行

**处理**:
- 定时任务也通过 `checkAndTriggerScan()` 触发
- 如果检测到 running 记录，跳过本次定时任务
- 记录日志："Scheduled scan skipped: manual scan still running"

---

## 6. 测试策略

### 6.1 单元测试

1. **ScanServiceTest**
   - `testCheckAndTriggerScan_NoRunning_Succeeds()`
   - `testCheckAndTriggerScan_Running_ReturnsConflict()`
   - `testGetScanStatus_WithRunningScan()`
   - `testGetScanStatus_NoRunningScan()`

2. **ScanOrchestratorTest**
   - `testCreateScanRecord_InsertsRecord()`
   - `testExecuteScanAsync_CompletesSuccessfully()`
   - `testExecuteScanAsync_HandlesException()`

### 6.2 集成测试

1. **ScanControllerIntegrationTest**
   - `testTriggerScan_Async_ReturnsImmediately()`
   - `testTriggerScan_Conflict_Returns409()`
   - `testGetStatus_ReturnsCompleteInfo()`

2. **端到端测试**
   - 触发扫描 → 验证立即返回
   - 轮询状态 → 验证状态变化
   - 等待完成 → 验证最终状态

### 6.3 性能测试

- 验证异步化后响应时间 < 100ms
- 验证后台扫描不影响其他接口
- 验证并发控制有效性

---

## 7. 迁移计划

### 7.1 数据库变更

**无需变更**：`dashboard_scan_history` 表结构已经满足需求

### 7.2 代码变更顺序

1. **Step 1**: 新增 DTO 类
2. **Step 2**: 新增 Mapper 方法
3. **Step 3**: 创建 `ScanService`
4. **Step 4**: 修改 `ScanOrchestrator`（拆分方法、添加 @Async）
5. **Step 5**: 修改 `ScanController`
6. **Step 6**: 编写测试
7. **Step 7**: 集成测试
8. **Step 8**: 部署

### 7.3 回滚方案

如果出现问题，可以：
1. 恢复 `ScanController` 和 `ScanOrchestrator` 到之前的版本
2. 删除 `ScanService`
3. 删除新增的 DTO 和 Mapper 方法

---

## 8. 预期效果

### 8.1 用户体验改进

| 指标 | 优化前 | 优化后 | 改善 |
|------|--------|--------|------|
| `/scan/trigger` 响应时间 | 几分钟到几十分钟 | < 100ms | **99%+** |
| 状态可见性 | ❌ 无 | ✅ 实时查询 | **显著提升** |
| 冲突提示 | ❌ 返回 null | ✅ 明确提示 | **显著提升** |
| duration_ms 准确性 | ⚠️ 不完整 | ✅ 完整 | **提升** |

### 8.2 系统稳定性改进

- ✅ 应用重启后状态不丢失
- ✅ 并发控制更可靠
- ✅ 异常情况有明确记录
- ✅ 便于问题排查和审计

---

## 9. 风险与缓解

### 9.1 风险 1：线程池配置不当

**风险**: 如果 `scanExecutor` 线程池大小 > 1，可能导致多个扫描同时执行

**缓解**: 
- 确保线程池大小为 1
- 在配置文件中明确注释

### 9.2 风险 2：数据库查询性能

**风险**: 频繁查询数据库可能影响性能

**缓解**: 
- 扫描是低频操作，查询开销可忽略
- 如有必要，可以添加缓存（但会增加复杂性）

### 9.3 风险 3：异步异常处理

**风险**: 异步方法中的异常可能被吞掉

**缓解**: 
- 在 `executeScanAsync()` 中使用 try-catch
- 确保异常情况下更新数据库状态为 `failed`
- 添加日志记录

---

## 10. 后续优化建议

### 10.1 进度实时更新（可选）

- 在 `dashboard_scan_progress` 表中定期更新进度
- 前端可以实时显示扫描进度（例如：已处理 100/200 文件）

### 10.2 取消扫描功能（可选）

- 添加 `POST /api/v1/scan/{scanId}/cancel` 接口
- 支持用户主动取消长时间运行的扫描

### 10.3 扫描历史记录分页（可选）

- 当前的 `/scan/history` 返回空数据
- 实现真正的分页查询，支持查看历史扫描记录

### 10.4 启动时自动清理（可选）

- 应用启动时检查是否有 `status='running'` 的旧记录
- 自动标记为 `failed`，避免手动干预

---

## 11. 附录

### 11.1 相关文件清单

**新增文件**:
- `src/main/java/com/company/clawboard/service/ScanService.java`
- `src/main/java/com/company/clawboard/dto/ScanTriggerResponse.java`
- `src/main/java/com/company/clawboard/dto/ScanConflictResponse.java`
- `src/main/java/com/company/clawboard/dto/ScanStatusResponse.java`
- `src/main/java/com/company/clawboard/dto/ScanDetail.java`

**修改文件**:
- `src/main/java/com/company/clawboard/controller/ScanController.java`
- `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`
- `src/main/java/com/company/clawboard/mapper/ScanHistoryMapper.java`
- `src/main/resources/mapper/ScanHistoryMapper.xml`

**测试文件**:
- `src/test/java/com/company/clawboard/service/ScanServiceTest.java`
- `src/test/java/com/company/clawboard/scanner/ScanOrchestratorTest.java`
- `src/test/java/com/company/clawboard/controller/ScanControllerIntegrationTest.java`（增强）

### 11.2 参考文档

- [ClawBoard 实现计划](./2026-04-18-clawboard-implementation-plan.md)
- [OpenClaw 监控仪表盘设计](./2026-04-18-openclaw-monitoring-dashboard-design.md)
- [API 接口文档 v1.2](../api/API接口文档v1.2.md)

---

**文档版本**: v1.0  
**最后更新**: 2026-04-29  
**下一步**: 提交审阅，获得批准后进入实施阶段
