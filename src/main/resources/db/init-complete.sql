-- ============================================
-- ClawBoard Complete Database Schema
-- MySQL 5.7 Compatible
-- Generated: 2026-04-19
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. dashboard_employee - Employee information
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_employee (
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    name             VARCHAR(100)  NOT NULL COMMENT '姓名',
    department       VARCHAR(100)  COMMENT '部门',
    email            VARCHAR(100)  COMMENT '邮箱',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (employee_id),
    INDEX idx_name (name),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

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
    INDEX idx_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扫描历史记录表';

-- ============================================
-- 3. dashboard_scan_progress - Real-time scan progress
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_scan_progress (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '进度记录ID',
    scan_id          BIGINT        NOT NULL COMMENT '关联扫描ID',
    current_user     VARCHAR(50)   COMMENT '当前处理用户',
    processed_files  INT           NOT NULL DEFAULT 0 COMMENT '已处理文件数',
    total_files      INT           NOT NULL DEFAULT 0 COMMENT '总文件数',
    current_messages INT           NOT NULL DEFAULT 0 COMMENT '当前消息数',
    current_turns    INT           NOT NULL DEFAULT 0 COMMENT '当前对话轮次数',
    current_issues   INT           NOT NULL DEFAULT 0 COMMENT '当前问题数',
    updated_at       DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_scan_id (scan_id),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扫描进度表';

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
    message_id       VARCHAR(36)   NOT NULL COMMENT '消息 ID (通常为8位hex，fallback时可能为完整UUID)',
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    role             VARCHAR(20)   NOT NULL COMMENT '角色: user/assistant/toolResult',
    message_timestamp DATETIME(3)  COMMENT '消息时间戳',
    input_tokens     INT           COMMENT '输入 token (assistant)',
    output_tokens    INT           COMMENT '输出 token',
    cache_read_tokens INT          COMMENT '缓存读取 token',
    cache_write_tokens INT         COMMENT '缓存写入 token',
    total_tokens     INT           COMMENT '总 token',
    cost_total       DECIMAL(14,8) COMMENT '费用(USD)',
    provider         VARCHAR(50)   COMMENT '模型提供商',
    model            VARCHAR(100)  COMMENT '模型名称',
    stop_reason      VARCHAR(50)   COMMENT '停止原因',
    duration_ms      INT           COMMENT '响应耗时(ms)',
    is_error         TINYINT       NOT NULL DEFAULT 0 COMMENT '是否报错',
    error_message    TEXT          COMMENT '错误消息内容',
    tool_name        VARCHAR(100)  COMMENT '工具名(toolCall/toolResult)',
    tool_call_id     VARCHAR(100)  COMMENT '工具调用ID',
    parent_id        VARCHAR(36)   COMMENT '父消息ID',
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
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '对话轮次ID',
    session_id          VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    scan_id             BIGINT        COMMENT '关联扫描ID',
    turn_number         INT           NOT NULL COMMENT '轮次序号',
    employee_id         VARCHAR(50)   NOT NULL COMMENT '工号',
    user_message_id     VARCHAR(36)   COMMENT '用户消息ID',
    assistant_message_id VARCHAR(36)  COMMENT '助手消息ID',
    turn_timestamp      DATETIME(3)   COMMENT '轮次时间戳',
    user_input          TEXT          COMMENT '用户输入',
    assistant_response  TEXT          COMMENT '助手回复',
    has_error           TINYINT       NOT NULL DEFAULT 0 COMMENT '是否有错误',
    error_types         VARCHAR(200)  COMMENT '错误类型列表(逗号分隔)',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_scan_id (scan_id),
    INDEX idx_session (session_id),
    INDEX idx_employee_time (employee_id, turn_timestamp),
    UNIQUE INDEX uk_session_turn (session_id, turn_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话轮次表';

-- ============================================
-- 7. dashboard_skill_invocation - Skill/tool invocations
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_skill_invocation (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '技能调用ID',
    session_id      VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    scan_id         BIGINT        COMMENT '关联扫描ID',
    message_id      VARCHAR(36)   NOT NULL COMMENT '关联消息ID',
    employee_id     VARCHAR(50)   NOT NULL COMMENT '工号',
    skill_name      VARCHAR(100)  NOT NULL COMMENT '技能名称',
    skill_type      VARCHAR(50)   COMMENT '技能类型: builtin/custom',
    invocation_timestamp DATETIME(3) COMMENT '调用时间戳',
    arguments       TEXT          COMMENT '调用参数(JSON)',
    result          TEXT          COMMENT '执行结果',
    is_error        TINYINT       NOT NULL DEFAULT 0 COMMENT '是否执行失败',
    error_message   TEXT          COMMENT '错误信息',
    duration_ms     INT           COMMENT '执行耗时(ms)',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_scan_id (scan_id),
    INDEX idx_session (session_id),
    INDEX idx_employee_time (employee_id, invocation_timestamp),
    INDEX idx_skill_name (skill_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能调用记录表';

-- ============================================
-- 8. dashboard_transcript_issue - Transcript issues/errors
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_transcript_issue (
    id                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    session_id        VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    scan_id           BIGINT        COMMENT '关联扫描ID',
    message_id        VARCHAR(36)   NOT NULL DEFAULT '' COMMENT '关联消息 ID (空字符串表示非消息事件)',
    employee_id       VARCHAR(50)   NOT NULL COMMENT '工号',
    error_type        VARCHAR(50)   NOT NULL COMMENT '错误分类',
    severity          VARCHAR(10)   NOT NULL COMMENT '严重程度: HIGH/MEDIUM/LOW',
    description       VARCHAR(500)  COMMENT '错误描述',
    error_message     TEXT          COMMENT '原始错误信息(截断500字符)',
    user_input        TEXT          COMMENT '用户输入内容',
    cause_analysis    VARCHAR(500)  COMMENT '原因分析',
    file_path         VARCHAR(500)  COMMENT '文件路径',
    error_line_content TEXT         COMMENT '错误行内容',
    next_line_content TEXT          COMMENT '下一行内容',
    event_type        VARCHAR(50)   COMMENT '事件类型: message/custom',
    provider          VARCHAR(50)   COMMENT '模型提供商',
    model             VARCHAR(100)  COMMENT '模型名称',
    line_number       INT           COMMENT 'JSONL 文件行号',
    occurred_at       DATETIME(3)   COMMENT '错误发生时间',
    turn_id           BIGINT        COMMENT '关联轮次ID',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_scan_id (scan_id),
    UNIQUE INDEX uk_session_msg_type (session_id, message_id, error_type),
    INDEX idx_employee_time (employee_id, occurred_at),
    INDEX idx_type_time (error_type, occurred_at),
    INDEX idx_severity (severity, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错误/问题记录表';

-- ============================================
-- 9. dashboard_hourly_stats - Hourly aggregated statistics
-- ============================================
CREATE TABLE IF NOT EXISTS dashboard_hourly_stats (
    employee_id        VARCHAR(50)   NOT NULL COMMENT '工号',
    stat_hour          DATETIME      NOT NULL COMMENT '统计小时',
    total_messages     INT           NOT NULL DEFAULT 0 COMMENT '总消息数',
    total_turns        INT           NOT NULL DEFAULT 0 COMMENT '总对话轮次数',
    total_issues       INT           NOT NULL DEFAULT 0 COMMENT '总问题数',
    total_skills       INT           NOT NULL DEFAULT 0 COMMENT '总技能调用数',
    avg_response_time  DECIMAL(10,2) COMMENT '平均响应时间(ms)',
    total_tokens       BIGINT        NOT NULL DEFAULT 0 COMMENT '总 token 数',
    total_cost         DECIMAL(14,8) COMMENT '总费用(USD)',
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (employee_id, stat_hour),
    INDEX idx_stat_hour (stat_hour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小时统计数据表';

SET FOREIGN_KEY_CHECKS = 1;
