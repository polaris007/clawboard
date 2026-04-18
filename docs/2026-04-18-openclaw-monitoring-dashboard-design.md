# OpenClaw 监控统计面板 — 系统设计文档

> 版本: 1.0  
> 日期: 2026-04-18  
> 状态: 设计评审

---

## 1. 背景与目标

### 1.1 业务背景

公司基于 OpenClaw 打造企业级个人助理，采用 PaaS 容器化部署。每个用户拥有独立的 OpenClaw 实例，通过内部聊天工具对接，后端连接内部部署的大模型。每个实例的 `~/.openclaw` 目录通过 NAS 挂载（以工号命名目录）。当前用户规模约 100 人，预计扩展至 1000+。

### 1.2 系统目标

构建一个独立于 OpenClaw 之外的监控统计面板，通过解析 NAS 上各用户的 session transcript JSONL 文件，提供以下能力：

1. **错误统计** — 检测对话中的各类错误（模型错误、超时、限流、工具错误等），按时间/人员/类型维度统计
2. **Skill 使用统计** — 追踪公司维护的 Skill 调用情况，含成功/失败分析
3. **Token 使用统计** — 统计输入/输出/总 token 数及费用
4. **对话轮次统计** — 统计对话轮次数，记录每轮的完整调用链路，判断对话成功/失败
5. **热度趋势图** — 按时间粒度展示 token 消耗、对话轮次、Skill 调用量的趋势
6. **多维度聚合** — 支持按用户/部门/公司维度的日/周/月统计

### 1.3 技术选型

| 组件 | 选型 | 说明 |
|------|------|------|
| JDK | JDK 17 | 最低要求 JDK 17，使用 Records、Sealed Classes、Text Blocks 等语言特性 |
| 后端框架 | Java SpringBoot | 只提供 REST API，不含前端 |
| 数据库 | OceanBase MySQL 模式 | 生产数据库 |
| NAS 访问 | 本地挂载 | NAS 挂载到统计服务器，作为本地文件系统访问 |
| 数据源 | Session Transcript JSONL | 统一从 transcript 文件解析所有数据 |

### 1.4 关于数据源的决策

经过调研，OpenClaw Gateway 的 usage 接口（`sessions.usage`）底层也是解析 session transcript JSONL 文件（`src/infra/session-cost-usage.ts`），两者数据源完全相同。Gateway 接口仅通过 WebSocket 暴露，无 HTTP 端点，为 100~1000 用户维护等量的 WebSocket 连接复杂度过高。因此统一采用 transcript 文件解析方案。

---

## 2. 数据源分析

### 2.1 JSONL 文件结构

Session transcript 文件存储在 `~/.openclaw/agents/<agentId>/sessions/` 目录下，格式为 JSONL（每行一个 JSON）。

**文件命名规则**：

| 文件名格式 | 类型 | 是否需要解析 |
|------------|------|-------------|
| `<uuid>.jsonl` | 活跃 session | 是 |
| `<uuid>.jsonl.reset.<timestamp>` | reset 归档 | 是 |
| `<uuid>.jsonl.deleted.<timestamp>` | 删除归档 | 是 |
| `<uuid>.jsonl.bak.<timestamp>` 或 `<uuid>.jsonl.bak-<N>-<epochMs>` | compaction 备份 | 是（靠数据库去重） |
| `<uuid>.checkpoint.<checkpoint-uuid>.jsonl` | compaction 临时快照 | 是（靠数据库去重） |

**选择扫描所有包含 `.jsonl` 的文件的原因**：

1. **Compaction 备份**：Compaction 操作会将原始文件备份为 `.bak`，然后用截断后的内容覆写原文件（只保留尾部 N 行）。如果跳过 `.bak` 文件，compaction 截掉的历史消息将永久丢失。

2. **Checkpoint 快照**：某些版本的 OpenClaw 在 compaction 过程中会创建 `.checkpoint.*` 临时文件。虽然这些文件主要包含内部消息，但为确保数据完整性，统一纳入扫描范围。

3. **去重策略**：通过扫描所有文件并依靠数据库 `(session_id, message_id)` 复合主键去重（使用 `INSERT IGNORE INTO`），保证数据完整性的同时避免重复入库。

4. **简化逻辑**：不依赖具体的文件名模式匹配，即使 OpenClaw 未来引入新的归档格式也能自动适配。

**唯一排除**：`sessions.json` 元数据文件（不是 transcript 数据）

### 2.2 JSONL 记录类型

```jsonl
// 行1: Session 头部
{"type":"session","version":3,"id":"<uuid>","timestamp":"<ISO>","cwd":"<path>"}

// 元数据行（跳过）
{"type":"model_change","id":"...","timestamp":"...","provider":"...","modelId":"..."}
{"type":"thinking_level_change","id":"...","timestamp":"...","thinkingLevel":"off"}
{"type":"custom","customType":"model-snapshot","data":{...},"id":"...","timestamp":"..."}

// 消息行（核心数据）
{"type":"message","id":"<8位hex>","parentId":"<id>","timestamp":"<ISO>","message":{
  "role":"user|assistant|toolResult",
  "content":[...],
  "usage":{"input":13650,"output":36,"cacheRead":0,"cacheWrite":0,"totalTokens":13686,
           "cost":{"input":0,"output":0,"cacheRead":0,"cacheWrite":0,"total":0}},
  "provider":"anthropic","model":"claude-sonnet-4-20250514",
  "stopReason":"end_turn","timestamp":1774425141601,"durationMs":5200
}}
```

### 2.3 Message ID 唯一性

Message ID 是 8 位十六进制字符串（取 UUID v4 前 8 位），**仅在单个 session 内唯一**。跨 session 可能碰撞（4.3B 空间，百万级记录时碰撞概率 ~11%）。必须使用 `(session_id, message_id)` 作为复合主键。

