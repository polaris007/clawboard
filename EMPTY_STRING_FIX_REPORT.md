# `/turns/search` 接口空字符串处理修复报告

## 问题描述

当 `/api/v1/turns/search` 接口传入的 `startTime` 或 `endTime` 为空字符串 `""` 时，原有的 SQL 条件判断只检查了 `null`，没有检查空字符串，导致生成错误的 SQL 查询。

## 问题分析

### 原有代码

**ConversationTurnMapper.xml（修复前）**
```xml
<select id="selectTurnsWithFilters" resultMap="BaseResultMap">
    SELECT * FROM dashboard_conversation_turn 
    WHERE (system_turn = 0 OR system_turn IS NULL)
    
    <!-- ❌ 只检查 null，不检查空字符串 -->
    <if test="startTime != null">
        AND start_time >= #{startTime}
    </if>
    <if test="endTime != null">
        AND start_time <= #{endTime}
    </if>
    
    ORDER BY start_time DESC
</select>
```

### 问题表现

| 输入值 | MyBatis 判断 | 是否添加条件 | SQL 行为 | 结果 |
|--------|-------------|-------------|---------|------|
| `null` | `!= null` → false | ❌ 不添加 | 不筛选时间 | ✅ 正确 |
| `""` | `!= null` → true | ✅ **添加** | `start_time >= ''` | ❌ **错误** |
| `"2026-04-21 00:00:00"` | `!= null` → true | ✅ 添加 | 正常比较 | ✅ 正确 |

#### 具体场景

**场景 1：`startTime = ""`**
```sql
-- 生成的 SQL
SELECT * FROM dashboard_conversation_turn 
WHERE (system_turn = 0 OR system_turn IS NULL)
  AND start_time >= ''  -- MySQL 将 '' 转换为 '0000-00-00 00:00:00'
ORDER BY start_time DESC
```
- 看似返回所有数据（因为所有时间都 >= 最小值）
- 但依赖 MySQL 的隐式类型转换，**不可靠且不符合预期**

**场景 2：`endTime = ""`**
```sql
-- 生成的 SQL
SELECT * FROM dashboard_conversation_turn 
WHERE (system_turn = 0 OR system_turn IS NULL)
  AND start_time <= ''  -- MySQL 将 '' 转换为 '0000-00-00 00:00:00'
ORDER BY start_time DESC
```
- **不会返回任何数据**（因为没有时间 <= '0000-00-00 00:00:00'）
- ❌ **完全错误！**

## 修复方案

### 修改位置

在 `ConversationTurnMapper.xml` 中，为所有 `startTime` 和 `endTime` 的条件判断添加空字符串检查。

### 修复内容

#### 1. `selectTurnsWithFilters` （第117、120行）

```xml
<!-- ✅ 修复后：同时检查 null 和空字符串 -->
<if test="startTime != null and startTime != ''">
    AND start_time >= #{startTime}
</if>
<if test="endTime != null and endTime != ''">
    AND start_time <= #{endTime}
</if>
```

#### 2. `countNonSystemTurns` （第138、141行）

```xml
<!-- ✅ 修复后：同时检查 null 和空字符串 -->
<if test="startTime != null and startTime != ''">
    AND t.start_time >= #{startTime}
</if>
<if test="endTime != null and endTime != ''">
    AND t.start_time <= #{endTime}
</if>
```

#### 3. `countNoErrorCompleteTurns` （第160、163行）

```xml
<!-- ✅ 修复后：同时检查 null 和空字符串 -->
<if test="startTime != null and startTime != ''">
    AND t.start_time >= #{startTime}
</if>
<if test="endTime != null and endTime != ''">
    AND t.start_time <= #{endTime}
</if>
```

### 修复后的行为

| 输入值 | MyBatis 判断 | 是否添加条件 | 结果 |
|--------|-------------|-------------|------|
| `null` | `!= null` → false | ❌ 不添加 | ✅ 不筛选时间 |
| `""` | `!= ''` → false | ❌ 不添加 | ✅ 不筛选时间 |
| `"2026-04-21 00:00:00"` | 两个条件都为 true | ✅ 添加 | ✅ 正常筛选 |

## 验证方法

### 测试用例 1：空字符串
```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{"startTime": "", "endTime": "", "page": 1, "pageSize": 10}'
```

**预期结果：**
- 返回所有非系统轮次（不筛选时间）
- 与传入 `null` 的行为一致

### 测试用例 2：正常时间范围
```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{"startTime": "2026-04-21 00:00:00", "endTime": "2026-04-21 23:59:59", "page": 1, "pageSize": 10}'
```

**预期结果：**
- 返回指定时间范围内的轮次
- 正常筛选

### 测试用例 3：只有 startTime
```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{"startTime": "2026-04-21 00:00:00", "endTime": "", "page": 1, "pageSize": 10}'
```

**预期结果：**
- 只筛选开始时间，不筛选结束时间
- 返回 `startTime` 之后的所有数据

## 相关接口检查

### ✅ 已正确处理的接口

**HourlyStatsMapper.xml**
```xml
<!-- 已经正确检查空字符串 -->
<if test="startTime != null and startTime != ''">
  AND s.stat_hour >= #{startTime}
</if>
<if test="endTime != null and endTime != ''">
  AND s.stat_hour <= #{endTime}
</if>
```

这个接口的实现是正确的，不需要修改。

## 最佳实践建议

### MyBatis 动态 SQL 条件判断规范

对于字符串类型的参数，应该**始终**同时检查 `null` 和空字符串：

```xml
<!-- ✅ 推荐写法 -->
<if test="param != null and param != ''">
    AND column = #{param}
</if>

<!-- ❌ 不推荐：可能遗漏空字符串 -->
<if test="param != null">
    AND column = #{param}
</if>
```

### Service 层预处理（可选增强）

虽然 Mapper 层已经修复，但也可以在 Service 层进行预处理：

```java
public PageResult<TurnSearchItem> searchTurns(TurnSearchRequest request) {
    // 将空字符串转换为 null
    String startTimeStr = StringUtils.isBlank(request.getStartTime()) 
        ? null : request.getStartTime();
    String endTimeStr = StringUtils.isBlank(request.getEndTime()) 
        ? null : request.getEndTime();
    
    List<DashboardConversationTurn> turns = turnMapper.selectTurnsWithFilters(
        request.getUserId(),
        startTimeStr,
        endTimeStr
    );
    // ...
}
```

**优点：**
- 更早地清理无效数据
- 统一处理逻辑
- 便于单元测试

**缺点：**
- 增加了一层处理
- 如果 Mapper 已经正确处理，可能冗余

**建议：** 
- 当前项目已在 Mapper 层修复，足够可靠
- 如果未来有更多类似场景，可以考虑在 Service 层统一处理

## 总结

### 修复的文件
- ✅ `src/main/resources/mapper/ConversationTurnMapper.xml`
  - `selectTurnsWithFilters`
  - `countNonSystemTurns`
  - `countNoErrorCompleteTurns`

### 修复的原则
- 所有字符串类型的时间参数都应该检查 `null` **和** 空字符串
- 保持与其他参数（如 `userId`、`teamName`）的判断逻辑一致

### 影响范围
- `/api/v1/turns/search` - 对话轮次搜索
- `/api/v1/dashboard/summary` - 仪表板汇总（通过 `countNonSystemTurns` 和 `countNoErrorCompleteTurns`）

### 兼容性
- ✅ 向后兼容：原有的 `null` 值处理保持不变
- ✅ 修复了空字符串的边界情况
- ✅ 不影响正常的时间字符串处理
