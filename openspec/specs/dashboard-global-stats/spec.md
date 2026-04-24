# dashboard-global-stats

## Purpose

提供平台全局累计统计数据 API，返回从上线至今的总体统计信息，包括 Token 消耗、对话轮次、技能调用、用户数等指标。

---

## Requirements

### Requirement: 获取平台全局累计数据

系统 SHALL 提供获取平台从上线至今的总体累计统计数据的 API，响应包含 totalTokens、totalTurns、totalSkillCalls、totalUsers、registeredUsers、instanceTotalCount 和 instanceAbnormalCount 字段。

#### Scenario: 成功返回全局统计数据
- **WHEN** 调用 `GET /dashboard/global-stats` API
- **THEN** 返回状态码 200，响应数据结构为 `{ code: 200, message: "success", data: { totalTokens, totalTurns, totalSkillCalls, totalUsers, registeredUsers, instanceTotalCount, instanceAbnormalCount } }`

#### Scenario: registeredUsers 字段统计正确
- **WHEN** 数据库中有注册用户（status != 'deleted' 的 uid）
- **THEN** `registeredUsers` 字段值为不重复的 uid 数量，统计条件为 `status != 'deleted'`

#### Scenario: instanceTotalCount 统计正确
- **WHEN** 数据库中有 OpenClaw 实例记录
- **THEN** `instanceTotalCount` 字段值为排除 status = 'deleted' 后的记录总数

#### Scenario: instanceAbnormalCount 统计正确
- **WHEN** 数据库中有异常状态的实例（非 deleted 和 running）
- **THEN** `instanceAbnormalCount` 字段值为排除 status IN ('deleted', 'running') 后的记录数

#### Scenario: 不受筛选条件影响
- **WHEN** 调用全局统计 API
- **THEN** 返回平台所有数据的累计值，不受团队、用户、时间等筛选条件影响

---

### Requirement: GlobalStatsResponse 数据结构

系统 SHALL 使用以下字段结构返回全局统计数据：

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `totalTokens` | Integer | 累计消耗 Token 总量 |
| `totalTurns` | Integer | 累计对话轮次 |
| `totalSkillCalls` | Integer | 累计技能调用次数 |
| `totalUsers` | Integer | 累计活跃用户数（从 hourly_stats 表去重） |
| `registeredUsers` | Integer | 注册用户总数（从 v_instance_detail 查询，status != 'deleted'） |
| `instanceTotalCount` | Integer | OpenClaw 实例总数量（排除 status = 'deleted'） |
| `instanceAbnormalCount` | Integer | OpenClaw 异常实例数量（排除 status = 'deleted' 和 'running'） |

#### Scenario: 新增 registeredUsers 字段
- **WHEN** 前端调用 `/dashboard/global-stats` API
- **THEN** 响应数据包含 `registeredUsers` 字段，类型为 Integer，允许为 null（向后兼容）

#### Scenario: 新增 instanceTotalCount 和 instanceAbnormalCount 字段
- **WHEN** 前端调用 `/dashboard/global-stats` API
- **THEN** 响应数据包含 `instanceTotalCount` 和 `instanceAbnormalCount` 字段，类型为 Integer

#### Scenario: 字段值计算正确
- **WHEN** 数据库中有有效数据
- **THEN** 所有字段值都正确计算并返回