Session ID 从 JSONL 第一行 `type=session` 的 `id` 字段提取。如果第一行不是 session header（compaction 后可能被截掉），则从文件名解析。

**Session ID 从文件名提取规则**：取文件名中第一个 `.jsonl` 之前的部分。适用于所有文件格式：

```
c8ea5d68-5d98-4607-bad3-ad453d0aeacb.jsonl                              → c8ea5d68-5d98-4607-bad3-ad453d0aeacb
c8ea5d68-5d98-4607-bad3-ad453d0aeacb.jsonl.bak.2026-04-18T09-00-00.000Z → c8ea5d68-5d98-4607-bad3-ad453d0aeacb
c8ea5d68-5d98-4607-bad3-ad453d0aeacb.jsonl.reset.2026-04-01T...Z        → c8ea5d68-5d98-4607-bad3-ad453d0aeacb
c8ea5d68-5d98-4607-bad3-ad453d0aeacb.jsonl.deleted.2026-04-01T...Z      → c8ea5d68-5d98-4607-bad3-ad453d0aeacb
```

Java 伪代码：`fileName.substring(0, fileName.indexOf(".jsonl"))`

### 2.4 需要过滤的消息

| 过滤对象 | 识别方式 | 原因 |
|----------|----------|------|
| Delivery-mirror 消息 | `provider == "openclaw"` 且 `model == "delivery-mirror"` | 镜像消息，usage 全为 0，会导致 assistant 消息计数翻倍 |
| 系统生成的 user 消息 | 见下方完整模式列表 | 非真实用户输入，会虚增轮次计数 |

**系统生成的 user 消息过滤模式**（完整列表，参照 `detect-all-transcript-issues.py` 的 `is_system_generated_user_message` 函数）：

content 以下列任一字符串**开头**的 user 消息视为系统消息，不计入轮次：

1. `"A new session was started"`
2. `"Run your Session Startup sequence"`
3. `"Read HEARTBEAT.md"`
4. `"<inbound_metadata>"` — 内部上下文标记
5. `"<openclaw-envelope>"` — OpenClaw 信封元数据

实现时建议将这些模式配置化（配置文件中维护列表），便于后续增减。

### 2.5 数据生命周期与丢失风险

| 清理机制 | 默认保留 | 影响 | 应对 |
|----------|----------|------|------|
| Cron Session Reaper | 24 小时 | 定时任务的 session 文件被永久删除 | 扫描频率必须短于 24h |
| Session Store Maintenance | 30 天（默认 warn 模式不实际删除） | 旧 session 可能被归档后删除 | 数据一旦入库就不受影响 |
| Disk Budget Enforcement | 视配置 | 磁盘超预算时删除最旧文件 | 提高扫描频率 |
| Compaction | 随时可能发生 | 文件被覆写，前面的行丢失 | 扫描 `.bak` 文件补回 |

---

## 3. 数据库设计

所有表使用 `dashboard_` 前缀。数据库为 OceanBase MySQL 模式。

### 3.0 外部依赖：用户-部门对应关系

本系统依赖一张**已有的用户-部门对应关系表**（由 HR 或用户管理系统维护）。如果该表不存在或不可直接访问，需要在本系统中创建一张映射表：

```sql
-- 如果已有外部表，通过视图或配置指向即可；如果没有，创建此表
CREATE TABLE dashboard_employee (
    employee_id   VARCHAR(50)   NOT NULL COMMENT '工号',
    employee_name VARCHAR(100)  COMMENT '姓名',
    team_name     VARCHAR(100)  COMMENT '部门/团队名称',
    is_active     TINYINT       NOT NULL DEFAULT 1 COMMENT '是否在职',
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (employee_id),
    INDEX idx_team (team_name)
) COMMENT '用户-部门对应关系（可由外部系统同步）';
```

本文档后续所有涉及 `teamName`、`userName` 筛选的接口，均通过 `employee_id` 关联此表。

### 3.1 dashboard_scan_progress — 扫描进度表

```sql
CREATE TABLE dashboard_scan_progress (
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
```

**增量扫描逻辑**：

1. `file_size` / `file_mtime` 没变化 → 跳过
2. `file_size >= 上次` → 文件追加了内容 → 从 `last_offset` 字节处继续读取
3. `file_size < 上次` → 文件被覆写（compaction）：
   - a. 从头扫描文件，查找 `last_message_id` 所在行
   - b. 找到 → 从该行之后继续处理
   - c. 未找到 → 全文件逐行处理，靠 `INSERT IGNORE INTO` 去重

### 3.2 dashboard_message — 消息明细表

```sql
CREATE TABLE dashboard_message (
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
    cost_total       DECIMAL(14,8) COMMENT '费用(USD) — 注意:此表中命名为cost_total以对齐OpenClaw原始字段,其他表统一用total_cost',
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
```

### 3.3 dashboard_conversation_turn — 对话轮次表

```sql
CREATE TABLE dashboard_conversation_turn (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    session_id          VARCHAR(36)   NOT NULL COMMENT 'Session UUID',
    employee_id         VARCHAR(50)   NOT NULL COMMENT '工号',
    turn_index          INT           COMMENT '该 session 中的第几轮（按 start_time 排序赋值，可在查询时用 ROW_NUMBER() 动态计算）',
    start_message_id    VARCHAR(36)   NOT NULL COMMENT '起始 user 消息 ID（轮次的唯一标识）',
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
```

**chain_summary JSON 格式**（存储完整链路，支持链路详情展示）：

