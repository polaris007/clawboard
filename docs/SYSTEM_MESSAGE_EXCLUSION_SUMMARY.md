# 排除系统轮次和系统消息的统计修改总结

## 修改日期
2026-04-21

## 修改目的
在所有接口统计数量时，排除掉系统轮次（system_turn = 1）和系统消息（is_system = 1），确保统计数据只反映真实的用户交互。

## 已完成的修改

### 1. HourlyStatsMapper.xml - 聚合查询排除系统数据

#### 修改位置 1: selectDistinctEmployeeHours
**文件**: `src/main/resources/mapper/HourlyStatsMapper.xml`

**修改内容**:
- 在从 `dashboard_message` 表查询时添加条件: `AND is_system = 0`
- 在从 `dashboard_conversation_turn` 表查询时添加条件: `AND system_turn = 0`

**原因**: 确保在生成员工-小时组合列表时，不包含系统消息和系统轮次的时间点。

#### 修改位置 2: selectDistinctEmployeeHoursByEmployees
**文件**: `src/main/resources/mapper/HourlyStatsMapper.xml`

**修改内容**:
- 在从 `dashboard_message` 表查询时添加条件: `AND is_system = 0`
- 在从 `dashboard_conversation_turn` 表查询时添加条件: `AND system_turn = 0`

**原因**: 与上面相同，但针对特定员工列表的查询。

#### 已正确实现: aggregateTurnsByHour
**文件**: `src/main/resources/mapper/HourlyStatsMapper.xml`

该查询已经包含了 `AND system_turn = 0` 条件（第144行），无需修改。

### 2. TurnErrorService.java - 搜索轮次排除系统轮次

#### 修改位置: searchTurns 方法
**文件**: `src/main/java/com/company/clawboard/service/TurnErrorService.java`

**修改内容**:
```java
// 过滤掉系统轮次
List<DashboardConversationTurn> nonSystemTurns = turns.stream()
    .filter(turn -> turn.getSystemTurn() == null || turn.getSystemTurn() == 0)
    .collect(Collectors.toList());
```

**原因**: 确保 `/api/v1/turns/search` 接口返回的轮次列表中不包含系统轮次。

### 3. DataIngestionService.java - 会话摘要计算已正确处理

#### 已正确实现: conversationTurns 计算
**文件**: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

该方法已经在第89-103行正确实现了排除系统消息的逻辑：
- 遍历消息时检查是否为 "user" 角色
- 使用 `systemMessageFilter.isSystemGeneratedUserMessage()` 判断是否为系统生成的用户消息
- 只有非系统生成的用户消息才计入对话轮次数

**无需修改**。

## 影响范围

### 受影响的接口

1. **GET /api/v1/dashboard/global-stats**
   - 通过 `HourlyStatsMapper.selectByTimeRange` 查询
   - 统计数据来自 `dashboard_hourly_stats` 表
   - 该表的聚合逻辑已修改，会自动排除系统数据

2. **POST /api/v1/dashboard/summary**
   - 通过 `HourlyStatsMapper.selectByTimeRange` 查询
   - 同上，统计数据已排除系统数据

3. **POST /api/v1/dashboard/trend**
   - 通过 `HourlyStatsMapper.selectByTimeRange` 查询
   - 同上，统计数据已排除系统数据

4. **POST /api/v1/dashboard/usersummary**
   - 通过 `HourlyStatsMapper.selectByTimeRange` 查询
   - 同上，统计数据已排除系统数据

5. **POST /api/v1/turns/search**
   - 通过 `TurnErrorService.searchTurns` 查询
   - 已添加过滤逻辑，排除系统轮次

6. **POST /api/v1/errors/summary**
   - 查询 `dashboard_transcript_issue` 表
   - 问题(issue)本身不应该区分系统/非系统，因为问题是检测出来的异常情况
   - **无需修改**

7. **POST /api/v1/errors/search**
   - 查询 `dashboard_transcript_issue` 表
   - 同上，**无需修改**

### 不受影响的接口

1. **GET /api/v1/skills/options** - 技能选项列表，不涉及数量统计

2. **GET /api/v1/turns/{turnId}/trace** - 单个轮次的追踪信息，如果传入的是系统轮次ID，仍会返回其信息

## 数据库字段说明

### dashboard_message 表
- `is_system` (TINYINT): 标记是否为系统消息
  - 0: 用户或助手的正常消息
  - 1: 系统生成的消息（如心跳、系统提示等）

### dashboard_conversation_turn 表
- `system_turn` (TINYINT): 标记是否为系统轮次
  - 0: 正常的用户对话轮次
  - 1: 系统自动触发的轮次

## 验证建议

修改完成后，建议进行以下验证：

1. **重新扫描数据**: 清空数据库后重新触发扫描，确保新数据正确标记了 `is_system` 和 `system_turn` 字段

2. **测试各接口**:
   ```bash
   # 测试全局统计
   curl http://localhost:8080/api/v1/dashboard/global-stats
   
   # 测试汇总统计
   curl -X POST http://localhost:8080/api/v1/dashboard/summary \
     -H "Content-Type: application/json" \
     -d '{"startTime":"2026-04-21 00:00:00","endTime":"2026-04-21 23:59:59"}'
   
   # 测试轮次搜索
   curl -X POST http://localhost:8080/api/v1/turns/search \
     -H "Content-Type: application/json" \
     -d '{"page":1,"pageSize":10}'
   ```

3. **对比数据**: 检查统计结果是否合理，确认系统消息和系统轮次已被排除

## 注意事项

1. **历史数据**: 如果数据库中已有历史数据，需要确认这些数据是否正确设置了 `is_system` 和 `system_turn` 字段。如果没有，可能需要运行数据迁移脚本。

2. **性能影响**: 添加过滤条件可能会对查询性能产生轻微影响，但由于这些字段都有索引（或应该添加索引），影响应该很小。

3. **向后兼容**: 对于 `system_turn` 字段为 NULL 的情况，代码中使用了 `turn.getSystemTurn() == null || turn.getSystemTurn() == 0` 的判断，确保兼容性。

## 相关文件清单

### 已修改的文件
1. `src/main/resources/mapper/HourlyStatsMapper.xml`
2. `src/main/java/com/company/clawboard/service/TurnErrorService.java`

### 相关但未修改的文件
1. `src/main/java/com/company/clawboard/service/DataIngestionService.java` - 已正确实现
2. `src/main/java/com/company/clawboard/service/DashboardService.java` - 依赖聚合数据，无需修改
3. `src/main/java/com/company/clawboard/entity/DashboardMessage.java` - 实体类已有 isSystem 字段
4. `src/main/java/com/company/clawboard/entity/DashboardConversationTurn.java` - 实体类已有 systemTurn 字段
