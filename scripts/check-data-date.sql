SELECT 
    MIN(stat_hour) as earliest_date,
    MAX(stat_hour) as latest_date,
    COUNT(*) as total_records
FROM dashboard_hourly_stats;
