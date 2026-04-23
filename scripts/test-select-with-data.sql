-- ============================================
-- 测试 SELECT 查询结果打印 - 使用今天的数据
-- ============================================

-- 1. 查询今天的统计数据
SELECT 
    employee_id,
    stat_hour,
    total_tokens,
    conversation_turns,
    skill_invocations
FROM dashboard_hourly_stats
WHERE DATE(stat_hour) = CURDATE()
ORDER BY total_tokens DESC
LIMIT 10;
