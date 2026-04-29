# 小时级统计聚合修复 - 端到端测试用例

## 测试目标

验证以下修复是否正确工作：
1. **笛卡尔积 Bug 修复**：不再为每个小时 × 所有员工生成无用记录
2. **24 小时前数据聚合**：从文件提取小时的机制能正确处理任意时间的数据
3. **增量聚合准确性**：跨文件的同小时数据能正确聚合

---

## 测试场景设计

### 场景 1：基础扫描 - 验证小时收集功能

**目的**：验证 DataIngestionService 能正确收集小时信息

**步骤**：
1. 清空数据库
2. 启动应用
3. 触发第一次扫描
4. 检查日志中的 "Collected X distinct hours from Y employees"
5. 查询 `dashboard_hourly_stats` 表验证记录数

**预期结果**：
- ✅ 日志显示收集到 N 个不同的小时
- ✅ `dashboard_hourly_stats` 记录数 = N × 员工数（不是笛卡尔积的 N × 总员工数）
- ✅ 没有大量全 0 的记录

---

### 场景 2：24 小时前数据聚合

**目的**：验证扫描到旧数据时能正确聚合

**准备测试数据**：
创建一个包含 7 天前消息的 Session 文件

**步骤**：
1. 在 prod-logs 目录下创建新的测试用户目录
2. 创建包含 7 天前时间戳的 JSONL 文件
3. 触发第二次扫描
4. 检查日志中是否收集到 7 天前的小时
5. 查询数据库验证 7 天前的数据已聚合

**预期结果**：
- ✅ 日志显示收集到包含 7 天前的小时
- ✅ 数据库中 `MIN(stat_hour)` 是 7 天前的日期
- ✅ 该小时的统计数据正确（tokens、turns 等）

---

### 场景 3：跨文件同小时数据聚合

**目的**：验证同一小时内多个 Session 的数据能正确聚合

**准备测试数据**：
创建两个 Session 文件，都包含 2026-04-29 10:xx 的消息

**Session A** (10:10 的消息):
```jsonl
{"type":"message","id":"msg-001","role":"assistant","timestamp":"2026-04-29T10:10:00Z","usage":{"inputTokens":100,"outputTokens":50,"totalTokens":150}}
```

**Session B** (10:20 的消息):
```jsonl
{"type":"message","id":"msg-002","role":"assistant","timestamp":"2026-04-29T10:20:00Z","usage":{"inputTokens":200,"outputTokens":100,"totalTokens":300}}
```

**步骤**：
1. 清空数据库
2. 放置 Session A 文件
3. 触发第一次扫描
4. 查询 10:00 小时的聚合结果（应该有 150 tokens）
5. 放置 Session B 文件
6. 触发第二次扫描
7. 查询 10:00 小时的聚合结果（应该有 450 tokens = 150 + 300）

**预期结果**：
- ✅ 第一次扫描后，10:00 小时有 150 tokens
- ✅ 第二次扫描后，10:00 小时有 450 tokens（正确累加）
- ✅ 没有重复记录或数据丢失

---

### 场景 4：时间窗口兜底机制

**目的**：验证当文件提取失败时，时间窗口查询能作为兜底

**步骤**：
1. 先执行一次完整扫描，确保数据库中有最近 24 小时的数据
2. 模拟文件提取失败（禁用 `enableFileBasedIncremental` 配置）
3. 触发扫描
4. 检查日志中 "Added X hours from time window"

**预期结果**：
- ✅ 即使文件提取被禁用，时间窗口仍能收集到小时
- ✅ 聚合正常完成

---

## 测试数据准备

### 测试用户目录结构

```
test/e2e-test-data/
├── test-user-001/                    # 用于场景 1 & 2
│   └── agents/
│       └── main/
│           └── sessions/
│               ├── session-old.jsonl    # 7 天前的数据
│               └── session-recent.jsonl # 今天的数据
└── test-user-002/                    # 用于场景 3
    └── agents/
        └── main/
            └── sessions/
                ├── session-a.jsonl      # 10:10 的消息
                └── session-b.jsonl      # 10:20 的消息
```

### 示例 JSONL 文件内容

**session-old.jsonl** (7 天前):
```jsonl
{"type":"session","version":3,"id":"session-old-001","timestamp":"2026-04-22T08:00:00Z"}
{"type":"message","id":"msg-old-001","parentId":"session-old-001","role":"user","timestamp":"2026-04-22T08:05:00Z","content":"Hello"}
{"type":"message","id":"msg-old-002","parentId":"msg-old-001","role":"assistant","timestamp":"2026-04-22T08:05:30Z","content":"Hi there!","usage":{"inputTokens":10,"outputTokens":5,"totalTokens":15}}
```

