-- ============================================
-- ClawBoard Complete Database Schema
-- MySQL 5.7 Compatible
-- Generated: 2026-04-21
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. dashboard_employee - Employee information
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_employee (
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    employee_name   VARCHAR(100)  COMMENT '姓名',
    team_name       VARCHAR(100)  COMMENT '部门/团队名称',
    is_active       TINYINT(4)    NOT NULL DEFAULT 1 COMMENT '是否在职',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (employee_id),
    INDEX idx_team (team_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-部门对应关系（可由外部系统同步)';

-- ============================================
-- 2. dashboard_scan_history - Scan execution history
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_scan_history (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '扫描记录ID',
    trigger_type     VARCHAR(20)   NOT NULL COMMENT '触发类型: manual/scheduled',
    status           VARCHAR(20)   NOT NULL DEFAULT 'running' COMMENT '状态: running/completed/failed',
    started_at       DATETIME(3)   NOT NULL COMMENT '开始时间',
    finished_at      DATETIME(3)   COMMENT '结束时间',
    duration_ms      BIGINT        COMMENT '耗时(毫秒)',
    users_scanned    INT           NOT NULL DEFAULT 0 COMMENT '扫描用户数',
    dirs_scanned     INT           NOT NULL DEFAULT 0 COMMENT '扫描目录数',
    files_total      INT           NOT NULL DEFAULT 0 COMMENT '文件总数',
    files_processed  INT           NOT NULL DEFAULT 0 COMMENT '已处理文件数',
    files_skipped    INT           NOT NULL DEFAULT 0 COMMENT '跳过文件数',
    files_error      INT           NOT NULL DEFAULT 0 COMMENT '错误文件数',
    new_messages     INT           NOT NULL DEFAULT 0 COMMENT '新增消息数',
    new_turns        INT           NOT NULL DEFAULT 0 COMMENT '新增对话轮次数',
    new_issues       INT           NOT NULL DEFAULT 0 COMMENT '新增问题数',
    new_skill_calls  INT           NOT NULL DEFAULT 0 COMMENT '新增技能调用数',
    error_message    TEXT          COMMENT '错误信息',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_status (status),
    INDEX idx_started (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扫描历史记录表';

-- ============================================
-- 3. dashboard_scan_progress - File scanning progress
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_scan_progress (
    employee_id     VARCHAR(50)   NOT NULL COMMENT '工号',
    file_path       VARCHAR(500)  NOT NULL COMMENT '文件路径',
    last_offset     BIGINT(20)   NOT NULL DEFAULT 0 COMMENT '最后读取位置',
    file_size       BIGINT(20)   NOT NULL DEFAULT 0 COMMENT '文件大小',
    file_mtime      BIGINT(20)   NOT NULL DEFAULT 0 COMMENT '文件修改时间',
    session_id      VARCHAR(36)   COMMENT '当前Session UUID',
    last_message_id VARCHAR(36)   COMMENT '最后处理的消息ID',
    last_message_ts DATETIME(3)   COMMENT '最后消息时间戳',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (employee_id, file_path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件扫描进度表';

-- ============================================
-- 4. dashboard_session_summary - Session level summary
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_session_summary (
    session_id       VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    agent_name       VARCHAR(100)  COMMENT 'Agent名称',
    total_messages   INT           NOT NULL DEFAULT 0 COMMENT '总消息数',
    total_turns      INT           NOT NULL DEFAULT 0 COMMENT '总对话轮次数',
    total_issues     INT           NOT NULL DEFAULT 0 COMMENT '总问题数',
    total_skills     INT           NOT NULL DEFAULT 0 COMMENT '总技能调用数',
    first_message_at DATETIME(3)   COMMENT '第一条消息时间',
    last_message_at  DATETIME(3)   COMMENT '最后一条消息时间',
    last_scan_id     BIGINT        COMMENT '最后扫描ID',
    last_updated_at  DATETIME(3)   COMMENT '最后更新时间',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (session_id),
    INDEX idx_employee (employee_id),
    INDEX idx_last_updated (last_updated_at),
    INDEX idx_agent (agent_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Session汇总表';

-- ============================================
-- 5. dashboard_message - Message details
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_message (
    session_id       VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    scan_id          BIGINT        COMMENT '关联扫描ID',
    message_id       VARCHAR(36)   NOT NULL COMMENT '消息ID',
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    role             VARCHAR(20)   NOT NULL COMMENT '角色: user/assistant/tool',
    message_timestamp DATETIME(3)  COMMENT '消息时间戳',
    input_tokens     INT           COMMENT '输入token数',
    output_tokens    INT           COMMENT '输出token数',
    cache_read_tokens INT          COMMENT '缓存读取token数',
    cache_write_tokens INT         COMMENT '缓存写入token数',
    total_tokens     INT           COMMENT '总token数',
    cost_total       DECIMAL(14,8) COMMENT '费用(USD)',
    provider         VARCHAR(50)   COMMENT '模型提供商',
    model            VARCHAR(100)  COMMENT '模型名称',
    stop_reason      VARCHAR(50)   COMMENT '停止原因',
    duration_ms      INT           COMMENT '响应耗时(毫秒)',
    is_error         TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '是否报错',
    error_message    TEXT          COMMENT '错误消息内容',
    tool_name        VARCHAR(100)  COMMENT '工具名称',
    tool_call_id     VARCHAR(100)  COMMENT '工具调用ID',
    parent_id        VARCHAR(36)   COMMENT '父消息ID',
    is_system        TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '是否系统消息',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (session_id, message_id),
    INDEX idx_scan_id (scan_id),
    INDEX idx_employee_time (employee_id, message_timestamp),
    INDEX idx_time (message_timestamp),
    INDEX idx_role_time (role, message_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息明细表';

-- ============================================
-- 6. dashboard_conversation_turn - Conversation turns
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_conversation_turn (
    id                    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '对话轮次ID',
    scan_id               BIGINT        COMMENT '关联扫描ID',
    session_id            VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    employee_id           VARCHAR(50)   NOT NULL COMMENT '工号',
    turn_index            INT           COMMENT '轮次序号',
    start_message_id      VARCHAR(36)   NOT NULL COMMENT '起始消息ID',
    end_message_id        VARCHAR(36)   COMMENT '结束消息ID',
    start_time            DATETIME(3)   COMMENT '轮次开始时间',
    end_time              DATETIME(3)   COMMENT '轮次结束时间',
    user_input            VARCHAR(200)  COMMENT '用户输入摘要',
    total_input_tokens    INT           DEFAULT 0 COMMENT '总输入token数',
    total_output_tokens   INT           DEFAULT 0 COMMENT '总输出token数',
    total_tokens          INT           DEFAULT 0 COMMENT '总token数',
    total_cost            DECIMAL(14,8) DEFAULT 0 COMMENT '总费用(USD)',
    tool_calls_count      INT           DEFAULT 0 COMMENT '工具调用次数',
    tool_calls_success    INT           DEFAULT 0 COMMENT '工具调用成功次数',
    tool_calls_error      INT           DEFAULT 0 COMMENT '工具调用失败次数',
    skill_calls_count     INT           DEFAULT 0 COMMENT '技能调用次数',
    skill_calls_success   INT           DEFAULT 0 COMMENT '技能调用成功次数',
    skill_calls_error     INT           DEFAULT 0 COMMENT '技能调用失败次数',
    is_complete           TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '是否完成',
    has_error             TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '是否有错误',
    status                VARCHAR(20)   NOT NULL DEFAULT 'incomplete' COMMENT '状态: incomplete/complete/error',
    total_duration_ms     INT           COMMENT '总耗时(毫秒)',
    tool_duration_ms      INT           COMMENT '工具执行耗时(毫秒)',
    model_duration_ms      INT           COMMENT '模型调用耗时(毫秒)',
    chain_summary         TEXT          COMMENT '调用链摘要',
    log_file_path         VARCHAR(500)  COMMENT '日志文件路径',
    quality_status        TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '质量状态',
    system_turn           TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '是否系统轮次',
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_start_msg (session_id, start_message_id),
    INDEX idx_scan_id (scan_id),
    INDEX idx_employee_time (employee_id, start_time),
    INDEX idx_time (start_time),
    INDEX idx_status (status, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话轮次表';

-- ============================================
-- 7. dashboard_skill_invocation - Skill/tool invocations
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_skill_invocation (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '技能调用ID',
    session_id          VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    scan_id             BIGINT        COMMENT '关联扫描ID',
    employee_id         VARCHAR(50)   NOT NULL COMMENT '工号',
    turn_id             BIGINT        COMMENT '关联轮次ID',
    skill_name          VARCHAR(100)  NOT NULL COMMENT '技能名称',
    skill_path          VARCHAR(500)  COMMENT '技能文件路径',
    invoked_at          DATETIME(3)   COMMENT '调用时间',
    read_message_id     VARCHAR(36)   COMMENT '触发读取的消息ID',
    result_message_id   VARCHAR(36)   COMMENT '结果消息ID',
    is_error            TINYINT(4)    NOT NULL DEFAULT 0 COMMENT '是否执行失败',
    trigger_type        VARCHAR(20)   DEFAULT 'model_read' COMMENT '触发类型',
    duration_ms         INT           COMMENT '执行耗时(毫秒)',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_read_msg (session_id, read_message_id),
    INDEX idx_scan_id (scan_id),
    INDEX idx_employee_time (employee_id, invoked_at),
    INDEX idx_turn (turn_id),
    INDEX idx_skill_time (skill_name, invoked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能调用记录表';

-- ============================================
-- 8. dashboard_transcript_issue - Transcript issues/errors
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_transcript_issue (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    session_id          VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    scan_id             BIGINT        COMMENT '关联扫描ID',
    message_id          VARCHAR(36)   NOT NULL DEFAULT '' COMMENT '关联消息ID',
    employee_id         VARCHAR(50)   NOT NULL COMMENT '工号',
    error_type          VARCHAR(50)   NOT NULL COMMENT '错误分类',
    severity            VARCHAR(10)   NOT NULL COMMENT '严重程度: HIGH/MEDIUM/LOW',
    description         VARCHAR(500)  COMMENT '错误描述',
    error_message       TEXT          COMMENT '原始错误信息',
    user_input          TEXT          COMMENT '用户输入内容',
    cause_analysis      VARCHAR(500)  COMMENT '原因分析',
    file_path           VARCHAR(500)  COMMENT '文件路径',
    error_line_content  TEXT          COMMENT '错误行内容',
    next_line_content   TEXT          COMMENT '下一行内容',
    event_type          VARCHAR(50)   COMMENT '事件类型: message/custom',
    run_id              VARCHAR(50)   COMMENT 'Run ID for custom events',
    provider            VARCHAR(50)   COMMENT '模型提供商',
    model               VARCHAR(100)  COMMENT '模型名称',
    line_number         INT           COMMENT 'JSONL文件行号',
    occurred_at         DATETIME(3)   COMMENT '错误发生时间',
    turn_id             BIGINT        COMMENT '关联轮次ID',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_msg_type (session_id, message_id, error_type),
    INDEX idx_scan_id (scan_id),
    INDEX idx_employee_time (employee_id, occurred_at),
    INDEX idx_type_time (error_type, occurred_at),
    INDEX idx_severity (severity, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错误/问题记录表';

-- ============================================
-- 9. dashboard_hourly_stats - Hourly aggregated statistics
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_hourly_stats (
    employee_id         VARCHAR(50)   NOT NULL COMMENT '工号',
    stat_hour           DATETIME      NOT NULL COMMENT '统计小时',
    total_tokens        BIGINT(20)    NOT NULL DEFAULT 0 COMMENT '总token数',
    input_tokens        BIGINT(20)    NOT NULL DEFAULT 0 COMMENT '输入token数',
    output_tokens       BIGINT(20)    NOT NULL DEFAULT 0 COMMENT '输出token数',
    total_cost          DECIMAL(14,8) NOT NULL DEFAULT 0 COMMENT '总费用(USD)',
    cache_read_tokens   BIGINT(20)    NOT NULL DEFAULT 0 COMMENT '缓存读取token数',
    cache_write_tokens  BIGINT(20)    NOT NULL DEFAULT 0 COMMENT '缓存写入token数',
    conversation_turns  INT           NOT NULL DEFAULT 0 COMMENT '对话轮次数',
    complete_turns     INT           NOT NULL DEFAULT 0 COMMENT '完成轮次数',
    error_turns         INT           NOT NULL DEFAULT 0 COMMENT '错误轮次数',
    skill_invocations   INT           NOT NULL DEFAULT 0 COMMENT '技能调用次数',
    skill_errors        INT           NOT NULL DEFAULT 0 COMMENT '技能错误次数',
    tool_calls          INT           NOT NULL DEFAULT 0 COMMENT '工具调用次数',
    tool_errors         INT           NOT NULL DEFAULT 0 COMMENT '工具错误次数',
    error_count         INT           NOT NULL DEFAULT 0 COMMENT '错误总数',
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (employee_id, stat_hour),
    INDEX idx_hour (stat_hour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小时统计数据表';

SET FOREIGN_KEY_CHECKS = 1;
