-- Test schema for H2 database (MySQL compatible mode)

CREATE TABLE IF NOT EXISTS dashboard_employee (
    employee_id   VARCHAR(50)   NOT NULL,
    employee_name VARCHAR(100),
    team_name     VARCHAR(100),
    is_active     TINYINT       NOT NULL DEFAULT 1,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id)
);

CREATE TABLE IF NOT EXISTS dashboard_scan_progress (
    employee_id      VARCHAR(50)   NOT NULL,
    file_path        VARCHAR(500)  NOT NULL,
    last_offset      BIGINT        NOT NULL DEFAULT 0,
    file_size        BIGINT        NOT NULL DEFAULT 0,
    file_mtime       BIGINT        NOT NULL DEFAULT 0,
    session_id       VARCHAR(36),
    last_message_id  VARCHAR(36),
    last_message_ts  DATETIME(3),
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id, file_path)
);

CREATE TABLE IF NOT EXISTS dashboard_message (
    scan_id            BIGINT,
    session_id         VARCHAR(36)   NOT NULL,
    message_id         VARCHAR(36)   NOT NULL,
    employee_id        VARCHAR(50)   NOT NULL,
    role               VARCHAR(20)   NOT NULL,
    message_timestamp  DATETIME(3),
    input_tokens       INT,
    output_tokens      INT,
    cache_read_tokens  INT,
    cache_write_tokens INT,
    total_tokens       INT,
    cost_total         DECIMAL(14,8),
    provider           VARCHAR(50),
    model              VARCHAR(100),
    stop_reason        VARCHAR(50),
    duration_ms        INT,
    is_error           TINYINT       NOT NULL DEFAULT 0,
    tool_name          VARCHAR(100),
    tool_call_id       VARCHAR(100),
    parent_id          VARCHAR(36),
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (session_id, message_id)
);

CREATE TABLE IF NOT EXISTS dashboard_conversation_turn (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    scan_id               BIGINT,
    session_id            VARCHAR(36)   NOT NULL,
    employee_id           VARCHAR(50)   NOT NULL,
    turn_index            INT,
    start_message_id      VARCHAR(36)   NOT NULL,
    end_message_id        VARCHAR(36),
    start_time            DATETIME(3),
    end_time              DATETIME(3),
    user_input            VARCHAR(200),
    total_input_tokens    INT           DEFAULT 0,
    total_output_tokens   INT           DEFAULT 0,
    total_tokens          INT           DEFAULT 0,
    total_cost            DECIMAL(14,8) DEFAULT 0,
    tool_calls_count      INT           DEFAULT 0,
    tool_calls_success    INT           DEFAULT 0,
    tool_calls_error      INT           DEFAULT 0,
    skill_calls_count     INT           DEFAULT 0,
    skill_calls_success   INT           DEFAULT 0,
    skill_calls_error     INT           DEFAULT 0,
    is_complete           TINYINT       NOT NULL DEFAULT 0,
    has_error             TINYINT       NOT NULL DEFAULT 0,
    status                VARCHAR(20)   NOT NULL DEFAULT 'incomplete',
    total_duration_ms     INT,
    tool_duration_ms      INT,
    model_duration_ms     INT,
    chain_summary         TEXT,
    log_file_path         VARCHAR(500),
    quality_status        TINYINT       NOT NULL DEFAULT 0,
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS dashboard_skill_invocation (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    scan_id           BIGINT,
    session_id        VARCHAR(36)   NOT NULL,
    employee_id       VARCHAR(50)   NOT NULL,
    turn_id           BIGINT,
    skill_name        VARCHAR(100)  NOT NULL,
    skill_path        VARCHAR(500),
    invoked_at        DATETIME(3),
    read_message_id   VARCHAR(36),
    result_message_id VARCHAR(36),
    is_error          TINYINT       NOT NULL DEFAULT 0,
    trigger_type      VARCHAR(20)   DEFAULT 'model_read',
    duration_ms       INT,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS dashboard_transcript_issue (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    scan_id        BIGINT,
    session_id     VARCHAR(36)   NOT NULL,
    message_id     VARCHAR(36)   NOT NULL DEFAULT '',
    employee_id    VARCHAR(50)   NOT NULL,
    error_type     VARCHAR(50)   NOT NULL,
    severity       VARCHAR(10)   NOT NULL,
    description    VARCHAR(500),
    error_message  TEXT,
    event_type     VARCHAR(50),
    provider       VARCHAR(50),
    model          VARCHAR(100),
    line_number    INT,
    occurred_at    DATETIME(3),
    turn_id        BIGINT,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS dashboard_hourly_stats (
    employee_id       VARCHAR(50)   NOT NULL,
    stat_hour         DATETIME      NOT NULL,
    total_tokens      BIGINT        NOT NULL DEFAULT 0,
    input_tokens      BIGINT        NOT NULL DEFAULT 0,
    output_tokens     BIGINT        NOT NULL DEFAULT 0,
    total_cost        DECIMAL(14,8) NOT NULL DEFAULT 0,
    cache_read_tokens BIGINT        NOT NULL DEFAULT 0,
    cache_write_tokens BIGINT       NOT NULL DEFAULT 0,
    conversation_turns INT          NOT NULL DEFAULT 0,
    complete_turns    INT           NOT NULL DEFAULT 0,
    error_turns       INT           NOT NULL DEFAULT 0,
    skill_invocations INT           NOT NULL DEFAULT 0,
    skill_errors      INT           NOT NULL DEFAULT 0,
    tool_calls        INT           NOT NULL DEFAULT 0,
    tool_errors       INT           NOT NULL DEFAULT 0,
    error_count       INT           NOT NULL DEFAULT 0,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id, stat_hour)
);

CREATE TABLE IF NOT EXISTS dashboard_scan_history (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    trigger_type      VARCHAR(20)   NOT NULL,
    status            VARCHAR(20)   NOT NULL DEFAULT 'running',
    started_at        DATETIME(3)   NOT NULL,
    finished_at       DATETIME(3),
    duration_ms       BIGINT,
    users_scanned     INT           NOT NULL DEFAULT 0,
    dirs_scanned      INT           NOT NULL DEFAULT 0,
    files_total       INT           NOT NULL DEFAULT 0,
    files_processed   INT           NOT NULL DEFAULT 0,
    files_skipped     INT           NOT NULL DEFAULT 0,
    files_error       INT           NOT NULL DEFAULT 0,
    new_messages      INT           NOT NULL DEFAULT 0,
    new_turns         INT           NOT NULL DEFAULT 0,
    new_issues        INT           NOT NULL DEFAULT 0,
    new_skill_calls   INT           NOT NULL DEFAULT 0,
    error_message     TEXT,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
