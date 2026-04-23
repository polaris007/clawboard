-- ============================================
-- 诊断时间范围报告数据差异
-- ============================================

-- 1. 检查所有 issue 的 turn_id 分布
SELECT 
    COUNT(*) as total_issues,
    COUNT(turn_id) as issues_with_turn_id,
    COUNT(*) - COUNT(turn_id) as issues_without_turn_id,
    ROUND(COUNT(turn_id) * 100.0 / COUNT(*), 2) as fill_rate_percent
FROM dashboard_transcript_issue;

-- 2. 检查最新一次扫描的所有 issues
SELECT 
    scan_id,
    COUNT(*) as total_issues,
    COUNT(turn_id) as issues_with_turn_id,
    COUNT(*) - COUNT(turn_id) as issues_without_turn_id
FROM dashboard_transcript_issue
GROUP BY scan_id
ORDER BY scan_id DESC
LIMIT 5;

-- 3. 检查通过 turn_id 能查到的 issues vs 通过 scan_id 能查到的 issues
SELECT 
    'by_scan_id' as query_method,
    COUNT(*) as issue_count
FROM dashboard_transcript_issue
WHERE scan_id = (SELECT MAX(scan_id) FROM dashboard_scan)

UNION ALL

SELECT 
    'by_turn_ids' as query_method,
    COUNT(DISTINCT ti.id) as issue_count
FROM dashboard_transcript_issue ti
INNER JOIN dashboard_conversation_turn t ON ti.turn_id = t.id
WHERE t.scan_id = (SELECT MAX(scan_id) FROM dashboard_scan);

-- 4. 找出那些 turn_id 为 NULL 但属于最新扫描的 issues
SELECT 
    id,
    session_id,
    error_type,
    error_message,
    turn_id,
    occurred_at
FROM dashboard_transcript_issue
WHERE scan_id = (SELECT MAX(scan_id) FROM dashboard_scan)
  AND turn_id IS NULL
LIMIT 10;

-- 5. 验证时间范围查询是否能覆盖所有数据
SELECT 
    MIN(start_time) as earliest_turn,
    MAX(start_time) as latest_turn,
    COUNT(*) as total_turns
FROM dashboard_conversation_turn
WHERE scan_id = (SELECT MAX(scan_id) FROM dashboard_scan);
