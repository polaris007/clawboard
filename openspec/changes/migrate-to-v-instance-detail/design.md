## Context

当前 ClawBoard 项目从 `openclaw_instances` 表读取用户实例信息时，需要通过 JSON 解析获取用户名和机构号：
- `user_config_json` 字段存储 JSON 格式的用户配置
- 查询时使用 `JSON_EXTRACT(user_config_json, '$.userName')` 提取用户名
- Java 代码中使用 `ObjectMapper.readTree()` 再次解析 JSON 字符串

数据库已提供 `v_instance_detail` 视图，该视图预先解析了 JSON 字段：
```sql
JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`user_config_json`, '$.userName')) as `user_config_name`,
JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`user_config_json`, '$.orgCode')) as `user_config_org_code`
```

同时，项目需要增强 API 功能：
1. `/dashboard/global-stats` 需要增加 `registeredUsers` 字段
2. `/dashboard/usersummary` 的 `status` 字段需要根据实例状态动态判断
3. `dashboard_conversation_turn.log_file_path` 需要记录 transcript 文件路径

## Goals / Non-Goals

**Goals:**
- 将数据源从 `openclaw_instances` 表迁移到 `v_instance_detail` 视图
- 简化 `AccountsCsvReader` 中的 JSON 解析逻辑
- 为 `GlobalStatsResponse` 增加 `registeredUsers` 字段
- 实现 `status` 字段的动态判断逻辑（基于 `v_instance_detail.status`）
- 实现 `log_file_path` 字段的记录功能
- 保持现有 API 向后兼容

**Non-Goals:**
- 不修改 `openclaw_instances` 表结构
- 不修改 `v_instance_detail` 视图定义
- 不修改其他未受影响的 Mapper 和 Service
- 不修改前端代码（新增字段为可选）

## Decisions

### 1. 数据源迁移策略

**决策**: 创建新的 `VInstanceDetailMapper` 替代 `OpenclawInstanceMapper`

**理由**:
- 视图已预先解析 JSON，查询性能更优
- 代码更清晰，不需要在 Java 层解析 JSON
- 未来视图逻辑变化时，只需修改 SQL，不影响 Java 代码

**替代方案**:
- 方案 A: 直接修改 `OpenclawInstanceMapper` 的 SQL 语句
  - 优点：改动最小
  - 缺点：Mapper 名称与实际数据源不一致，语义不清晰
- 方案 B: 保留两个 Mapper 并存
  - 优点：可逐步迁移
  - 缺点：代码冗余，维护成本高

**结论**: 采用新 Mapper 方案，保持代码语义清晰

### 2. registeredUsers 查询实现

**决策**: 在 `VInstanceDetailMapper` 中新增 `countRegisteredUsers()` 方法

**SQL**:
```sql
SELECT COUNT(DISTINCT uid) FROM v_instance_detail WHERE status != 'deleted'
```

**理由**:
- 使用 DISTINCT 确保同一用户的多个实例只计一次
- 排除 `deleted` 状态的实例
- 在 Mapper 层实现，便于单元测试

### 3. status 字段判断逻辑

**决策**: 在 `DashboardService.getUserSummaries()` 中为每个 `employeeId` 查询实例状态

**实现方式**:
- 在 `VInstanceDetailMapper` 中增加 `selectStatusByUid(String uid)` 方法
- 返回 `String status`，判断 `status.equals("running")` 设置 `Boolean status`

**理由**:
- 符合 API 文档要求：`status` 字段表示龙虾是否在线
- 简单直接，不需要额外的缓存或心跳判断

### 4. log_file_path 记录方案

**决策**: 修改 `ParsedTranscript` 记录增加 `filePath` 字段

**数据流**:
```
TranscriptParser.parseFile(Path filePath) 
  → ParsedTranscript(filePath, ...) 
  → DataIngestionService.ingestParsedTranscript(...) 
  → convertToTurns(parsed, filePath) 
  → DashboardConversationTurn.logFilePath
```

**理由**:
- 与 `DashboardTranscriptIssue.filePath` 处理逻辑一致
- 数据流清晰，易于追踪
- 不需要修改 `ScanOrchestrator` 的参数传递

### 5. MyBatis 查询优化

**决策**: 对于批量状态查询，使用 `IN` 子句一次性查询多个 uid 的状态

**SQL**:
```sql
SELECT uid, status FROM v_instance_detail 
WHERE uid IN <list>
```

**理由**:
- 避免 N+1 查询问题
- 提升分页查询性能（用户列表可能包含多个用户）

## Risks / Trade-offs

### 风险 1: 视图性能问题

**风险**: `v_instance_detail` 视图包含多个 `JSON_EXTRACT` 调用，可能影响查询性能

**缓解措施**:
- 监控查询执行时间
- 必要时在视图上创建函数索引（MySQL 5.7+ 支持）
- 考虑物化视图方案

### 风险 2: 数据一致性问题

**风险**: 视图数据依赖于 `openclaw_instances` 表，如果表数据不一致，视图查询结果可能异常

**缓解措施**:
- 保持现有表的完整性约束
- 定期数据校验

### 风险 3: API 响应结构变更

**风险**: `GlobalStatsResponse` 增加字段可能影响现有前端代码

**缓解措施**:
- 新增字段为可选字段（允许 null）
- 前端代码向后兼容处理

### 风险 4: logFilePath 为空的历史数据

**风险**: 历史数据的 `log_file_path` 字段为空，可能导致查询异常

**缓解措施**:
- 允许字段为 null 或空字符串
- 前端代码处理空值情况

## Migration Plan

### 步骤 1: 创建 VInstanceDetailMapper
- 创建接口 `VInstanceDetailMapper.java`
- 定义查询方法：`selectRunningInstances()`, `selectStatusByUid()`, `countRegisteredUsers()`
- 创建 MyBatis XML 映射文件（或使用注解）

### 步骤 2: 修改 GlobalStatsResponse
- 增加 `Integer registeredUsers` 字段
- 更新 `DashboardService.getGlobalStats()` 方法

### 步骤 3: 修改 DashboardService
- 注入 `VInstanceDetailMapper`
- 修改 `getUserSummaries()` 方法，查询实例状态
- 修改 `status` 字段判断逻辑

### 步骤 4: 修改 ParsedTranscript
- 增加 `String filePath` 字段
- 修改 `TranscriptParser.parseFile()` 方法

### 步骤 5: 修改 DataIngestionService
- 修改 `convertToTurns()` 方法
- 设置 `logFilePath` 字段

### 步骤 6: 修改 AccountsCsvReader
- 注入 `VInstanceDetailMapper`
- 简化 `loadFromDatabase()` 方法，移除 JSON 解析逻辑

### 步骤 7: 测试验证
- 运行单元测试
- 运行集成测试
- 验证 API 响应

### 步骤 8: 清理（可选）
- 删除或弃用 `OpenclawInstanceMapper`
- 更新相关文档

## Open Questions

1. **是否需要保留 OpenclawInstanceMapper？**
   - 如果其他地方还有引用，建议保留并标记为 `@Deprecated`
   - 如果无引用，可以直接删除

2. **logFilePath 是否需要完整路径还是相对路径？**
   - 当前设计使用完整路径（`filePath.toString()`）
   - 如果需要考虑隐私或可移植性，可改为相对路径

3. **status 字段是否需要缓存？**
   - 当前设计每次查询都从数据库获取
   - 如果性能成为瓶颈，可考虑缓存实例状态
