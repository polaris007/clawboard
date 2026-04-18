-- V1__init_schema.sql
-- Create all 8 dashboard tables for ClawBoard

-- 3.0 dashboard_employee - User-department mapping
CREATE TABLE IF NOT EXISTS dashboard_employee (
    employee_id   VARCHAR(50)   NOT NULL COMMENT '工号',
    employee_name VARCHAR(100)  COMMENT '姓名',
    team_name     VARCHAR(100)  COMMENT '部门/团队名称',
    is_active     TINYINT       NOT NULL DEFAULT 1 COMMENT '是否在职',
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id),
    INDEX idx_team (team_name)
) COMMENT '用户-部门对应关系（可由外部系统同步）';

-- 3.1 dashboard_scan_progress - Scan progress tracking
CREATE TABLE IF NOT EXISTS dashboard_scan_progress (
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    file_path        VARCHAR(500)  NOT NULL COMMENT 'JSONL 文件相对路径',
    last_offset      BIGINT        NOT NULL DEFAULT 0 COMMENT '上次解析到的字节偏移量',
    file_size        BIGINT        NOT NULL DEFAULT 0 COMMENT '上次文件大小',
    file_mtime       BIGINT        NOT NULL DEFAULT 0 COMMENT '上次文件修改时间戳(ms)',
    session_id       VARCHAR(36)   COMMENT '该文件对应的 session ID',
    last_message_id  VARCHAR(36)   COMMENT '上次处理的最后一条消息 ID (通常8位hex)',
    last_message_ts  DATETIME(3)   COMMENT '上次处理的最后一条消息时间戳',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id, file_path)
) COMMENT '扫描进度跟踪';

-- 3.2 dashboard_message - Message details
CREATE TABLE IF NOT EXISTS dashboard_message (
    session_id       VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    message_id       VARCHAR(36)   NOT NULL COMMENT '消息 ID (通常为8位hex，fallback时可能为完整UUID)',
    employee_id      VARCHAR(50)   NOT NULL COMMENT '工号',
    role             VARCHAR(20)   NOT NULL COMMENT 'user/assistant/toolResult',
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
    tool_name        VARCHAR(100)  COMMENT '工具名(toolCall/toolResult)',
    tool_call_id     VARCHAR(100)  COMMENT '工具调用ID',
    parent_id        VARCHAR(36)   COMMENT '父消息ID',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (session_id, message_id),
    INDEX idx_employee_time (employee_id, message_timestamp),
    INDEX idx_time (message_timestamp),
    INDEX idx_role_time (role, message_timestamp)
) COMMENT '消息明细';

