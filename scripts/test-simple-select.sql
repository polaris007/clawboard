-- ============================================
-- 简单测试 SELECT 查询结果打印功能
-- ============================================

-- 1. 查看有哪些表
SHOW TABLES;

-- 2. 查看 dashboard_scan_history 表结构
DESCRIBE dashboard_scan_history;

-- 3. 查询扫描历史（即使为空也会显示表头）
SELECT 
    id,
    scan_id,
    status,
    started_at,
    completed_at
FROM dashboard_scan_history
ORDER BY id DESC
LIMIT 5;
