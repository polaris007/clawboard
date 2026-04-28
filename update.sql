ALTER TABLE dashboard_message ADD COLUMN turn_id BIGINT COMMENT '关联轮次ID' AFTER session_id;
ALTER TABLE dashboard_message ADD INDEX idx_turn_id (turn_id);

CREATE TABLE IF NOT EXISTS dashboard_execution_trace (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '链路节点ID',
    scan_id         BIGINT        COMMENT '关联扫描ID（可选，用于追溯数据来源）',
    turn_id         BIGINT        NOT NULL COMMENT '关联轮次ID（外键指向 dashboard_conversation_turn.id）',
    message_id      VARCHAR(36)   COMMENT '关联消息ID（来自 transcript 的 message.id）',
    node_index      INT           NOT NULL COMMENT '节点序号（从0开始，表示在链路中的顺序）',
    node_type       VARCHAR(20)   NOT NULL COMMENT '节点类型：user_input/tool_call/tool_result/reply',
    tool_name       VARCHAR(100)  COMMENT '工具名称（仅 tool_call 和 tool_result 节点有值）',
    tool_call_id    VARCHAR(100)  COMMENT '工具调用ID（仅 tool_call 和 tool_result 节点有值）',
    content         TEXT          COMMENT '内容（user_input/reply 的文本内容，tool_result 的执行结果）',
    timestamp_ms    BIGINT        COMMENT '执行时间戳（毫秒，来自 transcript 的 timestamp 字段）',
    duration_ms     INT           COMMENT '耗时（毫秒，计算相邻节点的时间差）',
    success         TINYINT(4)    COMMENT '是否成功（仅 tool_result 节点有值，1=成功，0=失败）',
    error_message   TEXT          COMMENT '错误信息（仅 tool_result 节点且失败时有值）',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_turn_id (turn_id) COMMENT '按轮次查询索引',
    INDEX idx_scan_id (scan_id) COMMENT '按扫描ID查询索引',
    INDEX idx_node_type (node_type) COMMENT '按节点类型统计索引',
    INDEX idx_message_id (message_id) COMMENT '按消息ID查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行链路追踪表';

ALTER TABLE dashboard_scan_history 
ADD COLUMN agg_hours_from_files INT NOT NULL DEFAULT 0 COMMENT '从文件提取的小时数',
ADD COLUMN agg_hours_from_window INT NOT NULL DEFAULT 0 COMMENT '从时间窗口获取的小时数',
ADD COLUMN agg_hours_total INT NOT NULL DEFAULT 0 COMMENT '聚合的总小时数（去重后）',
ADD COLUMN agg_duration_ms BIGINT NOT NULL DEFAULT 0 COMMENT '聚合耗时(ms)';