-- 3.3 dashboard_conversation_turn - Conversation turns
CREATE TABLE IF NOT EXISTS dashboard_conversation_turn (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    session_id          VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    employee_id         VARCHAR(50)   NOT NULL COMMENT '工号',
    turn_index          INT           COMMENT '该 session 中的第几轮',
    start_message_id    VARCHAR(36)   NOT NULL COMMENT '起始 user 消息 ID',
    end_message_id      VARCHAR(36)   COMMENT '终止 assistant 消息 ID',
    start_time          DATETIME(3)   COMMENT '轮次开始时间',
    end_time            DATETIME(3)   COMMENT '轮次结束时间',
    user_input          VARCHAR(200)  COMMENT '用户输入(截断200字符)',
    total_input_tokens  INT           DEFAULT 0 COMMENT '该轮总输入 token',
    total_output_tokens INT           DEFAULT 0 COMMENT '该轮总输出 token',
    total_tokens        INT           DEFAULT 0 COMMENT '该轮总 token',
    total_cost          DECIMAL(14,8) DEFAULT 0 COMMENT '该轮费用',
    tool_calls_count    INT           DEFAULT 0 COMMENT '工具调用次数',
    tool_calls_success  INT           DEFAULT 0 COMMENT '工具调用成功次数',
    tool_calls_error    INT           DEFAULT 0 COMMENT '工具调用失败次数',
    skill_calls_count   INT           DEFAULT 0 COMMENT 'Skill 调用次数',
    skill_calls_success INT           DEFAULT 0 COMMENT 'Skill 调用成功次数',
    skill_calls_error   INT           DEFAULT 0 COMMENT 'Skill 调用失败次数',
    is_complete         TINYINT       NOT NULL DEFAULT 0 COMMENT '轮次是否完整',
    has_error           TINYINT       NOT NULL DEFAULT 0 COMMENT '是否有报错',
    status              VARCHAR(20)   NOT NULL DEFAULT 'incomplete' COMMENT 'complete/incomplete/error',
    total_duration_ms   INT           COMMENT '整轮总耗时(ms)',
    tool_duration_ms    INT           COMMENT '工具调用总耗时(ms)',
    model_duration_ms   INT           COMMENT '模型推理总耗时(ms)',
    chain_summary       TEXT          COMMENT '调用链路 JSON',
    log_file_path       VARCHAR(500)  COMMENT '来源 JSONL 文件路径',
    quality_status      TINYINT       NOT NULL DEFAULT 0 COMMENT '质量评价: 0未评价/1正确/2错误/3待优化',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_session_start_msg (session_id, start_message_id),
    INDEX idx_employee_time (employee_id, start_time),
    INDEX idx_time (start_time),
    INDEX idx_status (status, start_time)
) COMMENT '对话轮次';

-- 3.4 dashboard_skill_invocation - Skill invocations
CREATE TABLE IF NOT EXISTS dashboard_skill_invocation (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    session_id        VARCHAR(36)   NOT NULL,
    employee_id       VARCHAR(50)   NOT NULL,
    turn_id           BIGINT        COMMENT '关联 dashboard_conversation_turn.id',
    skill_name        VARCHAR(100)  NOT NULL COMMENT 'Skill 名称',
    skill_path        VARCHAR(500)  COMMENT 'SKILL.md 路径',
    invoked_at        DATETIME(3)   COMMENT '调用时间',
    read_message_id   VARCHAR(36)   COMMENT '读取 SKILL.md 的 toolCall 消息 ID',
    result_message_id VARCHAR(36)   COMMENT '对应 toolResult 消息 ID',
    is_error          TINYINT       NOT NULL DEFAULT 0 COMMENT '读取是否出错',
    trigger_type      VARCHAR(20)   DEFAULT 'model_read' COMMENT 'model_read/slash_command',
    duration_ms       INT           COMMENT '调用耗时(ms)',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_session_read_msg (session_id, read_message_id),
    INDEX idx_skill_time (skill_name, invoked_at),
    INDEX idx_employee_time (employee_id, invoked_at),
    INDEX idx_turn (turn_id)
) COMMENT 'Skill 调用记录';

-- 3.5 dashboard_transcript_issue - Error/issue records
CREATE TABLE IF NOT EXISTS dashboard_transcript_issue (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    session_id        VARCHAR(36)   NOT NULL,
    message_id        VARCHAR(36)   NOT NULL DEFAULT '' COMMENT '关联消息 ID (空字符串表示非消息事件)',
    employee_id       VARCHAR(50)   NOT NULL,
    error_type        VARCHAR(50)   NOT NULL COMMENT '错误分类',
    severity          VARCHAR(10)   NOT NULL COMMENT 'HIGH/MEDIUM',
    description       VARCHAR(500)  COMMENT '错误描述',
    error_message     TEXT          COMMENT '原始错误信息(截断500字符)',
    event_type        VARCHAR(50)   COMMENT 'message/custom event type',
    provider          VARCHAR(50)   COMMENT '模型提供商',
    model             VARCHAR(100)  COMMENT '模型名称',
    line_number       INT           COMMENT 'JSONL 文件行号',
    occurred_at       DATETIME(3)   COMMENT '错误发生时间',
    turn_id           BIGINT        COMMENT '关联轮次',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_session_msg_type (session_id, message_id, error_type),
    INDEX idx_employee_time (employee_id, occurred_at),
    INDEX idx_type_time (error_type, occurred_at),
    INDEX idx_severity (severity, occurred_at)
) COMMENT '错误/问题记录';

