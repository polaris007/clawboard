## Context

当前 ClawBoard 系统的数据入库流程存在以下问题：

1. **入库顺序不合理**：先插入消息，再插入轮次，导致无法在消息中设置 `turn_id`
2. **错误检测不完整**：`is_error` 字段只检查了 `msg.isError()`，忽略了工具调用错误、停止原因等其他错误情况
3. **关联关系缺失**：`dashboard_transcript_issue.turn_id` 始终为 NULL，无法直接通过轮次查询问题

当前流程：
```
Messages → Turns → Skills → Issues
```

目标流程：
```
Turns → Build Mappings → Messages (with turn_id) → Issues (with turn_id)
```

## Goals / Non-Goals

**Goals:**
- 新增 `dashboard_message.turn_id` 字段并正确填充
- 修复 `dashboard_message.is_error` 的判断逻辑，确保准确性
- 填充 `dashboard_transcript_issue.turn_id` 字段
- 调整入库顺序，先插入轮次再插入消息和问题
- 实现按 Session 的重复扫描跳过机制

**Non-Goals:**
- 不处理 `dashboard_skill_invocation.turn_id` 的填充（留待后续优化）
- 不提供历史数据回填脚本（依赖重新扫描）
- 不修改现有 API 接口（纯内部优化）

## Decisions

### Decision 1: 数据库字段添加策略

**选择**：使用 `ALTER TABLE ADD COLUMN` 添加 `turn_id` 字段，允许 NULL

**理由**：
- 向后兼容，不影响现有数据
- 新扫描的数据会自动填充
- 避免复杂的数据迁移脚本

**备选方案**：
- ❌ 创建新表并迁移数据：成本过高，风险大
- ❌ 不允许 NULL 并设置默认值：无法区分"未填充"和"确实没有轮次"

### Decision 2: 入库顺序调整

**选择**：Turns → Messages → Issues

**理由**：
- 轮次的 ID 是自增主键，必须先插入才能获取
- 消息和问题需要引用轮次 ID
- 符合外键依赖关系

**备选方案**：
- ❌ 保持原顺序 + 事后 UPDATE：需要额外的写操作，性能差
- ❌ 应用层预计算 ID：不可靠，依赖内存状态

### Decision 3: 映射关系构建方式

**选择**：在解析阶段返回 `messageIdToTurnIndex` 映射

**理由**：
- 解析器已经知道每条消息属于哪个轮次
- 避免在入库阶段重新计算
- 数据源单一，减少不一致风险

**实现**：
```java
public record ParsedTranscript(
    String sessionId,
    List<MessageRecord> messages,
    List<TurnAssembler.AssembledTurn> turns,
    List<IssueDetector.DetectedIssue> issues,
    List<SkillInvocation> skillInvocations,
    Map<String, Integer> messageIdToTurnIndex  // NEW
) {}
```

### Decision 4: 重复扫描处理策略

**选择**：使用 `INSERT IGNORE` + Session 存在性检查

**理由**：
- 简单高效，无需复杂的差异比对
- 避免不必要的数据库写操作
- 适合增量扫描场景

**实现**：
```java
if (messageMapper.countBySessionId(sessionId) > 0) {
    log.info("Session {} already scanned, skipping", sessionId);
    return;
}
```

**备选方案**：
- ❌ Smart Upsert（先查询再决定 Insert/Update）：增加复杂度，收益有限
- ❌ ON DUPLICATE KEY UPDATE：即使数据不变也会执行 UPDATE，浪费资源

### Decision 5: is_error 判断逻辑增强

**选择**：综合检查多个错误指标

**实现**：
```java
private boolean hasMessageError(MessageRecord msg) {
    // 1. 直接错误标志
    if (msg.isError()) return true;
    
    // 2. 错误消息内容
    if (msg.errorMessage() != null && !msg.errorMessage().isEmpty()) return true;
    
    // 3. 工具调用错误
    if (msg.toolCalls() != null) {
        for (var toolCall : msg.toolCalls()) {
            if (toolCall.isError() || 
                (toolCall.error() != null && !toolCall.error().isEmpty())) {
                return true;
            }
        }
    }
    
    // 4. 停止原因包含错误
    if (msg.stopReason() != null && 
        (msg.stopReason().toLowerCase().contains("error") || 
         msg.stopReason().toLowerCase().contains("timeout") ||
         msg.stopReason().toLowerCase().contains("failure"))) {
        return true;
    }
    
    return false;
}
```

**理由**：
- 覆盖所有可能的错误情况
- 与 `dashboard_transcript_issue` 的错误检测保持一致
- 提高数据准确性

## Risks / Trade-offs

### Risk 1: 历史数据的 turn_id 为 NULL

**影响**：旧数据无法通过 turn_id 查询

**缓解**：
- 提供 SQL 脚本说明如何重新扫描
- 在新功能文档中明确说明此限制
- 未来可考虑添加后台任务自动回填

### Risk 2: 解析阶段返回映射增加内存占用

**影响**：每个 Session 的映射关系存储在内存中

**缓解**：
- Session 通常只有几十到几百条消息，内存占用可控
- 扫描完成后立即释放
- 监控内存使用情况，必要时优化

### Risk 3: INSERT IGNORE 可能掩盖真正的错误

**影响**：如果唯一键冲突但不是因为重复扫描，错误会被静默忽略

**缓解**：
- 在入库前检查 Session 是否存在
- 记录详细的日志，包括跳过的原因
- 定期审计数据一致性

### Trade-off 1: 性能 vs 数据完整性

**选择**：优先保证数据完整性，接受轻微的性能开销

**理由**：
- 首次扫描性能增加 5-10%，可接受
- 重复扫描性能提升 90%+，收益更大
- 数据准确性比性能更重要

### Trade-off 2: 复杂性 vs 灵活性

**选择**：简化设计，暂不处理 skill_invocation 的 turn_id

**理由**：
- 聚焦核心问题（message 和 issue）
- 避免过度设计
- skill_invocation 的使用频率较低，可后续优化
