## 1. 扩展 AssembledTurn 数据结构

- [x] 1.1 在 `AssembledTurn` record 中添加 6 个统计字段：totalInputTokens, totalOutputTokens, totalTokens, toolCallsCount, toolCallsSuccess, toolCallsError
- [x] 1.2 验证编译通过，确认 Lombok 正确生成构造函数和访问器

## 2. 实现 Token 统计逻辑

- [x] 2.1 在 `TurnAssembler.assembleTurn()` 中初始化 token 统计变量（totalInputTokens, totalOutputTokens, totalTokens）
- [x] 2.2 遍历消息列表，累加 assistant message 的 usage 字段
- [x] 2.3 处理 NULL usage 情况（当作 0 处理，避免 NullPointerException）
- [x] 2.4 将统计结果传递给 AssembledTurn 构造函数

## 3. 实现 ToolCall 统计逻辑

- [x] 3.1 构建 toolCall → toolResult 映射表（遍历所有 toolResult message）
- [x] 3.2 遍历 assistant message 的 toolCalls 列表，统计总数
- [x] 3.3 对每个 toolCall 检查错误条件：缺少 toolResult、toolResult 有错误、stopReason 为 error/aborted
- [x] 3.4 计算 tool_calls_success = tool_calls_count - tool_calls_error
- [x] 3.5 确保孤立 toolResult 不计入 tool_calls_error

## 4. 修改数据持久化层

- [x] 4.1 在 `DataIngestionService.saveConversationTurns()` 中从 AssembledTurn 读取统计值
- [x] 4.2 设置 ConversationTurnEntity 的统计字段（注意类型转换：long → Integer）
- [x] 4.3 验证 MyBatis 批量插入时统计字段正确保存

## 5. 编写基础场景单元测试

- [x] 5.1 创建 `TurnAssemblerTest` 测试类，配置 SystemMessageFilter 依赖
- [x] 5.2 编写 testTurnWithoutToolCalls：验证无工具调用时的 token 统计
- [x] 5.3 编写 testSingleToolCallSuccess：验证单个工具调用成功的统计
- [x] 5.4 创建辅助方法：createUserMessage, createAssistantMessage, createToolResultMessage, createUsage

## 6. 编写异常场景单元测试

- [x] 6.1 编写 testMissingToolResult：验证缺少 toolResult 计为错误
- [x] 6.2 编写 testToolResultWithError：验证 toolResult 有错误计为失败
- [x] 6.3 编写 testAssistantStopReasonError：验证 stopReason 为 error 时计为失败
- [x] 6.4 编写 testMultipleToolCallsPartialSuccess：验证多个 toolCall 部分成功的情况
- [x] 6.5 编写 testNullUsage：验证 NULL usage 不导致异常

## 7. 验证与清理

- [x] 7.1 运行完整测试套件，确保所有测试通过（预期 7/7）
- [x] 7.2 执行 Maven 编译，确认无编译错误
- [x] 7.3 删除临时 Python 脚本（check_toolcall.py 等）
- [x] 7.4 提交代码到 Git，包含清晰的 commit message
