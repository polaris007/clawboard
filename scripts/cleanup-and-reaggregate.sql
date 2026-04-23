-- ============================================
-- 清理旧的统计数据并重新聚合
-- ============================================

-- 1. 备份当前数据（可选）
CREATE TABLE IF NOT EXISTS dashboard_hourly_stats_backup_20260423 AS
SELECT * FROM dashboard_hourly_stats
WHERE DATE(stat_hour) = '2026-04-23';

-- 2. 删除 2026-04-23 的旧统计数据
DELETE FROM dashboard_hourly_stats
WHERE DATE(stat_hour) = '2026-04-23';

-- 3. 验证删除结果
SELECT COUNT(*) as remaining_records
FROM dashboard_hourly_stats
WHERE DATE(stat_hour) = '2026-04-23';
-- 应该返回 0

-- 4. 查看其他日期的数据是否正常
SELECT 
    DATE(stat_hour) as date,
    COUNT(DISTINCT employee_id) as user_count,
    SUM(total_tokens) as total_tokens,
    SUM(conversation_turns) as total_turns
FROM dashboard_hourly_stats
GROUP BY DATE(stat_hour)
ORDER BY date DESC
LIMIT 10;

-- 5. 如果需要恢复数据（谨慎使用）
-- DELETE FROM dashboard_hourly_stats WHERE DATE(stat_hour) = '2026-04-23';
-- INSERT INTO dashboard_hourly_stats SELECT * FROM dashboard_hourly_stats_backup_20260423;
-- DROP TABLE dashboard_hourly_stats_backup_20260423;
