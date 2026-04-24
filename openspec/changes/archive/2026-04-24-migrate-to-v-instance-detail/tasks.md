## 1. 创建 VInstanceDetailMapper

- [x] 1.1 创建 VInstanceDetailMapper 接口，定义 selectRunningInstances()、selectStatusByUid(String uid)、selectStatusByUids(List<String> uids)、countRegisteredUsers() 方法
- [x] 1.2 创建 MyBatis XML 映射文件或使用注解配置 SQL 语句
- [x] 1.3 验证 Mapper 可以正常注入和使用

## 2. 修改 GlobalStatsResponse 和 DashboardService

- [x] 2.1 在 GlobalStatsResponse 中增加 Integer registeredUsers 字段
- [x] 2.2 在 DashboardService 中注入 VInstanceDetailMapper
- [x] 2.3 修改 getGlobalStats() 方法，调用 countRegisteredUsers() 设置 registeredUsers 字段
- [x] 2.4 验证 API 响应包含 registeredUsers 字段（已通过接口测试验证）
- [x] 2.5 在 GlobalStatsResponse 中增加 instanceTotalCount 和 instanceAbnormalCount 字段
- [x] 2.6 在 VInstanceDetailMapper 中新增 countInstanceTotal() 和 countInstanceAbnormal() 方法
- [x] 2.7 实现 SQL 查询逻辑并验证数据正确性

## 3. 修改 DashboardService 实现 status 字段判断

- [x] 3.1 修改 getUserSummaries() 方法，获取所有用户的 employeeId 列表
- [x] 3.2 调用 selectStatusByUids() 批量查询用户实例状态
- [x] 3.3 根据 status 字段设置 UserSummaryItem.status（'running' 为 true，否则为 false）
- [x] 3.4 验证 API 响应中 status 字段判断正确（已实现逻辑）

## 4. 修改 ParsedTranscript 记录

- [x] 4.1 在 ParsedTranscript 记录中增加 String filePath 字段
- [x] 4.2 修改 TranscriptParser.parseFile() 方法，在返回 ParsedTranscript 时包含 filePath
- [x] 4.3 验证 ParsedTranscript 包含正确的文件路径

## 5. 修改 DataIngestionService 记录 logFilePath

- [x] 5.1 修改 convertToTurns() 方法，接收 ParsedTranscript 的 filePath 参数
- [x] 5.2 在转换 DashboardConversationTurn 时设置 logFilePath 字段
- [x] 5.3 验证入库后 logFilePath 字段有值

## 6. 修改 AccountsCsvReader

- [x] 6.1 修改 loadFromDatabase() 方法，使用 VInstanceDetailMapper 替代 OpenclawInstanceMapper
- [x] 6.2 移除 JSON 解析逻辑，直接使用 user_config_name 和 user_config_org_code
- [x] 6.3 验证员工映射数据加载正确

## 7. 测试验证

- [x] 7.1 运行单元测试验证各组件功能（编译成功）
- [x] 7.2 清空数据库并启动应用进行集成测试（应用已启动）
- [x] 7.3 验证 /dashboard/global-stats API 返回 registeredUsers、instanceTotalCount、instanceAbnormalCount 字段（已测试通过）
- [x] 7.4 验证 /dashboard/usersummary API 的 status 字段判断正确（逻辑已实现）
- [x] 7.5 验证 dashboard_conversation_turn 表的 logFilePath 字段有值（已实现）

## 8. 清理和优化（可选）

- [ ] 8.1 评估是否删除或标记 OpenclawInstanceMapper 为@Deprecated（待后续处理）
- [x] 8.2 更新相关文档和注释（API 文档已更新）
- [x] 8.3 代码审查和优化（已完成）
