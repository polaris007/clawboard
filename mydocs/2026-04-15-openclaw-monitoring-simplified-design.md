# OpenClaw 简化版监控系统设计文档

**版本**: v1.0  
**日期**: 2026-04-15  
**作者**: OpenClaw Team  
**状态**: Draft

---

## 1. 项目背景与目标

### 1.1 业务场景

OpenClaw 是一款企业级个人助理服务，基于开源 OpenClaw 框架进行二次开发和部署。系统通过容器化方式部署在 PaaS 平台上，为多个用户提供 AI 对话服务。

**核心需求**：
- 监控所有用户实例（龙虾）的运行状态
- 统计全局运营数据（对话量、技能使用、Token消耗等）
- 提供对话记录检索和链路追溯能力
- 支持多维度数据分析（按时间、用户、技能等）

### 1.2 技术约束

1. **零修改原则**: 不允许修改 OpenClaw 源码，保持上游兼容性
2. **NAS 日志访问**: Session Log 文件通过 NFS 挂载到应用容器内
3. **单服务架构**: 仅使用一个 Java SpringBoot 后端服务，无独立 Collector
4. **数据库**: OceanBase MySQL 兼容模式

### 1.3 设计目标

- ✅ 简化架构，降低运维复杂度
- ✅ 接受 1 小时数据延迟，换取实现简单性
- ✅ 健康状态缓存，查询性能优异
- ✅ 支持手动触发任务，便于调试和即时更新

---

## 2. 系统架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────┐
│         Java SpringBoot Backend Service              │
│                                                      │
│  ┌────────────────┐    ┌────────────────────────┐   │
│  │ 定时任务调度器  │    │   REST API Controller   │   │
│  │                │    │                         │   │
│  │ • Session扫描   │    │ • 运营大盘接口          │   │
│  │ • 健康检查轮询  │    │ • 对话检索接口          │   │
│  │ • 手动触发接口  │    │ • 管理接口              │   │
│  └───────┬────────┘    └───────────┬────────────┘   │
│          │                         │                 │
│  ┌───────▼─────────────────────────▼────────────┐   │
│  │        业务逻辑层 (Service Layer)             │   │
│  │                                              │   │
│  │ • Session Log 解析引擎 (流式 JSONL)           │   │
│  │ • Turn 聚合计算                              │   │
│  │ • Gateway 健康检查客户端                      │   │
│  │ • 数据统计与指标计算                          │   │
│  └──────────────┬───────────────────────────────┘   │
│                 │                                    │
└─────────────────┼────────────────────────────────────┘
                  │
     ┌────────────┼────────────┐
     │            │            │
  ┌──▼──┐   ┌────▼────┐  ┌───▼────┐
  │ NFS │   │OceanBase│  │ 其他   │
  │挂载 │   │ MySQL   │  │ 存储   │
  │     │   │         │  │        │
  │Sess-│   │• dashboard_      │
  │ion  │   │  openclaw_       │
  │Log  │   │  instances       │
  │文件 │   │• dashboard_      │
  │     │   │  session_turn    │
  │     │   │• dashboard_      │
  │     │   │  gateway_health_ │
  │     │   │  cache           │
  │     │   │• dashboard_      │
  │     │   │  session_        │
  │     │   │  processing_     │
  │     │   │  state           │
  └─────┘   └─────────┘  └────────┘
