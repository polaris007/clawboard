-- dashboard_scan_history 字段注释
ALTER TABLE dashboard_scan_history MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '扫描记录ID';
ALTER TABLE dashboard_scan_history MODIFY COLUMN trigger_type VARCHAR(20) NOT NULL COMMENT '触发类型: manual/scheduled';
ALTER TABLE dashboard_scan_history MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'running' COMMENT '状态: running/completed/failed';
ALTER TABLE dashboard_scan_history MODIFY COLUMN started_at DATETIME(3) NOT NULL COMMENT '开始时间';
ALTER TABLE dashboard_scan_history MODIFY COLUMN finished_at DATETIME(3) COMMENT '结束时间';
ALTER TABLE dashboard_scan_history MODIFY COLUMN duration_ms BIGINT COMMENT '耗时(毫秒)';
ALTER TABLE dashboard_scan_history MODIFY COLUMN users_scanned INT NOT NULL DEFAULT 0 COMMENT '扫描用户数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN dirs_scanned INT NOT NULL DEFAULT 0 COMMENT '扫描目录数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN files_total INT NOT NULL DEFAULT 0 COMMENT '文件总数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN files_processed INT NOT NULL DEFAULT 0 COMMENT '已处理文件数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN files_skipped INT NOT NULL DEFAULT 0 COMMENT '跳过文件数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN files_error INT NOT NULL DEFAULT 0 COMMENT '错误文件数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN new_messages INT NOT NULL DEFAULT 0 COMMENT '新增消息数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN new_turns INT NOT NULL DEFAULT 0 COMMENT '新增对话轮次数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN new_issues INT NOT NULL DEFAULT 0 COMMENT '新增问题数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN new_skill_calls INT NOT NULL DEFAULT 0 COMMENT '新增技能调用数';
ALTER TABLE dashboard_scan_history MODIFY COLUMN error_message TEXT COMMENT '错误信息';
ALTER TABLE dashboard_scan_history MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
