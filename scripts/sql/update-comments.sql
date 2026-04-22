-- ============================================
-- Update table and column comments
-- MySQL 5.7 Compatible
-- ============================================

-- Update table comments
ALTER TABLE dashboard_scan_history COMMENT='扫描历史记录表';
ALTER TABLE dashboard_scan_progress COMMENT='文件扫描进度表';
ALTER TABLE dashboard_message COMMENT='消息明细表';
ALTER TABLE dashboard_conversation_turn COMMENT='对话轮次表';
ALTER TABLE dashboard_skill_invocation COMMENT='技能调用记录表';
ALTER TABLE dashboard_transcript_issue COMMENT='错误/问题记录表';
ALTER TABLE dashboard_hourly_stats COMMENT='小时统计数据表';

-- dashboard_employee
ALTER TABLE dashboard_employee MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- dashboard_scan_history - add comments to all columns
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

-- dashboard_scan_progress - add comments to all columns
ALTER TABLE dashboard_scan_progress MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN file_path VARCHAR(500) NOT NULL COMMENT '文件路径';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN last_offset BIGINT(20) NOT NULL DEFAULT 0 COMMENT '最后读取位置';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN file_size BIGINT(20) NOT NULL DEFAULT 0 COMMENT '文件大小';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN file_mtime BIGINT(20) NOT NULL DEFAULT 0 COMMENT '文件修改时间';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN session_id VARCHAR(36) COMMENT '当前Session UUID';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN last_message_id VARCHAR(36) COMMENT '最后处理的消息ID';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN last_message_ts DATETIME(3) COMMENT '最后消息时间戳';
ALTER TABLE dashboard_scan_progress MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- dashboard_session_summary - add comments to all columns
ALTER TABLE dashboard_session_summary MODIFY COLUMN session_id VARCHAR(36) NOT NULL COMMENT 'Session UUID';
ALTER TABLE dashboard_session_summary MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_session_summary MODIFY COLUMN agent_name VARCHAR(100) COMMENT 'Agent名称';
ALTER TABLE dashboard_session_summary MODIFY COLUMN total_messages INT NOT NULL DEFAULT 0 COMMENT '总消息数';
ALTER TABLE dashboard_session_summary MODIFY COLUMN total_turns INT NOT NULL DEFAULT 0 COMMENT '总对话轮次数';
ALTER TABLE dashboard_session_summary MODIFY COLUMN total_issues INT NOT NULL DEFAULT 0 COMMENT '总问题数';
ALTER TABLE dashboard_session_summary MODIFY COLUMN total_skills INT NOT NULL DEFAULT 0 COMMENT '总技能调用数';
ALTER TABLE dashboard_session_summary MODIFY COLUMN first_message_at DATETIME(3) COMMENT '第一条消息时间';
ALTER TABLE dashboard_session_summary MODIFY COLUMN last_message_at DATETIME(3) COMMENT '最后一条消息时间';
ALTER TABLE dashboard_session_summary MODIFY COLUMN last_scan_id BIGINT COMMENT '最后扫描ID';
ALTER TABLE dashboard_session_summary MODIFY COLUMN last_updated_at DATETIME(3) COMMENT '最后更新时间';
ALTER TABLE dashboard_session_summary MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE dashboard_session_summary MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- dashboard_message - add comments to all columns
ALTER TABLE dashboard_message MODIFY COLUMN session_id VARCHAR(36) NOT NULL COMMENT 'Session UUID';
ALTER TABLE dashboard_message MODIFY COLUMN scan_id BIGINT COMMENT '关联扫描ID';
ALTER TABLE dashboard_message MODIFY COLUMN message_id VARCHAR(36) NOT NULL COMMENT '消息ID';
ALTER TABLE dashboard_message MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_message MODIFY COLUMN role VARCHAR(20) NOT NULL COMMENT '角色: user/assistant/tool';
ALTER TABLE dashboard_message MODIFY COLUMN message_timestamp DATETIME(3) COMMENT '消息时间戳';
ALTER TABLE dashboard_message MODIFY COLUMN input_tokens INT COMMENT '输入token数';
ALTER TABLE dashboard_message MODIFY COLUMN output_tokens INT COMMENT '输出token数';
ALTER TABLE dashboard_message MODIFY COLUMN cache_read_tokens INT COMMENT '缓存读取token数';
ALTER TABLE dashboard_message MODIFY COLUMN cache_write_tokens INT COMMENT '缓存写入token数';
ALTER TABLE dashboard_message MODIFY COLUMN total_tokens INT COMMENT '总token数';
ALTER TABLE dashboard_message MODIFY COLUMN cost_total DECIMAL(14,8) COMMENT '费用(USD)';
ALTER TABLE dashboard_message MODIFY COLUMN provider VARCHAR(50) COMMENT '模型提供商';
ALTER TABLE dashboard_message MODIFY COLUMN model VARCHAR(100) COMMENT '模型名称';
ALTER TABLE dashboard_message MODIFY COLUMN stop_reason VARCHAR(50) COMMENT '停止原因';
ALTER TABLE dashboard_message MODIFY COLUMN duration_ms INT COMMENT '响应耗时(毫秒)';
ALTER TABLE dashboard_message MODIFY COLUMN is_error TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否报错';
ALTER TABLE dashboard_message MODIFY COLUMN error_message TEXT COMMENT '错误消息内容';
ALTER TABLE dashboard_message MODIFY COLUMN tool_name VARCHAR(100) COMMENT '工具名称';
ALTER TABLE dashboard_message MODIFY COLUMN tool_call_id VARCHAR(100) COMMENT '工具调用ID';
ALTER TABLE dashboard_message MODIFY COLUMN parent_id VARCHAR(36) COMMENT '父消息ID';
ALTER TABLE dashboard_message MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- dashboard_conversation_turn - add comments to all columns
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '对话轮次ID';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN scan_id BIGINT COMMENT '关联扫描ID';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN session_id VARCHAR(36) NOT NULL COMMENT 'Session UUID';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN turn_index INT COMMENT '轮次序号';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN start_message_id VARCHAR(36) NOT NULL COMMENT '起始消息ID';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN end_message_id VARCHAR(36) COMMENT '结束消息ID';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN start_time DATETIME(3) COMMENT '轮次开始时间';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN end_time DATETIME(3) COMMENT '轮次结束时间';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN user_input VARCHAR(200) COMMENT '用户输入摘要';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN total_input_tokens INT DEFAULT 0 COMMENT '总输入token数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN total_output_tokens INT DEFAULT 0 COMMENT '总输出token数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN total_tokens INT DEFAULT 0 COMMENT '总token数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN total_cost DECIMAL(14,8) DEFAULT 0 COMMENT '总费用(USD)';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN tool_calls_count INT DEFAULT 0 COMMENT '工具调用次数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN tool_calls_success INT DEFAULT 0 COMMENT '工具调用成功次数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN tool_calls_error INT DEFAULT 0 COMMENT '工具调用失败次数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN skill_calls_count INT DEFAULT 0 COMMENT '技能调用次数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN skill_calls_success INT DEFAULT 0 COMMENT '技能调用成功次数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN skill_calls_error INT DEFAULT 0 COMMENT '技能调用失败次数';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN is_complete TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否完成';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN has_error TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否有错误';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'incomplete' COMMENT '状态: incomplete/complete/error';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN total_duration_ms INT COMMENT '总耗时(毫秒)';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN tool_duration_ms INT COMMENT '工具执行耗时(毫秒)';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN model_duration_ms INT COMMENT '模型调用耗时(毫秒)';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN chain_summary TEXT COMMENT '调用链摘要';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN log_file_path VARCHAR(500) COMMENT '日志文件路径';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN quality_status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '质量状态';
ALTER TABLE dashboard_conversation_turn MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- dashboard_skill_invocation - add comments to all columns
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能调用ID';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN session_id VARCHAR(36) NOT NULL COMMENT 'Session UUID';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN scan_id BIGINT COMMENT '关联扫描ID';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN turn_id BIGINT COMMENT '关联轮次ID';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN skill_name VARCHAR(100) NOT NULL COMMENT '技能名称';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN skill_path VARCHAR(500) COMMENT '技能文件路径';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN invoked_at DATETIME(3) COMMENT '调用时间';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN read_message_id VARCHAR(36) COMMENT '触发读取的消息ID';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN result_message_id VARCHAR(36) COMMENT '结果消息ID';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN is_error TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否执行失败';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN trigger_type VARCHAR(20) DEFAULT 'model_read' COMMENT '触发类型';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN duration_ms INT COMMENT '执行耗时(毫秒)';
ALTER TABLE dashboard_skill_invocation MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- dashboard_transcript_issue - add comments to all columns
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '问题ID';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN session_id VARCHAR(36) NOT NULL COMMENT 'Session UUID';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN scan_id BIGINT COMMENT '关联扫描ID';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN message_id VARCHAR(36) NOT NULL DEFAULT '' COMMENT '关联消息ID';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN error_type VARCHAR(50) NOT NULL COMMENT '错误分类';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN severity VARCHAR(10) NOT NULL COMMENT '严重程度: HIGH/MEDIUM/LOW';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN description VARCHAR(500) COMMENT '错误描述';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN error_message TEXT COMMENT '原始错误信息';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN user_input TEXT COMMENT '用户输入内容';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN cause_analysis VARCHAR(500) COMMENT '原因分析';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN file_path VARCHAR(500) COMMENT '文件路径';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN error_line_content TEXT COMMENT '错误行内容';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN next_line_content TEXT COMMENT '下一行内容';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN event_type VARCHAR(50) COMMENT '事件类型: message/custom';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN run_id VARCHAR(50) COMMENT 'Run ID for custom events';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN provider VARCHAR(50) COMMENT '模型提供商';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN model VARCHAR(100) COMMENT '模型名称';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN line_number INT COMMENT 'JSONL文件行号';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN occurred_at DATETIME(3) COMMENT '错误发生时间';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN turn_id BIGINT COMMENT '关联轮次ID';
ALTER TABLE dashboard_transcript_issue MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- dashboard_hourly_stats - add comments to all columns
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN employee_id VARCHAR(50) NOT NULL COMMENT '工号';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN stat_hour DATETIME NOT NULL COMMENT '统计小时';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN total_tokens BIGINT(20) NOT NULL DEFAULT 0 COMMENT '总token数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN input_tokens BIGINT(20) NOT NULL DEFAULT 0 COMMENT '输入token数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN output_tokens BIGINT(20) NOT NULL DEFAULT 0 COMMENT '输出token数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN total_cost DECIMAL(14,8) NOT NULL DEFAULT 0 COMMENT '总费用(USD)';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN cache_read_tokens BIGINT(20) NOT NULL DEFAULT 0 COMMENT '缓存读取token数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN cache_write_tokens BIGINT(20) NOT NULL DEFAULT 0 COMMENT '缓存写入token数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN conversation_turns INT NOT NULL DEFAULT 0 COMMENT '对话轮次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN complete_turns INT NOT NULL DEFAULT 0 COMMENT '完成轮次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN error_turns INT NOT NULL DEFAULT 0 COMMENT '错误轮次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN skill_invocations INT NOT NULL DEFAULT 0 COMMENT '技能调用次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN skill_errors INT NOT NULL DEFAULT 0 COMMENT '技能错误次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN tool_calls INT NOT NULL DEFAULT 0 COMMENT '工具调用次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN tool_errors INT NOT NULL DEFAULT 0 COMMENT '工具错误次数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN error_count INT NOT NULL DEFAULT 0 COMMENT '错误总数';
ALTER TABLE dashboard_hourly_stats MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