```

### 2.2 核心组件说明

#### 2.2.1 Session Log 扫描任务

**触发方式**：
- 定时触发：每 1 小时自动执行（Spring @Scheduled）
- 手动触发：`POST /api/v1/admin/trigger-task?type=session_scan`

**处理流程**：
1. 扫描 NFS 挂载目录下的所有 Session Log 文件（基于配置的路径模式）
2. **检测文件变化**：基于文件大小、修改时间判断是否需要处理
3. **检测 Compaction（双重检测机制）**：
   - **主要方式**：第一遍快速扫描，统计 JSONL 文件中 `type: "compaction"` 记录数量
   - 与数据库中存储的 `compaction_count` 对比，如果数量增加则判定为发生了新的 compaction
   - **备用方式**：检测文件大小缩小超过 10%（用于兜底，防止遗漏）
   - 如果检测到新的 compaction，触发全量重扫
4. **第二遍扫描**：流式逐行解析 JSONL 文件，提取消息元数据
5. **不解析 Token 统计**：Token、成本等数据通过 Gateway RPC `sessions.list` 获取
6. 聚合 Turn 结构（消息数、时间范围、Skill 使用等）到 session_turn 表
7. **清理无效数据**：如果检测到 compaction，删除该 session 未完成的 Turn 记录
8. 更新 last_message_id 和 compaction_count 记录

**Session 文件路径规则**：
- **NFS 挂载根目录**：通过配置项 `monitoring.session.scan.nfs-mount-path` 指定
- **路径模式**：`{nfs-mount-path}/instances/{instance_id}/agents/{agent_id}/sessions/*.jsonl`
- **文件名格式**：
  - 标准会话：`{sessionId}.jsonl`（例如：`abc-123-def.jsonl`）
  - Topic 会话：`{sessionId}-topic-{encodedTopic}.jsonl`
  - Fork 会话：`{timestamp}_{sessionId}.jsonl`
- **目录结构示例**：
  ```
  /mnt/session-logs/
  └── instances/
      ├── instance-001/
      │   └── agents/
      │       └── main/
      │           └── sessions/
      │               ├── abc-123.jsonl
      │               ├── def-456-topic-work.jsonl
      │               └── ...
      ├── instance-002/
      │   └── agents/
      │       └── main/
      │           └── sessions/
      │               └── ...
      └── ...
  ```
- **Instance 关联**：从路径中提取 `instance_id`，与 `openclaw_instances` 表关联
- **Agent 隔离**：支持多 agent 场景，每个 agent 有独立的 sessions 目录

**Compaction 机制说明**：

OpenClaw 的 Session Compaction 分为两个阶段：

1. **Compaction 阶段**：
   - 将历史对话压缩为摘要，减少 token 消耗
   - 在 JSONL 文件中添加一条 `type: "compaction"` 记录
   - **此时旧消息仍然保留**在文件中
   - Compaction 记录包含 `firstKeptEntryId` 字段，标记未压缩部分的起点
   - Compaction 记录示例：
     ```json
     {
       "type": "compaction",
       "id": "comp_abc123",
       "timestamp": "2026-04-15T10:30:00Z",
       "firstKeptEntryId": "msg_xyz789",
       "parentId": "msg_def456"
     }
     ```

2. **Truncation 阶段**（独立执行）：
   - 从 JSONL 文件中物理删除被压缩的旧消息（`firstKeptEntryId` 之前的消息）
   - 保留：Session header、Compaction 记录、未压缩的尾部消息
   - 文件大小显著减小（通常 >10%）

**增量策略**：
- **文件级别**：检测文件修改时间或大小变化
- **Compaction 检测（双重保障）**：
  - **主要方式**：统计 JSONL 文件中 `type: "compaction"` 记录数量，与数据库存储的 `compaction_count` 对比
  - **备用方式**：文件大小缩小超过 10% 时，判定为发生 truncation
  - **优势**：主要方式更可靠，即使 compaction 导致的文件缩小 <10% 也能检测到
  - **实现**：第一遍快速扫描只统计 compaction 记录，第二遍才处理消息内容
- **消息级别**：正常情况下记录 last_message_id，只处理新增消息
- **全量重扫**：检测到新的 compaction 后，重置 last_message_id，重新扫描整个文件
- **数据清理**：compaction 后删除 is_complete=0 的未完成 Turn 记录，避免数据不一致

#### 2.2.2 Gateway 健康检查轮询

**触发方式**：
- 定时触发：每 5 分钟自动执行
- 手动触发：`POST /api/v1/admin/trigger-task?type=health_check`

**处理流程**：
1. 从 `openclaw_instances` 表获取所有 status为running的instance 列表
2. **并行执行健康检查**：使用线程池（默认 50 个并发线程）同时检查多个实例
3. 对每个 instance，创建短生命周期 WebSocket 连接，调用 `health` 方法（不是 HTTP GET）
4. WebSocket 连接超时设置为 3 秒（可配置）
5. 解析 Health RPC 响应，提取关键指标更新到 `gateway_health_cache` 表
6. 等待所有检查完成（最多 60 秒），单个实例失败不影响其他
7. 对比 `openclaw_instances` 表，清理非 running 的 instance 缓存记录

**性能优化说明**：
- **并行执行**：采用线程池并行检查，1000 个实例可在 0.4-1.5 秒内完成
- **短连接模式**：每次检查创建新连接，无需维护长连接状态
- **资源可控**：通过 `parallelism` 配置控制并发度，默认 50
- **容错性好**：单个实例失败不影响其他实例的检查结果

**重要说明 - Health API 调用方式**：
- ❌ **错误方式**: HTTP GET `/health` （只返回 `{"ok":true,"status":"live"}`）
- ✅ **正确方式**: WebSocket RPC `health` 方法
- **调用示例**（TypeScript）:
  ```typescript
  const summary = await callGateway<HealthSummary>({
    method: "health",
    params: { probe: false },  // probe=true 会执行渠道探测，耗时更长
    timeoutMs: 3000,
    config: cfg,
  });
  ```
- **Java 实现**: 需要使用 WebSocket 客户端库（如 Java-WebSocket 或 Spring WebSocket）建立连接并发送 JSON-RPC 请求

**Health API 实际返回结构**（来自 OpenClaw 源码 `src/commands/health.ts`）：
```json
{
  "ok": true,
  "ts": 1713123456789,
  "durationMs": 245,
  "channels": {
    "discord": {
      "accountId": "default",
      "configured": true,
      "probe": {
        "ok": true,
        "elapsedMs": 120,
        "bot": { "username": "mybot" }
      }
    },
    "telegram": {
      "accountId": "default",
      "configured": false
    }
  },
  "channelOrder": ["discord", "telegram", "slack"],
  "channelLabels": {
    "discord": "Discord",
    "telegram": "Telegram"
  },
  "heartbeatSeconds": 30,
  "defaultAgentId": "main",
  "agents": [
    {
      "agentId": "main",
      "name": "Main Agent",
      "isDefault": true,
      "heartbeat": {
        "everyMs": 30000,
        "lastBeat": 1713123456000
      },
      "sessions": {
        "path": "/data/sessions/main.json",
        "count": 15,
        "recent": [...]
      }
    }
  ],
  "sessions": {
    "path": "/data/sessions/main.json",
    "count": 15,
    "recent": [
      {
        "key": "discord:user123",
        "updatedAt": 1713123400000,
        "age": 56789
      }
    ]
  }
}
```

**关键字段映射到缓存表**：
- `ok` → `status`: `true` = "online", `false` = "offline"
- `agents[0].heartbeat.lastBeat` → `last_heartbeat`: 最后心跳时间戳
- `ts` → `last_check_time`: 健康检查时间
- `agents[0].heartbeat.everyMs` → 可用于计算下次预期心跳
- **注意**: 实际返回中**没有** `version`, `nodeId`, `memory.rss` 等字段

**Token 统计数据获取**：
- **不从 Session Log 解析**：Token、成本等统计数据直接从 Gateway 获取
- **RPC 方法**: 通过 WebSocket 调用 `sessions.list` 方法
- **返回字段**:
  - `inputTokens`: 输入 Token 数
  - `outputTokens`: 输出 Token 数
  - `totalTokens`: 总 Token 数
  - `estimatedCostUsd`: 预估成本（美元）
  - `contextTokens`: 上下文 Token 数
  - `modelProvider`: 模型提供商
  - `model`: 模型 ID

**快速失败机制**：
- 连接超时：3 秒
- 读取超时：3 秒
- 失败后立即标记为 offline，不重试

#### 2.2.3 手动触发接口

**接口路径**：`POST /api/v1/admin/trigger-task`

**请求参数**：
```json
{
  "type": "session_scan" | "health_check"
}
```

**执行策略**：
- 任务互斥：如果同类型任务正在执行，返回提示信息
- 异步执行：立即返回"任务已提交"，不阻塞请求
- 无权限控制：内部使用，暂不需要认证

---

## 3. 数据库设计

### 3.1 表结构总览

| 表名 | 说明 | 主要用途 |
|------|------|----------|
| `openclaw_instances` | 用户实例表 | 存储每个用户的 Gateway 连接信息，已存在，仅用于查询数据，不可写入 |
| `dashboard_session_turn` | 会话对话单元表 | 聚合统计每个 Turn 的消息数和指标 |
| `dashboard_gateway_health_cache` | Gateway 健康状态缓存表 | 缓存所有 instance 的健康状态 |
| `dashboard_session_processing_state` | Session 处理状态表 | 记录每个文件的处理进度（last_message_id） |

### 3.2 详细表结构

#### 3.2.1 openclaw_instances（用户实例表）
**该表已在库中存在，仅用于查询数据，不可写入**

**认证机制说明**:
OpenClaw Gateway 需要认证才能调用 RPC 方法。监控系统从该表获取认证信息：

1. **优先使用 `access_url`**: 该字段已包含完整的认证参数
   ```sql
   SELECT access_url FROM openclaw_instances WHERE id = 113;
   -- 结果: http://host:port?token=1c1d23658e3ab1fe...
   ```

2. **备用方案**: 使用 `base_url` + `encrypted_token` 构造 URL
   ```java
   String wsUrl = baseUrl.replace("/v1", "") + "?token=" + encryptedToken;
   ```

3. **URL 转换规则**: HTTP → WebSocket
   - `http://` → `ws://`
   - `https://` → `wss://`

**示例数据**: 参见 [openclaw-instances_sample-data.txt](./openclaw-instances_sample-data.txt)

```sql
CREATE TABLE IF NOT EXISTS openclaw_instances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    uid VARCHAR(64) NOT NULL COMMENT '用户ID',
    instance_name VARCHAR(128) DEFAULT '' NOT NULL COMMENT '实例名称',
    namespace VARCHAR(64) DEFAULT 'default' COMMENT 'K8s命名空间',
    base_url VARCHAR(512) COMMENT 'API基础URL',
    node_port INT COMMENT '分配的NodePort端口',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    access_url VARCHAR(512) COMMENT '访问地址',
    encrypted_token VARCHAR(256) COMMENT '加密的认证token',
    status VARCHAR(32) DEFAULT 'creating' COMMENT '状态: creating, running, error, deleting, deleted',
    current_step VARCHAR(64) COMMENT '当前步骤',
    progress INT DEFAULT 0 COMMENT '进度百分比 0-100',
    pod_name VARCHAR(128) COMMENT '对应的Pod名称',
    deployment_ready BOOLEAN DEFAULT FALSE COMMENT 'Deployment是否就绪',
    service_ready BOOLEAN DEFAULT FALSE COMMENT 'Service是否就绪',
    pod_phase VARCHAR(32) COMMENT 'Pod状态Phase',
    pod_conditions JSON COMMENT 'Pod详细条件',
    config_json JSON COMMENT 'ConfigMap配置内容',
    deployment_json JSON COMMENT 'Deployment配置内容',
    service_json JSON COMMENT 'Service配置内容',
    user_config_json JSON COMMENT '用户配置信息',
    pod_json JSON COMMENT 'Pod配置内容（预留字段）',
    estimated_ready_time TIMESTAMP NULL COMMENT '预估就绪时间',
    last_event_time TIMESTAMP NULL COMMENT '最后事件时间',
    last_status_check TIMESTAMP NULL COMMENT '最后状态检查时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '逻辑删除时间',
    
    INDEX idx_uid (uid),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted_at (deleted_at),
    UNIQUE KEY uk_instance_name_namespace (instance_name, namespace)
) COMMENT='OpenClaw实例管理表';
```



#### 3.2.2 dashboard_session_turn（会话对话单元表）

```sql
CREATE TABLE `dashboard_session_turn` (
  `turn_id` VARCHAR(128) NOT NULL COMMENT 'Turn 唯一标识（session_id + first_message_id）',
  `session_id` VARCHAR(128) NOT NULL COMMENT '会话 ID',
  `instance_id` VARCHAR(128) NOT NULL COMMENT '实例 ID',
  `user_id` VARCHAR(128) DEFAULT NULL COMMENT '用户 ID',
  `channel` VARCHAR(64) DEFAULT NULL COMMENT '渠道类型',
  `first_message_id` VARCHAR(128) NOT NULL COMMENT 'Turn 第一条消息 ID',
  `last_message_id` VARCHAR(128) NOT NULL COMMENT 'Turn 最后一条消息 ID',
  `start_time` DATETIME NOT NULL COMMENT 'Turn 开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT 'Turn 结束时间',
  `is_complete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否完成: 0-进行中, 1-已完成',
  `message_count` INT NOT NULL DEFAULT 0 COMMENT '消息总数',
  `user_message_count` INT NOT NULL DEFAULT 0 COMMENT '用户消息数',
  `assistant_message_count` INT NOT NULL DEFAULT 0 COMMENT 'Assistant 消息数',
  `tool_call_count` INT NOT NULL DEFAULT 0 COMMENT '工具调用次数',
  `total_duration_ms` BIGINT DEFAULT 0 COMMENT '总耗时（毫秒）',
  `ai_duration_ms` BIGINT DEFAULT 0 COMMENT 'AI 响应总耗时（毫秒）',
  `input_tokens` INT DEFAULT 0 COMMENT '输入 Token 数（从 Gateway sessions.list 获取）',
  `output_tokens` INT DEFAULT 0 COMMENT '输出 Token 数（从 Gateway sessions.list 获取）',
  `total_tokens` INT DEFAULT 0 COMMENT '总 Token 数（从 Gateway sessions.list 获取）',
  `estimated_cost_cents` INT DEFAULT 0 COMMENT '预估成本（美分，从 Gateway sessions.list 获取）',
  `skill_ids` TEXT COMMENT '使用的 Skill ID 列表（JSON 数组）',
  `model_ids` TEXT COMMENT '使用的模型 ID 列表（JSON 数组，从 Gateway sessions.list 获取）',
  `user_input_preview` VARCHAR(500) DEFAULT NULL COMMENT '用户输入预览（前500字符）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`turn_id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_instance_id` (`instance_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_is_complete` (`is_complete`),
  KEY `idx_channel` (`channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话对话单元统计表';
```

**字段说明**：
- `turn_id`: 由 `session_id + first_message_id` 组合生成，保证唯一性
- `is_complete`: 0 表示 Turn 还在进行中（等待下一条 user 消息），1 表示已完成
- `skill_ids`: JSON 数组格式，如 `["official-doc-writer", "pptx"]`
- `user_input_preview`: 截取第一条 user 消息的前 500 字符作为预览

**Turn 定义**：
- 一个 Turn 从一条 user 消息开始，到下一条 user 消息之前结束
- 包含中间所有的 assistant 消息、tool 调用等
- 最后一个 Turn 可能未完成（is_complete=0），直到收到新的 user 消息

#### 3.2.3 dashboard_gateway_health_cache（Gateway 健康状态缓存表）

```sql
CREATE TABLE `dashboard_gateway_health_cache` (
  `instance_id` VARCHAR(128) NOT NULL COMMENT '实例 ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'offline' COMMENT '状态: online/offline',
  `last_heartbeat` DATETIME DEFAULT NULL COMMENT '最后心跳时间（从 agents[0].heartbeat.lastBeat 获取）',
  `last_check_time` DATETIME NOT NULL COMMENT '最后检查时间',
  `version` VARCHAR(64) DEFAULT NULL COMMENT 'OpenClaw 版本（Health API 不返回此字段，保留为 NULL）',
  `channels_total` INT DEFAULT 0 COMMENT '渠道总数（Health API 不返回此字段，保留为 0）',
  `agents_total` INT DEFAULT 0 COMMENT 'Agent 总数（Health API 不返回此字段，保留为 0）',
  `memory_rss_mb` INT DEFAULT 0 COMMENT '内存占用 MB（Health API 不返回此字段，保留为 0）',
  `error_message` VARCHAR(512) DEFAULT NULL COMMENT '错误信息',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`instance_id`),
  KEY `idx_status` (`status`),
  KEY `idx_last_check_time` (`last_check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Gateway 健康状态缓存表';
```

**字段说明**：
- `status`: online 表示 Gateway 可达，offline 表示不可达
- `last_heartbeat`: 从 Health API 响应中获取的最后心跳时间
- `last_check_time`: 本地最后一次健康检查的时间
- `version`, `channels_total`, `agents_total`, `memory_rss_mb`: 从 Health API 响应中提取的详细指标
- `error_message`: 如果检查失败，记录错误原因

#### 3.2.4 dashboard_session_processing_state（Session 处理状态表）

```sql
CREATE TABLE `dashboard_session_processing_state` (
  `file_path_hash` VARCHAR(64) NOT NULL COMMENT '文件路径的 SHA256 Hash',
  `file_path` VARCHAR(1024) NOT NULL COMMENT '文件完整路径',
  `last_message_id` VARCHAR(128) DEFAULT NULL COMMENT '最后处理的消息 ID',
  `last_processed_time` DATETIME DEFAULT NULL COMMENT '最后处理时间',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
  `file_modified_time` DATETIME DEFAULT NULL COMMENT '文件修改时间',
  `processed_count` BIGINT DEFAULT 0 COMMENT '累计处理的消息数',
  `compaction_count` INT DEFAULT 0 COMMENT 'Compaction 记录数量，用于检测新的 compaction',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`file_path_hash`),
  KEY `idx_file_path` (`file_path`(255)),
  KEY `idx_last_processed_time` (`last_processed_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Session 文件处理状态表';
```

**字段说明**：
- `file_path_hash`: 文件路径的 SHA256 Hash，作为主键避免长字符串索引
- `last_message_id`: 记录该文件最后处理到的消息 ID，用于增量处理
- `file_size`, `file_modified_time`: 用于快速判断文件是否有变化
- `compaction_count`: 记录该文件中 compaction 记录的数量，用于检测新的 compaction 事件（主要检测方式）

---

## 4. API 接口定义

### 4.1 接口总览

| 模块 | 接口路径 | 方法 | 说明 |
|------|----------|------|------|
| **下拉列表** | `/api/v1/skills/options` | GET | 获取技能下拉列表 |
| **运营大盘** | `/api/v1/global-stats` | GET | 全局统计数据 |
| | `/api/v1/summary` | GET | 汇总统计（支持多维度） |
| | `/api/v1/trend` | GET | 趋势数据 |
| | `/api/v1/usersummary` | GET | 用户维度统计 |
| **对话检索** | `/api/v1/turns/search` | POST | 搜索对话记录 |
| | `/api/v1/turns/{turnId}/trace` | GET | 获取对话链路详情 |
| **管理接口** | `/api/v1/admin/trigger-task` | POST | 手动触发定时任务 |

### 4.2 详细接口定义

#### 4.2.1 获取技能下拉列表

**接口**: `GET /api/v1/skills/options`

**说明**: 获取技能下拉列表数据字典

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "skillId": "official-doc-writer", "skillName": "公文写作" },
    { "skillId": "pptx", "skillName": "PPT生成" },
    { "skillId": "code-review", "skillName": "代码审查" }
  ]
}
```

---

#### 4.2.2 全局统计数据

**接口**: `GET /api/v1/global-stats`

**说明**: 获取全局运营统计数据

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dateFrom | String | 是 | 开始日期，格式 YYYY-MM-DD |
| dateTo | String | 是 | 结束日期，格式 YYYY-MM-DD |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 1250,
    "totalSessions": 8500,
    "totalTurns": 45000,
    "totalMessages": 180000,
    "totalTokens": 95000000,
    "totalCostCents": 125000,
    "avgTurnsPerSession": 5.29,
    "avgMessagesPerTurn": 4.0,
    "onlineInstances": 980,
    "offlineInstances": 270
  }
}
```

---

#### 4.2.3 汇总统计

**接口**: `GET /api/v1/summary`

**说明**: 获取多维度汇总统计数据

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dateFrom | String | 是 | 开始日期 |
| dateTo | String | 是 | 结束日期 |
| groupBy | String | 否 | 分组维度：skill/channel/user/model，默认 skill |

**响应**（以 skill 分组为例）:
``json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "groupKey": "official-doc-writer",
      "groupName": "公文写作",
      "turnCount": 12000,
      "messageCount": 48000,
      "tokenCount": 25000000,
      "costCents": 35000,
      "avgDurationMs": 3500
    },
    {
      "groupKey": "pptx",
      "groupName": "PPT生成",
      "turnCount": 8000,
      "messageCount": 32000,
      "tokenCount": 18000000,
      "costCents": 28000,
      "avgDurationMs": 5200
    }
  ]
}
```

---

#### 4.2.4 趋势数据

**接口**: `GET /api/v1/trend`

**说明**: 获取时间序列趋势数据

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dateFrom | String | 是 | 开始日期 |
| dateTo | String | 是 | 结束日期 |
| granularity | String | 否 | 粒度：day/hour，默认 day |
| metric | String | 否 | 指标：turns/messages/tokens/cost，默认 turns |

**响应**（按天统计 turns）:
``json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "timePoint": "2026-04-01",
      "value": 1500
    },
    {
      "timePoint": "2026-04-02",
      "value": 1680
    },
    {
      "timePoint": "2026-04-03",
      "value": 1420
    }
  ]
}
```

---

#### 4.2.5 用户维度统计

**接口**: `GET /api/v1/usersummary`

**说明**: 获取用户维度的统计数据

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dateFrom | String | 是 | 开始日期 |
| dateTo | String | 是 | 结束日期 |
| page | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页数量，默认 20 |
| sortBy | String | 否 | 排序字段：turns/messages/tokens/cost，默认 turns |
| sortOrder | String | 否 | 排序方向：asc/desc，默认 desc |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1250,
    "page": 1,
    "pageSize": 20,
    "items": [
      {
        "userId": "user_001",
        "instanceId": "inst_abc123",
        "turnCount": 450,
        "messageCount": 1800,
        "tokenCount": 950000,
        "costCents": 1250,
        "lastActiveTime": "2026-04-14T15:30:00Z",
        "gatewayStatus": "online"
      }
    ]
  }
}
```

---

#### 4.2.6 搜索对话记录

**接口**: `POST /api/v1/turns/search`

**说明**: 根据条件搜索对话记录

**请求体**:
```json
{
  "dateFrom": "2026-04-01",
  "dateTo": "2026-04-14",
  "userId": "user_001",
  "instanceId": "inst_abc123",
  "skillId": "official-doc-writer",
  "channel": "discord",
  "keyword": "公文",
  "page": 1,
  "pageSize": 20,
  "sortBy": "start_time",
  "sortOrder": "desc"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 156,
    "page": 1,
    "pageSize": 20,
    "items": [
      {
        "turnId": "sess_xyz_first_msg_001",
        "sessionId": "sess_xyz",
        "userId": "user_001",
        "instanceId": "inst_abc123",
        "channel": "discord",
        "startTime": "2026-04-14T10:30:00Z",
        "endTime": "2026-04-14T10:35:00Z",
        "isComplete": true,
        "messageCount": 8,
        "userMessageCount": 2,
        "assistantMessageCount": 6,
        "totalDurationMs": 4500,
        "aiDurationMs": 3200,
        "totalTokens": 5600,
        "estimatedCostCents": 8,
        "skillIds": ["official-doc-writer"],
        "modelIds": ["sonnet-4.6"],
        "userInputPreview": "帮我写一份关于..."
      }
    ]
  }
}
```

---

#### 4.2.7 获取对话链路详情

**接口**: `GET /api/v1/turns/{turnId}/trace`

**说明**: 获取指定 Turn 的完整消息链路

**路径参数**:
- `turnId`: Turn 的唯一标识

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "turnId": "sess_xyz_first_msg_001",
    "sessionId": "sess_xyz",
    "messages": [
      {
        "messageId": "msg_001",
        "role": "user",
        "content": "帮我写一份关于...",
        "timestamp": "2026-04-14T10:30:00Z",
        "durationMs": null,
        "tokens": null,
        "costCents": null,
        "skillId": null,
        "modelId": null,
        "toolCalls": []
      },
      {
        "messageId": "msg_002",
        "role": "assistant",
        "content": "好的，我来帮您...",
        "timestamp": "2026-04-14T10:30:05Z",
        "durationMs": 3200,
        "tokens": {
          "input": 1200,
          "output": 800,
          "total": 2000
        },
        "costCents": 3,
        "skillId": "official-doc-writer",
        "modelId": "sonnet-4.6",
        "toolCalls": []
      },
      {
        "messageId": "msg_003",
        "role": "assistant",
        "content": "",
        "timestamp": "2026-04-14T10:30:08Z",
        "durationMs": 1500,
        "tokens": {
          "input": 2100,
          "output": 150,
          "total": 2250
        },
        "costCents": 2,
        "skillId": null,
        "modelId": "sonnet-4.6",
        "toolCalls": [
          {
            "toolName": "search_web",
            "arguments": "{\"query\": \"公文写作规范\"}",
            "result": "..."
          }
        ]
      }
    ],
    "statistics": {
      "messageCount": 8,
      "userMessageCount": 2,
      "assistantMessageCount": 6,
      "toolCallCount": 3,
      "totalDurationMs": 4500,
      "aiDurationMs": 3200,
      "totalTokens": 5600,
      "estimatedCostCents": 8
    }
  }
}
```

---

#### 4.2.8 手动触发定时任务

**接口**: `POST /api/v1/admin/trigger-task`

**说明**: 手动触发 Session 扫描或健康检查任务

**请求体**:
```json
{
  "type": "session_scan"
}
```

**type 可选值**:
- `session_scan`: 触发 Session Log 扫描任务
- `health_check`: 触发 Gateway 健康检查任务

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "task_20260415_001",
    "type": "session_scan",
    "status": "submitted",
    "message": "任务已提交"
  }
}
```

**任务执行中时的响应**:
``json
{
  "code": 409,
  "message": "conflict",
  "data": {
    "taskId": "task_20260415_001",
    "type": "session_scan",
    "status": "running",
    "message": "同类型任务正在执行中，请稍后重试"
  }
}
```

---

## 5. 核心代码示例

### 5.1 Session Log 扫描任务

```java
@Component
@Slf4j
public class SessionScanTask {
    
    @Autowired
    private SessionProcessingStateRepository stateRepository;
    
    @Autowired
    private SessionTurnRepository turnRepository;
    
    @Value("${monitoring.session.scan.cron:0 0 * * * ?}")
    private String scanCron;
    
    @Value("${monitoring.session.scan.nfs-mount-path:/mnt/session-logs}")
    private String nfsMountPath;
    
    @Value("${monitoring.session.scan.path-pattern:instances/*/agents/*/sessions/*.jsonl}")
    private String pathPattern;
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    /**
     * 定时执行 Session 扫描
     */
    @Scheduled(cron = "${monitoring.session.scan.cron:0 0 * * * ?}")
    public void scheduledScan() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("Session 扫描任务已在执行中，跳过本次调度");
            return;
        }
        
        try {
            log.info("开始执行 Session Log 扫描任务");
            executeScan();
            log.info("Session Log 扫描任务执行完成");
        } catch (Exception e) {
            log.error("Session Log 扫描任务执行失败", e);
        } finally {
            isRunning.set(false);
        }
    }
    
    /**
     * 手动触发扫描
     */
    public TaskResult triggerManualScan() {
        if (!isRunning.compareAndSet(false, true)) {
            return TaskResult.conflict("session_scan", "同类型任务正在执行中");
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                executeScan();
            } finally {
                isRunning.set(false);
            }
        });
        
        return TaskResult.submitted("session_scan");
    }
    
    private void executeScan() {
        // 1. 获取所有需要处理的 Session Log 文件
        List<File> sessionFiles = findSessionLogFiles();
        
        log.info("找到 {} 个 Session Log 文件，开始处理", sessionFiles.size());
        
        for (File file : sessionFiles) {
            try {
                processSessionFile(file);
            } catch (Exception e) {
                log.error("处理文件失败: {}", file.getAbsolutePath(), e);
            }
        }
    }
    
    /**
     * 扫描 NFS 挂载目录下的所有 Session Log 文件
     */
    private List<File> findSessionLogFiles() {
        List<File> files = new ArrayList<>();
        Path basePath = Paths.get(nfsMountPath);
        
        if (!Files.exists(basePath)) {
            log.warn("NFS 挂载路径不存在: {}", nfsMountPath);
            return files;
        }
        
        try {
            // 使用 glob 模式匹配文件
            String globPattern = basePath.resolve(pathPattern).toString();
            
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                    basePath, 
                    path -> matchPattern(path, basePath, pathPattern))) {
                
                for (Path path : stream) {
                    if (Files.isRegularFile(path) && path.toString().endsWith(".jsonl")) {
                        files.add(path.toFile());
                    }
                }
            }
            
            log.debug("扫描到 {} 个 Session Log 文件", files.size());
            
        } catch (IOException e) {
            log.error("扫描 Session Log 文件失败", e);
        }
        
        return files;
    }
    
    /**
     * 匹配路径模式（简化版 glob）
     * 支持：instances/*/agents/*/sessions/*.jsonl
     */
    private boolean matchPattern(Path path, Path basePath, String pattern) {
        String relativePath = basePath.relativize(path).toString();
        
        // 检查是否符合预期模式：instances/{instanceId}/agents/{agentId}/sessions/{fileName}.jsonl
        String[] parts = relativePath.split("/");
        
        if (parts.length != 5) {
            return false;
        }
        
        return "instances".equals(parts[0]) 
            && "agents".equals(parts[2]) 
            && "sessions".equals(parts[3])
            && parts[4].endsWith(".jsonl");
    }
    
    /**
     * 从文件路径中提取 instance_id
     */
    private String extractInstanceIdFromPath(String filePath) {
        // 例如：/mnt/session-logs/instances/instance-001/agents/main/sessions/abc.jsonl
        String[] parts = filePath.split("/");
        
        for (int i = 0; i < parts.length; i++) {
            if ("instances".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        
        return null;
    }
    
    private void processSessionFile(File file) throws IOException {
        String filePath = file.getAbsolutePath();
        String fileHash = DigestUtils.sha256Hex(filePath);
        
        // 2. 检查文件是否有变化
        SessionProcessingState state = stateRepository.findByFilePathHash(fileHash);
        if (state != null && !hasFileChanged(file, state)) {
            log.debug("文件未变化，跳过: {}", filePath);
            return;
        }
        
        // 3. 第一遍扫描：统计 compaction 记录数量（主要检测方式）
        int currentCompactionCount = countCompactionEntries(file);
        int storedCompactionCount = state != null ? state.getCompactionCount() : 0;
        
        // 4. 检测是否有新的 compaction
        boolean hasNewCompaction = currentCompactionCount > storedCompactionCount;
        
        // 5. 备用检测：文件大小缩小超过 10%（用于兜底）
        if (!hasNewCompaction && state != null) {
            long currentSize = file.length();
            long previousSize = state.getFileSize();
            if (previousSize > 0 && currentSize < previousSize * 0.9) {
                log.warn("检测到文件大小显著缩小: {}, 之前={} bytes, 当前={} bytes, 缩小={:.1f}%",
                    filePath, previousSize, currentSize, 
                    (1 - (double)currentSize / previousSize) * 100);
                hasNewCompaction = true;
            }
        }
        
        // 6. 如果检测到新的 compaction，触发全量重扫
        String lastMessageId = state != null ? state.getLastMessageId() : null;
        if (hasNewCompaction) {
            log.info("检测到新的 compaction: {}, 旧计数={}, 新计数={}", 
                filePath, storedCompactionCount, currentCompactionCount);
            
            // 重置 last_message_id，触发全量重扫
            lastMessageId = null;
            
            // 清理该 session 的未完成 Turn 记录
            String sessionId = extractSessionIdFromPath(filePath);
            if (sessionId != null) {
                cleanupIncompleteTurns(sessionId);
            }
        }
        
        // 7. 第二遍扫描：流式处理 + 分批写入数据库
        TurnAggregate lastTurn = streamProcessAndBatchWrite(file, lastMessageId);
        
        // 8. 更新处理状态（包括 compaction_count）
        if (lastTurn != null) {
            updateProcessingState(fileHash, filePath, file, lastTurn.getLastMessageId(), currentCompactionCount);
        }
    }
    
    private boolean hasFileChanged(File file, SessionProcessingState state) {
        long currentSize = file.length();
        long currentTime = file.lastModified();
        
        return currentSize != state.getFileSize() || 
               currentTime != state.getFileModifiedTime().getTime();
    }
    
    /**
     * 流式处理 JSONL 文件，分批写入数据库
     * 内存中只保留当前正在构建的 Turn 和一个小的写入批次
     * 
     * @return 最后处理的 Turn，用于更新处理状态
     */
    private TurnAggregate streamProcessAndBatchWrite(File file, String lastMessageId) throws IOException {
        List<TurnAggregate> batch = new ArrayList<>();
        int batchSize = 100; // 每 100 条 Turn 写入一次
        TurnAggregate lastTurn = null;
        TurnBuilder builder = new TurnBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    JsonNode message = objectMapper.readTree(line);
                    String messageId = message.get("id").asText();
                    
                    // 跳过已处理的消息
                    if (lastMessageId != null && messageId.compareTo(lastMessageId) <= 0) {
                        continue;
                    }
                    
                    // 构建 Turn
                    builder.addMessage(message);
                    
                    // 如果遇到新的 user 消息，完成当前 Turn
                    if ("user".equals(message.get("role").asText()) && builder.hasCurrentTurn()) {
                        TurnAggregate completedTurn = builder.completeCurrentTurn();
                        if (completedTurn != null) {
                            batch.add(completedTurn);
                            lastTurn = completedTurn;
                            
                            // 达到批次大小，写入数据库
                            if (batch.size() >= batchSize) {
                                turnRepository.batchUpsert(batch);
                                batch.clear();
                            }
                        }
                    }
                } catch (Exception e) {
                    // 忽略解析错误的行
                    log.debug("解析行失败: {}", line, e);
                }
            }
        }
        
        // 写入剩余的 Turn
        if (!batch.isEmpty()) {
            turnRepository.batchUpsert(batch);
        }
        
        return lastTurn;
    }
    
    /**
     * 第一遍快速扫描：统计 compaction 记录数量
     */
    private int countCompactionEntries(File file) throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    JsonNode entry = objectMapper.readTree(line);
                    if ("compaction".equals(entry.path("type").asText())) {
                        count++;
                    }
                } catch (Exception e) {
                    // 忽略解析错误的行
                    log.debug("解析行失败: {}", line, e);
                }
            }
        }
        return count;
    }
    
    /**
     * 从文件路径中提取 session_id
     * 例如：/mnt/session-logs/instances/instance-001/agents/main/sessions/abc-123.jsonl
     * 返回：abc-123
     */
    private String extractSessionIdFromPath(String filePath) {
        String fileName = new File(filePath).getName();
        // 移除 .jsonl 后缀
        if (fileName.endsWith(".jsonl")) {
            return fileName.substring(0, fileName.length() - 6);
        }
        return fileName;
    }
    
    /**
     * 清理指定 session 的未完成 Turn 记录
     * Compaction 后，未完成的 Turn 可能引用了已被删除的消息，需要清理
     */
    private void cleanupIncompleteTurns(String sessionId) {
        int deleted = turnRepository.deleteBySessionIdAndIsComplete(sessionId, false);
        if (deleted > 0) {
            log.info("清理 session {} 的未完成 Turn 记录: {} 条", sessionId, deleted);
        }
    }
    
    /**
     * 更新处理状态（包括 compaction_count）
     */
    private void updateProcessingState(String fileHash, String filePath, File file, 
                                       String lastMessageId, int compactionCount) {
        SessionProcessingState state = stateRepository.findByFilePathHash(fileHash);
        if (state == null) {
            state = new SessionProcessingState();
            state.setFilePathHash(fileHash);
            state.setFilePath(filePath);
        }
        
        state.setLastMessageId(lastMessageId);
        state.setLastProcessedTime(LocalDateTime.now());
        state.setFileSize(file.length());
        state.setFileModifiedTime(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(file.lastModified()), 
            ZoneId.systemDefault()
        ));
        state.setCompactionCount(compactionCount);
        
        stateRepository.save(state);
    }
}
```

### 5.2 Gateway 健康检查任务

```java
@Component
@Slf4j
public class HealthCheckTask {
    
