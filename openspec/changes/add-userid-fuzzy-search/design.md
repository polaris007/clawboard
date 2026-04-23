## Context

当前 `/api/v1/turns/search` 接口已实现基于 `userId` 的工号精确匹配功能,底层使用 MyBatis 动态 SQL 查询 `dashboard_conversation_turn` 表的 `employee_id` 字段。现有实现通过 `<if test="userId != null and userId != ''">` 条件判断添加精确匹配条件 `employee_id = #{userId}`。

业务场景中,用户经常需要通过部分工号进行模糊查询(如输入机构代码 "181" 查询该机构下所有员工),但现有接口不支持此需求。需要在保持向后兼容的前提下,新增模糊匹配能力。

**技术约束**:
- 数据库: MySQL 5.7,`employee_id` 字段为 VARCHAR 类型,无全文索引
- ORM: MyBatis 3.0.3,使用 XML 配置动态 SQL
- 分页: PageHelper 2.1.0 自动处理 LIMIT/OFFSET
- API 规范: 遵循 RESTful 风格,参数命名使用 camelCase

## Goals / Non-Goals

**Goals:**
- 为 `/turns/search` 接口新增 `userIdLike` 参数,支持工号模糊匹配
- 实现 `userId`(精确)与 `userIdLike`(模糊)的优先级逻辑,确保互斥使用
- 保持向后兼容,不影响现有调用方
- 更新 API 文档,明确参数使用说明

**Non-Goals:**
- 不修改其他接口(如 `/dashboard/usersummary`)的筛选逻辑
- 不引入全文索引或性能优化方案
- 不改变现有的 `userId` 精确匹配行为
- 不对模糊匹配做字符数限制(由前端控制)

## Decisions

### Decision 1: 参数命名选择 `userIdLike`

**选择**: 使用 `userIdLike` 作为模糊匹配参数名

**理由**:
- 语义清晰:`Like` 后缀明确表示模糊匹配,与 SQL `LIKE` 操作符对应
- 避免歧义:不同于 `userName`(姓名)或 `userId`(精确工号)
- 前端友好:驼峰命名符合 JavaScript/TypeScript 规范

**备选方案**:
- ❌ `userIdFuzzy`: 语义不够直观
- ❌ `employeeIdLike`: 与现有 `userId` 命名不一致
- ❌ 复用 `userId` 智能判断: 逻辑复杂,容易误判

---

### Decision 2: 优先级逻辑 - userId 优先于 userIdLike

**选择**: 当 `userId` 有值时使用精确匹配,否则检查 `userIdLike` 进行模糊匹配

**实现方式**: 使用 MyBatis `<choose>` 标签
```xml
<choose>
    <when test="userId != null and userId != ''">
        AND employee_id = #{userId}
    </when>
    <when test="userIdLike != null and userIdLike != ''">
        AND employee_id LIKE CONCAT('%', #{userIdLike}, '%')
    </when>
</choose>
```

**理由**:
- 逻辑清晰:显式表达优先级关系
- 互斥执行:`<choose>` 确保只执行一个分支
- 易于维护:后续如需调整优先级只需修改顺序

**备选方案**:
- ❌ 嵌套 `<if>`: 可读性差,嵌套层级深
- ❌ 同时生效(AND): 实际场景少,增加复杂度

---

### Decision 3: SQL 模糊匹配使用 `CONCAT('%', #{userIdLike}, '%')`

**选择**: 使用 MyBatis `CONCAT` 函数拼接通配符

**理由**:
- 安全性:参数化查询,防止 SQL 注入
- 兼容性:MySQL 5.7+ 原生支持 `CONCAT` 函数
- 简洁性:比 Java 层拼接更优雅

**备选方案**:
- ❌ Java 层拼接: `"%" + userIdLike + "%"` - 需额外处理 null/empty
- ❌ `${}` 占位符: 存在 SQL 注入风险

---

### Decision 4: 不限制最小字符数

**选择**: 后端不做字符数校验,由前端控制

**理由**:
- 职责分离:前端更适合做输入验证和用户体验优化
- 灵活性:不同场景可能需要不同的最小字符数
- 简单性:后端保持简单,避免过度设计

**注意**: 如果未来发现单字符查询性能问题,可考虑:
- 前端限制至少 2-3 个字符
- 后端添加日志监控慢查询
- 评估是否需要全文索引

---

### Decision 5: 仅修改 `/turns/search` 接口

**选择**: 本次变更仅针对 `/api/v1/turns/search` 接口

