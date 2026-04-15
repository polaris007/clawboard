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
1. 扫描 NFS 挂载目录下的所有 Session Log 文件
2. 基于文件修改时间和 last_message_id 判断是否有新数据
3. 流式逐行解析 JSONL 文件
4. 提取消息元数据并聚合到 session_turn 表
5. 更新 last_message_id 记录

**增量策略**：
- 文件级别：检测文件修改时间或大小变化
- 消息级别：记录每个文件的 last_message_id，只处理新增消息
- 双重保障：避免重复处理和遗漏数据

#### 2.2.2 Gateway 健康检查轮询

**触发方式**：
- 定时触发：每 5 分钟自动执行
- 手动触发：`POST /api/v1/admin/trigger-task?type=health_check`

**处理流程**：
1. 从 `openclaw_instances` 表获取所有 status为running的instance 列表
2. 遍历每个 instance，调用其 Gateway 的 `/health` 端点
3. HTTP 请求超时设置为 3 秒（可配置）
4. 更新 `gateway_health_cache` 表的状态信息
5. 对比 `openclaw_instances` 表，清理非running的 instance 缓存记录

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
  `input_tokens` INT DEFAULT 0 COMMENT '输入 Token 数',
  `output_tokens` INT DEFAULT 0 COMMENT '输出 Token 数',
  `total_tokens` INT DEFAULT 0 COMMENT '总 Token 数',
  `estimated_cost_cents` INT DEFAULT 0 COMMENT '预估成本（美分）',
  `skill_ids` TEXT COMMENT '使用的 Skill ID 列表（JSON 数组）',
  `model_ids` TEXT COMMENT '使用的模型 ID 列表（JSON 数组）',
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
  `last_heartbeat` DATETIME DEFAULT NULL COMMENT '最后心跳时间',
  `last_check_time` DATETIME NOT NULL COMMENT '最后检查时间',
  `version` VARCHAR(64) DEFAULT NULL COMMENT 'OpenClaw 版本',
  `channels_total` INT DEFAULT 0 COMMENT '渠道总数',
  `agents_total` INT DEFAULT 0 COMMENT 'Agent 总数',
  `memory_rss_mb` INT DEFAULT 0 COMMENT '内存占用（MB）',
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
        
        for (File file : sessionFiles) {
            try {
                processSessionFile(file);
            } catch (Exception e) {
                log.error("处理文件失败: {}", file.getAbsolutePath(), e);
            }
        }
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
        
        // 3. 流式解析 JSONL 文件
        String lastMessageId = state != null ? state.getLastMessageId() : null;
        List<TurnAggregate> aggregates = parseJsonlFile(file, lastMessageId);
        
        // 4. 批量插入或更新 session_turn 表
        if (!aggregates.isEmpty()) {
            turnRepository.batchUpsert(aggregates);
        }
        
        // 5. 更新处理状态
        if (!aggregates.isEmpty()) {
            String newLastMessageId = aggregates.get(aggregates.size() - 1).getLastMessageId();
            updateProcessingState(fileHash, filePath, file, newLastMessageId);
        }
    }
    
    private boolean hasFileChanged(File file, SessionProcessingState state) {
        long currentSize = file.length();
        long currentTime = file.lastModified();
        
        return currentSize != state.getFileSize() || 
               currentTime != state.getFileModifiedTime().getTime();
    }
    
    private List<TurnAggregate> parseJsonlFile(File file, String lastMessageId) throws IOException {
        List<TurnAggregate> aggregates = new ArrayList<>();
        TurnBuilder builder = new TurnBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
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
                        aggregates.add(completedTurn);
                    }
                }
            }
        }
        
        return aggregates;
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
    private int httpTimeout;
    
    private final RestTemplate restTemplate;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    public HealthCheckTask() {
        this.restTemplate = createRestTemplate();
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
        // 1. 获取所有 instance
        List<OpenclawInstance> instances = instanceRepository.findAll();
        
        Set<String> existingInstanceIds = instances.stream()
            .map(OpenclawInstance::getInstanceId)
            .collect(Collectors.toSet());
        
        // 2. 逐个检查健康状态
        for (OpenclawInstance instance : instances) {
            try {
                checkInstanceHealth(instance);
            } catch (Exception e) {
                log.error("检查实例健康状态失败: {}", instance.getInstanceId(), e);
            }
        }
        
        // 3. 清理已删除的 instance 缓存
        cleanupDeletedInstances(existingInstanceIds);
    }
    
    private void checkInstanceHealth(OpenclawInstance instance) {
        String healthUrl = instance.getGatewayUrl() + "/health";
        
        GatewayHealthCache cache = new GatewayHealthCache();
        cache.setInstanceId(instance.getInstanceId());
        cache.setLastCheckTime(LocalDateTime.now());
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                healthUrl,
                HttpMethod.GET,
                null,
                JsonNode.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode body = response.getBody();
                cache.setStatus("online");
                cache.setLastHeartbeat(parseHeartbeat(body));
                cache.setVersion(body.path("version").asText(null));
                cache.setChannelsTotal(body.path("channels").path("total").asInt(0));
                cache.setAgentsTotal(body.path("agents").path("total").asInt(0));
                cache.setMemoryRssMb(body.path("memory").path("rss_mb").asInt(0));
                cache.setErrorMessage(null);
            } else {
                cache.setStatus("offline");
                cache.setErrorMessage("HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            cache.setStatus("offline");
            cache.setErrorMessage(e.getMessage());
        }
        
        healthCacheRepository.save(cache);
    }
    
    private void cleanupDeletedInstances(Set<String> existingInstanceIds) {
        List<GatewayHealthCache> allCaches = healthCacheRepository.findAll();
        
        for (GatewayHealthCache cache : allCaches) {
            if (!existingInstanceIds.contains(cache.getInstanceId())) {
                healthCacheRepository.deleteByInstanceId(cache.getInstanceId());
                log.info("清理已删除实例的健康缓存: {}", cache.getInstanceId());
            }
        }
    }
    
    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(httpTimeout);
        factory.setReadTimeout(httpTimeout);
        return new RestTemplate(factory);
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
      cron: "0 0 * * * ?"  # 每小时执行
      nfs-mount-path: /mnt/session-logs
  health:
    check:
      timeout: 3000  # HTTP 超时 3 秒