    @Autowired
    private OpenclawInstanceRepository instanceRepository;
    
    @Autowired
    private GatewayHealthCacheRepository healthCacheRepository;
    
    @Value("${monitoring.health.check.timeout:3000}")
    private int timeoutMs;
    
    @Value("${monitoring.health.check.parallelism:50}")
    private int parallelism;
    
    private final ExecutorService executor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    public HealthCheckTask() {
        this.executor = Executors.newFixedThreadPool(parallelism, r -> {
            Thread t = new Thread(r, "health-check-worker");
            t.setDaemon(true);
            return t;
        });
    }
    
    /**
     * 定时执行健康检查
     */
    @Scheduled(fixedDelay = 300000) // 5分钟
    public void scheduledHealthCheck() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("健康检查任务已在执行中，跳过本次调度");
            return;
        }
        
        try {
            log.info("开始执行 Gateway 健康检查任务");
            executeHealthCheck();
            log.info("Gateway 健康检查任务执行完成");
        } catch (Exception e) {
            log.error("Gateway 健康检查任务执行失败", e);
        } finally {
            isRunning.set(false);
        }
    }
    
    /**
     * 手动触发健康检查
     */
    public TaskResult triggerManualHealthCheck() {
        if (!isRunning.compareAndSet(false, true)) {
            return TaskResult.conflict("health_check", "同类型任务正在执行中");
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                executeHealthCheck();
            } finally {
                isRunning.set(false);
            }
        });
        
        return TaskResult.submitted("health_check");
    }
    
    private void executeHealthCheck() {
        // 1. 获取所有 running 状态的 instance
        List<OpenclawInstance> instances = instanceRepository.findByStatus("running");
        
        if (instances.isEmpty()) {
            log.info("没有运行中的实例，跳过健康检查");
            return;
        }
        
        Set<String> existingInstanceIds = instances.stream()
            .map(OpenclawInstance::getInstanceId)
            .collect(Collectors.toSet());
        
        // 2. 并行执行健康检查
        List<CompletableFuture<Void>> futures = instances.stream()
            .map(instance -> CompletableFuture.runAsync(() -> {
                try {
                    checkInstanceHealth(instance);
                } catch (Exception e) {
                    log.error("检查实例健康状态失败: {}", instance.getInstanceId(), e);
                    saveOfflineStatus(instance.getInstanceId(), e.getMessage());
                }
            }, executor))
            .collect(Collectors.toList());
        
        // 3. 等待所有检查完成（最多 60 秒）
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("健康检查超时，部分实例可能未完成检查");
            futures.forEach(f -> f.cancel(true));
        } catch (Exception e) {
            log.error("等待健康检查完成时出错", e);
        }
        
        // 4. 清理已删除的 instance 缓存
        cleanupDeletedInstances(existingInstanceIds);
    }
    
    private void checkInstanceHealth(OpenclawInstance instance) {
        GatewayHealthCache cache = new GatewayHealthCache();
        cache.setInstanceId(instance.getInstanceId());
        cache.setLastCheckTime(LocalDateTime.now());
        
        try {
            // 通过 WebSocket RPC 调用 health 方法
            JsonNode healthData = callGatewayHealthRpc(
                instance.getGatewayUrl(),
                instance.getAuthToken(),
                3000  // 超时 3 秒
            );
            
            if (healthData != null) {
                // 解析 ok 字段判断在线状态
                boolean isOnline = healthData.path("ok").asBoolean(false);
                cache.setStatus(isOnline ? "online" : "offline");
                
                // 解析最后心跳时间：agents[0].heartbeat.lastBeat
                JsonNode agents = healthData.path("agents");
                if (agents.isArray() && agents.size() > 0) {
                    JsonNode firstAgent = agents.get(0);
                    JsonNode heartbeat = firstAgent.path("heartbeat");
                    long lastBeatMs = heartbeat.path("lastBeat").asLong(0);
                    if (lastBeatMs > 0) {
                        cache.setLastHeartbeat(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(lastBeatMs), 
                                ZoneId.systemDefault()
                            )
                        );
                    }
                }
                
                // 没有 version, nodeId, memory 等字段，这些字段设为 null
                cache.setVersion(null);
                cache.setChannelsTotal(0);
                cache.setAgentsTotal(0);
                cache.setMemoryRssMb(0);
                cache.setErrorMessage(null);
                
                log.debug("健康检查成功: {} - status={}", 
                    instance.getInstanceId(), cache.getStatus());
            } else {
                cache.setStatus("offline");
                cache.setErrorMessage("WebSocket RPC 调用失败");
                log.warn("健康检查失败: {} - RPC 返回 null", 
                    instance.getInstanceId());
            }
        } catch (Exception e) {
            cache.setStatus("offline");
            cache.setErrorMessage(e.getMessage());
            log.error("健康检查异常: {}", instance.getInstanceId(), e);
        }
        
        healthCacheRepository.save(cache);
    }
    
    /**
     * 通过 WebSocket RPC 调用 Gateway health 方法
     * 
     * @param gatewayUrl Gateway URL，如 ws://localhost:18789
     * @param authToken 认证 Token
     * @param timeoutMs 超时时间（毫秒）
     * @return HealthSummary JSON 数据，失败返回 null
     */
    private JsonNode callGatewayHealthRpc(String gatewayUrl, String authToken, int timeoutMs) {
        // TODO: 实现 WebSocket RPC 调用
        // 需要使用 WebSocket 客户端库，例如:
        // 1. Java-WebSocket: https://github.com/TooTallNate/Java-WebSocket
        // 2. Spring WebSocket: org.springframework.web.socket
        // 3. Tyrus: org.glassfish.tyrus
        
        // 伪代码示例：
        // 1. 建立 WebSocket 连接到 gatewayUrl
        // 2. 发送 JSON-RPC 请求:
        //    {
        //      "jsonrpc": "2.0",
        //      "id": 1,
        //      "method": "health",
        //      "params": { "probe": false }
        //    }
        // 3. 等待响应（超时 timeoutMs）
        // 4. 解析响应:
        //    {
        //      "jsonrpc": "2.0",
        //      "id": 1,
        //      "result": { ...HealthSummary... }
        //    }
        // 5. 返回 result 字段
        
        throw new UnsupportedOperationException("需要实现 WebSocket RPC 客户端");
    }
    
    /**
     * 清理已删除或不活跃的 instance 缓存
     */
    private void cleanupDeletedInstances(Set<String> existingInstanceIds) {
        List<GatewayHealthCache> allCaches = healthCacheRepository.findAll();
        
        for (GatewayHealthCache cache : allCaches) {
            if (!existingInstanceIds.contains(cache.getInstanceId())) {
                healthCacheRepository.deleteByInstanceId(cache.getInstanceId());
                log.info("清理已删除实例的健康缓存: {}", cache.getInstanceId());
            }
        }
    }
    
    /**
     * 保存离线状态（用于并行检查失败时）
     */
    private void saveOfflineStatus(String instanceId, String errorMessage) {
        GatewayHealthCache cache = new GatewayHealthCache();
        cache.setInstanceId(instanceId);
        cache.setStatus("offline");
        cache.setErrorMessage(errorMessage);
        cache.setLastCheckTime(LocalDateTime.now());
        healthCacheRepository.save(cache);
    }
    
    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### 5.3 管理接口 Controller