```json
{
  "steps": [
    {
      "stepOrder": 0,
      "nodeType": "user_input",
      "nodeName": "用户输入",
      "messageId": "abc12345",
      "status": true,
      "timeStamp": 1680000000000,
      "durationMs": null
    },
    {
      "stepOrder": 1,
      "nodeType": "skill_call",
      "nodeName": "公文写作",
      "messageId": "def67890",
      "toolCallId": "call_xxx",
      "status": true,
      "timeStamp": 1680000200000,
      "durationMs": 50
    },
    {
      "stepOrder": 2,
      "nodeType": "tool_call",
      "nodeName": "exec",
      "messageId": "ghi11111",
      "toolCallId": "call_yyy",
      "status": true,
      "timeStamp": 1680000500000,
      "durationMs": 8200
    },
    {
      "stepOrder": 3,
      "nodeType": "reply",
      "nodeName": "回复用户",
      "messageId": "jkl22222",
      "status": true,
      "timeStamp": 1680009000000,
      "durationMs": 6000
    }
  ],
  "totalDurationMs": 15200,
  "toolDurationMs": 8250,
  "modelDurationMs": 6000
}
```

**nodeType 取值**：

| nodeType | 说明 | 来源 |
|----------|------|------|
| `user_input` | 用户输入 | role=user 消息 |
| `tool_call` | 普通工具调用 | assistant 消息中的 toolCall 块（非 SKILL.md 读取） |
| `skill_call` | Skill 调用 | toolCall 读取 SKILL.md 文件 |
| `reply` | 回复用户 | 最后一条 assistant 消息（stopReason != toolUse） |

### 3.4 dashboard_skill_invocation — Skill 调用记录表

```sql
CREATE TABLE dashboard_skill_invocation (
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
```

### 3.5 dashboard_transcript_issue — 错误/问题记录表

```sql
CREATE TABLE dashboard_transcript_issue (
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
```

**错误分类（error_type）**：

| error_type | 说明 | 严重度 |
|------------|------|--------|
| `modelErrors` | 模型 API 错误 | HIGH |
| `timeoutErrors` | 超时错误 | HIGH |
| `rateLimitErrors` | 限流错误 | HIGH |
| `toolErrors` | 工具执行错误 | HIGH |
| `permissionErrors` | 权限错误 | HIGH |
| `parsingErrors` | 解析错误 | HIGH |
| `networkErrors` | 网络错误 | HIGH |
| `flow_integrity_no_reply` | user 消息后无 assistant 回复 | HIGH |
| `flow_integrity_missing_tool_result` | toolCall 后无 toolResult | HIGH |
| `flow_integrity_missing_final_answer` | toolResult 后无最终回复 | MEDIUM |
| `abnormal_stop_high` | 异常停止 (aborted/error) | HIGH |
| `abnormal_stop_medium` | 异常停止 (其他非正常 stopReason) | MEDIUM |

### 3.6 dashboard_hourly_stats — 小时粒度聚合表

```sql
CREATE TABLE dashboard_hourly_stats (
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
```

此表在每次扫描后从明细数据增量更新。API 查询趋势图和聚合统计时直接查此表，避免全量扫描明细表。

### 3.7 dashboard_scan_history — 扫描历史表

```sql
CREATE TABLE dashboard_scan_history (
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
```

此表记录每次扫描（不论定时触发还是手动触发）的执行过程和结果。`ScanOrchestrator` 在扫描开始时插入一条 `status=running` 记录，扫描结束后更新为 `completed` 或 `failed`，并回填各统计字段。

**聚合表增量更新策略**：

每次扫描完成后，对扫描期间涉及到的 `(employee_id, hour)` 组合执行 `INSERT ... ON DUPLICATE KEY UPDATE`。具体逻辑：

1. 扫描器在解析消息时，记录本次扫描涉及到的所有 `(employee_id, stat_hour)` 组合（从消息时间戳截断到小时）
2. 对每个涉及的 `(employee_id, stat_hour)` 组合，从 `dashboard_conversation_turn`、`dashboard_skill_invocation`、`dashboard_transcript_issue` 中重新聚合该小时的数据
3. 使用 `INSERT ... ON DUPLICATE KEY UPDATE` 写入 `dashboard_hourly_stats`，覆盖该小时的所有聚合字段

这是"重算受影响小时"的策略，而不是简单的累加，避免重复扫描导致数据膨胀。

**字段计数规则说明**：

- `conversation_turns` = 总轮次数（含完成、未完成、错误）
- `complete_turns` = `status='complete'` 的轮次数
- `error_turns` = `status='error'` 的轮次数
- 未完成轮次数 = `conversation_turns - complete_turns - error_turns`
- `skill_invocations` = skill 调用总次数（含成功和失败）
- `skill_errors` = skill 调用失败次数
- `tool_calls` = 工具调用总次数（含成功和失败）
- `tool_errors` = 工具调用失败次数

---

## 4. 解析引擎设计

### 4.1 整体架构

