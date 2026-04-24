## ADDED Requirements

### Requirement: GlobalStatsResponse 增加 registeredUsers 字段

系统 SHALL 在 `/dashboard/global-stats` API 的响应中增加 `registeredUsers` 字段，表示注册用户总数。

#### Scenario: 响应包含 registeredUsers 字段
- **WHEN** 调用 `GET /dashboard/global-stats` API
- **THEN** 响应数据的 `data` 对象包含 `registeredUsers` 字段，类型为 Integer

#### Scenario: registeredUsers 统计正确
- **WHEN** 数据库中有 10 个不重复的 uid，且 status 都不是 'deleted'
- **THEN** `registeredUsers` 字段值为 10

#### Scenario: 排除已删除用户
- **WHEN** 数据库中有 10 个 uid，其中 3 个 uid 的 status 为 'deleted'
- **THEN** `registeredUsers` 字段值为 7（排除已删除用户）

#### Scenario: 同一用户多实例只计一次
- **WHEN** 同一个 uid 有 3 条实例记录，status 都不是 'deleted'
- **THEN** 该 uid 只计数一次，`registeredUsers` 加 1

---

### Requirement: registeredUsers 从 v_instance_detail 查询

系统 SHALL 从 `v_instance_detail` 视图查询注册用户数，查询条件为 `status != 'deleted'`。

#### Scenario: 使用 v_instance_detail 视图
- **WHEN** 执行注册用户数统计查询
- **THEN** 查询来源是 `v_instance_detail` 视图，不是 `openclaw_instances` 表

#### Scenario: 查询性能
- **WHEN** 数据库中有大量实例记录
- **THEN** 查询应在合理时间内完成（使用 DISTINCT 去重）