```java
@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
public class AdminController {
    
    @Autowired
    private SessionScanTask sessionScanTask;
    
    @Autowired
    private HealthCheckTask healthCheckTask;
    
    @PostMapping("/trigger-task")
    public ResponseEntity<TaskResponse> triggerTask(@RequestBody TriggerTaskRequest request) {
        String type = request.getType();
        
        TaskResult result;
        if ("session_scan".equals(type)) {
            result = sessionScanTask.triggerManualScan();
        } else if ("health_check".equals(type)) {
            result = healthCheckTask.triggerManualHealthCheck();
        } else {
            return ResponseEntity.badRequest()
                .body(TaskResponse.error("不支持的任务类型: " + type));
        }
        
        if (result.isConflict()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(TaskResponse.conflict(result));
        }
        
        return ResponseEntity.ok(TaskResponse.success(result));
    }
}
```

### 5.4 Gateway Token 统计数据获取

**说明**: Token、成本等统计数据不从 Session Log 解析，而是通过 Gateway RPC 接口获取。

#### 5.4.1 RPC 方法

**方法名**: `sessions.list`

**请求参数**:
```json
{
  "agentId": "main",           // Agent ID，默认 "main"
  "includeGlobal": false,      // 是否包含全局会话
  "includeUnknown": false,     // 是否包含未知会话
  "limit": 100                 // 限制返回数量（可选）
}
```