```
┌─────────────────────────────────────────────────────┐
│                SpringBoot Application                │
│                                                      │
│  ┌──────────────┐    ┌────────────────────────────┐ │
│  │  Scheduler    │───▶│  ScanOrchestrator          │ │
│  │  (可配置cron) │    │  (调度扫描流程)             │ │
│  └──────────────┘    └────────────┬───────────────┘ │
│                                   │                  │
│                     ┌─────────────┼──────────┐      │
│                     ▼             ▼          ▼      │
│   ┌──────────┐ ┌──────────┐ ┌──────────────┐      │
│   │UserScanner│ │UserScanner│ │ UserScanner  │      │
│   │(工号001)  │ │(工号002)  │ │ (工号NNN)    │      │
│   └─────┬────┘ └─────┬────┘ └──────┬───────┘      │
│         │             │             │               │
│         ▼             ▼             ▼               │
│   ┌──────────────────────────────────────────────┐ │
│   │             TranscriptParser                  │ │
│   │  ├─ MessageParser      (消息解析)             │ │
│   │  ├─ TurnAssembler      (轮次组装)             │ │
│   │  ├─ SkillDetector      (Skill识别)            │ │
│   │  ├─ IssueDetector      (错误检测)             │ │
│   │  └─ UsageExtractor     (Token/费用提取)       │ │
│   └──────────────────────────────────────────────┘ │
│         │                                           │
│         ▼                                           │
│   ┌──────────────────────────────────────────────┐ │
│   │           OceanBase (MySQL模式)               │ │
│   └──────────────────────────────────────────────┘ │
│         │                                           │
│         ▼                                           │
│   ┌──────────────────────────────────────────────┐ │
│   │              REST API Layer                   │ │
│   └──────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────┘
          │
          ▼
    NAS 挂载目录
    /mnt/nas/{工号}/.openclaw/agents/*/sessions/*.jsonl
```

### 4.2 调度流程

```
ScanOrchestrator.run(triggerType)     // triggerType = "scheduled" | "manual"
│
├─ 0. 尝试获取扫描锁（AtomicBoolean CAS）
│     ├─ 获取失败 → 正在扫描中，跳过本次执行并记录日志
│     └─ 获取成功 → 继续
│
├─ 1. 创建扫描历史记录
│     INSERT INTO dashboard_scan_history (trigger_type, status, started_at)
│     VALUES (triggerType, 'running', NOW())
│     记录 scanHistoryId
│
├─ 2. 扫描 NAS 根目录，获取所有工号子目录
│     /mnt/nas/001/.openclaw/
│     /mnt/nas/002/.openclaw/
│     → 累计 users_scanned, dirs_scanned
│
├─ 3. 对每个工号，递归查找 agents/*/sessions/ 下
│     所有文件名包含 ".jsonl" 的文件
│     → 累计 files_total
│
├─ 4. 对每个文件，查询 dashboard_scan_progress 获取上次状态
│     ├─ 文件不存在于进度表 → 新文件，全量解析
│     ├─ file_size/mtime 没变 → 跳过 → files_skipped++
│     ├─ file_size >= 上次 → 增量解析（从 last_offset 开始）
│     └─ file_size < 上次 → compaction 处理：
│         ├─ 从头扫描查找 last_message_id
│         ├─ 找到 → 从其后继续
│         └─ 未找到 → 全文件处理，靠 DB 去重
│     → 处理成功 files_processed++，处理失败 files_error++
│
├─ 5. TranscriptParser 解析文件 → 写入各表
│     → 累计 new_messages, new_turns, new_issues, new_skill_calls
│
├─ 6. 更新 dashboard_scan_progress
│
├─ 7. 增量更新 dashboard_hourly_stats
│
├─ 8. 更新扫描历史记录
│     UPDATE dashboard_scan_history SET
│       status = 'completed'/'failed',
│       finished_at = NOW(),
│       duration_ms = ...,
│       users_scanned = ..., dirs_scanned = ...,
│       files_total = ..., files_processed = ...,
│       files_skipped = ..., files_error = ...,
│       new_messages = ..., new_turns = ...,
│       new_issues = ..., new_skill_calls = ...,
│       error_message = ...  -- 仅 failed 时填写
│     WHERE id = scanHistoryId
│
└─ 9. 释放扫描锁
```

**异常安全**：步骤 8-9 在 `finally` 块中执行。即使扫描过程发生未捕获异常，也会将扫描历史标记为 `failed` 并释放锁，防止死锁。

### 4.3 并发控制

- 不同用户之间**无依赖**，使用可配置的线程池并行处理
- 同一用户的同一文件**串行处理**
- 默认线程池大小：`min(用户数, CPU核心数 * 2)`
- NAS I/O 是主要瓶颈，线程过多反而降低吞吐
- **防止扫描重叠**：使用 `AtomicBoolean` 或 `ReentrantLock` 作为全局扫描锁。如果上一次扫描尚未完成，新的触发（定时或手动）跳过执行。
  - **定时触发跳过**：静默跳过，记录 `WARN` 日志 `"Scan skipped: previous scan still running (scanId=xxx)"`
  - **手动触发跳过**：API 返回 `409 Conflict` 并携带当前扫描信息，告知调用方正在扫描中（详见 5.11）
- `ScanOrchestrator` 维护 `currentScanId` 字段（`volatile Long`），扫描开始时赋值为 `dashboard_scan_history.id`，扫描结束后置为 `null`，便于状态查询接口使用

### 4.4 TranscriptParser 解析流程（单文件）

