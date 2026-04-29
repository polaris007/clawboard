-- ============================================
-- 验证 dashboard_hourly_stats 全 0 数据优化
-- ============================================

-- 1. 检查总记录数
SELECT 'Total Records' as metric, COUNT(*) as value FROM dashboard_hourly_stats;

-- 2. 检查全 0 记录数（所有字段都为 0）
SELECT 'All-Zero Records' as metric, COUNT(*) as value 
FROM dashboard_hourly_stats
WHERE total_tokens = 0 
  AND input_tokens = 0 
  AND output_tokens = 0
  AND total_cost = 0
  AND cache_read_tokens = 0
  AND cache_write_tokens = 0
  AND conversation_turns = 0
  AND complete_turns = 0
  AND error_turns = 0
  AND skill_invocations = 0
  AND skill_errors = 0
  AND tool_calls = 0
  AND tool_errors = 0
  AND error_count = 0;

-- 3. 检查非 0 记录数
SELECT 'Non-Zero Records' as metric, COUNT(*) as value 
FROM dashboard_hourly_stats
WHERE total_tokens > 0 
   OR input_tokens > 0 
   OR output_tokens > 0
   OR total_cost > 0
   OR cache_read_tokens > 0
   OR cache_write_tokens > 0
   OR conversation_turns > 0
   OR complete_turns > 0
   OR error_turns > 0
   OR skill_invocations > 0
   OR skill_errors > 0
   OR tool_calls > 0
   OR tool_errors > 0
   OR error_count > 0;

-- 4. 查看最近 10 条记录
SELECT employee_id, stat_hour, total_tokens, conversation_turns, complete_turns, error_count
FROM dashboard_hourly_stats
ORDER BY stat_hour DESC
LIMIT 10;

-- 5. 按员工统计
SELECT 
    employee_id,
    COUNT(*) as record_count,
    SUM(CASE WHEN total_tokens = 0 THEN 1 ELSE 0 END) as zero_records,
    SUM(CASE WHEN total_tokens > 0 THEN 1 ELSE 0 END) as non_zero_records
FROM dashboard_hourly_stats
GROUP BY employee_id
ORDER BY record_count DESC
LIMIT 20;