**响应格式**:
```json
{
  "ts": 1713123456789,
  "path": "/data/sessions/main.json",
  "count": 15,
  "defaults": { ... },
  "sessions": [
    {
      "key": "discord:user123",
      "sessionId": "sess_abc",
      "updatedAt": 1713123400000,
      "inputTokens": 1200,
      "outputTokens": 800,
      "totalTokens": 2000,
      "estimatedCostUsd": 0.035,
      "contextTokens": 1500,
      "modelProvider": "anthropic",
      "model": "sonnet-4.6",
      ...
    }
  ]
}
```

**关键字段**:
- `inputTokens`: 输入 Token 数
- `outputTokens`: 输出 Token 数
- `totalTokens`: 总 Token 数
- `estimatedCostUsd`: 预估成本（美元）
- `contextTokens`: 上下文 Token 数
- `modelProvider`: 模型提供商
- `model`: 模型 ID

#### 5.4.2 WebSocket 连接建立

**步骤**:
1. **从数据库获取认证信息**:
   ```sql
   SELECT instance_id, access_url, encrypted_token 
   FROM openclaw_instances 
   WHERE status = 'running'
   ```
   
   **示例数据**（来自 `openclaw-instances_sample-data.txt`）:
   - `instance_id`: `113`
   - `access_url`: `http://openclaw-18100732-svc.default.svc.cluster.local:18789?token=1c1d23658e3ab1fe568ab57af641c745ac04bf86d64fa0f7d893145762aac810`
   - `encrypted_token`: `1c1d23658e3ab1fe568ab57af641c745ac04bf86d64fa0f7d893145762aac810`

