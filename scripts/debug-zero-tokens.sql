-- ============================================
-- 诊断 Token 为 0 的问题
-- ============================================

-- 1. 检查 dashboard_hourly_stats 中的记录
SELECT 
    employee_id,
    stat_hour,
    total_tokens,
    input_tokens,
    output_tokens,
    conversation_turns,
    complete_turns,
    error_turns,
    skill_invocations,
    tool_calls,
    updated_at
FROM dashboard_hourly_stats
WHERE DATE(stat_hour) = '2026-04-23'
ORDER BY employee_id, stat_hour;

-- 2. 检查这些用户在 dashboard_message 中的实际数据
SELECT 
    m.employee_id,
    COUNT(*) as message_count,
    SUM(m.total_tokens) as total_tokens_sum,
    SUM(CASE WHEN m.is_system = 0 THEN m.total_tokens ELSE 0 END) as non_system_tokens_sum,
    SUM(CASE WHEN m.is_system = 1 THEN m.total_tokens ELSE 0 END) as system_tokens_sum,
    COUNT(CASE WHEN m.is_system = 0 THEN 1 END) as non_system_msg_count,
    COUNT(CASE WHEN m.is_system = 1 THEN 1 END) as system_msg_count
FROM dashboard_message m
WHERE DATE(m.message_timestamp) = '2026-04-23'
  AND m.role = 'assistant'
  AND m.employee_id IN (
      SELECT DISTINCT employee_id 
      FROM dashboard_hourly_stats 
      WHERE DATE(stat_hour) = '2026-04-23'
        AND total_tokens = 0
  )
GROUP BY m.employee_id;

-- 3. 检查这些用户在 dashboard_conversation_turn 中的数据
SELECT 
    t.employee_id,
    COUNT(*) as turn_count,
    SUM(CASE WHEN t.system_turn = 0 THEN 1 ELSE 0 END) as non_system_turns,
    SUM(CASE WHEN t.system_turn = 1 THEN 1 ELSE 0 END) as system_turns,
    SUM(t.total_tokens) as total_tokens_sum
FROM dashboard_conversation_turn t
WHERE DATE(t.start_time) = '2026-04-23'
  AND t.employee_id IN (
      SELECT DISTINCT employee_id 
      FROM dashboard_hourly_stats 
      WHERE DATE(stat_hour) = '2026-04-23'
        AND total_tokens = 0
  )
GROUP BY t.employee_id;

-- 4. 对比：正常有 Token 的用户数据
SELECT 
    employee_id,
    stat_hour,
    total_tokens,
    conversation_turns,
    skill_invocations
FROM dashboard_hourly_stats
WHERE DATE(stat_hour) = '2026-04-23'
  AND total_tokens > 0
LIMIT 5;