```
逐行读取 JSONL（使用 BufferedReader 流式读取）
│
├─ 解析 JSON，跳过空行和非法 JSON 行
│
├─ 根据 type 分流：
│   │
│   ├─ type=session
│   │   └─ 提取 session_id（与文件名交叉校验）
│   │
│   ├─ type=message
│   │   ├─ role=user
│   │   │   ├─ 过滤系统生成消息（heartbeat、session startup 等）
│   │   │   ├─ INSERT IGNORE INTO dashboard_message
│   │   │   └─ 标记"新轮次开始"
│   │   │
│   │   ├─ role=assistant
│   │   │   ├─ 过滤 delivery-mirror
│   │   │   │   (provider=="openclaw" && model=="delivery-mirror")
│   │   │   ├─ 提取 usage (input/output/cache/cost)
│   │   │   ├─ 提取 toolCall 块
│   │   │   │   └─ 检测 SKILL.md 读取 → 标记 skill 调用
│   │   │   ├─ 检查 stopReason → 异常停止检测
│   │   │   ├─ 检查 errorMessage → 已知错误检测
│   │   │   ├─ INSERT INTO dashboard_message
│   │   │   └─ 更新当前轮次链路步骤
│   │   │
│   │   └─ role=toolResult
│   │       ├─ 检查 isError
│   │       ├─ 计算工具耗时 = 当前时间戳 - 对应 toolCall 时间戳
│   │       ├─ 如果是 SKILL.md 的 result
│   │       │   └─ INSERT INTO dashboard_skill_invocation
│   │       ├─ INSERT INTO dashboard_message
│   │       └─ 更新当前轮次链路步骤
│   │
│   ├─ type=custom
│   │   ├─ 匹配错误模式（如 openclaw:prompt-error）
│   │   └─ INSERT INTO dashboard_transcript_issue
│   │
│   └─ 其他 type → 跳过
│
├─ 轮次边界检测（遇到新 user 消息或文件结束）：
│   ├─ 组装 chain_summary JSON
│   ├─ 计算各耗时字段
│   ├─ 判断轮次完整性（is_complete / status）
│   ├─ 流程完整性检查 → dashboard_transcript_issue
│   └─ INSERT INTO dashboard_conversation_turn
│       ON DUPLICATE KEY UPDATE（以 session_id + start_message_id 去重，
│       合并取更完整版本）
│
└─ 文件结束 → 关闭最后一个轮次
```

### 4.5 轮次边界定义

一个对话轮次 = 从一条 `role=user` 消息开始，到下一条 `role=user` 消息之前（或文件结束）的所有消息。

**跨文件轮次处理**：同一 session 的消息可能分散在多个文件中（原始文件 + `.bak` 文件）。由于 compaction 会将原文件整体备份为 `.bak` 再截断原文件，.bak 与主文件之间必然存在重叠消息。处理策略：

1. 每个文件独立组装轮次。使用 `(session_id, start_message_id)` 作为唯一键进行去重。同一个 user 消息无论出现在 `.bak` 文件还是主文件中，其 `message_id` 相同，因此天然匹配到同一行。
2. 使用 `INSERT ... ON DUPLICATE KEY UPDATE` 合并数据。合并规则：取更完整的版本（chain_summary 的 steps 数量更多、status 为 `complete` 优先于 `incomplete`、token/cost 取非零值）。
3. `turn_index` 不参与去重，作为排序辅助字段。在每次扫描结束后（或查询时）按 `start_time` 排序赋值。查询时也可用 `ROW_NUMBER() OVER (PARTITION BY session_id ORDER BY start_time)` 动态计算。
4. 在 `.bak` 文件中出现的不完整轮次（文件尾部截断），标记为 `incomplete`。如果后续在主文件中找到相同轮次的完整版本，通过 `ON DUPLICATE KEY UPDATE` 覆盖更新。

**轮次状态判定**：

| status | 条件 |
|--------|------|
| `complete` | 有 user 消息，有最终 assistant 回复（stopReason != toolUse），无报错 |
| `incomplete` | 有 user 消息，但缺少最终 assistant 回复 |
| `error` | 轮次中存在任何报错（isError=true 或匹配错误模式） |

### 4.6 Skill 识别逻辑

**检测信号 1 — 模型主动读取 SKILL.md**（主要路径）：
- assistant 消息中的 `toolCall` 块，`name` 匹配 `read` 或 `Read`
- `arguments` 中的文件路径匹配 `*/skills/*/SKILL.md` 或 `*SKILL.md`
- 从路径提取 skill 名称（路径中 `skills/` 后的目录名）
- `trigger_type = "model_read"`

**检测信号 2 — 斜杠命令触发**：
- user 消息内容匹配 `Use the "(\w[\w-]*)" skill` 模式
- 或 user 消息以 `/<skill-name>` 开头
- `trigger_type = "slash_command"`

**Skill 调用成功/失败判定**：
- 检查对应 `toolResult` 的 `isError` 字段
- 如果 toolResult 不存在（流程中断），标记为失败

### 4.7 错误检测逻辑

复刻 `scripts/detect-all-transcript-issues.py` 的三类检测：

**1. 已知错误模式匹配**（正则表达式，不区分大小写）

| 分类 | 正则模式示例 |
|------|-------------|
| modelErrors | `model.*error`, `api.*error`, `LLM.*timeout`, `operation.*aborted` |
| timeoutErrors | `timeout`, `timed.*out`, `ETIMEDOUT`, `connection.*timeout` |
| rateLimitErrors | `rate.*limit`, `\b429\b`, `too.*many.*requests`, `quota.*exceeded` |
| toolErrors | `tool.*error`, `tool.*failed`, `execution.*error` |
| permissionErrors | `permission.*denied`, `access.*denied`, `forbidden`, `\b403\b` |
| parsingErrors | `parse.*error`, `invalid.*json`, `syntax.*error` |
| networkErrors | `network.*error`, `connection.*refused`, `ECONNREFUSED` |

匹配目标：
- `type=custom` 的 `customType` + `data` 序列化文本
- assistant 消息的 `errorMessage` 字段

**2. 流程完整性检查**

| 检查项 | 规则 | 严重度 |
|--------|------|--------|
| flow_integrity_no_reply | user 消息后无 assistant 消息 | HIGH |
| flow_integrity_missing_tool_result | toolCall 后无 toolResult | HIGH |
| flow_integrity_missing_final_answer | toolResult 后无 assistant 或另一个 toolResult | MEDIUM |

排除条件：
- 如果下一个事件是 `openclaw:prompt-error` custom event，跳过检测（避免重复计数）
- 如果 assistant 的 `stopReason` 是 `aborted` 或 `error`，跳过 missing_tool_result
- 如果 `toolName` 是 `sessions_yield`，跳过 missing_final_answer