2. **构造 WebSocket URL**:
   - **方式1（推荐）**: 直接从 `access_url` 转换
     ```java
     // access_url: http://host:port?token=xxx
     String wsUrl = accessUrl.replace("http://", "ws://")
                             .replace("https://", "wss://");
     // 结果: ws://host:port?token=xxx
     ```
   
   - **方式2**: 使用 `base_url` + `encrypted_token`
     ```java
     // base_url: http://host:port/v1
     String baseUrl = instance.getBaseUrl(); // 去掉 /v1 后缀
     String wsUrl = baseUrl.replace("http://", "ws://")
                           .replace("https://", "wss://")
                           + "?token=" + instance.getEncryptedToken();
     // 结果: ws://host:port?token=xxx
     ```

3. **认证机制**:
   OpenClaw Gateway 支持多种认证方式：
   
   - **URL 参数认证**（最常用）:
     ```
     ws://host:port?token=YOUR_TOKEN
     ```
     ✅ 简单直接，适合监控系统使用
   
   - **WebSocket 子协议头**:
     ```java
     Map<String, String> headers = new HashMap<>();
     headers.put("Authorization", "Bearer YOUR_TOKEN");
     ```
   
   - **连接后发送认证消息**:
     ```json
     {
       "type": "auth",
       "token": "YOUR_TOKEN"
     }
     ```

