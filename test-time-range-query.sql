-- 测试时间范围查询逻辑
-- 模拟传入: startTime = '2026-04-22 00:59:03', endTime = '2026-04-22 23:00:00'

SELECT 
    stat_hour,
    CASE 
        WHEN stat_hour >= '2026-04-22 00:59:03' AND stat_hour <= '2026-04-22 23:00:00' 
        THEN '✅ 应该返回'
        ELSE '❌ 不应返回'
    END AS expected_result
FROM dashboard_hourly_stats
WHERE stat_hour >= '2026-04-22 00:00:00' 
  AND stat_hour <= '2026-04-22 23:59:59'
ORDER BY stat_hour;

-- 查看具体某条记录
SELECT * FROM dashboard_hourly_stats 
WHERE DATE(stat_hour) = '2026-04-22' 
  AND HOUR(stat_hour) = 0
LIMIT 5;
