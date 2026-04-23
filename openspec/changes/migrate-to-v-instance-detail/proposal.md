## Why

当前项目从 `openclaw_instances` 表读取用户和实例信息时，需要通过 `JSON_EXTRACT` 解析`user_config_json` 字段来获取用户名 (`userName`) 和机构号 (`orgCode`)。这种方案存在以下问题：
1. **性能开销**：每次查询都需要解析 JSON 字段
2. **代码复杂**：Java 代码中需要使用 ObjectMapper 解析 JSON 字符串
3. **可维护性差**：JSON 字段结构变化时，查询语句和代码都需要同步修改

数据库已提供 `v_instance_detail` 视图，该视图已将用户名和机构号提取为独立列 (`user_config_name`, `user_config_org_code`)。本变更旨在将数据源从表迁移到视图，简化代码并提升性能。

## What Changes

- **数据源迁移**：从 `openclaw_instances` 表改为 `v_instance_detail` 视图
- **Mapper 重构**：创建 `VInstanceDetailMapper` 替代 `OpenclawInstanceMapper` 的查询逻辑
- **API 增强**：
  - `/dashboard/global-stats` 响应增加 `registeredUsers` 字段（统计所有 status != 'deleted' 的 uid 数量）
  - `/dashboard/usersummary` 的 `status` 字段根据 `v_instance_detail.status` 判断（'running' 为 true，否则为 false）
- **日志路径记录**：`dashboard_conversation_turn.log_file_path` 字段记录 session transcript 文件路径
- **代码简化**：`AccountsCsvReader` 不再需要解析 JSON，直接读取视图列

## Capabilities

### New Capabilities

- `v-instance-detail-mapper`: 从 v_instance_detail 视图查询实例信息的 Mapper
- `registered-users-stats`: 统计注册用户数的功能，从 v_instance_detail 查询 status != 'deleted' 的记录
- `instance-status-check`: 根据实例 status 判断用户在线状态的功能
- `log-file-path-recording`: 记录对话轮次对应的 transcript 文件路径

### Modified Capabilities

- `dashboard-global-stats`: `/dashboard/global-stats` API 增加 registeredUsers 字段
- `dashboard-user-summary`: `/dashboard/usersummary` API 的 status 字段改为根据 v_instance_detail.status 判断

## Impact

- **受影响文件**：
  - `src/main/java/com/company/clawboard/mapper/OpenclawInstanceMapper.java` - 替换为 VInstanceDetailMapper
  - `src/main/java/com/company/clawboard/scanner/AccountsCsvReader.java` - 简化 JSON 解析逻辑
  - `src/main/java/com/company/clawboard/service/DashboardService.java` - 增加 registeredUsers 查询和 status 判断逻辑
  - `src/main/java/com/company/clawboard/dto/GlobalStatsResponse.java` - 增加 registeredUsers 字段
  - `src/main/java/com/company/clawboard/parser/TranscriptParser.java` - ParsedTranscript 增加 filePath 字段
  - `src/main/java/com/company/clawboard/service/DataIngestionService.java` - convertToTurns 方法记录 logFilePath
- **数据库依赖**：依赖 `v_instance_detail` 视图存在
- **API 变更**：`/dashboard/global-stats` 响应结构增加字段（向后兼容）
- **性能影响**：正面影响，减少 JSON 解析开销
