## Why

当前 `dashboard_conversation_turn` 表中的 token 统计字段（`total_input_tokens`, `total_output_tokens`, `total_tokens`）和 toolCall 统计字段（`tool_calls_count`, `tool_calls_success`, `tool_calls_error`）全部为 0，无法提供有价值的统计数据。

这些字段对于以下场景至关重要：
- **成本分析**：通过 token 消耗了解 AI 模型使用成本分布
- **性能监控**：识别高频 toolCall 的用户和技能，优化资源分配
- **错误诊断**：追踪 toolCall 失败率，定位系统问题
- **趋势分析**：按时间维度展示 token 和 toolCall 的变化趋势

需要在扫描 transcript 文件时自动计算并填充这些字段，无需额外数据库查询或后处理。

## What Changes

### 新增功能
- **Token 统计**：在对话轮次组装阶段累加所有 assistant message 的 usage 字段
  - `total_input_tokens`：输入 token 总数
  - `total_output_tokens`：输出 token 总数
  - `total_tokens`：总 token 数
- **ToolCall 统计**：检测每个 toolCall 的执行状态
  - `tool_calls_count`：toolCall 总数
  - `tool_calls_success`：成功执行的 toolCall 数量
  - `tool_calls_error`：失败的 toolCall 数量（缺少 toolResult、toolResult 有错误、stopReason 为 error/aborted）
- **单元测试**：覆盖基础场景和异常场景，确保统计逻辑正确性

### 修改内容
- 扩展 `AssembledTurn` record，添加 6 个统计字段
- 在 `TurnAssembler.assembleTurn()` 中实现统计逻辑
- 修改 `DataIngestionService.saveConversationTurns()` 使用新的统计值
- 创建 `TurnAssemblerTest` 单元测试类

## Capabilities

### New Capabilities
- `turn-statistics`: 对话轮次级别的 token 和 toolCall 统计能力，包括数据收集、计算和持久化

### Modified Capabilities
无

## Impact

### 影响的代码模块
- `src/main/java/com/company/clawboard/parser/TurnAssembler.java` - 核心统计逻辑
- `src/main/java/com/company/clawboard/service/DataIngestionService.java` - 数据持久化
- `src/test/java/com/company/clawboard/parser/TurnAssemblerTest.java` - 单元测试（新建）

### 数据库影响
- `dashboard_conversation_turn` 表的以下字段将从 0 变为实际统计值：
  - `total_input_tokens`, `total_output_tokens`, `total_tokens`
  - `tool_calls_count`, `tool_calls_success`, `tool_calls_error`
- **非破坏性变更**：只填充现有字段，不修改表结构

### API 影响
- 无 API 接口变更
- 现有查询接口返回的数据将包含正确的统计值

### 性能影响
- **轻微增加**：在内存中遍历消息列表进行统计（O(n) 复杂度）
- **无额外数据库查询**：统计在扫描阶段完成，不影响查询性能
- **预期影响 < 5%**：统计逻辑简单，主要开销已在 JSONL 解析阶段

### 向后兼容性
- ✅ 完全兼容：只填充现有字段，不改变数据结构
- ✅ 历史数据：重新扫描后会更新统计值
- ✅ API 响应格式不变