```

### 6.3 NFS 挂载

在容器启动时挂载 NFS：
```bash
docker run -d \
  -v nfs-server:/session-logs:/mnt/session-logs:nfs \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=secret \
  openclaw-monitoring:latest
```

### 6.4 数据库初始化

执行 DDL 脚本创建表结构：
```bash
mysql -h oceanbase-host -u admin -p openclaw_monitoring < schema.sql
```

---

## 7. 性能优化建议

### 7.1 Session Log 解析优化

1. **流式解析**: 使用 BufferedReader 逐行读取，避免一次性加载大文件
2. **批量写入**: 累积一定数量的 Turn 后批量插入数据库
3. **增量处理**: 基于 last_message_id 只处理新增消息

### 7.2 数据库优化

1. **索引优化**: 确保常用查询字段有索引（instance_id, start_time, user_id）
2. **分区表**: 当数据量大时，考虑按时间对 `dashboard_session_turn` 表分区
3. **连接池**: 使用 HikariCP，合理配置连接池大小

### 7.3 健康检查优化

1. **并发控制**: 使用线程池并行检查多个 instance
2. **超时控制**: 严格设置 HTTP 超时，避免长时间阻塞
3. **缓存读取**: 查询时直接读缓存表，不调用 Gateway

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
- [源码](D:\workplace\github\openclaw)
- [Session Log 格式说明](./2026-04-14-openclaw-monitoring-design.md)
- [API 接口文档](./API接口文档.md)

---

**文档版本历史**:

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| v1.0 | 2026-04-15 | OpenClaw Team | 初始版本 |
