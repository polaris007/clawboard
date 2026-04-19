# 扫描功能使用说明

## 当前状态

✅ **多线程扫描功能已实现**
- 使用 8 线程池并行扫描不同用户目录
- 每个线程负责一个完整用户的文件扫描
- 同一用户的文件串行处理（避免冲突）

❌ **增量扫描尚未实现**
- 每次都会重新扫描所有文件
- 依赖数据库主键去重（`INSERT IGNORE INTO`）
- `ScanProgressService` 已创建但未使用

## 如何触发扫描

### 方式1：通过 API（需要应用运行）

```bash
# 启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 触发扫描
curl -X POST http://localhost:8080/api/scan/trigger
```

### 方式2：定时任务（自动）

配置在 `application.yml`：
```yaml
clawboard:
  scan:
    enabled: true
    cron: "0 0 */6 * * *"  # 每6小时执行一次
```

### 方式3：编程方式

```java
@Autowired
private ScanOrchestrator scanOrchestrator;

// 手动触发
Long scanId = scanOrchestrator.executeScan("manual");
```

## 扫描流程

```
1. 获取扫描锁（防止重叠）
   └─ AtomicBoolean CAS 检查
   
2. 创建扫描历史记录
   └─ INSERT INTO dashboard_scan_history
   
3. 加载用户映射（accounts.csv）
   
4. 扫描用户目录
   └─ UserScanner.scanUsers()
   
5. 并行提交扫描任务（8线程池）
   ├─ CompletableFuture.supplyAsync(() -> scanUserDirectory(user), scanExecutor)
   ├─ 线程1 → 用户001
   ├─ 线程2 → 用户002
   ├─ ...
   └─ 线程8 → 用户008
   
6. 每个线程扫描单个用户
   ├─ 递归查找所有 .jsonl 文件
   ├─ 逐个解析文件
   ├─ 批量入库（DataIngestionService）
   └─ 统计结果
   
7. 聚合所有用户的结果
   
8. 更新扫描历史
   └─ UPDATE dashboard_scan_history SET status='completed'
   
9. 生成报告
   └─ ReportGenerator.generateReport(scanId)
```

## 报告生成

报告位置：`scripts/reports/{yyyy-MM-dd}/transcript-comprehensive-issues.md`

可配置（`application.yml`）：
```yaml
clawboard:
  reports:
    output-dir: scripts/reports  # 自定义报告目录
```

## 性能优化建议

### 当前瓶颈
- ❌ 无增量扫描（每次都全量读取）
- ⚠️ NAS I/O 是主要瓶颈
- ⚠️ 数据库连接池大小需 ≥ 线程数

### 建议配置
```yaml
clawboard:
  scan:
    thread-pool-size: 8  # 根据 NAS I/O 能力调整
    
spring:
  datasource:
    hikari:
      maximum-pool-size: 16  # 至少是 thread-pool-size 的2倍
```

### 未来优化方向
1. **实现增量扫描**（高优先级）
   - 检查 `dashboard_scan_progress` 表
   - 对比文件大小和修改时间
   - 只扫描变化的文件
   
2. **批量入库优化**
   - 当前已使用批量插入
   - 可调整 batch-size 参数

3. **异步报告生成**
   - 报告生成不影响扫描完成
   - 可使用 @Async 注解

## 监控扫描状态

```bash
# 查询当前扫描状态
curl http://localhost:8080/api/scan/status

# 查询扫描历史
curl http://localhost:8080/api/scan/history?page=1&pageSize=10
```

## 故障排查

### 问题1：扫描失败 - 数据库连接错误
```
Caused by: java.net.ConnectException: Connection refused
```
**解决**：确保 MySQL 服务正在运行

### 问题2：扫描跳过 - 已有扫描在运行
```
WARN Scan skipped: previous scan still running (scanId=xxx)
```
**解决**：等待当前扫描完成，或检查是否有死锁

### 问题3：报告为空
**原因**：最新扫描没有处理任何文件
**解决**：检查 NAS 路径配置和数据是否存在

## 代码位置

- 扫描编排器：`ScanOrchestrator.java`
- 用户扫描器：`UserScanner.java`
- 文件扫描器：`TranscriptFileScanner.java`
- 进度服务：`ScanProgressService.java`（未使用）
- 报告生成：`ReportGenerator.java`
