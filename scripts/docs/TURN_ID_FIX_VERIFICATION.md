# Turn ID 修复验证指南

## 问题背景

发现数据库中 `dashboard_message` 和 `dashboard_transcript_issue` 表的 `turn_id` 字段大量为 NULL。

## 根本原因

`buildTurnLineNumberMapping` 方法只映射了 turn 的起始和结束消息的行号，导致中间消息对应的 issue 找不到 turn_id。

## 修复方案

改为使用 TranscriptParser 生成的 `messageIdToTurnIndex` 映射，该映射包含了所有消息到 turn 的完整映射关系。

## 验证步骤

### 1. 重新编译项目

```powershell
cd g:\Workplace\github\clawboard
mvn clean compile -DskipTests
```

预期结果：BUILD SUCCESS，无编译错误

### 2. 重置数据库并重新扫描

```powershell
# 重置数据库
.\scripts\reset-database.ps1

# 启动应用（在新终端）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 触发扫描（在另一个终端）
curl -X POST http://localhost:8080/api/v1/scan/trigger -UseBasicParsing
```

等待扫描完成。

### 3. 执行验证SQL

```powershell
mysql -u clawboard -p'Clqc@1234' -h 127.0.0.1 clawboard < scripts\sql\verify-turn-id-fix.sql
```

### 4. 检查结果

#### 关键指标

**Query 1 & 2 - 填充率检查：**
- `fill_rate_percent` 应该接近 100%
- `records_without_turn_id` 应该非常少或为 0

**预期结果：**
```
table_name                 | fill_rate_percent
---------------------------|------------------
dashboard_message          | 95-100%
dashboard_transcript_issue | 95-100%
```

**Query 5 & 6 - 检查 NULL 记录：**
- 查看 turn_id 为 NULL 的具体记录
- 分析为什么这些记录没有 turn_id（可能是特殊情况）

**Query 7 & 8 - 关联正确性：**
- `validation_result` 应该全部显示 '✓ CORRECT'
- 不应该有 '✗ MISMATCH'

**Query 10 - 总体摘要：**
```
total_turns | total_messages | messages_with_turn_id | total_issues | issues_with_turn_id
------------|----------------|----------------------|--------------|--------------------
    N       |      M         |         ~M           |      K       |        ~K
```

### 5. 生成报告验证

```powershell
# 生成时间范围报告
curl -X POST http://localhost:8080/api/v1/reports/generate-by-time-range `
  -H "Content-Type: application/json" `
  -d '{"startTime": "2026-04-20 00:00:00", "endTime": "2026-04-23 23:59:59"}' `
  -UseBasicParsing
```

检查生成的报告：
- **总对话轮数**：应该是合理的数字（与数据库中非系统轮次数量一致）
- **有错误轮数**：应该小于或等于总对话轮数
- **错误率**：应该在 0-100% 之间

### 6. 对比修复前后

如果之前有旧数据，可以对比：

```sql
-- 修复前（如果有备份）
SELECT COUNT(*) FROM dashboard_message WHERE turn_id IS NULL;
SELECT COUNT(*) FROM dashboard_transcript_issue WHERE turn_id IS NULL;

-- 修复后
SELECT COUNT(*) FROM dashboard_message WHERE turn_id IS NULL;
SELECT COUNT(*) FROM dashboard_transcript_issue WHERE turn_id IS NULL;
```

预期：修复后的 NULL 数量显著减少。

## 常见问题

### Q1: 仍有少量 turn_id 为 NULL？

**可能原因：**
1. Issue 的 `messageId` 为空或无效
2. Message 不在任何 turn 的范围内
3. 解析过程中的边界情况

**处理：**
- 检查 Query 5 & 6 中的具体记录
- 确认这些是否是合理的例外情况

### Q2: 填充率不是 100%？

**正常情况：**
- 95-100% 是可以接受的
- 某些特殊消息（如系统消息、元数据）可能不属于任何 turn

**异常情况：**
- 低于 90% 需要进一步调查
- 检查 TranscriptParser 的 turn 组装逻辑

### Q3: 发现 MISMATCH？

**说明 turn_id 关联错误，需要检查：**
1. `messageIdToTurnIndex` 映射是否正确
2. Turn 的 start/end message ID 是否准确
3. Message 和 Turn 的时序是否一致

## 成功标准

✅ 编译无错误  
✅ Message 填充率 ≥ 95%  
✅ Issue 填充率 ≥ 95%  
✅ 无关联错误（MISMATCH）  
✅ 报告统计数据合理  
✅ 错误率在 0-100% 之间  

## 下一步

验证通过后：
1. 提交代码修改
2. 更新相关文档
3. 考虑是否需要数据迁移脚本修复已有数据
