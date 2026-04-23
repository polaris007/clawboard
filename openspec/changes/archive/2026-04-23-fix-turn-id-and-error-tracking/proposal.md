## Why

当前系统存在三个数据质量问题：

1. **`dashboard_message.is_error` 字段不准确**：很多在 `dashboard_transcript_issue` 表中有错误记录的消息，在 `dashboard_message` 表中的 `is_error` 字段却是 0。这是因为当前的错误判断逻辑只检查了 `msg.isError()`，没有考虑工具调用错误、停止原因等其他错误情况。

2. **`dashboard_transcript_issue.turn_id` 未填充**：问题表中虽然有 `turn_id` 字段，但实际入库时该字段始终为 NULL，导致无法直接通过轮次 ID 查询相关问题。

3. **`dashboard_message` 缺少 `turn_id` 字段**：消息表没有轮次关联字段，查询某个轮次的所有消息需要通过复杂的时间范围或消息 ID 范围匹配，效率低下且不够直观。

这些问题影响了数据的一致性和查询效率，需要在数据入库阶段就确保正确的关联关系和错误标记。

## What Changes

- **新增 `dashboard_message.turn_id` 字段**：添加外键关联到 `dashboard_conversation_turn.id`，支持按轮次快速查询消息
- **修复 `dashboard_message.is_error` 判断逻辑**：综合考虑消息错误标志、错误消息内容、工具调用错误、停止原因等多种错误情况
- **填充 `dashboard_transcript_issue.turn_id`**：在入库时根据问题的行号映射到对应的轮次 ID
- **调整数据入库顺序**：先插入轮次获取 ID，再构建映射关系，最后插入消息和问题时携带正确的 `turn_id`
- **跳过重复扫描**：使用 `INSERT IGNORE` 避免重复处理已存在的会话，提高扫描效率

## Capabilities

### New Capabilities
- `message-turn-association`: 消息与对话轮次的关联能力，包括 turn_id 字段的添加、映射关系的构建、以及入库时的正确填充
- `error-detection-enhancement`: 增强的错误检测能力，修复 is_error 字段的判断逻辑，确保准确反映消息的错误状态
- `issue-turn-mapping`: 问题与轮次的映射能力，根据问题的行号信息自动关联到对应的对话轮次

### Modified Capabilities
<!-- 无现有能力的需求变更 -->

## Impact

**影响的代码模块**：
- `DataIngestionService.java`：重构数据入库流程，调整顺序并添加映射逻辑
- `TranscriptParser.java`：增强解析结果，返回消息到轮次的映射关系
- `DashboardMessage.java`：添加 `turnId` 字段
- `MessageMapper.java/xml`：添加按 Session 查询和批量插入方法
- 数据库迁移脚本：添加 `turn_id` 字段和索引

**API 影响**：
- 无 API 变更，纯内部数据模型优化

**性能影响**：
- 首次扫描：轻微增加（需要构建映射关系），约 5-10%
- 重复扫描：显著提升（直接跳过），减少 90%+ 的处理时间
- 查询性能：显著提升（通过 turn_id 索引查询）

**数据兼容性**：
- 新字段允许 NULL，不影响现有数据
- 历史数据需要重新扫描才能填充 `turn_id`
- 使用 `INSERT IGNORE` 确保不会破坏已有数据