**理由**:
- 范围可控:聚焦核心需求,降低风险
- 渐进式:先验证效果,再决定是否推广到其他接口
- 独立性:`/dashboard/usersummary` 等接口的筛选逻辑可能不同

**后续扩展**: 如果模糊匹配效果好,可在后续迭代中为其他接口添加类似功能

## Risks / Trade-offs

### Risk 1: 模糊匹配性能问题

**风险**: `LIKE '%xxx%'` 无法利用索引,大数据量时查询速度慢

**影响**: 
- 当 `dashboard_conversation_turn` 表数据量达到百万级时,模糊匹配可能超时
- 并发查询时可能占用较多数据库资源

**缓解措施**:
- 前端建议用户输入至少 2-3 个字符,减少匹配结果集
- 监控慢查询日志,识别性能瓶颈
- 必要时可考虑:
  - 添加 `employee_id` 前缀索引(如 `INDEX idx_emp_prefix (employee_id(5))`)
  - 使用全文索引(FULLTEXT INDEX)
  - 缓存热门查询结果

**权衡**: 当前数据量较小(预计十万级以内),性能影响可接受。优先保证功能完整性,后续根据实际使用情况优化。

---

### Risk 2: 参数混淆导致误用

**风险**: 前端开发者可能同时传入 `userId` 和 `userIdLike`,期望两者都生效

**影响**: 
- 实际只会使用 `userId`(精确匹配),`userIdLike` 被忽略
- 可能导致查询结果不符合预期

**缓解措施**:
- API 文档明确说明优先级规则和互斥关系
- 在响应中添加警告信息(可选,暂不实施)
- 单元测试覆盖同时传入两个参数的场景

**权衡**: 通过文档和测试降低误用风险,不增加运行时校验复杂度。

---

### Risk 3: 特殊字符处理

**风险**: 用户输入包含 SQL 通配符 `%` 或 `_`,可能导致意外匹配

**影响**:
- 输入 `181%` 会被解释为 "以 181 开头的所有工号"
- 输入 `1_1` 会匹配 "1任意字符1" 的工号

**缓解措施**:
- MyBatis 参数化查询已防止 SQL 注入
- 如需转义通配符,可使用 `ESCAPE` 子句(暂不实施,等待实际需求)
- 前端可做输入过滤,禁止特殊字符

**权衡**: 当前场景下特殊字符使用概率低,暂不做转义处理。如有实际需求再补充。

---

### Trade-off: 灵活性与复杂性

**选择**: 提供两种查询方式(精确 + 模糊)

**优点**:
- 满足不同场景需求
- 用户体验更好

**缺点**:
- 增加接口复杂度
- 需要维护优先级逻辑

**结论**: 收益大于成本,值得实施。

## Migration Plan

### 部署步骤

1. **代码部署**:
   - 修改 `TurnSearchRequest.java`、`ConversationTurnMapper.java`、`ConversationTurnMapper.xml`
   - 更新 `API接口文档v1.2.md`
   - 编译打包,重启应用

2. **验证步骤**:
   - 测试精确匹配:`userId="18101142"` → 返回单条记录
   - 测试模糊匹配:`userIdLike="181"` → 返回多条记录
   - 测试优先级:同时传入 `userId` 和 `userIdLike` → 使用精确匹配
   - 测试组合筛选:`userIdLike` + `startTime` + `endTime` + `skillId`

3. **回滚策略**:
   - 如果发现严重 bug,回滚代码到上一版本
   - `userIdLike` 为可选参数,不回滚也不影响现有功能

### 无需数据迁移

- 无表结构变更
- 无历史数据处理
- 纯代码层面改动

## Open Questions

### Question 1: 是否需要为其他接口添加模糊匹配?

**现状**: 仅针对 `/turns/search` 接口

**待决策**: 
- `/dashboard/usersummary` 是否也需要 `userIdLike`?
- `/errors/search` 是否需要类似的模糊筛选?

**建议**: 先观察 `/turns/search` 的使用情况,收集用户反馈后再决定。

---

### Question 2: 是否需要添加查询结果数量限制?

**现状**: 无限制,完全依赖分页参数

**待决策**: 
- 是否限制模糊匹配最多返回 N 条记录?
- 是否在结果过多时提示用户缩小范围?

**建议**: 暂不实施,PageHelper 已有分页机制。如出现性能问题再考虑。

---

### Question 3: 是否需要在日志中记录模糊匹配查询?

**现状**: 无特殊日志

**待决策**:
- 是否记录所有使用 `userIdLike` 的查询?
- 是否记录慢查询(超过阈值)?

**建议**: 可添加 DEBUG 级别日志,便于排查问题和性能分析。
