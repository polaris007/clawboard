-- ============================================
-- 验证 log_file_path 字段填充情况
-- ============================================

-- 1. 检查 log_file_path 填充率
SELECT 
    COUNT(*) as total_turns,
    COUNT(log_file_path) as turns_with_path,
    COUNT(*) - COUNT(log_file_path) as turns_without_path,
    SUM(CASE WHEN log_file_path = '' THEN 1 ELSE 0 END) as turns_with_empty_path,
    ROUND(COUNT(NULLIF(log_file_path, '')) * 100.0 / COUNT(*), 2) as fill_rate_percent
FROM dashboard_conversation_turn;

-- 2. 查看有文件路径的记录示例
SELECT 
    id,
    session_id,
    turn_index,
    log_file_path,
    LENGTH(log_file_path) as path_length
FROM dashboard_conversation_turn
WHERE log_file_path IS NOT NULL AND log_file_path != ''
LIMIT 10;

-- 3. 查看没有文件路径的记录
SELECT 
    id,
    session_id,
    turn_index,
    log_file_path
FROM dashboard_conversation_turn
WHERE log_file_path IS NULL OR log_file_path = ''
LIMIT 10;

-- 4. 按文件路径分组统计
SELECT 
    log_file_path,
    COUNT(*) as turn_count
FROM dashboard_conversation_turn
WHERE log_file_path IS NOT NULL AND log_file_path != ''
GROUP BY log_file_path
ORDER BY turn_count DESC
LIMIT 20;

-- 5. 总体摘要
SELECT 
    '=== LOG FILE PATH SUMMARY ===' as report,
    (SELECT COUNT(*) FROM dashboard_conversation_turn) as total_turns,
    (SELECT COUNT(NULLIF(log_file_path, '')) FROM dashboard_conversation_turn) as turns_with_valid_path,
    (SELECT COUNT(*) FROM dashboard_conversation_turn WHERE log_file_path IS NULL OR log_file_path = '') as turns_without_path;