-- 3.6 dashboard_hourly_stats - Hourly aggregated statistics
CREATE TABLE IF NOT EXISTS dashboard_hourly_stats (
    employee_id        VARCHAR(50)   NOT NULL,
    stat_hour          DATETIME      NOT NULL COMMENT '精确到小时',
    total_tokens       BIGINT        NOT NULL DEFAULT 0,
    input_tokens       BIGINT        NOT NULL DEFAULT 0,
    output_tokens      BIGINT        NOT NULL DEFAULT 0,
    total_cost         DECIMAL(14,8) NOT NULL DEFAULT 0,
    cache_read_tokens  BIGINT        NOT NULL DEFAULT 0,
    cache_write_tokens BIGINT        NOT NULL DEFAULT 0,
    conversation_turns INT           NOT NULL DEFAULT 0 COMMENT '对话轮次数',
    complete_turns     INT           NOT NULL DEFAULT 0 COMMENT '成功轮次数',
    error_turns        INT           NOT NULL DEFAULT 0 COMMENT '错误轮次数',
    skill_invocations  INT           NOT NULL DEFAULT 0 COMMENT 'Skill 调用次数',
    skill_errors       INT           NOT NULL DEFAULT 0 COMMENT 'Skill 调用失败次数',
    tool_calls         INT           NOT NULL DEFAULT 0 COMMENT '工具调用次数',
    tool_errors        INT           NOT NULL DEFAULT 0 COMMENT '工具调用失败次数',
    error_count        INT           NOT NULL DEFAULT 0 COMMENT '错误数',
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id, stat_hour),
    INDEX idx_hour (stat_hour)
) COMMENT '小时粒度聚合统计';

-- 3.7 dashboard_scan_history - Scan execution history
CREATE TABLE IF NOT EXISTS dashboard_scan_history (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    trigger_type        VARCHAR(20)   NOT NULL COMMENT '触发方式: scheduled/manual',
    status              VARCHAR(20)   NOT NULL DEFAULT 'running' COMMENT '扫描状态: running/completed/failed',
    started_at          DATETIME(3)   NOT NULL COMMENT '扫描开始时间',
    finished_at         DATETIME(3)   COMMENT '扫描结束时间',
    duration_ms         BIGINT        COMMENT '扫描总耗时(ms)',
    users_scanned       INT           NOT NULL DEFAULT 0 COMMENT '扫描的用户数',
    dirs_scanned        INT           NOT NULL DEFAULT 0 COMMENT '扫描的目录数',
    files_total         INT           NOT NULL DEFAULT 0 COMMENT '发现的文件总数',
    files_processed     INT           NOT NULL DEFAULT 0 COMMENT '实际处理的文件数',
    files_skipped       INT           NOT NULL DEFAULT 0 COMMENT '跳过的文件数（无变化）',
    files_error         INT           NOT NULL DEFAULT 0 COMMENT '处理出错的文件数',
    new_messages        INT           NOT NULL DEFAULT 0 COMMENT '本次新增的消息数',
    new_turns           INT           NOT NULL DEFAULT 0 COMMENT '本次新增/更新的轮次数',
    new_issues          INT           NOT NULL DEFAULT 0 COMMENT '本次新增的问题数',
    new_skill_calls     INT           NOT NULL DEFAULT 0 COMMENT '本次新增的 Skill 调用数',
    error_message       TEXT          COMMENT '失败时的错误信息',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_status (status),
    INDEX idx_started (started_at)
) COMMENT '扫描执行历史';