**3. 异常停止检测**

正常 stopReason：`stop`, `end_turn`, `toolUse`, `tool_use`, `length`  
其他值标记为异常停止：`aborted`/`error` → HIGH，其余 → MEDIUM

---

## 5. REST API 设计

严格对齐前端 API 接口文档 `mydocs/API接口文档.md`。所有接口使用统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 通用约定

**时间参数格式**：所有 `startTime` / `endTime` 参数统一使用**毫秒级时间戳**（Long 类型），如 `1680000000000`。后端同时兼容 ISO 8601 字符串格式（如 `"2026-04-18T00:00:00+08:00"`）作为备选。响应中的时间字段统一返回毫秒时间戳。

**分页默认值**：`page` 默认 1，`pageSize` 默认 10。

**排序默认规则**：列表接口默认按时间降序（最新在前）。

**turnId 编码**：`dashboard_conversation_turn.id`（BIGINT 自增）直接作为 `turnId` 返回（数字类型），前端接收后自行处理展示格式。

### 模块一：数据字典接口

#### 5.1 获取技能列表

- **路径**: `GET /api/v1/skills/options`
- **说明**: 从 `dashboard_skill_invocation` 表中聚合出所有已使用的 skill，返回去重列表
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "skillId": "official-doc-writer", "skillName": "公文写作" },
    { "skillId": "pptx", "skillName": "ppt生成" }
  ]
}
```

**说明**: `skillId` 取 `skill_name` 字段。`skillName` 可通过维护一个 skill 名称映射表（或配置文件）来提供中文名称。如果没有映射，`skillName` 与 `skillId` 相同。

### 模块二：运营大盘接口

#### 5.2 获取平台全局累计数据

- **路径**: `GET /api/v1/dashboard/global-stats`
- **说明**: 不受筛选条件影响，返回所有用户从上线至今的累计数据
- **数据来源**: `dashboard_hourly_stats` 全表 SUM + 用户表 COUNT(DISTINCT employee_id)
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalTokens": 1250000,
    "totalTurns": 52000,
    "totalSkillCalls": 25000,
    "totalUsers": 872
  }
}
```

#### 5.3 获取大盘统计卡片数据

- **路径**: `POST /api/v1/dashboard/summary`
- **请求体**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| teamName | String | 否 | 团队/部门筛选 |
| userName | String | 否 | 姓名筛选 |
| startTime | Long/String | 否 | 开始时间（默认当日 0 点） |
| endTime | Long/String | 否 | 结束时间（默认当前时间） |

- **数据来源**: `dashboard_hourly_stats` JOIN 用户-部门对应关系表，按条件 SUM
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "consumedTokens": 1250000,
    "conversationTurns": 45200,
    "skillCalls": 12300,
    "activeUsers": 150
  }
}
```

`activeUsers` = 在时间范围内 `dashboard_hourly_stats` 中有记录的 `DISTINCT employee_id` 数量。

#### 5.4 获取热度趋势图表数据

- **路径**: `POST /api/v1/dashboard/trend`
- **请求体**: 同 5.3 的通用筛选参数
- **时间粒度规则**（基于时间跨度 `duration = endTime - startTime`）:
  - duration <= 24 小时 → 2 小时粒度（日视图）
  - 24 小时 < duration <= 7 天 → 天粒度（周视图）
  - duration > 7 天 → 周粒度（月视图）
- **数据来源**: `dashboard_hourly_stats` 按对应粒度 GROUP BY
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "timeLabel": 1680000000000,
      "tokens": 4500,
      "turns": 120,
      "skills": 85
    }
  ]
}
```

`timeLabel` 为时间段起点的毫秒级时间戳。

#### 5.5 分页查询用户状态与消耗明细

- **路径**: `POST /api/v1/dashboard/usersummary`
- **请求体**: 通用筛选参数 + `page`(默认1) + `pageSize`(默认10)
- **数据来源**: `dashboard_hourly_stats` + `dashboard_conversation_turn` + `dashboard_skill_invocation` 聚合
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 150,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "userId": "18101142",
        "userName": "王颜",
        "teamName": "新技术",
        "status": true,
        "lastHeartbeat": 1680000000000,
        "tokens": {
          "total": 15400,
          "input": 5400,
          "output": 10000
        },
        "turns": {
          "success": 3,
          "total": 3
        },
        "skillCalls": {
          "success": 2,
          "total": 2
        },
        "toolCalls": {
          "success": 1,
          "total": 1
        },
        "topSkills": ["公文写作", "ppt生成"]
      }
    ]
  }
}
```

**待定字段**:
- `status` / `lastHeartbeat`: 用户在线状态和最后心跳时间，需要额外的检测机制（待后续设计）
- `topSkills`: 从 `dashboard_skill_invocation` 中按 `employee_id` + 时间范围 GROUP BY `skill_name` 取 TOP N

### 模块三：对话记录检索接口

#### 5.6 分页查询对话记录

- **路径**: `POST /api/v1/turns/search`
- **请求体**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userName | String | 否 | 姓名筛选 |
| startTime | Long/String | 否 | 开始时间 |
| endTime | Long/String | 否 | 结束时间 |
| skillId | String | 否 | Skill 筛选 |
| page | Integer | 否 | 页码（默认 1） |
| pageSize | Integer | 否 | 每页条数（默认 10） |

- **数据来源**: `dashboard_conversation_turn` LEFT JOIN `dashboard_skill_invocation`
- **当有 `skillId` 筛选时**: 通过 `dashboard_skill_invocation.skill_name` 关联查询
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 125,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "turnId": "d6gte29d",
        "timeStamp": 1680000000000,
        "userName": "王颜",
        "userInput": "帮我生成一份二十届三中全会学习PPT",
        "durationMs": 86000,
        "resultStatus": false,
        "qualityStatus": 2,
        "tokens": {
          "total": 15400,
          "input": 5400,
          "output": 10000
        },
        "skills": ["公文写作"],
        "tools": [],
        "logFileName": "/datafs/openclaw/agents/main/sessions/xxx.jsonl"
      }
    ]
  }
}
```

