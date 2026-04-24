# userid-fuzzy-search

## Purpose

为 `/api/v1/turns/search` 接口添加工号模糊匹配能力，支持用户通过部分工号查询相关对话记录。

---

## Requirements

### Requirement: 接口支持 userIdLike 参数进行工号模糊匹配

`/api/v1/turns/search` 接口的请求体 SHALL 支持可选参数 `userIdLike`，用于对 employee_id 字段进行模糊匹配查询。当传入 `userIdLike` 时，系统 SHALL 执行 SQL LIKE 查询，匹配所有包含该字符串的工号记录。

#### Scenario: 使用 userIdLike 进行模糊匹配查询

- **WHEN** 客户端发送 POST 请求到 `/api/v1/turns/search`，请求体包含 `{"userIdLike": "181", "page": 1, "pageSize": 10}`
- **THEN** 系统返回所有 employee_id 包含 "181" 的对话记录（如 18101142, 18102233, 20181001 等）
- **THEN** 响应中的分页信息正确（total, page, pageSize）

#### Scenario: userIdLike 支持部分工号匹配

- **WHEN** 客户端传入 `userIdLike` 为 "0114"
- **THEN** 系统返回所有 employee_id 包含 "0114" 的记录（如 18101142, 18201145 等）

#### Scenario: userIdLike 为空字符串时不添加过滤条件

- **WHEN** 客户端传入 `userIdLike` 为空字符串 `""`
- **THEN** 系统不添加 employee_id 过滤条件，返回所有符合条件的记录

---

### Requirement: userId 优先于 userIdLike

当请求体中同时包含 `userId` 和 `userIdLike` 参数时，系统 SHALL 优先使用 `userId` 进行精确匹配，忽略 `userIdLike` 参数。两个参数互斥，不会同时生效。

#### Scenario: 同时传入 userId 和 userIdLike 时使用精确匹配

- **WHEN** 客户端发送请求 `{"userId": "18101142", "userIdLike": "181", "page": 1, "pageSize": 10}`
- **THEN** 系统使用精确匹配 `employee_id = '18101142'`
- **THEN** 仅返回 employee_id 完全等于 "18101142" 的记录
- **THEN** `userIdLike` 参数被忽略，不影响查询结果

#### Scenario: 仅传入 userId 时使用精确匹配

- **WHEN** 客户端发送请求 `{"userId": "18101142", "page": 1, "pageSize": 10}`
- **THEN** 系统使用精确匹配 `employee_id = '18101142'`
- **THEN** 仅返回 employee_id 完全等于 "18101142" 的记录

#### Scenario: 仅传入 userIdLike 时使用模糊匹配

- **WHEN** 客户端发送请求 `{"userIdLike": "181", "page": 1, "pageSize": 10}`
- **THEN** 系统使用模糊匹配 `employee_id LIKE '%181%'`
- **THEN** 返回所有 employee_id 包含 "181" 的记录

#### Scenario: userId 和 userIdLike 都为空时不添加过滤条件

- **WHEN** 客户端发送请求 `{"page": 1, "pageSize": 10}` （不包含 userId 和 userIdLike）
- **THEN** 系统不添加 employee_id 过滤条件
- **THEN** 返回所有符合其他条件（时间、技能等）的记录

---

### Requirement: 模糊匹配使用参数化查询防止 SQL 注入

系统 SHALL 使用 MyBatis 参数化查询执行模糊匹配，禁止使用字符串拼接或 `${}` 占位符。SQL 语句 SHALL 使用 `CONCAT('%', #{userIdLike}, '%')` 构造 LIKE 条件。

#### Scenario: 特殊字符输入不会导致 SQL 注入

- **WHEN** 客户端传入 `userIdLike` 为 `"'; DROP TABLE dashboard_conversation_turn; --"`
- **THEN** 系统将该字符串作为普通文本处理，不会执行恶意 SQL
- **THEN** 查询返回 employee_id 包含该字符串的记录（通常为空）

#### Scenario: SQL 通配符 % 和 _ 被当作普通字符处理

- **WHEN** 客户端传入 `userIdLike` 为 `"181%"`
- **THEN** 系统将 `%` 作为普通字符，而非 SQL 通配符
- **THEN** 查询匹配 employee_id 包含 "181%" 字面量的记录

---

### Requirement: 模糊匹配与其他筛选条件组合生效

`userIdLike` 参数 SHALL 与现有筛选条件（startTime, endTime, skillId）兼容，所有条件通过 AND 逻辑组合。

#### Scenario: userIdLike 与时间范围组合筛选

- **WHEN** 客户端发送请求：
```json
{
  "userIdLike": "181",
  "startTime": "2026-04-21 00:00:00",
  "endTime": "2026-04-21 23:59:59",
  "page": 1,
  "pageSize": 10
}
```
- **THEN** 系统返回满足以下所有条件的记录：
  - employee_id 包含 "181"
  - start_time 在 2026-04-21 00:00:00 到 2026-04-21 23:59:59 之间

#### Scenario: userIdLike 与 skillId 组合筛选

- **WHEN** 客户端发送请求：
```json
{
  "userIdLike": "181",
  "skillId": "pptx",
  "page": 1,
  "pageSize": 10
}
```
- **THEN** 系统返回满足以下所有条件的记录：
  - employee_id 包含 "181"
  - 对话中使用了 skill_id 为 "pptx" 的技能

#### Scenario: userIdLike 与所有筛选条件组合

- **WHEN** 客户端发送请求：
```json
{
  "userIdLike": "181",
  "startTime": "2026-04-21 00:00:00",
  "endTime": "2026-04-21 23:59:59",
  "skillId": "official-doc-writer",
  "page": 1,
  "pageSize": 10
}
```
- **THEN** 系统返回满足以下所有条件的记录：
  - employee_id 包含 "181"
  - start_time 在指定时间范围内
  - 对话中使用了指定技能

---

### Requirement: API 文档明确说明 userIdLike 参数

API 接口文档 v1.2 SHALL 在 `/turns/search` 接口的请求体参数表中添加 `userIdLike` 字段说明，包括：
- 字段类型：String
- 是否必填：否
- 功能描述：工号模糊匹配，与 userId 二选一，userId 优先
- 示例值："181"

#### Scenario: 文档包含 userIdLike 参数说明

- **WHEN** 开发者查阅 `docs/api/API接口文档v1.2.md` 中的 `/turns/search` 接口文档
- **THEN** 请求体参数表中包含 `userIdLike` 字段
- **THEN** 说明中明确指出与 `userId` 的优先级关系

#### Scenario: 文档提供使用示例

- **WHEN** 开发者查看接口示例
- **THEN** 示例中包含使用 `userIdLike` 的请求体示例
- **THEN** 示例展示模糊匹配的典型用法

---

### Requirement: 向后兼容性保证

新增 `userIdLike` 参数 SHALL 不影响现有调用方的行为。未传入 `userIdLike` 的请求 SHALL 保持与之前完全一致的查询逻辑和结果。

#### Scenario: 旧版客户端调用不受影响

- **WHEN** 客户端发送不包含 `userIdLike` 的请求（仅使用 userId 或其他参数）
- **THEN** 系统行为与修改前完全一致
- **THEN** 查询结果、性能、错误处理均无变化

#### Scenario: 新增参数为可选

- **WHEN** 客户端发送的请求体中不包含 `userIdLike` 字段
- **THEN** 系统正常处理请求，不报错
- **THEN** 不使用 userIdLike 相关的过滤逻辑