**session-recent.jsonl** (今天):
```jsonl
{"type":"session","version":3,"id":"session-recent-001","timestamp":"2026-04-29T09:00:00Z"}
{"type":"message","id":"msg-recent-001","parentId":"session-recent-001","role":"user","timestamp":"2026-04-29T09:05:00Z","content":"Test"}
{"type":"message","id":"msg-recent-002","parentId":"msg-recent-001","role":"assistant","timestamp":"2026-04-29T09:05:30Z","content":"Response","usage":{"inputTokens":20,"outputTokens":10,"totalTokens":30}}
```

**session-a.jsonl** (10:10):
```jsonl
{"type":"session","version":3,"id":"session-a-001","timestamp":"2026-04-29T10:00:00Z"}
{"type":"message","id":"msg-a-001","parentId":"session-a-001","role":"user","timestamp":"2026-04-29T10:10:00Z","content":"Question A"}
{"type":"message","id":"msg-a-002","parentId":"msg-a-001","role":"assistant","timestamp":"2026-04-29T10:10:30Z","content":"Answer A","usage":{"inputTokens":100,"outputTokens":50,"totalTokens":150}}
```

**session-b.jsonl** (10:20):
```jsonl
{"type":"session","version":3,"id":"session-b-001","timestamp":"2026-04-29T10:00:00Z"}
{"type":"message","id":"msg-b-001","parentId":"session-b-001","role":"user","timestamp":"2026-04-29T10:20:00Z","content":"Question B"}
{"type":"message","id":"msg-b-002","parentId":"msg-b-001","role":"assistant","timestamp":"2026-04-29T10:20:30Z","content":"Answer B","usage":{"inputTokens":200,"outputTokens":100,"totalTokens":300}}
```

---

## 验证 SQL 查询

### 查询 1：检查总记录数和分布
```sql
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT employee_id) as unique_employees,
    COUNT(DISTINCT stat_hour) as unique_hours,
    MIN(stat_hour) as earliest_hour,
    MAX(stat_hour) as latest_hour
FROM dashboard_hourly_stats;
```

### 查询 2：检查是否有大量全 0 记录（笛卡尔积 Bug）
```sql
SELECT 
    COUNT(*) as zero_records,
    COUNT(*) * 100.0 / (SELECT COUNT(*) FROM dashboard_hourly_stats) as percentage
FROM dashboard_hourly_stats
WHERE total_tokens = 0 
  AND conversation_turns = 0 
  AND error_count = 0;
```

**预期**：percentage < 50%（如果超过 80%，说明仍有笛卡尔积问题）

### 查询 3：检查特定小时的聚合准确性
```sql
SELECT 
    employee_id,
    stat_hour,
    total_tokens,
    conversation_turns,
    error_count
FROM dashboard_hourly_stats
WHERE stat_hour = '2026-04-29 10:00:00'
ORDER BY employee_id;
```

### 查询 4：检查 scan_history 中的聚合统计
```sql
SELECT 
    id,
    agg_hours_from_files,
    agg_hours_from_window,
    agg_hours_total,
    agg_duration_ms
FROM dashboard_scan_history
ORDER BY id DESC
LIMIT 5;
```

---

## 执行步骤

### Phase 1: 场景 1 - 基础扫描

1. **清空数据库**
   ```powershell
   Get-Content scripts\sql\reset-database.sql | mysql -h 127.0.0.1 -P 3306 -u clawboard -p'Clqc@1234' clawboard
   ```