**`turnId`**: 使用 `dashboard_conversation_turn.id` 的编码值（可直接使用自增 ID 或编码为短字符串）。  
**`resultStatus`**: `true` = status 为 `complete`，`false` = status 为 `incomplete` 或 `error`。  
**`skills`/`tools`**: 从 `chain_summary` JSON 中提取，或从关联的 `dashboard_skill_invocation` 表查询。

#### 5.7 获取执行链路详情

- **路径**: `GET /api/v1/turns/{turnId}/trace`
- **数据来源**: `dashboard_conversation_turn.chain_summary` JSON 字段
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "turnId": "d6gte29d",
    "nodes": [
      {
        "stepOrder": 0,
        "nodeType": "user_input",
        "nodeName": "用户输入",
        "status": true,
        "timeStamp": 1680000000000,
        "durationMs": null
      },
      {
        "stepOrder": 1,
        "nodeType": "skill_call",
        "nodeName": "公文写作",
        "status": true,
        "timeStamp": 1680000000000,
        "durationMs": 50
      },
      {
        "stepOrder": 2,
        "nodeType": "tool_call",
        "nodeName": "exec",
        "status": true,
        "timeStamp": 1680000000000,
        "durationMs": 8200
      },
      {
        "stepOrder": 3,
        "nodeType": "reply",
        "nodeName": "回复用户",
        "status": false,
        "timeStamp": 1680000000000,
        "durationMs": 6000
      }
    ]
  }
}
```

### 模块四：错误统计接口

#### 5.8 错误统计概览

- **路径**: `POST /api/v1/errors/summary`
- **请求体**: 同通用筛选参数（teamName, userName, startTime, endTime）
- **数据来源**: `dashboard_transcript_issue` JOIN `dashboard_employee`
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalErrors": 45,
    "totalTurns": 5600,
    "problemRate": 0.008,
    "byType": [
      { "errorType": "modelErrors", "count": 15, "severity": "HIGH", "description": "模型API错误" },
      { "errorType": "flow_integrity_no_reply", "count": 12, "severity": "HIGH", "description": "用户消息无回复" },
      { "errorType": "timeoutErrors", "count": 8, "severity": "HIGH", "description": "超时错误" }
    ],
    "bySeverity": {
      "HIGH": 35,
      "MEDIUM": 10
    }
  }
}
```

#### 5.9 错误记录列表

- **路径**: `POST /api/v1/errors/search`
- **请求体**: 通用筛选参数 + `errorType`(可选) + `severity`(可选) + `page` + `pageSize`
- **数据来源**: `dashboard_transcript_issue` JOIN `dashboard_employee`
- **响应**: 分页的错误记录列表，每条含 errorType、severity、description、errorMessage、employeeName、occurredAt、turnId

### 模块五：质量评价接口（待定功能预留）

#### 5.10 更新对话质量评价

- **路径**: `PUT /api/v1/turns/{turnId}/quality`
- **请求体**:

```json
{
  "qualityStatus": 1
}
```

- `qualityStatus` 取值：0=未评价（默认）, 1=正确, 2=错误, 3=待优化
- **说明**: 管理员人工标注对话质量。此接口待后续确认需求后实现。

### 管理接口

#### 5.11 手动触发扫描

- **路径**: `POST /api/v1/scan/trigger`
- **说明**: 手动触发一次全量扫描。如果当前已有扫描正在进行，返回 409 拒绝。
- **成功响应**（扫描已触发）:

```json
{
  "code": 200,
  "message": "scan triggered",
  "data": {
    "scanId": 42,
    "triggerType": "manual",
    "startedAt": 1680000000000
  }
}
```

- **冲突响应**（正在扫描中）:

```json
{
  "code": 409,
  "message": "scan already in progress, skipping this trigger",
  "data": {
    "currentScanId": 41,
    "triggerType": "scheduled",
    "startedAt": 1680000000000
  }
}
```

#### 5.12 扫描状态查询

- **路径**: `GET /api/v1/scan/status`
- **说明**: 返回当前扫描状态、下次计划扫描时间及最近一次完成的扫描概要
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "scanning": true,
    "nextScheduledAt": 1680021600000,
    "currentScan": {
      "scanId": 42,
      "triggerType": "manual",
      "startedAt": 1680000000000
    },
    "lastCompletedScan": {
      "scanId": 41,
      "triggerType": "scheduled",
      "startedAt": 1679990000000,
      "finishedAt": 1679990045000,
      "durationMs": 45000,
      "usersScanned": 98,
      "dirsScanned": 196,
      "filesTotal": 1520,
      "filesProcessed": 320,
      "filesSkipped": 1200,
      "filesError": 0,
      "newMessages": 450,
      "newTurns": 85,
      "newIssues": 3,
      "newSkillCalls": 12
    }
  }
}
```

**字段说明**:
- `scanning`: 当前是否正在扫描
- `nextScheduledAt`: 下一次定时扫描的计划触发时间（毫秒时间戳）。根据 cron 表达式计算，如果定时扫描未启用则为 `null`
- `currentScan`: 当前正在进行的扫描信息。如果 `scanning=false` 则为 `null`
- `lastCompletedScan`: 最近一次成功完成的扫描概要（`status=completed`）。如果从未完成过扫描则为 `null`

#### 5.13 扫描历史列表

- **路径**: `GET /api/v1/scan/history`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码（默认 1） |
| pageSize | Integer | 否 | 每页条数（默认 10） |
| triggerType | String | 否 | 按触发方式筛选: scheduled/manual |
| status | String | 否 | 按状态筛选: running/completed/failed |

- **数据来源**: `dashboard_scan_history` 按 `started_at` 降序
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 56,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "scanId": 42,
        "triggerType": "manual",
        "status": "completed",
        "startedAt": 1680000000000,
        "finishedAt": 1680000045000,
        "durationMs": 45000,
        "usersScanned": 98,
        "dirsScanned": 196,
        "filesTotal": 1520,
        "filesProcessed": 320,
        "filesSkipped": 1200,
        "filesError": 0,
        "newMessages": 450,
        "newTurns": 85,
        "newIssues": 3,
        "newSkillCalls": 12,
        "errorMessage": null
      }
    ]
  }
}
```

