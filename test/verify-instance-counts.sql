-- 验证 /dashboard/global-stats 新增字段的统计逻辑

-- 1. 验证 instanceTotalCount（排除 deleted）
SELECT 
    COUNT(*) as instanceTotalCount,
    '应该等于接口返回的 instanceTotalCount' as description
FROM v_instance_detail
WHERE status != 'deleted';

-- 2. 验证 instanceAbnormalCount（排除 deleted 和 running）
SELECT 
    COUNT(*) as instanceAbnormalCount,
    '应该等于接口返回的 instanceAbnormalCount' as description
FROM v_instance_detail
WHERE status NOT IN ('deleted', 'running');

-- 3. 查看各状态的实例分布
SELECT 
    status,
    COUNT(*) as count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM v_instance_detail WHERE status != 'deleted'), 2), '%') as percentage
FROM v_instance_detail
WHERE status != 'deleted'
GROUP BY status
ORDER BY count DESC;

-- 4. 对比 registeredUsers 和 instanceTotalCount 的差异
SELECT 
    (SELECT COUNT(DISTINCT uid) FROM v_instance_detail WHERE status != 'deleted') as registeredUsers,
    (SELECT COUNT(*) FROM v_instance_detail WHERE status != 'deleted') as instanceTotalCount,
    (SELECT COUNT(*) FROM v_instance_detail WHERE status != 'deleted') - 
    (SELECT COUNT(DISTINCT uid) FROM v_instance_detail WHERE status != 'deleted') as difference,
    'registeredUsers 统计的是不重复 uid，instanceTotalCount 统计的是所有记录' as note;
