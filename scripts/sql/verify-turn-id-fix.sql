-- ============================================
-- 验证 turn_id 填充情况的SQL脚本
-- 执行此脚本检查修复后的数据质量
-- ============================================

-- 1. 检查 dashboard_message 表的 turn_id 填充情况
SELECT 
    'dashboard_message' as table_name,
    COUNT(*) as total_records,
    COUNT(turn_id) as records_with_turn_id,
    COUNT(*) - COUNT(turn_id) as records_without_turn_id,
    ROUND(COUNT(turn_id) * 100.0 / COUNT(*), 2) as fill_rate_percent
FROM dashboard_message;

-- 2. 检查 dashboard_transcript_issue 表的 turn_id 填充情况
SELECT 
    'dashboard_transcript_issue' as table_name,
    COUNT(*) as total_records,
    COUNT(turn_id) as records_with_turn_id,
    COUNT(*) - COUNT(turn_id) as records_without_turn_id,
    ROUND(COUNT(turn_id) * 100.0 / COUNT(*), 2) as fill_rate_percent
FROM dashboard_transcript_issue;

-- 3. 查看 message 的 turn_id 分布（按 session）
SELECT 
    session_id,
    COUNT(*) as message_count,
    COUNT(turn_id) as messages_with_turn_id,
    COUNT(*) - COUNT(turn_id) as messages_without_turn_id
FROM dashboard_message
GROUP BY session_id
ORDER BY messages_without_turn_id DESC
LIMIT 10;

-- 4. 查看 issue 的 turn_id 分布（按 session）
SELECT 
    session_id,
    COUNT(*) as issue_count,
    COUNT(turn_id) as issues_with_turn_id,
    COUNT(*) - COUNT(turn_id) as issues_without_turn_id
FROM dashboard_transcript_issue
GROUP BY session_id
ORDER BY issues_without_turn_id DESC
LIMIT 10;

-- 5. 检查有问题的 message（turn_id 为 null）
SELECT 
    m.id,
    m.session_id,
    m.message_id,
    m.role,
    m.turn_id,
    m.message_timestamp
FROM dashboard_message m
WHERE m.turn_id IS NULL
LIMIT 20;

-- 6. 检查有问题的 issue（turn_id 为 null）
SELECT 
    i.id,
    i.session_id,
    i.message_id,
    i.error_type,
    i.turn_id,
    i.line_number
FROM dashboard_transcript_issue i
WHERE i.turn_id IS NULL
LIMIT 20;

-- 7. 验证 message 和 turn 的关联是否正确
SELECT 
    m.session_id,
    m.message_id,
    m.turn_id,
    t.id as turn_db_id,
    t.turn_index,
    t.start_message_id,
    t.end_message_id,
    CASE 
        WHEN m.turn_id = t.id THEN '✓ CORRECT'
        ELSE '✗ MISMATCH'
    END as validation_result
FROM dashboard_message m
LEFT JOIN dashboard_conversation_turn t ON m.turn_id = t.id
WHERE m.turn_id IS NOT NULL
LIMIT 20;

-- 8. 验证 issue 和 turn 的关联是否正确
SELECT 
    i.session_id,
    i.message_id,
    i.turn_id,
    t.id as turn_db_id,
    t.turn_index,
    t.start_message_id,
    t.end_message_id,
    i.error_type,
    CASE 
        WHEN i.turn_id = t.id THEN '✓ CORRECT'
        ELSE '✗ MISMATCH'
    END as validation_result
FROM dashboard_transcript_issue i
LEFT JOIN dashboard_conversation_turn t ON i.turn_id = t.id
WHERE i.turn_id IS NOT NULL
LIMIT 20;

-- 9. 统计每个 turn 对应的 message 和 issue 数量
SELECT 
    t.id as turn_id,
    t.turn_index,
    t.session_id,
    COUNT(DISTINCT m.id) as message_count,
    COUNT(DISTINCT i.id) as issue_count
FROM dashboard_conversation_turn t
LEFT JOIN dashboard_message m ON t.id = m.turn_id
LEFT JOIN dashboard_transcript_issue i ON t.id = i.turn_id
WHERE t.session_id IN (
    SELECT DISTINCT session_id FROM dashboard_message WHERE turn_id IS NOT NULL LIMIT 5
)
GROUP BY t.id, t.turn_index, t.session_id
ORDER BY t.session_id, t.turn_index
LIMIT 30;

-- 10. 总体统计摘要
SELECT 
    '=== SUMMARY ===' as report,
    (SELECT COUNT(*) FROM dashboard_conversation_turn) as total_turns,
    (SELECT COUNT(*) FROM dashboard_message) as total_messages,
    (SELECT COUNT(turn_id) FROM dashboard_message) as messages_with_turn_id,
    (SELECT COUNT(*) FROM dashboard_transcript_issue) as total_issues,
    (SELECT COUNT(turn_id) FROM dashboard_transcript_issue) as issues_with_turn_id;
