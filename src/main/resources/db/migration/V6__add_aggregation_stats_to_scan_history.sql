-- 添加聚合统计字段到 dashboard_scan_history 表
ALTER TABLE dashboard_scan_history 
ADD COLUMN agg_hours_from_files INT NOT NULL DEFAULT 0 COMMENT '从文件提取的小时数',
ADD COLUMN agg_hours_from_window INT NOT NULL DEFAULT 0 COMMENT '从时间窗口获取的小时数',
ADD COLUMN agg_hours_total INT NOT NULL DEFAULT 0 COMMENT '聚合的总小时数（去重后）',
ADD COLUMN agg_duration_ms BIGINT NOT NULL DEFAULT 0 COMMENT '聚合耗时(ms)';
