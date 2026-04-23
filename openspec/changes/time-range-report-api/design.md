## Context

当前系统在每次扫描完成后会自动生成 Markdown 格式的报告，但用户无法针对特定时间范围进行灵活的数据分析。现有的 `ScanOrchestrator` 在扫描结束后调用 `ReportGenerator.generateReport(scanId)`，基于单次扫描的数据生成报告。

业务方需要能够：
- 按自定义时间范围（startTime, endTime）统计对话轮次
- 生成与扫描报告格式一致的 Markdown 报告
- 报告保存到相同目录结构（`reports/{yyyy-MM-dd}/`）

## Goals / Non-Goals

**Goals:**
- 提供 REST API 接受时间范围参数，触发报告生成
- 复用现有的 `ReportGenerator` 逻辑，确保报告格式一致
- 采用异步处理避免长时间阻塞 HTTP 请求
- 报告文件存储到 `reports/{date}/` 目录，与扫描报告统一管理
- 提供任务状态查询接口，支持前端轮询

**Non-Goals:**
- 不修改现有扫描报告生成逻辑
- 不支持实时报告更新（仅基于数据库已有数据）
- 不提供报告预览功能（直接返回文件路径）
- 不实现报告缓存机制（每次请求重新生成）

## Decisions

### Decision 1: 异步任务处理方式

**选择**: 使用 Spring `@Async` + `CompletableFuture` + 内存任务跟踪

**理由**:
- 简单轻量，无需引入消息队列
- 适合中小规模部署（单机或少量实例）
- 任务状态存储在内存 Map 中，查询快速

**替代方案**:
- Quartz/Spring Scheduler: 过度复杂，适合定时任务而非即时触发
- Redis + 消息队列: 需要额外基础设施，增加运维成本

**风险**: 应用重启后任务状态丢失 → 可接受，用户可重新触发

---

### Decision 2: 数据统计来源

**选择**: 直接从 `dashboard_conversation_turn` 表查询，基于 `start_time` 过滤

**理由**:
- `start_time` 字段已有索引（`idx_employee_time`, `idx_time`）
- 对话轮次是报告的核心统计单元
- 可通过 JOIN 关联其他表获取详细信息

**SQL 示例**:
```sql
SELECT * FROM dashboard_conversation_turn
WHERE start_time >= #{startTime} AND start_time <= #{endTime}
ORDER BY start_time ASC
```

---

### Decision 3: 报告生成复用策略

**选择**: 扩展现有 `ReportGenerator`，新增方法 `generateReportByTimeRange(startTime, endTime)`

**理由**:
- 保持报告格式完全一致
- 避免代码重复
- 易于维护

**实现方式**:
- 提取公共报告生成逻辑到私有方法
- 原 `generateReport(scanId)` 调用新方法，传入从 scan_id 查询的时间范围
- 新 API 直接调用新方法，传入用户指定的时间范围

---

### Decision 4: 任务 ID 生成

**选择**: 使用 UUID.randomUUID().toString()

**理由**:
- 全局唯一，避免冲突
- 无需数据库自增 ID
- 适合分布式场景（未来扩展）

---

### Decision 5: 报告文件命名

**选择**: `time-range-report-{startTime}-{endTime}.md`

**示例**: `time-range-report-2026-04-20-00-00-00-2026-04-23-23-59-59.md`

**理由**:
- 清晰标识时间范围
- 避免文件名冲突
- 便于排序和查找

## Risks / Trade-offs

### Risk 1: 大时间范围导致性能问题
- **影响**: 查询数百万条记录可能耗时较长
- **缓解**: 
  - 添加时间范围限制（最大 90 天）
  - 数据库索引优化
  - 考虑分页聚合统计

### Risk 2: 并发报告生成占用资源
- **影响**: 多个用户同时触发生成可能导致服务器负载过高
- **缓解**:
  - 限制并发任务数（最多 5 个）
  - 监控 JVM 内存和 CPU 使用率

### Risk 3: 磁盘空间不足
- **影响**: 频繁生成报告可能占满磁盘
- **缓解**:
  - 定期清理旧报告（保留 30 天）
  - 监控磁盘使用率并告警

### Trade-off: 内存任务状态 vs 持久化
- **选择**: 内存存储（简单快速）
- **权衡**: 重启后状态丢失，但实现成本低
- **未来改进**: 如需持久化，可迁移到 Redis 或数据库
