## Why

当前 `/api/v1/turns/search` 接口的 `userId` 参数仅支持工号精确匹配,无法满足用户通过部分工号进行模糊查询的需求。例如,用户希望输入 "181" 查询所有包含该数字的工号(如 18101142, 18102233, 20181001 等),但现有实现只能进行完全匹配。这限制了用户在对话记录检索时的灵活性,特别是在忘记完整工号或需要批量查询某机构人员时体验不佳。

## What Changes

- **新增参数**: 在 `/api/v1/turns/search` 接口请求体中新增 `userIdLike` 字段,支持工号(employee_id)的模糊匹配查询
- **优先级逻辑**: `userId`(精确匹配)优先于 `userIdLike`(模糊匹配),两者互斥使用
- **SQL 实现**: 使用 MyBatis `<choose>` 标签实现条件分支,确保精确匹配和模糊匹配不会同时生效
- **文档更新**: 更新 API接口文档v1.2.md,添加 `userIdLike` 参数说明和使用示例

**非目标 (Non-goals)**:
- 不修改其他接口的筛选逻辑(如 `/dashboard/usersummary`)
- 不改变现有的 `userId` 精确匹配行为
- 不引入全文索引或其他性能优化方案(后续可根据数据量评估)

## Capabilities

### New Capabilities
- `userid-fuzzy-search`: 为 `/turns/search` 接口添加工号模糊匹配能力,支持通过部分工号查询相关对话记录

### Modified Capabilities
<!-- 无现有能力的需求变更 -->

## Impact

**受影响组件**:
- DTO 层: `TurnSearchRequest.java` - 新增 `userIdLike` 字段
- Mapper 接口: `ConversationTurnMapper.java` - 方法签名增加参数
- Mapper XML: `ConversationTurnMapper.xml` - 修改 `selectTurnsWithFilters` 查询逻辑
- API 文档: `docs/api/API接口文档v1.2.md` - 补充参数说明

**API 影响**:
- ✅ **向后兼容**: 现有调用不受影响,`userIdLike` 为可选参数
- ⚠️ **性能注意**: 模糊匹配使用 `LIKE '%xxx%'` 无法利用索引,大数据量时可能较慢

**数据库影响**:
- 无表结构变更
- 查询条件动态生成,不影响现有索引策略

**风险评估**:
- 🟢 **低风险**: 仅新增可选参数,不修改核心逻辑
- 🟢 **安全性**: 使用 MyBatis 参数化查询,无 SQL 注入风险
- 🟡 **性能**: 模糊匹配可能影响查询速度,建议前端限制最小字符数(如至少 2-3 个字符)
