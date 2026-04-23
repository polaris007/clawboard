## 1. DTO 层修改

- [ ] 1.1 在 TurnSearchRequest.java 中新增 userIdLike 字段(String 类型)
- [ ] 1.2 为 userIdLike 添加注释说明:工号模糊匹配(对应 employee_id LIKE '%xxx%')
- [ ] 1.3 验证编译通过,无语法错误

## 2. Mapper 接口修改

- [ ] 2.1 在 ConversationTurnMapper.java 的 selectTurnsWithFilters 方法签名中新增参数 @Param("userIdLike") String userIdLike
- [ ] 2.2 更新方法注释,说明 userIdLike 参数用途
- [ ] 2.3 验证编译通过,无语法错误

## 3. Mapper XML 修改

- [ ] 3.1 在 ConversationTurnMapper.xml 的 selectTurnsWithFilters 查询中使用 <choose> 标签替换原有的 userId <if> 标签
- [ ] 3.2 实现优先级逻辑:当 userId 有值时使用精确匹配,否则检查 userIdLike 使用模糊匹配
- [ ] 3.3 模糊匹配使用 CONCAT('%', #{userIdLike}, '%') 构造 LIKE 条件
- [ ] 3.4 保留 startTime 和 endTime 的原有过滤逻辑不变
- [ ] 3.5 验证 XML 语法正确,无拼写错误

## 4. API 文档更新

- [ ] 4.1 在 docs/api/API接口文档v1.2.md 的 /turns/search 接口请求体参数表中新增 userIdLike 行
- [ ] 4.2 说明 userIdLike 的类型、是否必填、功能描述
- [ ] 4.3 明确标注与 userId 的优先级关系(userId 优先,两者二选一)
- [ ] 4.4 在请求体示例中添加使用 userIdLike 的示例
- [ ] 4.5 验证文档格式正确,表格对齐

## 5. 单元测试

- [ ] 5.1 创建或更新 TurnErrorServiceTest,测试 userIdLike 模糊匹配场景
- [ ] 5.2 测试用例:仅传入 userIdLike 时执行模糊匹配
- [ ] 5.3 测试用例:同时传入 userId 和 userIdLike 时使用精确匹配(userId 优先)
- [ ] 5.4 测试用例:userIdLike 为空字符串时不添加过滤条件
- [ ] 5.5 测试用例:userIdLike 与 startTime/endTime/skillId 组合筛选
- [ ] 5.6 运行单元测试,确保所有测试通过

## 6. 集成测试

- [ ] 6.1 启动应用(dev profile),清空数据库并重新扫描生成测试数据
- [ ] 6.2 使用 Postman/curl 测试精确匹配:{"userId": "完整工号"}
- [ ] 6.3 使用 Postman/curl 测试模糊匹配:{"userIdLike": "部分工号"}
- [ ] 6.4 测试优先级:同时传入 userId 和 userIdLike,验证使用精确匹配
- [ ] 6.5 测试组合筛选:userIdLike + 时间范围 + skillId
- [ ] 6.6 验证分页功能正常,total/page/pageSize 正确
- [ ] 6.7 测试边界情况:userIdLike 为特殊字符(%、_、SQL 注入尝试)

## 7. 代码审查与优化

- [ ] 7.1 检查代码是否符合项目规范(Lombok 使用、命名规范、注释完整性)
- [ ] 7.2 确认 SQL 注入防护措施到位(使用参数化查询,未使用 ${})
- [ ] 7.3 检查是否有冗余代码或未使用的导入
- [ ] 7.4 验证日志记录是否充分(可选,根据项目规范)

## 8. 文档与交付

- [ ] 8.1 确认 OpenSpec artifacts 完整(proposal.md, design.md, specs/, tasks.md)
- [ ] 8.2 更新 CLAUDE.md 或相关开发文档(如需要)
- [ ] 8.3 准备发布说明(变更内容、影响范围、注意事项)
- [ ] 8.4 通知前端团队接口变更(新增可选参数 userIdLike)