---

## 6. 配置项

```yaml
openclaw-monitor:
  nas:
    base-path: /mnt/nas                 # NAS 挂载根目录
    openclaw-dir: .openclaw             # 用户目录下的 OpenClaw 子目录名
  scan:
    cron: "0 */6 * * *"                 # 默认每 6 小时
    thread-pool-size: 8                 # 并行扫描线程数
    batch-size: 100                     # 批量入库大小
    enabled: true                       # 是否启用定时扫描
  parser:
    max-error-message-length: 500       # 错误消息截断长度
    max-user-input-length: 200          # 用户输入截断长度
    skip-system-messages: true          # 跳过系统生成的 user 消息
    delivery-mirror-filter: true        # 过滤 delivery-mirror 消息
  skill:
    name-mapping:                       # Skill 名称中英文映射
      official-doc-writer: 公文写作
      pptx: ppt生成
```

---

## 7. 待定功能

以下功能在当前版本中预留字段/接口，但具体实现待后续确定：

| 功能 | 说明 | 预留位置 |
|------|------|----------|
| 质量评价 (qualityStatus) | 管理员对对话质量的人工标注 | `dashboard_conversation_turn.quality_status` 字段已预留 |
| 用户在线状态 (status) | OpenClaw 实例是否活跃 | `usersummary` 接口的 `status` 字段 |
| 最后心跳 (lastHeartbeat) | 最近一次活跃时间 | `usersummary` 接口的 `lastHeartbeat` 字段 |
| 数据保留策略 | 明细数据/聚合数据的自动清理 | 暂全部保留，后续按需配置 |

---

## 8. 风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| Cron session 24h 删除 | 定时任务 session 数据丢失 | 扫描频率默认 6h，远短于 24h 窗口 |
| Compaction 覆写文件 | 历史消息被截断 | 扫描 .bak 文件补回，DB 主键去重 |
| NAS I/O 性能 | 扫描大量文件耗时长 | 增量解析 + 并行线程 + 跳过无变化文件 |
| Delivery-mirror 重复 | assistant 消息计数翻倍 | 解析时过滤 provider=openclaw 消息 |
| Message ID 跨 session 碰撞 | 主键冲突导致数据丢失 | 使用 (session_id, message_id) 复合主键 |
| Skill 识别不完整 | 部分 skill 调用可能漏检 | 启发式规则覆盖主要场景，后续可扩展检测信号 |
| 文件读取时被 rename | 文件读取中断 | 捕获异常，下次扫描重试 |
| 时区不一致 | 日期分桶不一致 | 统一使用服务器本地时区 |

---

## 9. SpringBoot 模块结构（建议）

```
src/main/java/com/company/openclaw/monitor/
├── config/
│   ├── MonitorProperties.java          # 配置类
│   └── ThreadPoolConfig.java           # 线程池配置
├── scanner/
│   ├── ScanOrchestrator.java           # 扫描调度器
│   ├── UserScanner.java                # 单用户扫描器
│   └── ScanProgressService.java        # 进度管理
├── parser/
│   ├── TranscriptParser.java           # JSONL 解析主流程
│   ├── MessageParser.java              # 消息解析
│   ├── TurnAssembler.java              # 轮次组装
│   ├── SkillDetector.java              # Skill 识别
│   ├── IssueDetector.java              # 错误检测
│   ├── UsageExtractor.java             # Token/费用提取
│   └── SystemMessageFilter.java        # 系统消息过滤
├── model/
│   ├── entity/                         # JPA/MyBatis 实体类
│   │   ├── DashboardMessage.java
│   │   ├── DashboardConversationTurn.java
│   │   ├── DashboardSkillInvocation.java
│   │   ├── DashboardTranscriptIssue.java
│   │   ├── DashboardHourlyStats.java
│   │   ├── DashboardScanProgress.java
│   │   └── DashboardScanHistory.java
│   └── dto/                            # API 响应 DTO
├── repository/                         # 数据库访问层
├── service/                            # 业务服务层
│   ├── DashboardService.java           # 大盘统计
│   ├── TurnService.java                # 对话记录查询
│   ├── SkillService.java               # Skill 统计
│   ├── ErrorService.java               # 错误统计
│   ├── ScanManagementService.java      # 扫描管理（触发/状态）
│   └── ScanHistoryService.java         # 扫描历史查询
├── controller/                         # REST API 控制器
│   ├── DashboardController.java        # 大盘接口
│   ├── TurnController.java             # 对话记录接口
│   ├── SkillController.java            # Skill 接口
│   ├── ErrorController.java            # 错误统计接口
│   └── ScanController.java             # 扫描管理接口
└── MonitorApplication.java             # 启动类
```
