-- 检查 dashboard_transcript_issue 表中 turn_id 的分布情况
SELECT 
    COUNT(*) as total_issues,
    COUNT(DISTINCT turn_id) as distinct_turn_ids,
    COUNT(CASE WHEN turn_id IS NULL THEN 1 END) as null_turn_ids,
    COUNT(CASE WHEN turn_id IS NOT NULL THEN 1 END) as non_null_turn_ids
FROM dashboard_transcript_issue;

-- 查看每个 turn_id 对应的 issue 数量
SELECT 
    turn_id,
    COUNT(*) as issue_count
FROM dashboard_transcript_issue
WHERE turn_id IS NOT NULL
GROUP BY turn_id
ORDER BY issue_count DESC
LIMIT 20;

-- 检查 dashboard_conversation_turn 表中的总轮次数
SELECT 
    COUNT(*) as total_turns,
    SUM(CASE WHEN system_turn = 0 OR system_turn IS NULL THEN 1 ELSE 0 END) as non_system_turns,
    SUM(CASE WHEN system_turn = 1 THEN 1 ELSE 0 END) as system_turns
FROM dashboard_conversation_turn;
