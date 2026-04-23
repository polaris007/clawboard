-- ============================================
-- 测试 execute-sql-file 接口的查询结果打印功能
-- ============================================

-- 1. 查询 dashboard_hourly_stats 表的结构
DESCRIBE dashboard_hourly_stats;

-- 2. 查询最近的统计数据（SELECT 查询）
SELECT 
    employee_id,
    stat_hour,
    total_tokens,
    conversation_turns,
    skill_invocations
FROM dashboard_hourly_stats
ORDER BY stat_hour DESC
LIMIT 5;

-- 3. 统计每个用户的总 Token 消耗
SELECT 
    employee_id,
    COUNT(*) as hour_count,
    SUM(total_tokens) as total_tokens,
    SUM(conversation_turns) as total_turns
FROM dashboard_hourly_stats
GROUP BY employee_id
ORDER BY total_tokens DESC
LIMIT 10;
