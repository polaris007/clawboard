# instance-status-check

## Purpose

根据 `v_instance_detail` 视图的 status 字段判断用户在线状态，用于用户明细 API 的 status 字段。

---

## Requirements

### Requirement: 根据实例 status 判断用户在线状态

系统 SHALL 在 `/dashboard/usersummary` API 的响应中，根据 `v_instance_detail` 视图的 `status` 字段判断用户是否在线。

#### Scenario: status 为 running 时返回 true
- **WHEN** 用户的实例 `status = 'running'`
- **THEN** `UserSummaryItem.status` 字段值为 `true`

#### Scenario: status 为其他值时返回 false
- **WHEN** 用户的实例 `status != 'running'`（如 'creating', 'error', 'deleted' 等）
- **THEN** `UserSummaryItem.status` 字段值为 `false`

#### Scenario: 用户实例不存在
- **WHEN** 用户在 `v_instance_detail` 视图中没有对应记录
- **THEN** `UserSummaryItem.status` 字段值为 `false`

---

### Requirement: status 字段只依赖 v_instance_detail.status

系统 SHALL 仅根据 `v_instance_detail` 视图的 `status` 字段判断在线状态，不考虑心跳时间或其他因素。

#### Scenario: 不依赖心跳时间
- **WHEN** 实例 `status = 'running'` 但心跳时间较早
- **THEN** `status` 字段仍为 `true`（心跳时间不影响判断）

#### Scenario: 不依赖其他条件
- **WHEN** 实例 `status = 'running'`
- **THEN** 无论其他字段（如 `progress`, `deployment_ready` 等）如何，`status` 字段都为 `true`

---

### Requirement: 批量查询用户状态

系统 SHALL 支持批量查询多个用户的实例状态，用于分页查询用户列表场景。

#### Scenario: 批量查询优化
- **WHEN** 需要查询多个用户的状态
- **THEN** 使用 `IN` 子句一次性查询，避免 N+1 查询问题

#### Scenario: 部分用户不存在
- **WHEN** 查询 10 个 uid，其中 3 个在视图中不存在
- **THEN** 返回 7 个用户的状态，不存在的用户默认为 `false`