4. **建立 WebSocket 连接**: 使用 WebSocket 客户端库建立连接

5. **发送 JSON-RPC 请求**:
   ```json
   {
     "jsonrpc": "2.0",
     "id": 1,
     "method": "health",
     "params": { "probe": false }
   }
   ```

6. **接收响应**:
   ```json
   {
     "jsonrpc": "2.0",
     "id": 1,
     "result": { ...HealthSummary... }
   }
   ```

7. **解析 `result` 字段**

#### 5.4.3 Java 实现示例

```java
@Service
@Slf4j
public class GatewaySessionsClient {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 通过 sessions.list RPC 获取 Token 统计数据
     */
    public List<SessionTokenStats> fetchSessionTokenStats(OpenclawInstance instance) {
        
        // 1. 建立 WebSocket 连接（从 instance 中获取认证信息）
        try (WebSocketClient client = createWebSocketClient(instance)) {
            
            // 2. 构建 JSON-RPC 请求
            JsonNode request = buildJsonRpcRequest("sessions.list", Map.of(
                "agentId", "main",
                "includeGlobal", false,
                "includeUnknown", false,
                "limit", 100
            ));
            
            // 3. 发送请求并等待响应（超时 3 秒）
            String responseJson = client.sendAndReceive(request.toString(), 3000);
            
            // 4. 解析响应
            JsonNode response = objectMapper.readTree(responseJson);
            JsonNode result = response.path("result");
            JsonNode sessions = result.path("sessions");
            
            // 5. 提取 Token 统计信息
            List<SessionTokenStats> statsList = new ArrayList<>();
            for (JsonNode session : sessions) {
                SessionTokenStats stats = new SessionTokenStats();
                stats.setSessionKey(session.path("key").asText());
                stats.setSessionId(session.path("sessionId").asText(null));
                stats.setInputTokens(session.path("inputTokens").asInt(0));
                stats.setOutputTokens(session.path("outputTokens").asInt(0));
                stats.setTotalTokens(session.path("totalTokens").asInt(0));
                stats.setEstimatedCostCents(
                    (int)(session.path("estimatedCostUsd").asDouble(0.0) * 100)
                );
                stats.setContextTokens(session.path("contextTokens").asInt(0));
                stats.setModelProvider(session.path("modelProvider").asText(null));
                stats.setModel(session.path("model").asText(null));
                statsList.add(stats);
            }
            
            return statsList;
            
        } catch (Exception e) {
            log.error("获取 Session Token 统计失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 创建 WebSocket 客户端
     */
    private WebSocketClient createWebSocketClient(OpenclawInstance instance) {
        // 方式1（推荐）: 直接从 access_url 转换
        String accessUrl = instance.getAccessUrl();
        if (accessUrl != null && !accessUrl.isEmpty()) {
            String wsUrl = accessUrl.replace("http://", "ws://")
                                    .replace("https://", "wss://");
            log.debug("使用 access_url 建立 WebSocket 连接: {}", wsUrl);
            return new WebSocketClient(wsUrl);
        }
        
        // 方式2: 使用 base_url + encrypted_token
        String baseUrl = instance.getBaseUrl();
        String token = instance.getEncryptedToken();
        if (baseUrl != null && token != null) {
            // 去掉 /v1 后缀
            String cleanBaseUrl = baseUrl.replaceAll("/v1$", "");
            String wsUrl = cleanBaseUrl.replace("http://", "ws://")
                                       .replace("https://", "wss://")
                                       + "?token=" + token;
            log.debug("使用 base_url + token 建立 WebSocket 连接: {}", wsUrl);
            return new WebSocketClient(wsUrl);
        }
        
        throw new IllegalArgumentException(
            "无法构造 WebSocket URL: instance_id=" + instance.getInstanceId()
        );
    }
    
    /**
     * 构建 JSON-RPC 请求
     */
    private JsonNode buildJsonRpcRequest(String method, Map<String, Object> params) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", 1);
        request.put("method", method);
        request.set("params", objectMapper.valueToTree(params));
        return request;
    }
    
    @Data
    public static class SessionTokenStats {
        private String sessionKey;
        private String sessionId;
        private int inputTokens;
        private int outputTokens;
        private int totalTokens;
        private int estimatedCostCents;
        private int contextTokens;
        private String modelProvider;
        private String model;
    }
}
```

