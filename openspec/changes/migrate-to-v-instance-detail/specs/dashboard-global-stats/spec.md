## MODIFIED Requirements

### Requirement: 获取平台全局累计数据

系统 SHALL 提供获取平台从上线至今的总体累计统计数据的 API，响应包含 totalTokens、totalTurns、totalSkillCalls、totalUsers 和 registeredUsers 字段。

#### Scenario: 成功返回全局统计数据
- **WHEN** 调用 `GET /dashboard/global-stats` API
- **THEN** 返回状态码 200，响应数据结构为 `{ code: 200, message: "success", data: { totalTokens, totalTurns, totalSkillCalls, totalUsers, registeredUsers } }`

#### Scenario: registeredUsers 字段统计正确
- **WHEN** 数据库中有注册用户（status != 'deleted' 的 uid）
- **THEN** `registeredUsers` 字段值为不重复的 uid 数量，统计条件为 `status != 'deleted'`

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

#### Scenario: 新增 registeredUsers 字段
- **WHEN** 前端调用 `/dashboard/global-stats` API
- **THEN** 响应数据包含 `registeredUsers` 字段，类型为 Integer，允许为 null（向后兼容）

#### Scenario: 字段值计算正确
- **WHEN** 数据库中有有效数据
- **THEN** 所有字段值都正确计算并返回
