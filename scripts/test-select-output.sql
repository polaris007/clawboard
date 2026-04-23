-- ============================================
-- 测试 execute-sql-file 的 SELECT 查询结果打印
-- ============================================

-- 1. 先查看表结构
DESCRIBE dashboard_employee;

-- 2. 插入测试数据
INSERT INTO dashboard_employee (employee_id, employee_name, org_code, status) VALUES
('18101142', '张三', '18100000', 1),
('18101143', '李四', '18100000', 1),
('18101144', '王五', '18200000', 1);

-- 3. 查询刚插入的数据
SELECT 
    employee_id,
    employee_name,
    org_code,
    status
FROM dashboard_employee
ORDER BY employee_id;

-- 4. 统计每个机构的员工数
SELECT 
    org_code,
    COUNT(*) as employee_count
FROM dashboard_employee
GROUP BY org_code;
