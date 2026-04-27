# Turn Statistics Specification

## Purpose

定义对话轮次（Conversation Turn）级别的 token 消耗和 toolCall 执行统计能力。该规格说明系统在扫描 transcript 文件时如何自动计算并持久化统计数据，用于成本分析、性能监控、错误诊断和趋势分析。

## Requirements

### Requirement: Token 统计计算
系统 SHALL 在对话轮次组装阶段计算 token 消耗统计，包括输入 token、输出 token 和总 token 数。

#### Scenario: 累加 assistant message 的 usage 字段
- **WHEN** 对话轮次包含多个 assistant message，每个都有 usage 信息
- **THEN** total_input_tokens = 所有 assistant message 的 input_tokens 之和
- **THEN** total_output_tokens = 所有 assistant message 的 output_tokens 之和
- **THEN** total_tokens = 所有 assistant message 的 total_tokens 之和

#### Scenario: 处理 NULL usage
- **WHEN** assistant message 的 usage 字段为 null
- **THEN** 该 message 的 token 贡献为 0（不累加）
- **THEN** 统计结果不包含 null 值导致的错误

#### Scenario: 无 assistant message 的轮次
- **WHEN** 对话轮次只包含 user message（无 assistant message）
- **THEN** total_input_tokens = 0
- **THEN** total_output_tokens = 0
- **THEN** total_tokens = 0

### Requirement: ToolCall 数量统计
系统 SHALL 统计对话轮次中 toolCall 的总数、成功数和失败数。

#### Scenario: 统计 toolCall 总数
- **WHEN** 对话轮次包含多个 assistant message，每个有多个 toolCall
- **THEN** tool_calls_count = 所有 assistant message 的 toolCalls 列表长度之和

#### Scenario: 无 toolCall 的轮次
- **WHEN** 对话轮次中没有 assistant message 调用工具
- **THEN** tool_calls_count = 0
- **THEN** tool_calls_success = 0
- **THEN** tool_calls_error = 0

### Requirement: ToolCall 错误判定
系统 SHALL 根据以下规则判定 toolCall 是否失败：
1. 缺少对应的 toolResult（孤立 toolCall）
2. toolResult 有错误（isError() == true 或 errorMessage 不为空）
3. assistant stopReason 为 "error" 或 "aborted"

#### Scenario: 缺少 toolResult
- **WHEN** assistant message 包含 toolCall，但没有对应的 toolResult message
- **THEN** 该 toolCall 计为错误（tool_calls_error + 1）

#### Scenario: toolResult 有错误标志
- **WHEN** toolResult message 的 isError() 返回 true
- **THEN** 对应的 toolCall 计为错误

#### Scenario: toolResult 有错误消息
- **WHEN** toolResult message 的 errorMessage 字段不为空
- **THEN** 对应的 toolCall 计为错误

#### Scenario: stopReason 为 error
- **WHEN** assistant message 的 stopReason 为 "error"
- **THEN** 该 message 的所有 toolCall 计为错误

#### Scenario: stopReason 为 aborted
- **WHEN** assistant message 的 stopReason 为 "aborted"
- **THEN** 该 message 的所有 toolCall 计为错误

### Requirement: 孤立 ToolResult 处理
系统 SHALL 不计入孤立的 toolResult（找不到对应 toolCall 的记录）到 tool_calls_error。

#### Scenario: 孤立 toolResult 不影响错误统计
- **WHEN** transcript 中存在 toolResult message，但没有对应的 toolCall
- **THEN** tool_calls_error 不增加
- **THEN** 保持约束：tool_calls_error ≤ tool_calls_count

### Requirement: 统计数据持久化
系统 SHALL 将计算得到的统计值保存到 dashboard_conversation_turn 表。

#### Scenario: 保存统计值到数据库
- **WHEN** 对话轮次组装完成并准备入库
- **THEN** DataIngestionService 从 AssembledTurn 读取统计字段
- **THEN** 设置实体对象的 totalInputTokens、totalOutputTokens、totalTokens
- **THEN** 设置实体对象的 toolCallsCount、toolCallsSuccess、toolCallsError
- **THEN** 通过 MyBatis 批量插入数据库

#### Scenario: 统计值类型转换
- **WHEN** AssembledTurn 中的统计字段为 long 或 int 类型
- **THEN** 转换为数据库实体对应的 Integer 类型
- **THEN** 避免溢出（token 数通常 < 2^31）

### Requirement: 单元测试覆盖
系统 SHALL 提供完整的单元测试验证统计逻辑的正确性。

#### Scenario: 基础场景测试 - 无工具调用
- **WHEN** 运行 testTurnWithoutToolCalls 测试
- **THEN** 验证 token 统计正确
- **THEN** 验证 tool_calls_count = 0

#### Scenario: 基础场景测试 - 单个工具调用成功
- **WHEN** 运行 testSingleToolCallSuccess 测试
- **THEN** 验证 tool_calls_count = 1
- **THEN** 验证 tool_calls_success = 1
- **THEN** 验证 tool_calls_error = 0

#### Scenario: 异常场景测试 - 缺少 toolResult
- **WHEN** 运行 testMissingToolResult 测试
- **THEN** 验证 tool_calls_error = 1

#### Scenario: 异常场景测试 - toolResult 有错误
- **WHEN** 运行 testToolResultWithError 测试
- **THEN** 验证 tool_calls_error = 1

#### Scenario: 异常场景测试 - stopReason 为 error
- **WHEN** 运行 testAssistantStopReasonError 测试
- **THEN** 验证 tool_calls_error = 1

#### Scenario: 异常场景测试 - 多个 toolCall 部分成功
- **WHEN** 运行 testMultipleToolCallsPartialSuccess 测试
- **THEN** 验证 tool_calls_count = 2
- **THEN** 验证 tool_calls_success = 1
- **THEN** 验证 tool_calls_error = 1

#### Scenario: 异常场景测试 - NULL usage
- **WHEN** 运行 testNullUsage 测试
- **THEN** 验证 token 统计为 0（不抛出异常）