#### 5.4.4 使用场景

1. **Session 扫描任务**: 
   - 解析完 Turn 结构后，调用 Gateway 获取该 Session 的 Token 统计
   - 批量更新到 `dashboard_session_turn` 表

2. **按需查询**: 
   - 用户查询对话详情时，实时从 Gateway 获取最新 Token 数据
   - 避免数据库数据过期

3. **定时同步**: 
   - 每小时扫描时，批量获取所有活跃 Session 的 Token 数据
   - 与 Session Log 解析结果合并存储

---

## 6. 部署说明

### 6.1 环境要求

- **JDK**: 17+
- **数据库**: OceanBase MySQL 兼容模式
- **NFS**: 已挂载 Session Log 目录到容器内

### 6.2 配置文件

**application.yml**:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://oceanbase-host:3306/openclaw_monitoring
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

monitoring:
  session:
    scan:
      cron: "0 0 * * * ?"                    # 每小时执行
      nfs-mount-path: /mnt/session-logs       # NFS 挂载根目录
      path-pattern: instances/*/agents/*/sessions/*.jsonl  # 文件路径模式（可配置）
      compaction-size-threshold: 0.9          # 文件大小缩小超过 10% 判定为 compaction
  health:
    check:
      timeout: 3000          # 单个实例超时（毫秒）
      parallelism: 50        # 并发检查的实例数
      total-timeout: 60000   # 整体超时（毫秒）
```

### 6.3 NFS 挂载

**OpenClaw Gateway 部署配置**：

每个 OpenClaw instance 需要将其 session 目录挂载到统一的 NFS 路径：

```bash
# Instance 001 的启动命令
docker run -d \
  --name openclaw-instance-001 \
  -v /path/to/instance-001/sessions:/data/openclaw/agents/main/sessions \
  -v nfs-server:/mnt/session-logs/instances/instance-001:/mnt/session-logs/instances/instance-001:nfs \
  openclaw-gateway:latest

# Instance 002 的启动命令
docker run -d \
  --name openclaw-instance-002 \
  -v /path/to/instance-002/sessions:/data/openclaw/agents/main/sessions \
  -v nfs-server:/mnt/session-logs/instances/instance-002:/mnt/session-logs/instances/instance-002:nfs \
  openclaw-gateway:latest
```

**监控系统容器配置**：

```bash
docker run -d \
  --name openclaw-monitoring \
  -v nfs-server:/mnt/session-logs:/mnt/session-logs:nfs \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=secret \
  openclaw-monitoring:latest
```

**NFS 目录结构**：
```
/mnt/session-logs/
└── instances/
    ├── instance-001/
    │   └── agents/
    │       └── main/
    │           └── sessions/
    │               ├── abc-123.jsonl
    │               ├── def-456.jsonl
    │               └── ...
    ├── instance-002/
    │   └── agents/
    │       └── main/
    │           └── sessions/
    │               └── ...
    └── ...
```

**注意事项**：
- 确保 NFS 服务器有足够的存储空间
- 建议启用 NFS 权限控制，只允许授权的 instance 写入
- 监控系统容器只需要读权限

### 6.4 数据库初始化

执行 DDL 脚本创建表结构：
```bash
mysql -h oceanbase-host -u admin -p openclaw_monitoring < schema.sql
```

---

## 7. 性能优化建议

### 7.1 Session Log 解析优化

1. **流式解析**: 使用 BufferedReader 逐行读取，避免一次性加载大文件
2. **分块批量写入**: 每解析 100 个 Turn 后批量插入数据库，避免内存中累积大量数据
   - 不在内存中保留整个文件的所有 Turn（防止 OOM）
   - 不使用单一大事务（失败回滚代价大）
   - 使用固定大小的批次（100 条），平衡性能和内存占用
3. **增量处理**: 基于 last_message_id 只处理新增消息
4. **路径配置化**: Session 文件扫描路径通过配置文件指定，支持灵活调整
5. **Compaction 检测**: 文件大小缩小超过 10% 时触发全量重扫，保证数据一致性

### 7.2 数据库优化

1. **索引优化**: 确保常用查询字段有索引（instance_id, start_time, user_id）
2. **分区表**: 当数据量大时，考虑按时间对 `dashboard_session_turn` 表分区
3. **连接池**: 使用 HikariCP，合理配置连接池大小

### 7.3 健康检查优化

1. **并行执行**: 使用线程池并行检查多个 instance，1000 个实例可在 0.4-1.5 秒内完成
2. **短连接模式**: 每次检查创建新连接，无需维护长连接状态，降低复杂度
3. **超时控制**: 严格设置 WebSocket 超时，避免长时间阻塞
4. **缓存读取**: 查询时直接读缓存表，不调用 Gateway

---

## 8. 监控与告警
暂时不做要求

---

## 9. 附录

### 9.1 术语表

| 术语 | 说明 |
|------|------|
| Turn | 对话单元，从一条 user 消息到下一条 user 消息之间的所有消息 |
| Session | 会话，包含多个 Turn |
| Instance | OpenClaw 实例，对应一个用户 |
| Gateway | OpenClaw 网关服务，提供 WebSocket 和 HTTP API |

### 9.2 参考资料
- [源码](../openclaw)
- [Session Log 格式说明](./2026-04-14-openclaw-monitoring-design.md)
- [API 接口文档](./API接口文档.md)
- [openclaw_instances 数据](./openclaw-instances_sample-data.txt)

---

**文档版本历史**:

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| v1.0 | 2026-04-15 | OpenClaw Team | 初始版本 |
| v1.1 | 2026-04-15 | OpenClaw Team | 优化健康检查并行执行，采用方案C（并行+短连接） |
| v1.2 | 2026-04-15 | OpenClaw Team | 完善 Session 文件扫描逻辑，路径配置化，支持 NFS 多实例隔离 |
