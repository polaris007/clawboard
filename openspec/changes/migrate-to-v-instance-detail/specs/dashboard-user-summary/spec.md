## MODIFIED Requirements

### Requirement: 分页查询用户消耗明细

系统 SHALL 提供分页查询用户明细表格数据的 API，支持按团队号、用户 ID、时间范围筛选，返回用户列表包含 status 字段表示用户是否在线。

#### Scenario: 成功返回用户列表
- **WHEN** 调用 `POST /dashboard/usersummary` API 并提供有效请求体
- **THEN** 返回状态码 200，响应数据包含分页的用户列表

#### Scenario: status 字段根据实例状态判断
- **WHEN** 用户在 `v_instance_detail` 视图中的实例 `status = 'running'`
- **THEN** `UserSummaryItem.status` 字段值为 `true`

#### Scenario: status 字段为非 running 状态
- **WHEN** 用户在 `v_instance_detail` 视图中的实例 `status != 'running'`（如 'creating', 'error', 'deleted' 等）
- **THEN** `UserSummaryItem.status` 字段值为 `false`

#### Scenario: 用户实例不存在
- **WHEN** 用户在 `v_instance_detail` 视图中没有对应记录
- **THEN** `UserSummaryItem.status` 字段值为 `false`

---

### Requirement: UserSummaryItem 数据结构

系统 SHALL 使用以下字段结构返回用户明细数据：

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `userId` | String | 工号 |
| `userName` | String | 姓名 |
| `orgCode` | String | 机构号 |
| `status` | Boolean | 龙虾是否在线：`true` active / `false` inactive，根据 `v_instance_detail.status` 判断 |
| `lastHeartbeat` | Long | 最后心跳时间戳（毫秒） |
| `tokens` | Object | Token 统计信息 |
| `turns` | Object | 对话轮次统计 |
| `skillCalls` | Object | 技能调用统计 |
| `toolCalls` | Object | 工具调用统计 |
| `topSkills` | String[] | 常用技能名称列表 |

#### Scenario: status 字段判断逻辑变更
- **WHEN** 之前版本可能使用心跳时间或其他方式判断在线状态
- **THEN** 当前版本改为仅根据 `v_instance_detail.status = 'running'` 判断

#### Scenario: 数据一致性
- **WHEN** 查询用户明细时
- **THEN** `status` 字段与 `v_instance_detail` 视图中的 `status` 字段保持一致
