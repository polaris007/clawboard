## ADDED Requirements

### Requirement: 从 v_instance_detail 视图查询运行中实例

系统 SHALL 提供从 `v_instance_detail` 视图查询状态为 `running` 的实例信息的能力，返回 uid、user_config_name（用户名）、user_config_org_code（机构号）。

#### Scenario: 查询所有运行中实例
- **WHEN** 调用 `selectRunningInstances()` 方法
- **THEN** 返回所有 `status = 'running'` 的实例列表，每个实例包含 uid、user_config_name、user_config_org_code 字段

#### Scenario: 无运行中实例
- **WHEN** 数据库中没有 `status = 'running'` 的实例
- **THEN** 返回空列表

---

### Requirement: 根据 uid 查询实例状态

系统 SHALL 提供根据 uid 查询实例状态的能力，用于判断用户是否在线。

#### Scenario: 查询单个用户实例状态
- **WHEN** 调用 `selectStatusByUid(String uid)` 方法且实例存在
- **THEN** 返回该实例的 status 字段值

#### Scenario: 用户实例不存在
- **WHEN** 调用 `selectStatusByUid(String uid)` 方法但 uid 不存在
- **THEN** 返回 null

#### Scenario: 批量查询多个用户实例状态
- **WHEN** 调用 `selectStatusByUids(List<String> uids)` 方法
- **THEN** 返回包含 uid 和 status 的列表，只返回存在的用户

---

### Requirement: 统计注册用户数

系统 SHALL 提供统计注册用户数的能力，从 `v_instance_detail` 视图查询所有 `status != 'deleted'` 的记录，统计不重复的 uid 数量。

#### Scenario: 统计正常用户数
- **WHEN** 调用 `countRegisteredUsers()` 方法
- **THEN** 返回 `COUNT(DISTINCT uid)`，条件是 `status != 'deleted'`

#### Scenario: 所有用户都被删除
- **WHEN** 所有记录的 status 都是 'deleted'
- **THEN** 返回 0

#### Scenario: 同一用户有多个实例
- **WHEN** 同一个 uid 有多条记录且 status 都不是 'deleted'
- **THEN** 该 uid 只计数一次（使用 DISTINCT）