2. **启动应用**
   ```powershell
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

3. **触发扫描**
   ```powershell
   Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing
   ```

4. **等待扫描完成并检查日志**
   - 查找 "Collected X distinct hours from Y employees"
   - 查找 "Total (employee, hour) combinations to aggregate: Z"

5. **执行验证 SQL**
   - 运行查询 1 和查询 2
   - 记录结果

---

### Phase 2: 场景 2 - 24 小时前数据

1. **创建测试数据**
   - 在 `test/e2e-test-data/test-user-001/agents/main/sessions/` 创建 `session-old.jsonl`
   - 设置文件时间为 7 天前

2. **触发第二次扫描**

3. **验证旧数据已聚合**
   - 运行查询 1，检查 `earliest_hour` 是否是 7 天前
   - 运行查询 3，检查具体小时的统计数据

---

### Phase 3: 场景 3 - 跨文件同小时聚合

1. **清空数据库**

2. **只放置 session-a.jsonl**

3. **触发第一次扫描**

4. **查询 10:00 小时的 tokens**（预期 150）

5. **放置 session-b.jsonl**

6. **触发第二次扫描**

7. **再次查询 10:00 小时的 tokens**（预期 450）

---

### Phase 4: 场景 4 - 时间窗口兜底

1. **修改配置文件** `application-dev.yml`：
   ```yaml
   clawboard:
     aggregation:
       enable-file-based-incremental: false  # 禁用文件提取
       enable-time-window: true
       time-window-hours: 24
   ```

2. **重启应用**

3. **触发扫描**

4. **检查日志**：应该看到 "Added X hours from time window"

5. **恢复配置**

---

## 成功标准

✅ **所有场景通过**：
- 场景 1：收集到正确数量的小时，无笛卡尔积
- 场景 2：7 天前的数据被正确聚合
- 场景 3：跨文件同小时数据正确累加（150 → 450）
- 场景 4：时间窗口兜底机制正常工作

✅ **性能指标**：
- 聚合耗时 < 2 分钟（对于 ~20000 条记录）
- 无内存溢出或超时

✅ **数据一致性**：
- `dashboard_hourly_stats` 中的数据与源文件一致
- 多次扫描后数据不重复、不丢失

---

## 故障排查

### 问题 1：日志中没有 "Collected X distinct hours"

**可能原因**：
- DataIngestionService 的 `collectHoursFromMessages` 未被调用
- 消息的 `messageTimestamp` 为 null

**检查**：
```java
// 在 DataIngestionService.collectHoursFromMessages 中添加日志
log.debug("Collecting hours for employee {}, messages count: {}", employeeId, messages.size());
```

### 问题 2：仍有大量全 0 记录

**可能原因**：
- HourlyStatsAggregator 仍在生成笛卡尔积
- 时间窗口查询返回了空的 EmployeeHourKey

**检查**：
```sql
-- 查看哪些组合是全 0
SELECT employee_id, stat_hour, total_tokens, conversation_turns
FROM dashboard_hourly_stats
WHERE total_tokens = 0 AND conversation_turns = 0
LIMIT 10;
```

### 问题 3：跨文件聚合不正确

**可能原因**：
- 第二次扫描时删除了第一次的数据但没有重新聚合
- SQL 聚合逻辑有误

**检查**：
```sql
-- 检查 dashboard_message 表中的原始数据
SELECT 
    employee_id,
    DATE_FORMAT(message_timestamp, '%Y-%m-%d %H:00:00') as hour,
    SUM(total_tokens) as expected_tokens
FROM dashboard_message
WHERE role = 'assistant'
  AND is_system = 0
GROUP BY employee_id, DATE_FORMAT(message_timestamp, '%Y-%m-%d %H:00:00');

-- 对比 dashboard_hourly_stats
SELECT 
    employee_id,
    stat_hour,
    total_tokens as actual_tokens
FROM dashboard_hourly_stats;
```

---

## 测试报告模板

```markdown
# 小时级统计聚合修复 - E2E 测试报告

**测试日期**: 2026-04-29
**测试人员**: [Your Name]
**Git Commit**: [commit hash]

## 测试结果摘要

| 场景 | 状态 | 备注 |
|------|------|------|
| 场景 1: 基础扫描 | ✅/❌ | |
| 场景 2: 24 小时前数据 | ✅/❌ | |
| 场景 3: 跨文件同小时聚合 | ✅/❌ | |
| 场景 4: 时间窗口兜底 | ✅/❌ | |

## 详细结果

### 场景 1: 基础扫描
- 收集到的小时数: X
- 员工数: Y
- 生成的组合数: Z
- 全 0 记录比例: P%
- **结论**: ✅/❌

### 场景 2: 24 小时前数据
- 最早的小时: [datetime]
- 最晚的小时: [datetime]
- 7 天前的小时是否有数据: Yes/No
- **结论**: ✅/❌

### 场景 3: 跨文件同小时聚合
- 第一次扫描后 10:00 的 tokens: 150
- 第二次扫描后 10:00 的 tokens: 450
- 是否符合预期: Yes/No
- **结论**: ✅/❌

### 场景 4: 时间窗口兜底
- 时间窗口收集到的小时数: X
- 聚合是否成功: Yes/No
- **结论**: ✅/❌

## 性能指标

- 扫描总耗时: X ms
- 聚合耗时: Y ms
- 处理的记录数: Z
- 平均每条记录耗时: W ms

## 问题与建议

[列出发现的问题和改进建议]

## 最终结论

✅ **测试通过** - 所有场景符合预期
❌ **测试失败** - 存在以下问题：[详细描述]
```
