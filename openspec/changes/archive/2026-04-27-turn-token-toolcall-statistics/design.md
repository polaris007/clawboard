## Context

当前 `TurnAssembler` 负责将 transcript 文件中的消息组装成对话轮次（Conversation Turn），但只计算了基本的元数据（时间、状态、错误标记等），没有统计 token 消耗和 toolCall 执行情况。

`dashboard_conversation_turn` 表已预留以下字段，但全部为 0：
- Token 统计：`total_input_tokens`, `total_output_tokens`, `total_tokens`
- ToolCall 统计：`tool_calls_count`, `tool_calls_success`, `tool_calls_error`

需要在不改变现有架构的前提下，在组装阶段完成统计并持久化到数据库。

## Goals / Non-Goals

**Goals:**
1. 在 `TurnAssembler.assembleTurn()` 中实现 token 和 toolCall 统计逻辑
2. 扩展 `AssembledTurn` record 携带统计结果
3. 修改 `DataIngestionService` 使用新的统计值填充数据库
4. 编写完整的单元测试覆盖基础场景和异常场景
5. 保持向后兼容，不影响现有功能

**Non-Goals:**
1. ❌ 不修改数据库表结构（字段已存在）
2. ❌ 不实现 skill_calls 统计（后续迭代）
3. ❌ 不修改 API 接口（统计数据通过现有接口返回）
4. ❌ 不处理历史数据迁移（重新扫描即可更新）

## Decisions

### 决策 1：统计时机选择在 TurnAssembler 阶段

**选择**：在 `TurnAssembler.assembleTurn()` 方法中遍历消息列表进行统计

**理由**：
- ✅ 此时所有消息已在内存中，无需额外数据库查询
- ✅ 可以访问完整的 message 信息（usage、toolCalls、stopReason）
- ✅ 与现有的轮次组装逻辑自然结合
- ✅ 性能开销最小（O(n) 遍历，n 为消息数量）

**替代方案**：
- ❌ 在 DataIngestionService 中统计：需要额外遍历，代码重复
- ❌ 在数据库层计算：复杂度高，性能差

### 决策 2：Token 统计范围限定为 Assistant Message

**选择**：只累加 assistant message 的 usage 字段

**理由**：
- ✅ OpenClaw 的 transcript 文件中，只有 assistant message 包含 usage 信息
- ✅ user message 和 system message 通常没有 usage 字段
- ✅ 符合业务语义：assistant 是实际调用模型产生 token 消耗的一方

**验证**：通过检查实际 transcript 文件确认此规则

### 决策 3：ToolCall 错误判定规则

**选择**：以下情况计为 toolCall 错误：
1. 缺少对应的 toolResult（孤立 toolCall）
2. toolResult 有错误（isError() == true 或 errorMessage 不为空）
3. assistant stopReason 为 "error" 或 "aborted"

**特殊处理**：孤立的 toolResult（找不到对应 toolCall）**不计入** tool_calls_error

**理由**：
- ✅ 保持约束：`tool_calls_error ≤ tool_calls_count`
- ✅ 孤立 toolResult 可能是系统清理残留，不应影响统计准确性
- ✅ 只统计明确的 toolCall 失败情况

**替代方案**：
- ❌ 孤立 toolResult 也计为错误：会导致数量不匹配，违反约束

### 决策 4：每个 Assistant Message 最多 1 个 ToolCall

**发现**：通过检查实际 transcript 文件，每个 assistant message 的 content 数组中最多只有 1 个 type="toolCall" 的 block

**实现**：虽然实际只有 1 个，但代码使用循环处理，支持未来扩展到多个 toolCall

**理由**：
- ✅ 代码更具通用性，适应未来变化
- ✅ 不增加复杂度（只是简单的 for 循环）

### 决策 5：NULL Usage 处理策略

**选择**：如果 assistant message 的 usage 为 null，当作 0 处理（不累加）

**理由**：
- ✅ 防御性编程，避免 NullPointerException
- ✅ 符合业务语义：没有 usage 信息即表示无 token 消耗记录
- ✅ 不影响统计准确性（实际场景中极少出现）

## Risks / Trade-offs

### 风险 1：性能影响

**风险**：遍历消息列表可能增加扫描时间

**缓解措施**：
- 统计逻辑简单（O(n) 线性遍历），主要开销在 JSONL 解析
- 预期性能影响 < 5%
- 可通过压测验证

### 风险 2：统计逻辑与业务理解不一致

**风险**：用户对"错误"的定义可能与实现不同

**缓解措施**：
- 详细文档说明错误判定规则
- 单元测试覆盖各种边界情况
- 提供配置项调整规则（如需要）

### 风险 3：历史数据统计不准确

**风险**：已入库的历史数据仍为 0

**缓解措施**：
- 重新扫描即可更新统计值
- 提供脚本触发全量重扫（如需要）

### Trade-off 1：统计粒度

**选择**：在对话轮次级别统计，而非消息级别

**权衡**：
- ✅ 优点：符合业务需求（按轮次展示统计）
- ❌ 缺点：无法追溯单个消息的 token 消耗
- **结论**：当前需求不需要消息级别统计，如需可扩展

### Trade-off 2：孤立 ToolResult 处理

**选择**：不计入错误统计

**权衡**：
- ✅ 优点：保持数量约束，避免误导
- ❌ 缺点：可能遗漏部分异常情况
- **结论**：优先保证数据一致性，孤立 toolResult 可在其他维度监控
