# OpenClaw 监控面板简化设计 (Go + SpringBoot版)

**文档版本**: 1.0  
**创建日期**: 2026-04-14  
**作者**: AI Assistant  
**状态**: 待审批  

---

## 一、项目背景与目标

### 1.1 业务场景

公司基于 OpenClaw 打造企业级个人助理服务(国寿龙虾):
- **部署方式**: PaaS 容器化部署
- **对接方式**: 内部聊天工具 → OpenClaw → 内部部署大模型
- **核心需求**: 运营监控仪表板 + 会话记录检索

### 1.2 功能范围(基于原型界面)

| 页面 | 核心功能 | 关键指标 |
|------|---------|----------|
| **龙虾运营大盘** | 运营概览、热度趋势、用户统计 | Token消耗、对话轮次、技能调用、活跃用户数 |
| **龙虾会话检索** | 会话查询、执行链路追踪 | 会话详情、耗时、结果、质量、日志路径 |

### 1.3 核心约束

✅ **必须满足:**
- 零修改 OpenClaw 源码 (非侵入式)
- 所有 Session Log 通过 NAS 挂载直接访问
- 暂不实现文件备份,仅解析和统计
- 实现原型中列出的指标
- 不实现前端,仅定义接口和报文格式

### 1.4 龙虾状态说明

**"龙虾状态"来源**: OpenClaw Gateway Health API

```
┌─────────────────────────────────────────────┐
│  OpenClaw Instance                          │
│  ┌───────────────────────────────────────┐  │
│  │  Gateway Server                       │  │
│  │  • health API (WebSocket short-lived) │  │
│  │  • status API                         │  │
│  └───────────────┬───────────────────────┘  │
└──────────────────┼──────────────────────────┘
                   │ WebSocket 短连接(30-60s)
                   ▼
┌─────────────────────────────────────────────┐
│  Data Collector (Go)                        │
│  • 轮询 Gateway health API                  │
│  • 提取实例健康状态                         │
│  • 推送到后端                               │
└───────────────┬─────────────────────────────┘
                │ HTTP POST
                ▼
┌─────────────────────────────────────────────┐
│  Backend Service (SpringBoot)               │
│  • 接收健康状态数据                         │
│  • 写入 Redis 缓存 (TTL: 5分钟)             │
│  • Dashboard 从 Redis 读取最新状态          │
└─────────────────────────────────────────────┘
```

**关键设计决策**:
1. ✅ **实时状态存Redis**: 健康状态是瞬时值,不需要持久化到数据库
2. ✅ **TTL自动过期**: Redis key设置5分钟TTL,过期后显示"数据过时"
3. ✅ **短连接轮询**: Collector每30-60秒建立一次WebSocket短连接调用health API
4. ✅ **利用Gateway缓存**: Gateway内部有health缓存(默认60秒),降低实际查询开销

**Health API返回示例**:
```json
{
  "ok": true,
  "version": "1.0.0",
  "nodeId": "node-abc123",
  "channels": {
    "total": 5,
    "linked": 4,
    "unlinked": 1
  },
  "agents": {
    "total": 3,
    "idle": 2,
    "busy": 1
  },
  "uptime": 86400,
  "memory": {
    "rss": 524288000,
    "heapUsed": 262144000
  }
}
```

**Redis数据结构**:
```redis
# Key格式: monitor:instance:{instance_id}
# 类型: Hash
# TTL: 300秒 (5分钟)

HSET monitor:instance:inst-001 \
  status "active" \
  lastHeartbeat "2026-04-14T11:29:29Z" \
  version "1.0.0" \
  nodeId "node-abc123" \
  channelsTotal "5" \
  channelsLinked "4" \
  agentsTotal "3" \
  agentsIdle "2" \
  uptime "86400" \
  memoryRss "524288000"
```

### 1.5 关键概念澄清: Session Log 结构

**重要**: OpenClaw 的 Session Log 是 JSONL 格式,每条记录代表**一条消息**,不是整个会话。

#### JSONL 记录结构示例:
```json
{
  "id": "entry-123",
  "parentId": "entry-120",
  "type": "message",
  "timestamp": "2026-04-14T09:30:00Z",
  "message": {
    "role": "user",
    "content": [...],
    "usage": { ... }
  }
}
```

#### 关键字段说明:
- **`id`**: 每条消息记录的唯一ID (不是 session ID!)
- **`parentId`**: 父消息ID,指向上一条相关消息的 `id`,形成对话链路
- **对话链**: 通过 `parentId` 可以追溯完整的对话树:
  ```
  entry-120 (用户: "你好")
    └─ entry-121 (助手: "你好!有什么可以帮助你的吗?")
        └─ entry-123 (用户: "帮我生成一个公文写作模板...")
            └─ entry-124 (助手: "好的,我来帮你生成...")
  ```

#### 数据存储策略:
1. **文件级别聚合** (`session_log_metadata`): 
   - 每个JSONL文件的汇总统计(Token总数、消息数等)
   - 用于运营大盘的快速查询
   
2. **消息级别明细** (`session_message_detail`):
   - 存储每条JSONL记录的详细信息
   - 包含 `message_id` (对应JSONL的`id`) 和 `parent_message_id` (对应JSONL的`parentId`)
   - 用于会话检索页面的对话链路展示

---

## 二、技术选型

| 组件 | 技术选型 | 说明 |
|------|---------|------|
| **Edge Collector** | Go | 轻量、高并发,负责解析 JSONL |
| **Registry Service** | SpringBoot | Collector注册管理、实例分配、故障转移 |
| **Center Service** | SpringBoot | 业务逻辑、API 接口、数据存储 |
| **数据库** | OceanBase MySQL模式 | 存储聚合指标和元数据 |
| **缓存** | Redis 7+ | 实时状态缓存 |

---

## 三、整体架构

```
┌─────────────────────────────────────────┐
│       OpenClaw Instance (1000+)          │
│  /sessions/  (NAS挂载,直接访问)           │
│    ├─ session-abc.jsonl                 │
│    └─ session-xyz.jsonl                 │
└──────────────┬──────────────────────────┘
               │
               │ 直接读取 JSONL 文件
               ▼
┌─────────────────────────────────────────┐
│         Edge Collector (Go)              │
│  • 扫描 NAS 上的 Session Log             │
│  • 逐行解析 JSONL                        │
│  • 提取指标:Token、消息、技能、工具       │
│  • 批量推送到 Center Service             │
│  • 从 Registry 获取分配的实例列表         │
└──────────────┬──────────────────────────┘
               │                          │
               │ HTTP POST (批量+Gzip)     │ 注册/心跳/获取实例
               ▼                          ▼
┌────────────────────────┐  ┌────────────────────────┐
│   Center Service       │  │   Registry Service      │
│   (SpringBoot)         │  │   (SpringBoot)          │
│  • 接收批量数据        │  │  • Collector注册管理    │
│  • 写入 OceanBase      │  │  • 实例分配算法         │
│  • 提供 REST API       │  │  • 自动Rebalance        │
│  • Redis 缓存实时数据  │  │  • 故障检测与转移       │
└──────────────┬─────────┘  └────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│      OceanBase MySQL                     │
│  • metrics_token_daily                   │
│  • metrics_message_daily                 │
│  • session_log_metadata                  │
│  • collectors (Registry)                │
│  • openclaw_instances (Registry)        │
│  • instance_collector_mapping (Registry)│
└─────────────────────────────────────────┘
```

---

## 四、核心流程

### 4.1 数据采集流程

**采集策略**: 增量处理 (Incremental Processing)

```
1. 首次启动: 全量扫描
   ├─ 扫描 NAS 目录下所有 .jsonl 文件
   ├─ 解析所有文件,提取指标
   ├─ 建立本地缓存 (文件指纹 + last_message_id)
   └─ 推送到后端

2. 后续轮询: 增量检测 (每5分钟)
   ├─ 扫描 NAS 目录
   ├─ 遍历所有 .jsonl 文件
   │   ├─ 计算文件指纹 (path + size + mod_time)
   │   ├─ 与本地缓存对比
   │   │   ├─ 新文件 → 加入待处理列表
   │   │   ├─ 文件变化 (size/mod_time改变) → 加入待处理列表
   │   │   └─ 文件未变 → 跳过
   │   └─ 检测已删除/归档文件 → 从缓存移除或标记
   ├─ 批量解析待处理文件 (基于last_message_id增量读取)
   │   ├─ 逐行解析 JSONL
   │   ├─ 找到 last_message_id 的位置
   │   ├─ 只处理之后的新消息 (OpenClaw是append-only)
   │   ├─ 提取 Session ID (从文件名)
   │   ├─ 提取每条消息的 id 和 parentId (形成对话链)
   │   ├─ 统计 Token (input/output/cache_read/cache_write)
   │   ├─ 统计消息 (user/assistant/tool/toolResult)
   │   ├─ 统计技能调用 (tool_use 中的 name 字段)
   │   ├─ 统计工具调用量
   │   ├─ 计算耗时 (durationMs)
   │   └─ 判断结果和质量 (is_error、success)
   ├─ 轮询 Gateway Health API (每30-60秒)
   │   ├─ 建立 WebSocket 短连接
   │   ├─ 调用 health 方法
   │   ├─ 提取实例健康状态(status, nodeId, channels, agents等)
   │   └─ 记录最后心跳时间(lastHeartbeat)
   ├─ 数据存储
   │   ├─ 聚合指标存入 session_log_metadata (文件级别统计)
   │   ├─ 明细记录存入 session_message_detail (每条JSONL记录)
   │   ├─ 健康状态推送到后端 → Redis缓存 (TTL: 5分钟)
   │   └─ 时间序列数据存入 metrics_timeseries (小时级聚合)
   └─ 批量推送到后端
       ├─ Gzip 压缩传输
       ├─ 成功 → 更新本地缓存 (last_message_id)
       └─ 失败 → 保留待处理状态,下次重试

3. 特殊场景处理
   ├─ Checkpoint临时文件 (.checkpoint.) → 跳过
   ├─ 归档文件 (.reset. / .deleted.) → 仍然解析统计,标记为archived
   ├─ 文件改名检测: .jsonl → .reset./.deleted. → 继续处理
   ├─ 文件删除 → 从缓存移除
   └─ 推送失败 → 不更新缓存,下次重试
```

**关键设计决策**:
1. ✅ **增量处理**: 基于`last_message_id`只处理新增消息,避免重复解析
2. ✅ **文件指纹**: 使用 `path + size + mod_time` 三元组识别文件变化
3. ✅ **本地缓存**: Collector维护JSON文件缓存,记录`last_message_id`
4. ✅ **失败重试**: 推送失败时不更新缓存,下次继续尝试
5. ✅ **性能优化**: 日常扫描仅处理~1%的文件,节省95-99%开销
6. ✅ **归档文件处理**: `.reset.`/`.deleted.`文件仍会解析统计,保证数据完整性
7. ✅ **Backend幂等性**: 使用`INSERT IGNORE`防止Collector重试导致的重复

### 4.2 数据聚合与查询流程

**实时聚合策略:**
```
Collector 推送 Session Log 元数据
    ↓
Center Service 接收数据
    ↓
1. INSERT INTO session_log_metadata (原始数据)
2. INSERT INTO metrics_token_daily ON DUPLICATE KEY UPDATE (Token聚合)
3. INSERT INTO metrics_message_daily ON DUPLICATE KEY UPDATE (消息聚合)
4. INSERT INTO metrics_user_activity ON DUPLICATE KEY UPDATE (用户活跃度)
5. INSERT INTO metrics_skill_usage ON DUPLICATE KEY UPDATE (技能使用)
6. INSERT INTO metrics_timeseries ON DUPLICATE KEY UPDATE (时间序列)
    ↓
所有表原子性更新，保证数据一致性
```

**优势**:
- ✅ 实时性高: 数据立即可见，无需等待批处理
- ✅ 实现简单: 利用 MySQL 的 `ON DUPLICATE KEY UPDATE` 特性
- ✅ 一致性好: 在同一事务中更新，避免数据不一致
- ✅ 性能可控: 每次只更新几条记录（按 user_id + date 分组）

**运营大盘查询:**
```
1. 前端请求数据
   ├─ 传递筛选条件(团队、姓名、时间范围)
   └─ 指定指标类型

2. 后端查询 OceanBase
   ├─ 实时数据从 Redis 缓存读取
   ├─ 历史数据从聚合表查询 (metrics_token_daily, metrics_message_daily)
   └─ 会话详情从 session_log_metadata 查询

3. 组装响应并返回
   ├─ 按筛选条件过滤
   ├─ 计算聚合值(总计、平均等)
   └─ 返回 JSON 格式
```

**会话检索查询(Turn 列表)**:
```
1. 前端请求对话列表
   ├─ 传递搜索条件(userName, startTime, endTime, skillId)
   └─ 指定分页参数(page, pageSize)

2. 后端查询 OceanBase
   ├─ 从 session_turn 表查询对话记录 ⭐
   ├─ 按 start_timestamp 排序
   └─ 支持多维度过滤(用户、时间、技能)

3. 组装响应并返回
   ├─ 返回 Turn 级别的聚合信息
   ├─ 包含 is_complete 状态字段
   └─ 支持前端展示"进行中"的对话
```

**执行链路追踪(Turn Detail)**:
```
1. 前端点击某条 Turn 展开详情
   ├─ 调用 /api/v1/turns/{turnId}/trace
   └─ 传入 turn_id

2. 后端查询 OceanBase
   ├─ 从 session_turn 表获取 first_message_id 和 last_message_id
   ├─ 从 session_message_detail 表查询该范围内的所有消息
   ├─ 根据 parent_message_id 构建对话树
   └─ 按 stepOrder 排序生成执行链路

3. 组装响应并返回
   ├─ 返回节点列表(nodes)
   ├─ 每个节点包含 stepOrder, nodeType, nodeName, status, timeStamp
   └─ 前端渲染为流程图或时间线
```

---

## 五、数据库设计

### 5.1 Registry Service 表结构

**用途**: 管理Collector注册、实例分配、故障转移

```sql
-- ==================== Registry相关表 ====================

-- Collector注册表
CREATE TABLE collectors (
    collector_id VARCHAR(64) PRIMARY KEY COMMENT 'Collector唯一标识',
    host VARCHAR(100) NOT NULL COMMENT 'Collector主机IP或域名',
    port INT NOT NULL COMMENT 'Collector服务端口',
    capacity INT DEFAULT 100 COMMENT '最大管理实例数容量',
    status ENUM('active', 'inactive', 'draining') DEFAULT 'active' COMMENT '状态',
    last_heartbeat TIMESTAMP NULL COMMENT '最后一次心跳时间',
    instance_count INT DEFAULT 0 COMMENT '当前管理的实例数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_last_heartbeat (last_heartbeat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Edge Collector注册表';

-- OpenClaw实例表
CREATE TABLE openclaw_instances (
    instance_id VARCHAR(64) PRIMARY KEY COMMENT '实例唯一标识',
    gateway_url VARCHAR(200) NOT NULL COMMENT 'Gateway WebSocket地址 ws://host:port',
    auth_token VARCHAR(500) COMMENT '认证Token(如果需要)',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OpenClaw实例注册表';

-- 实例-Collector映射表
CREATE TABLE instance_collector_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    instance_id VARCHAR(64) NOT NULL COMMENT '实例ID',
    collector_id VARCHAR(64) NOT NULL COMMENT 'Collector ID',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    UNIQUE KEY uk_instance (instance_id) COMMENT '实例唯一约束',
    INDEX idx_collector (collector_id) COMMENT '按Collector查询索引',
    FOREIGN KEY (instance_id) REFERENCES openclaw_instances(instance_id) ON DELETE CASCADE,
    FOREIGN KEY (collector_id) REFERENCES collectors(collector_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实例与Collector分配关系表';

-- Rebalance历史记录表
CREATE TABLE rebalance_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    trigger_type ENUM('collector_register', 'collector_unregister', 'scheduled', 'manual') NOT NULL COMMENT '触发类型',
    trigger_collector_id VARCHAR(64) COMMENT '触发本次Rebalance的Collector ID',
    total_instances INT NOT NULL DEFAULT 0 COMMENT '涉及的总实例数',
    reassigned_count INT NOT NULL DEFAULT 0 COMMENT '实际重新分配的实例数',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    status ENUM('running', 'completed', 'failed') DEFAULT 'running' COMMENT '执行状态',
    error_message TEXT COMMENT '如果失败,记录错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_trigger_type (trigger_type),
    INDEX idx_started_at (started_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Rebalance操作历史记录表';
```

---

### 5.2 Session Log 元数据表

**重要说明**: 
- `session_log_metadata` 表存储的是 **文件级别** 的聚合统计信息（每个JSONL文件的汇总数据）
- 消息级别的 `id` 和 `parentId` 存储在 `session_message_detail` 表中，用于构建对话链路
- **性能指标说明**:
  - `avg_duration_ms` / `max_duration_ms`: **仅统计 assistant 消息**（AI回复）
  - Session Log 中**没有直接的 `durationMs` 字段**，需要通过时间戳计算
  - **计算方法**: `assistant.timestamp - previous_message.timestamp`
    - `assistant.timestamp`: AI 响应完成并写入日志的时间
    - `previous_message.timestamp`: 上一条消息（user 或 toolResult）的时间戳
    - 差值 = AI 生成时间 + 网络传输时间（近似值）
  - User 消息不参与耗时统计（作为请求起点）
  - 如果 assistant 是最后一条消息，无法计算耗时（跳过）
- **用户输入预览说明**:
  - `user_input_preview`: 提取 Session 中**第一条 user 消息**的前500字符
  - 用途: 快速识别会话主题（如 cron 任务、聊天话题等）
  - 如果内容超过500字符，截断并添加 "..."
  - 多轮对话时，不包含后续的用户输入

```sql
CREATE TABLE session_log_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    instance_id VARCHAR(64) COMMENT '实例ID',
    user_id VARCHAR(64) COMMENT '用户ID',
    -- user_name VARCHAR(100) COMMENT '用户姓名',
    -- team VARCHAR(100) COMMENT '产品团队',
    
    file_path VARCHAR(500) NOT NULL COMMENT '文件绝对路径',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    line_count INT COMMENT 'JSONL行数',
    
    first_timestamp TIMESTAMP NULL COMMENT '第一条日志时间',
    last_timestamp TIMESTAMP NULL COMMENT '最后一条日志时间',
    
    total_tokens BIGINT DEFAULT 0 COMMENT '总Token数',
    input_tokens BIGINT DEFAULT 0 COMMENT '输入Token',
    output_tokens BIGINT DEFAULT 0 COMMENT '输出Token',
    
    message_count INT DEFAULT 0 COMMENT '消息总数',
    user_messages INT DEFAULT 0 COMMENT '用户消息数',
    assistant_messages INT DEFAULT 0 COMMENT '助手消息数',
    
    skill_calls INT DEFAULT 0 COMMENT '技能调用次数',
    tool_calls INT DEFAULT 0 COMMENT '工具调用次数',
    
    -- 性能指标 (仅统计assistant消息,即AI回复的生成耗时)
    avg_duration_ms DECIMAL(10,2) COMMENT 'AI回复平均耗时(ms),仅统计assistant消息',
    max_duration_ms DECIMAL(10,2) COMMENT 'AI回复最大耗时(ms),仅统计assistant消息',
   
    error_count INT DEFAULT 0 COMMENT '错误数',
    success_count INT DEFAULT 0 COMMENT '成功数',
    
    user_input_preview VARCHAR(500) COMMENT '首次用户输入预览(前500字符),用于快速识别会话主题',
    
    status ENUM('active', 'archived', 'deleted') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间(数据摄入时间)',
    
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    -- INDEX idx_team (team),
    INDEX idx_last_timestamp (last_timestamp),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Session Log元数据表';
```

### 5.2 Token 消耗日报表

**数据生成方式**: 实时聚合 (Real-time Aggregation)

当 Collector 推送 Session Log 元数据时，Center Service 同时更新此表：

```sql
-- Center Service 接收数据后执行
INSERT INTO metrics_token_daily (
    stat_date, user_id,
    input_tokens, output_tokens, total_tokens
) VALUES (
    CURDATE(), :userId,
    :inputTokens, :outputTokens, :totalTokens
)
ON DUPLICATE KEY UPDATE
    input_tokens = input_tokens + VALUES(input_tokens),
    output_tokens = output_tokens + VALUES(output_tokens),
    total_tokens = total_tokens + VALUES(total_tokens);
```

**优点**:
- ✅ 实时性高: 数据立即可见
- ✅ 实现简单: 无需额外的批处理任务
- ✅ 一致性好: 与原始数据同步更新
- ✅ 适合增量推送: 每次只更新几条记录

```sql
CREATE TABLE metrics_token_daily (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    user_id VARCHAR(64) COMMENT '用户ID',
    -- team VARCHAR(100) COMMENT '团队',
    
    input_tokens BIGINT DEFAULT 0,
    output_tokens BIGINT DEFAULT 0,
    total_tokens BIGINT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_date_user (stat_date, user_id),
    INDEX idx_stat_date (stat_date)
    -- INDEX idx_team (team)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token消耗日报表';
```

### 5.3 消息统计日报表

**数据生成方式**: 实时聚合 (Real-time Aggregation)

与 Token 消耗表类似，每次推送时实时更新：

```sql
-- Center Service 接收数据后执行
INSERT INTO metrics_message_daily (
    stat_date, user_id,
    session_count, user_messages, assistant_messages,
    skill_calls, tool_calls
) VALUES (
    CURDATE(), :userId,
    1, :userMessages, :assistantMessages,
    :skillCalls, :toolCalls
)
ON DUPLICATE KEY UPDATE
    session_count = session_count + VALUES(session_count),
    user_messages = user_messages + VALUES(user_messages),
    assistant_messages = assistant_messages + VALUES(assistant_messages),
    skill_calls = skill_calls + VALUES(skill_calls),
    tool_calls = tool_calls + VALUES(tool_calls);
```

```sql
CREATE TABLE metrics_message_daily (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    user_id VARCHAR(64) COMMENT '用户ID',
    -- team VARCHAR(100) COMMENT '团队',
    
    session_count INT DEFAULT 0 COMMENT '会话数',
    user_messages INT DEFAULT 0 COMMENT '用户消息数',
    assistant_messages INT DEFAULT 0 COMMENT '助手消息数',
    skill_calls INT DEFAULT 0 COMMENT '技能调用次数',
    tool_calls INT DEFAULT 0 COMMENT '工具调用次数',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_date_user (stat_date, user_id),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息统计日报表';
```

### 5.4 用户活跃度表

**数据生成方式**: 实时聚合 (Real-time Aggregation)

当 Collector 推送 Session Log 元数据时，Center Service 同时更新此表：

```sql
-- Center Service 接收数据后执行
INSERT INTO metrics_user_activity (
    stat_date, user_id,
    session_count, message_count, total_tokens,
    skill_calls, tool_calls,
    first_activity, last_activity
) VALUES (
    CURDATE(), :userId,
    1, :messageCount, :totalTokens,
    :skillCalls, :toolCalls,
    :firstTimestamp, :lastTimestamp
)
ON DUPLICATE KEY UPDATE
    session_count = session_count + VALUES(session_count),
    message_count = message_count + VALUES(message_count),
    total_tokens = total_tokens + VALUES(total_tokens),
    skill_calls = skill_calls + VALUES(skill_calls),
    tool_calls = tool_calls + VALUES(tool_calls),
    first_activity = LEAST(first_activity, VALUES(first_activity)),
    last_activity = GREATEST(last_activity, VALUES(last_activity));
```

**优点**:
- ✅ 实时性高: 每次推送立即更新
- ✅ 逻辑简单: 无需定时任务
- ✅ 与现有架构一致

```sql
CREATE TABLE metrics_user_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID(工号)',
    -- user_name VARCHAR(100) COMMENT '用户姓名',
    -- team VARCHAR(100) COMMENT '团队',
    
    session_count INT DEFAULT 0 COMMENT '会话数',
    message_count INT DEFAULT 0 COMMENT '消息数',
    total_tokens BIGINT DEFAULT 0 COMMENT '总Token',
    skill_calls INT DEFAULT 0 COMMENT '技能调用次数',
    tool_calls INT DEFAULT 0 COMMENT '工具调用次数',
    
    first_activity TIMESTAMP NULL,
    last_activity TIMESTAMP NULL,
    
    -- status VARCHAR(50) DEFAULT 'active' COMMENT '龙虾状态',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_date_user (stat_date, user_id),
    INDEX idx_stat_date (stat_date)
    -- INDEX idx_team (team)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户活跃度表';
```

### 5.5 技能使用统计表

**数据生成方式**: 实时聚合 (Real-time Aggregation)

Collector 在解析 Session Log 时，已统计每个技能的调用情况，Center Service 接收后批量更新：

```go
// Collector 端伪代码 - 提取技能使用情况
func extractSkillUsage(entries []LogEntry) map[string]*SkillUsage {
    skills := make(map[string]*SkillUsage)
    
    for _, entry := range entries {
        if entry.Message.Role == "assistant" {
            for _, content := range entry.Message.Content {
                if content.Type == "toolCall" && content.Name != "" {
                    key := content.Name
                    if _, exists := skills[key]; !exists {
                        skills[key] = &SkillUsage{SkillName: content.Name}
                    }
                    skills[key].CallCount++
                    skills[key].TotalTokens += entry.Message.Usage.TotalTokens
                    if entry.Message.StopReason == "error" {
                        skills[key].ErrorCount++
                    } else {
                        skills[key].SuccessCount++
                    }
                }
            }
        }
    }
    
    return skills
}
```

Center Service 接收后执行:

```sql
-- 批量更新技能使用统计
INSERT INTO metrics_skill_usage (
    stat_date, user_id, skill_name,
    call_count, success_count, error_count, total_tokens
) VALUES 
    (CURDATE(), :userId, :skillName1, :callCount1, :successCount1, :errorCount1, :totalTokens1),
    (CURDATE(), :userId, :skillName2, :callCount2, :successCount2, :errorCount2, :totalTokens2),
    ...
ON DUPLICATE KEY UPDATE
    call_count = call_count + VALUES(call_count),
    success_count = success_count + VALUES(success_count),
    error_count = error_count + VALUES(error_count),
    total_tokens = total_tokens + VALUES(total_tokens);
```

**优点**:
- ✅ 实时性高: 推送后立即更新
- ✅ Collector 已预计算，Center Service 只做简单累加
- ✅ 支持按技能维度实时分析

```sql
CREATE TABLE metrics_skill_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    user_id VARCHAR(64) COMMENT '用户ID',
    skill_name VARCHAR(100) NOT NULL COMMENT '技能名称',
    
    call_count INT DEFAULT 0 COMMENT '调用次数',
    success_count INT DEFAULT 0 COMMENT '成功次数',
    error_count INT DEFAULT 0 COMMENT '失败次数',
    total_tokens BIGINT DEFAULT 0 COMMENT '总Token消耗',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_date_user_skill (stat_date, user_id, skill_name),
    INDEX idx_stat_date (stat_date),
    INDEX idx_skill_name (skill_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能使用统计表';
```

### 5.6 时间序列数据表(用于热度趋势)

**数据生成方式**: Collector 端预聚合 (Pre-aggregation at Collector)

Collector 在解析 Session Log 时，按小时窗口聚合数据并推送：

```go
// Collector 端伪代码
func aggregateByHour(entries []SessionLogEntry) map[string]*TimeSeriesBucket {
    buckets := make(map[string]*TimeSeriesBucket)
    
    for _, entry := range entries {
        // 计算小时窗口: 2026-04-14 10:30:00 → 2026-04-14 10:00:00
        bucketTime := truncateToHour(entry.Timestamp)
        key := fmt.Sprintf("%s|%s", bucketTime, entry.UserID)
        
        if _, exists := buckets[key]; !exists {
            buckets[key] = &TimeSeriesBucket{
                BucketTime: bucketTime,
                UserID:     entry.UserID,
            }
        }
        
        // 累加指标
        buckets[key].TotalTokens += entry.TotalTokens
        buckets[key].SessionCount++
        buckets[key].SkillCalls += entry.SkillCalls
    }
    
    return buckets
}
```

Center Service 接收后执行:

```sql
-- Center Service 接收数据后执行
INSERT INTO metrics_timeseries (
    bucket_time, user_id,
    total_tokens, session_count, skill_calls
) VALUES (
    :bucketTime, :userId,
    :totalTokens, :sessionCount, :skillCalls
)
ON DUPLICATE KEY UPDATE
    total_tokens = total_tokens + VALUES(total_tokens),
    session_count = session_count + VALUES(session_count),
    skill_calls = skill_calls + VALUES(skill_calls);
```

**优点**:
- ✅ 减轻 Center Service 计算压力
- ✅ 网络传输量小（已聚合）
- ✅ 实时性好（每小时更新一次）
- ✅ 支持细粒度查询（如最近24小时趋势）

**典型查询场景**:
```sql
-- 查询某用户最近24小时的Token消耗趋势
SELECT 
    bucket_time,
    total_tokens,
    session_count
FROM metrics_timeseries
WHERE user_id = 'user123'
  AND bucket_time >= NOW() - INTERVAL 24 HOUR
ORDER BY bucket_time;

-- 查询全局每小时活跃度
SELECT 
    bucket_time,
    SUM(total_tokens) as total_tokens,
    SUM(session_count) as session_count
FROM metrics_timeseries
WHERE user_id IS NULL  -- 全局聚合记录
  AND bucket_time >= CURDATE()
GROUP BY bucket_time
ORDER BY bucket_time;
```

```sql
CREATE TABLE metrics_timeseries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bucket_time TIMESTAMP NOT NULL COMMENT '时间窗口(小时级)',
    user_id VARCHAR(64) COMMENT '用户ID(空表示全局)',
    -- team VARCHAR(100) COMMENT '团队(空表示全局)',
    
    total_tokens BIGINT DEFAULT 0 COMMENT 'Token消耗',
    session_count INT DEFAULT 0 COMMENT '会话数',
    skill_calls INT DEFAULT 0 COMMENT '技能调用次数',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_bucket_time (bucket_time),
    INDEX idx_user_id (user_id)
    -- INDEX idx_team (team)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间序列数据表';
```

### 5.7 消息记录明细表 (每条JSONL记录)

```sql
CREATE TABLE session_message_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    
    -- 关联信息
    session_id VARCHAR(64) NOT NULL COMMENT 'Session ID (从文件名提取)',
    file_path VARCHAR(500) NOT NULL COMMENT '所属JSONL文件路径',
    
    -- 消息记录标识
    message_id VARCHAR(200) NOT NULL COMMENT '消息记录ID (JSONL中的id字段)',
    parent_message_id VARCHAR(200) COMMENT '父消息ID (JSONL中的parentId字段,形成对话链)',
    
    -- 消息内容
    role VARCHAR(50) COMMENT '角色(user/assistant/tool/toolResult)',
    content_summary TEXT COMMENT '内容摘要(前500字符)',
    
    -- Token统计
    total_tokens INT DEFAULT 0 COMMENT '该消息的Token数',
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    cache_read_tokens INT DEFAULT 0,
    cache_write_tokens INT DEFAULT 0,
    
    -- 技能和工具
    skill_name VARCHAR(200) COMMENT '调用的技能名称(从tool_use提取)',
    tool_name VARCHAR(200) COMMENT '调用的工具名称',
    tool_call_id VARCHAR(200) COMMENT '工具调用ID',
    
    -- 时间和状态
    timestamp DATETIME COMMENT '消息时间戳',
    duration_ms DECIMAL(10,2) COMMENT '该消息耗时(如果有)',
    stop_reason VARCHAR(50) COMMENT '停止原因',
    
    -- 元数据
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    
    UNIQUE KEY uk_message_id (message_id),
    INDEX idx_session_id (session_id),
    INDEX idx_parent_message_id (parent_message_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_skill_name (skill_name),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Session消息记录明细表(每条JSONL记录)';
```

**说明**:
- 此表存储 JSONL 文件中**每条消息记录**的详细信息
- `message_id` 对应 JSONL 中的 `id` 字段
- `parent_message_id` 对应 JSONL 中的 `parentId` 字段,用于构建对话链路
- "会话检索"页面查询此表,可以通过 `parent_message_id` 追溯完整的对话过程
- 通过递归查询或应用层组装,可以从任意一条消息追溯到整个对话树

**数据量估算**:
- 每条记录对应 Session Log JSONL 文件中的一行（一个 message 对象）
- 中等规模下：日均 10万条，月均 300万条，年均 3600万条
- 存储空间：每条约 500-1000 字节，年增量约 36 GB

**分区策略**: **按月分区**（推荐）

由于数据量大且查询模式明显（主要按时间范围查询），强烈建议采用分区策略：

```sql
-- 创建表时指定分区
CREATE TABLE session_message_detail (
    -- ... 字段定义同上 ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 
COMMENT='Session消息记录明细表(每条JSONL记录)'
PARTITION BY RANGE (TO_DAYS(timestamp)) (
    PARTITION p202601 VALUES LESS THAN (TO_DAYS('2026-02-01')),
    PARTITION p202602 VALUES LESS THAN (TO_DAYS('2026-03-01')),
    PARTITION p202603 VALUES LESS THAN (TO_DAYS('2026-04-01')),
    PARTITION p202604 VALUES LESS THAN (TO_DAYS('2026-05-01')),
    PARTITION p202605 VALUES LESS THAN (TO_DAYS('2026-06-01')),
    PARTITION p202606 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p202607 VALUES LESS THAN (TO_DAYS('2026-08-01')),
    PARTITION p202608 VALUES LESS THAN (TO_DAYS('2026-09-01')),
    PARTITION p202609 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p202610 VALUES LESS THAN (TO_DAYS('2026-11-01')),
    PARTITION p202611 VALUES LESS THAN (TO_DAYS('2026-12-01')),
    PARTITION p202612 VALUES LESS THAN (TO_DAYS('2027-01-01')),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
```

**分区优势**:
- ✅ **查询性能提升**: 分区裁剪（Partition Pruning）可大幅减少扫描行数
  - 查询最近7天数据：从全表扫描 1000万行 → 只扫描1个分区 ~300万行（3-5倍提升）
- ✅ **快速删除过期数据**: 直接 DROP PARTITION，比 DELETE 快数十倍
- ✅ **维护便利**: 可以单独备份、优化、修复某个分区
- ✅ **索引效率更高**: 每个分区的索引更小，查询更快

**分区维护**:

```sql
-- 1. 添加新分区（每月执行）
ALTER TABLE session_message_detail 
ADD PARTITION (PARTITION p202701 VALUES LESS THAN (TO_DAYS('2027-02-01')));

-- 2. 删除过期分区（如保留12个月）
ALTER TABLE session_message_detail 
DROP PARTITION p202501;

-- 3. 查看各分区的数据量
SELECT 
    PARTITION_NAME,
    TABLE_ROWS,
    DATA_LENGTH / 1024 / 1024 AS data_mb,
    INDEX_LENGTH / 1024 / 1024 AS index_mb
FROM INFORMATION_SCHEMA.PARTITIONS
WHERE TABLE_SCHEMA = 'your_database'
  AND TABLE_NAME = 'session_message_detail'
ORDER BY PARTITION_ORDINAL_POSITION;
```

**自动化维护**（可选）:

```sql
-- 创建定时任务，每月1号自动管理分区
CREATE EVENT auto_manage_partitions
ON SCHEDULE EVERY 1 MONTH
STARTS '2026-05-01 00:00:00'
DO
BEGIN
    -- 添加下月分区
    SET @next_month = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 2 MONTH), '%Y%m');
    SET @sql = CONCAT('ALTER TABLE session_message_detail ADD PARTITION (PARTITION p', @next_month, ' VALUES LESS THAN (TO_DAYS(''', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-01'), ''')))');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    
    -- 删除12个月前的分区
    SET @old_month = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 MONTH), '%Y%m');
    SET @sql = CONCAT('ALTER TABLE session_message_detail DROP PARTITION p', @old_month);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END;
```

**数据保留策略**:
- **热数据**（最近3个月）: 保留在 `session_message_detail`，频繁查询
- **温数据**（3-12个月）: 根据查询需求决定，可选择性归档
- **冷数据**（>12个月）: 归档到历史表或删除，释放存储空间

**替代方案**（小规模场景）:
- 如果日均消息数 < 1万，可以不分区，采用定期归档策略
- 将3个月前的数据移动到 `session_message_detail_archive` 表
- 但推荐直接使用分区，便于后续扩展

### 5.8 会话对话单元表 (Turn)

**业务定义**: 
- **对话(Turn)**: 从一条 `role="user"` 的消息开始，到下一条 `role="user"` 消息之前的所有消息构成一个完整的对话单元
- 代表一次完整的"用户输入 → AI回复"交互循环
- 是前端展示和用户理解的自然单位

**数据生成方式**: Collector 端预聚合 + Center Service UPSERT

#### 设计思路

**为什么需要 Turn 表？**

直接从 `session_message_detail` 实时计算 Turn 列表会面临以下挑战：

1. **复杂的窗口函数查询**: 需要使用 SQL 窗口函数识别每个 Turn 的边界
2. **性能开销大**: 每次查询需要扫描整个 Session 的所有消息并聚合
3. **分页困难**: Turn 不是物理存在的记录，无法直接 LIMIT OFFSET
4. **响应慢**: 预估性能差距 5-10 倍（无 Turn 表: 500ms-2s vs 有 Turn 表: 50ms-200ms）

**Turn 表的优势**:
- ✅ 查询性能提升 5-10 倍
- ✅ 支持高效的多维度过滤（按技能、状态、时间等）
- ✅ 分页简单直接
- ✅ 与应用层语义对齐（Turn 是业务概念）

#### 增量更新策略

**核心挑战**: Collector 推送的是增量数据，当前最后一个 Turn 可能还未完成（没有下一个 user 消息）

**解决方案**:

1. **Turn ID 生成规则**: 使用 `session_id + user_message_id` 组合
   - 格式: `{session_id}_turn_{user_message_id_hash}`
   - 示例: `sess_abc123_turn_a1b2c3d4e5f6g7h8`
   - 优势: 唯一性天然保证、不依赖解析顺序、可追溯

2. **is_complete 状态字段**: 区分已完成和进行中的 Turn
   - `false`: Turn 进行中（当前最后一个 user 消息，等待后续消息或超时）
   - `true`: Turn 已完成（收到下一个 user 消息）

3. **UPSERT 累加逻辑**: Center Service 接收时动态累加指标
   - 首次推送: INSERT 新记录，`is_complete=false`
   - 后续推送: UPDATE 累加 tokens、message_count、skills 等
   - 收到下一个 user 消息: 标记前一个 Turn 为 `is_complete=true`

4. **应用层去重**: Center Service 合并 skills_json 和 tools_json 时去重

#### 表结构设计

```sql
CREATE TABLE session_turn (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 关联信息
    turn_id VARCHAR(128) NOT NULL COMMENT 'Turn ID (格式: sessionID_turn_userMessageIDHash)',
    session_id VARCHAR(64) NOT NULL COMMENT '所属 Session',
    instance_id VARCHAR(64) COMMENT '实例ID',
    user_id VARCHAR(64) COMMENT '用户ID',
    
    -- Turn 边界
    first_message_id VARCHAR(200) NOT NULL COMMENT '第一条消息ID (user角色)',
    last_message_id VARCHAR(200) COMMENT '最后一条消息ID',
    
    -- 时间信息
    start_timestamp DATETIME NOT NULL COMMENT 'Turn开始时间',
    end_timestamp DATETIME COMMENT 'Turn结束时间',
    duration_ms INT COMMENT '耗时(毫秒)',
    
    -- 用户输入
    user_input_preview VARCHAR(500) COMMENT '用户输入预览(前500字符)',
    
    -- 聚合指标
    total_tokens INT DEFAULT 0,
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    
    message_count INT DEFAULT 0 COMMENT 'Turn内消息总数',
    skill_calls INT DEFAULT 0 COMMENT '技能调用次数',
    tool_calls INT DEFAULT 0 COMMENT '工具调用次数',
    
    -- 技能和工具列表 (JSON数组)
    skills_json JSON COMMENT '调用的技能列表 ["skill1", "skill2"]',
    tools_json JSON COMMENT '调用的工具列表 ["tool1", "tool2"]',
    
    -- 状态和质量
    result_status BOOLEAN DEFAULT TRUE COMMENT '结果状态: true=成功, false=失败',
    quality_status TINYINT DEFAULT 0 COMMENT '质量评价: 0=未评价, 1=正确, 2=错误, 3=待优化',
    
    -- Turn 完成状态
    is_complete BOOLEAN DEFAULT FALSE COMMENT 'Turn是否完成',
    complete_reason VARCHAR(50) COMMENT '完成原因: next_user/timeout/null',
    completed_at DATETIME COMMENT '完成时间',
    
    -- 元数据
    log_file_path VARCHAR(500) COMMENT '所属日志文件路径',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_turn_id (turn_id),
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_start_timestamp (start_timestamp),
    INDEX idx_is_complete (is_complete),
    INDEX idx_result_status (result_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 
COMMENT='Session Turn表(支持增量更新)';
```

**说明**:
- **不分区**: Turn 表数据量约为 `session_message_detail` 的 10%-20%，初期无需分区
- **未来扩展**: 当数据量达到千万级时，可考虑按月分区
- **触发条件**: table_rows > 1000万 或 data_size > 50GB 或 P95查询 > 500ms

#### Collector 端处理逻辑

```go
// TurnRecord - Turn 聚合记录
type TurnRecord struct {
    TurnID           string    `json:"turn_id"`
    SessionID        string    `json:"session_id"`
    UserID           string    `json:"user_id"`
    FirstMessageID   string    `json:"first_message_id"`
    LastMessageID    string    `json:"last_message_id"`
    StartTimestamp   time.Time `json:"start_timestamp"`
    EndTimestamp     time.Time `json:"end_timestamp"`
    DurationMs       int       `json:"duration_ms"`
    UserInputPreview string    `json:"user_input_preview"`
    TotalTokens      int       `json:"total_tokens"`
    InputTokens      int       `json:"input_tokens"`
    OutputTokens     int       `json:"output_tokens"`
    MessageCount     int       `json:"message_count"`
    SkillCalls       int       `json:"skill_calls"`
    ToolCalls        int       `json:"tool_calls"`
    Skills           []string  `json:"skills"`
    Tools            []string  `json:"tools"`
    ResultStatus     bool      `json:"result_status"`
    IsComplete       bool      `json:"is_complete"`
    CompleteReason   string    `json:"complete_reason"`
}

// extractTurns - 从 Session Log 条目中提取 Turn 记录
func extractTurns(entries []LogEntry) []TurnRecord {
    var turns []TurnRecord
    var currentTurn *TurnRecord
    
    for _, entry := range entries {
        if entry.Message.Role == "user" {
            // 保存上一个 Turn（标记为已完成）
            if currentTurn != nil {
                finalizeTurn(currentTurn, "next_user")
                turns = append(turns, *currentTurn)
            }
            
            // 开始新 Turn
            currentTurn = &TurnRecord{
                TurnID:           generateTurnID(extractSessionID(entry.FilePath), entry.ID),
                SessionID:        extractSessionID(entry.FilePath),
                UserID:           entry.UserID,
                FirstMessageID:   entry.ID,
                StartTimestamp:   entry.Timestamp,
                UserInputPreview: truncateContent(entry.Content, 500),
                IsComplete:       false,
                LastMessageID:    entry.ID,
                MessageCount:     1,
                TotalTokens:      entry.Usage.TotalTokens,
                InputTokens:      entry.Usage.InputTokens,
                OutputTokens:     entry.Usage.OutputTokens,
            }
        } else if currentTurn != nil {
            // 累加当前 Turn 的指标
            currentTurn.LastMessageID = entry.ID
            currentTurn.EndTimestamp = entry.Timestamp
            currentTurn.TotalTokens += entry.Usage.TotalTokens
            currentTurn.InputTokens += entry.Usage.InputTokens
            currentTurn.OutputTokens += entry.Usage.OutputTokens
            currentTurn.MessageCount++
            
            // 收集技能和工具
            if entry.Message.Role == "assistant" {
                for _, content := range entry.Message.Content {
                    if content.Type == "toolCall" && content.Name != "" {
                        currentTurn.Skills = append(currentTurn.Skills, content.Name)
                        currentTurn.ToolCalls++
                    }
                }
                
                // 检查结果状态
                if entry.Message.StopReason == "error" || entry.Message.IsError {
                    currentTurn.ResultStatus = false
                }
            }
        }
    }
    
    // 最后一个 Turn 保持未完成状态
    if currentTurn != nil {
        currentTurn.DurationMs = calculateDuration(currentTurn.StartTimestamp, currentTurn.EndTimestamp)
        turns = append(turns, *currentTurn)
    }
    
    return turns
}

// finalizeTurn - 完成 Turn 并计算最终指标
func finalizeTurn(turn *TurnRecord, reason string) {
    // 计算耗时
    turn.DurationMs = int(turn.EndTimestamp.Sub(turn.StartTimestamp).Milliseconds())
    
    // 去重技能和工具列表
    turn.Skills = uniqueStrings(turn.Skills)
    turn.Tools = uniqueStrings(turn.Tools)
    
    // 标记为完成
    turn.IsComplete = true
    turn.CompleteReason = reason
    turn.CompletedAt = time.Now()
}

// generateTurnID - 生成 Turn ID (使用 Hash 避免特殊字符问题)
func generateTurnID(sessionID string, userMessageID string) string {
    hash := sha256.Sum256([]byte(userMessageID))
    shortHash := hex.EncodeToString(hash[:8])  // 取前16位
    return fmt.Sprintf("%s_turn_%s", sessionID, shortHash)
}

// uniqueStrings - 字符串数组去重
func uniqueStrings(slice []string) []string {
    seen := make(map[string]bool)
    result := []string{}
    for _, s := range slice {
        if !seen[s] {
            seen[s] = true
            result = append(result, s)
        }
    }
    return result
}
```

#### Center Service 接收逻辑

```java
// TurnService.java - Center Service 端处理
@Service
public class TurnService {
    
    @Autowired
    private SessionTurnMapper turnMapper;
    
    /**
     * 批量插入或更新 Turn 记录
     */
    @Transactional
    public void upsertTurns(List<TurnRecord> turns) {
        for (TurnRecord turn : turns) {
            // 1. 查询现有记录
            SessionTurn existing = turnMapper.selectByTurnId(turn.getTurnId());
            
            if (existing != null) {
                // 2. 合并技能和工具列表（去重）
                List<String> mergedSkills = mergeAndDeduplicate(
                    existing.getSkillsJson(), 
                    turn.getSkills()
                );
                List<String> mergedTools = mergeAndDeduplicate(
                    existing.getToolsJson(), 
                    turn.getTools()
                );
                
                // 3. 更新记录（累加指标）
                existing.setLastMessageId(turn.getLastMessageId());
                existing.setEndTimestamp(turn.getEndTimestamp());
                existing.setDurationMs(turn.getDurationMs());
                existing.setTotalTokens(existing.getTotalTokens() + turn.getTotalTokens());
                existing.setInputTokens(existing.getInputTokens() + turn.getInputTokens());
                existing.setOutputTokens(existing.getOutputTokens() + turn.getOutputTokens());
                existing.setMessageCount(existing.getMessageCount() + turn.getMessageCount());
                existing.setSkillCalls(existing.getSkillCalls() + turn.getSkillCalls());
                existing.setToolCalls(existing.getToolCalls() + turn.getToolCalls());
                existing.setSkillsJson(mergedSkills);
                existing.setToolsJson(mergedTools);
                existing.setResultStatus(turn.getResultStatus());
                
                // 只有当 is_complete=true 时才更新完成状态
                if (turn.getIsComplete()) {
                    existing.setIsComplete(true);
                    existing.setCompleteReason(turn.getCompleteReason());
                    existing.setCompletedAt(turn.getCompletedAt());
                }
                
                existing.setUpdatedAt(new Date());
                turnMapper.updateById(existing);
            } else {
                // 4. 插入新记录
                SessionTurn newTurn = convertToEntity(turn);
                turnMapper.insert(newTurn);
            }
        }
    }
    
    /**
     * 合并两个字符串列表并去重
     */
    private List<String> mergeAndDeduplicate(List<String> list1, List<String> list2) {
        Set<String> merged = new LinkedHashSet<>(); // 保持插入顺序
        if (list1 != null) {
            merged.addAll(list1);
        }
        if (list2 != null) {
            merged.addAll(list2);
        }
        return new ArrayList<>(merged);
    }
}
```

#### 典型查询场景

```sql
-- 1. 分页查询用户的对话列表（包含进行中的 Turn）
SELECT 
    turn_id,
    start_timestamp AS time_stamp,
    user_id,
    user_input_preview AS user_input,
    duration_ms,
    result_status,
    quality_status,
    total_tokens,
    input_tokens,
    output_tokens,
    skills_json AS skills,
    tools_json AS tools,
    is_complete,
    log_file_path AS log_file_name
FROM session_turn
WHERE user_id = :userId
  AND start_timestamp BETWEEN :startTime AND :endTime
ORDER BY start_timestamp DESC
LIMIT :pageSize OFFSET :offset;

-- 2. 按技能过滤
SELECT * FROM session_turn
WHERE user_id = :userId
  AND JSON_CONTAINS(skills_json, '"pptx"')
ORDER BY start_timestamp DESC;

-- 3. 只查询已完成的 Turn
SELECT * FROM session_turn
WHERE user_id = :userId
  AND is_complete = TRUE
ORDER BY start_timestamp DESC;

-- 4. 统计某用户的 Turn 数量
SELECT 
    COUNT(*) AS total_turns,
    SUM(CASE WHEN is_complete = TRUE THEN 1 ELSE 0 END) AS completed_turns,
    SUM(CASE WHEN is_complete = FALSE THEN 1 ELSE 0 END) AS incomplete_turns
FROM session_turn
WHERE user_id = :userId;
```

#### 前端展示逻辑

```typescript
interface Turn {
  turnId: string;
  timeStamp: number;
  userName: string;
  userInput: string;
  durationMs?: number;
  resultStatus: boolean;
  qualityStatus: number;
  tokens: { total: number; input: number; output: number };
  skills: string[];
  tools: string[];
  isComplete: boolean;  // ⭐ 新增字段
  logFileName: string;
}

function TurnCard({ turn }: { turn: Turn }) {
  // 1. 进行中的 Turn
  if (!turn.isComplete) {
    return (
      <div className="turn-card processing">
        <div className="status-indicator">
          <Spinner size="small" />
          <span>AI 正在回复...</span>
        </div>
        <div className="turn-info">
          <span className="user-input">{turn.userInput}</span>
          <span className="meta">
            已处理 {turn.messageCount} 条消息
            {turn.durationMs && ` · ${formatDuration(turn.durationMs)}`}
          </span>
        </div>
      </div>
    );
  }
  
  // 2. 已完成的 Turn - 成功
  if (turn.resultStatus === true) {
    return (
      <div className="turn-card success">
        <div className="status-indicator">
          <CheckIcon />
          <span>完成</span>
        </div>
        <div className="turn-info">
          <span className="user-input">{turn.userInput}</span>
          <span className="meta">
            {formatDuration(turn.durationMs)} · 
            {turn.tokens.total.toLocaleString()} tokens
          </span>
        </div>
      </div>
    );
  }
  
  // 3. 已完成的 Turn - 失败
  return (
    <div className="turn-card error">
      <div className="status-indicator">
        <ErrorIcon />
        <span>失败</span>
      </div>
      <div className="turn-info">
        <span className="user-input">{turn.userInput}</span>
        <span className="meta">
          {formatDuration(turn.durationMs)} · 
          点击查看错误详情
        </span>
      </div>
    </div>
  );
}
```

**UI 效果示意**:

```
┌─────────────────────────────────────────────┐
│ 🔄 AI 正在回复...                            │
│ 帮我生成一份二十届三中全会学习PPT              │
│ 已处理 5 条消息 · 1分26秒                     │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ ✅ 完成                                      │
│ 帮我写一份季度工作总结                        │
│ 45秒 · 12,340 tokens                         │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ ❌ 失败                                      │
│ 分析这份销售数据                              │
│ 2分钟 · 点击查看错误详情                      │
└─────────────────────────────────────────────┘
```

#### 数据流示例

**场景 1: 正常对话流程**

```
时间线:
T1: User: "帮我生成PPT"
T2: Assistant: [思考中...]
T3: Assistant: [调用技能: pptx]
T4: Assistant: "好的，已生成"
T5: User: "再帮我写一份报告"  ← 触发 Turn 1 完成

Collector 推送批次 1 (T1-T4):
├─ Turn 1: is_complete=false, message_count=4
└─ Center Service: INSERT INTO session_turn (...)

Collector 推送批次 2 (T5):
├─ Turn 1: is_complete=true, complete_reason="next_user"  ← 更新
├─ Turn 2: is_complete=false, message_count=1             ← 新建
└─ Center Service: 
    UPDATE session_turn SET is_complete=true WHERE turn_id="..."
    INSERT INTO session_turn (Turn 2)
```

**场景 2: 用户中断对话（无超时机制）**

```
时间线:
T1: User: "帮我生成PPT"
T2: Assistant: [思考中...]
T3: Assistant: [调用技能: pptx]
... 用户长时间没有继续对话 ...

Turn 状态:
├─ Turn 1: is_complete=false (保持未完成状态)
└─ 前端查询时显示: "🔄 AI 正在回复..."

注意: 不引入超时自动完成逻辑，直接返回当前状态
```

**场景 3: Assistant 流式输出**

```
时间线:
T1: User: "帮我生成PPT"
T2: Assistant: [stream chunk 1]
T3: Assistant: [stream chunk 2]
T4: Assistant: [stream chunk 3]
... 还在输出中 ...

Collector 每次推送都会更新 Turn:
批次 1: Turn 1, message_count=2, is_complete=false
批次 2: Turn 1, message_count=3, is_complete=false  ← 累加
批次 3: Turn 1, message_count=4, is_complete=false  ← 累加

Center Service UPSERT:
UPDATE session_turn SET
  message_count = message_count + VALUES(message_count),
  total_tokens = total_tokens + VALUES(total_tokens),
  ...
WHERE turn_id = "...";

前端轮询查询:
GET /api/v1/turns/search?userName=王颜
→ 返回 Turn 1 (is_complete: false)
→ 前端显示: "🔄 AI 正在回复..." + 实时更新的消息数
```

#### 性能评估

**数据量估算**:
- 假设日均 10 万条 JSONL 消息
- 平均每个 Turn 有 5-10 条消息
- 日均 Turn 数: 1万 - 2万
- 月均 Turn 数: 30万 - 60万
- 年均 Turn 数: 360万 - 720万

**存储空间**:
- 每条约 500-800 字节
- 年增量约 2-6 GB

**查询性能对比**:

| 场景 | 无 Turn 表 | 有 Turn 表 | 提升倍数 |
|------|-----------|-----------|---------|
| 分页查询（10条/页） | 500ms - 2s | 50ms - 200ms | 5-10x |
| 按技能过滤 | 800ms - 3s | 80ms - 300ms | 5-10x |
| 统计查询 | 1s - 5s | 100ms - 500ms | 5-10x |

**结论**: Turn 表能显著提升查询性能，特别是在数据量增长后优势更明显。

---

## 六、关键数据结构

### 6.1 Go Collector 数据结构

```go
// SessionLogEntry - JSONL 单行解析结果
type SessionLogEntry struct {
    ID        string  `json:"id"`          // 消息记录ID
    ParentID  string  `json:"parentId"`    // 父消息ID (形成对话链)
    Type      string  `json:"type"`
    Timestamp string  `json:"timestamp"`
    Message   Message `json:"message"`
}

type Message struct {
    Role       string    `json:"role"`
    Provider   string    `json:"provider"`
    Model      string    `json:"model"`
    Content    []Content `json:"content"`
    Usage      *Usage    `json:"usage"`
    DurationMs int       `json:"durationMs"`
    IsError    bool      `json:"is_error,omitempty"`
}

type Content struct {
    Type     string                 `json:"type"`
    Name     string                 `json:"name,omitempty"`
    Input    map[string]interface{} `json:"input,omitempty"`
    ToolUseID string                `json:"tool_use_id,omitempty"`
    IsError  bool                   `json:"is_error,omitempty"`
}

type Usage struct {
    Input      int `json:"input"`
    Output     int `json:"output"`
    CacheRead  int `json:"cacheRead"`
    CacheWrite int `json:"cacheWrite"`
    Total      int `json:"totalTokens"`
}

// SessionMetrics - 聚合后的指标 (文件级别)
type SessionMetrics struct {
    SessionID          string    `json:"session_id"`
    InstanceID         string    `json:"instance_id"`
    UserID             string    `json:"user_id,omitempty"`
    UserName           string    `json:"user_name,omitempty"`
    Team               string    `json:"team,omitempty"`
    FilePath           string    `json:"file_path"`
    FileName           string    `json:"file_name"`
    FileSize           int64     `json:"file_size"`
    LineCount          int       `json:"line_count"`
    FirstTimestamp     time.Time `json:"first_timestamp"`
    LastTimestamp      time.Time `json:"last_timestamp"`
    
    TotalTokens        int64     `json:"total_tokens"`
    InputTokens        int64     `json:"input_tokens"`
    OutputTokens       int64     `json:"output_tokens"`
    
    MessageCount       int       `json:"message_count"`
    UserMessages       int       `json:"user_messages"`
    AssistantMessages  int       `json:"assistant_messages"`
    
    SkillCalls         int       `json:"skill_calls"`
    ToolCalls          int       `json:"tool_calls"`
    
    AvgDurationMs      float64   `json:"avg_duration_ms"`
    MaxDurationMs      float64   `json:"max_duration_ms"`
    
    ErrorCount         int       `json:"error_count"`
    SuccessCount       int       `json:"success_count"`
    
    ParentSessionID    string    `json:"parent_session_id,omitempty"`
    UserInputPreview   string    `json:"user_input_preview,omitempty"`
    Result             string    `json:"result"`
    Quality            string    `json:"quality"`
    
    CommonSkills       []string  `json:"common_skills"`
}

// BatchPushPayload - 批量推送载荷
type BatchPushPayload struct {
    CollectorID      string                 `json:"collector_id"`
    Timestamp        time.Time              `json:"timestamp"`
    Metrics          []SessionMetrics       `json:"metrics"`           // 文件级别聚合
    MessageDetails   []MessageDetailRecord  `json:"message_details"`   // 消息明细记录
}

// MessageDetailRecord - 消息明细记录 (每条JSONL记录)
type MessageDetailRecord struct {
    SessionID       string    `json:"session_id"`
    FilePath        string    `json:"file_path"`
    
    MessageID       string    `json:"message_id"`        // JSONL中的id字段
    ParentMessageID string    `json:"parent_message_id"` // JSONL中的parentId字段
    
    Role            string    `json:"role"`
    ContentSummary  string    `json:"content_summary"`
    
    TotalTokens     int       `json:"total_tokens"`
    InputTokens     int       `json:"input_tokens"`
    OutputTokens    int       `json:"output_tokens"`
    
    SkillName       string    `json:"skill_name,omitempty"`
    ToolName        string    `json:"tool_name,omitempty"`
    ToolCallID      string    `json:"tool_call_id,omitempty"`
    
    Timestamp       time.Time `json:"timestamp"`
    DurationMs      float64   `json:"duration_ms,omitempty"`
    StopReason      string    `json:"stop_reason,omitempty"`
}
```

### 6.2 SpringBoot 数据结构

```java
// SessionLogMetadataDTO
@Data
public class SessionLogMetadataDTO {
    private String sessionId;
    private String instanceId;
    private String userId;
    // @Deprecated private String userName;  // 已移除，数据库表中无此字段
    // @Deprecated private String team;      // 已移除，数据库表中无此字段
    
    private String filePath;
    private String fileName;
    private Long fileSize;
    private Integer lineCount;
    
    private LocalDateTime firstTimestamp;
    private LocalDateTime lastTimestamp;
    
    private Long totalTokens;
    private Long inputTokens;
    private Long outputTokens;
    
    private Integer messageCount;
    private Integer userMessages;
    private Integer assistantMessages;
    
    private Integer skillCalls;
    private Integer toolCalls;
    
    private BigDecimal avgDurationMs;
    private BigDecimal maxDurationMs;
    
    private Integer errorCount;
    private Integer successCount;
    
    private String userInputPreview;
    private String result;
    private String quality;
    
    private List<String> commonSkills;
    private String status;
    private LocalDateTime ingestedAt;
}

// DashboardSummaryDTO - 运营大盘汇总
@Data
public class DashboardSummaryDTO {
    // 顶部统计
    private Long cumulativeTokens;
    private Long totalConversations;
    private Long totalSkillCalls;
    private Integer totalUsers;
    
    // 卡片数据
    private Long dailyTokens;
    private Long dailyConversations;
    private Long dailySkillCalls;
    private Integer dailyActiveUsers;
    
    // 趋势数据
    private List<TimeSeriesPoint> trendData;
    
    // 用户列表
    private List<UserActivityDTO> userList;
}

// MessageDetailDTO - 消息明细 (每条JSONL记录)
@Data
public class MessageDetailDTO {
    private Long id;
    private String sessionId;
    private String filePath;
    
    private String messageId;          // JSONL中的id字段
    private String parentMessageId;    // JSONL中的parentId字段
    
    private String role;
    private String contentSummary;
    
    private Integer totalTokens;
    private Integer inputTokens;
    private Integer outputTokens;
    
    private String skillName;
    private String toolName;
    private String toolCallId;
    
    private LocalDateTime timestamp;
    private BigDecimal durationMs;
    private String stopReason;
    
    private LocalDateTime collectedAt;
}

// TimeSeriesPoint - 时间序列数据点
@Data
public class TimeSeriesPoint {
    private LocalDateTime time;
    private Long tokens;
    private Integer sessions;
    private Integer skillCalls;
}

// UserActivityDTO - 用户活跃度
@Data
public class UserActivityDTO {
    private String userId;
    // @Deprecated private String userName;  // 已移除，数据库表中无此字段
    // @Deprecated private String team;      // 已移除，数据库表中无此字段
    private Integer sessionCount;
    private Integer messageCount;
    private Long totalTokens;
    private Integer skillCalls;
    private String status;
}
```

---

## 七、前后端接口设计

### 7.1 数据采集接口

#### POST /api/collector/metrics/batch

**请求:**

**请求:**
```http
POST /api/collector/metrics/batch
Content-Type: application/json
Content-Encoding: gzip

{
  "collector_id": "collector-001",
  "timestamp": "2026-04-14T10:00:00Z",
  "metrics": [
    {
      "session_id": "session-abc",
      "instance_id": "inst-001",
      "user_id": "18101142",
      "user_name": "王颜",
      "team": "新技术",
      "file_path": "/datafs/openclaw/abc/sessions/session-abc.jsonl",
      "file_name": "session-abc.jsonl",
      "file_size": 15400,
      "line_count": 120,
      "first_timestamp": "2026-04-14T09:00:00Z",
      "last_timestamp": "2026-04-14T10:00:00Z",
      "total_tokens": 15400,
      "input_tokens": 5400,
      "output_tokens": 10000,
      "message_count": 6,
      "user_messages": 3,
      "assistant_messages": 3,
      "skill_calls": 2,
      "tool_calls": 1,
      "avg_duration_ms": 86000,
      "max_duration_ms": 120000,
      "error_count": 0,
      "success_count": 3,
      "result": "success",
      "quality": "良好",
      "common_skills": ["公文写作", "ppt生成"],
      "status": "active"
    }
  ],
  "message_details": [
    {
      "session_id": "session-abc",
      "file_path": "/datafs/openclaw/abc/sessions/session-abc.jsonl",
      
      "message_id": "entry-120",
      "parent_message_id": null,
      
      "role": "user",
      "content_summary": "帮我生成一个公文写作模板...",
      
      "total_tokens": 5400,
      "input_tokens": 5400,
      "output_tokens": 0,
      
      "skill_name": "公文写作",
      "tool_name": null,
      "tool_call_id": null,
      
      "timestamp": "2026-04-14T09:30:00Z",
      "duration_ms": 200,
      "stop_reason": null
    },
    {
      "session_id": "session-abc",
      "file_path": "/datafs/openclaw/abc/sessions/session-abc.jsonl",
      
      "message_id": "entry-121",
      "parent_message_id": "entry-120",
      
      "role": "assistant",
      "content_summary": "好的,我来帮你生成公文写作模板...",
      
      "total_tokens": 10000,
      "input_tokens": 5400,
      "output_tokens": 4600,
      
      "skill_name": "公文写作",
      "tool_name": "write_document",
      "tool_call_id": "tool_abc123",
      
      "timestamp": "2026-04-14T09:31:26Z",
      "duration_ms": 86000,
      "stop_reason": "stop"
    }
  ]
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accepted": 1,
    "failed": 0
  }
}
```

---

#### POST /api/collector/health/update

**说明**: 更新实例健康状态到Redis缓存

**请求:**
```http
POST /api/collector/health/update
Content-Type: application/json

{
  "instance_id": "inst-001",
  "timestamp": "2026-04-14T11:29:29Z",
  "health_data": {
    "status": "active",
    "version": "1.0.0",
    "nodeId": "node-abc123",
    "channelsTotal": 5,
    "channelsLinked": 4,
    "agentsTotal": 3,
    "agentsIdle": 2,
    "uptime": 86400,
    "memoryRss": 524288000
  }
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 7.2 运营大盘接口

**说明**: 以下接口对应 API 接口文档中的"模块一：下拉列表数据字典接口；模块二：龙虾运营大盘接口"

---

#### GET /api/v1/skills/options

**说明**: 获取技能下拉列表数据字典

**请求:**
```http
GET /api/v1/skills/options
```

**响应:**
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

---

#### GET /api/v1/dashboard/global-stats

**说明**: 获取平台全局累计数据（不受筛选条件影响）

**请求:**
```http
GET /api/v1/dashboard/global-stats
```

**响应:**
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

---

#### POST /api/v1/dashboard/summary

**说明**: 获取大盘统计卡片数据（受筛选条件联动）

**通用请求体参数** (应用于 summary、trend、usersummary 接口):
```json
{
  "teamName": "新技术",
  "userName": "王颜",
  "startTime": 1680000000000,
  "endTime": 1680000000000
}
```

**请求:**
```http
POST /api/v1/dashboard/summary
Content-Type: application/json

{
  "teamName": "新技术",
  "userName": "王颜",
  "startTime": 1680000000000,
  "endTime": 1680000000000
}
```

**响应:**
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

---

#### POST /api/v1/dashboard/trend

**说明**: 获取热度趋势图表数据

**请求:**
```http
POST /api/v1/dashboard/trend
Content-Type: application/json

{
  "teamName": "新技术",
  "userName": "王颜",
  "startTime": 1680000000000,
  "endTime": 1680000000000
}
```

**响应:**
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
    },
    {
      "timeLabel": 1680000000000,
      "tokens": 5200,
      "turns": 140,
      "skills": 95
    }
  ]
}
```

---

#### POST /api/v1/dashboard/usersummary

**说明**: 分页查询用户状态与消耗明细

**请求:**
```http
POST /api/v1/dashboard/usersummary
Content-Type: application/json

{
  "teamName": "新技术",
  "userName": "王颜",
  "startTime": 1680000000000,
  "endTime": 1680000000000,
  "page": 1,
  "pageSize": 10
}
```

**响应:**
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

---

### 7.3 对话记录检索接口 (Turn)

#### POST /api/v1/turns/search

**说明**: 此接口对应 API 接口文档中的"模块三 - 6. 分页查询对话记录"

**请求:**
```http
POST /api/v1/turns/search
Content-Type: application/json

{
  "userName": "王颜",
  "startTime": 1680000000000,
  "endTime": 1680000000000,
  "skillId": "pptx",
  "page": 1,
  "pageSize": 10
}
```

**参数:**
- `userName`: 姓名(可选)
- `startTime`: 开始时间戳(毫秒)
- `endTime`: 结束时间戳(毫秒)
- `skillId`: 技能ID(可选)
- `page`: 页码(默认1)
- `pageSize`: 每页数量(默认10)

**响应:**
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
        "turnId": "sess_abc123_turn_a1b2c3d4e5f6g7h8",
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
        "isComplete": false,
        "logFileName": "/datafs/openclaw/agents/main/sessions/xxx.jsonl"
      }
    ]
  }
}
```

**SQL 查询示例**:
```sql
SELECT 
    turn_id,
    UNIX_TIMESTAMP(start_timestamp) * 1000 AS time_stamp,
    user_id,
    user_input_preview AS user_input,
    duration_ms,
    result_status,
    quality_status,
    total_tokens,
    input_tokens,
    output_tokens,
    skills_json AS skills,
    tools_json AS tools,
    is_complete,
    log_file_path AS log_file_name
FROM session_turn
WHERE user_id = :userName
  AND start_timestamp >= FROM_UNIXTIME(:startTime / 1000)
  AND start_timestamp <= FROM_UNIXTIME(:endTime / 1000)
  AND (:skillId IS NULL OR JSON_CONTAINS(skills_json, CONCAT('"', :skillId, '"')))
ORDER BY start_timestamp DESC
LIMIT :pageSize OFFSET (:page - 1) * :pageSize;
```

**说明**: 
- 数据源: `session_turn` 表(不是 `session_message_detail`)
- `isComplete` 字段用于前端区分"进行中"和"已完成"的对话
- 支持按技能ID过滤(JSON_CONTAINS)

---

#### GET /api/v1/turns/{turnId}/trace

**说明**: 此接口对应 API 接口文档中的"模块三 - 7. 获取执行链路详情"

**请求:**
```http
GET /api/v1/turns/sess_abc123_turn_a1b2c3d4e5f6g7h8/trace
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "turnId": "sess_abc123_turn_a1b2c3d4e5f6g7h8",
    "nodes": [
      {
        "stepOrder": 0,
        "nodeType": "user_input",
        "nodeName": "用户输入",
        "status": true,
        "timeStamp": 1680000000000
      },
      {
        "stepOrder": 1,
        "nodeType": "skill_call",
        "nodeName": "公文写作技能调用",
        "status": true,
        "timeStamp": 1680000001000
      },
      {
        "stepOrder": 2,
        "nodeType": "tool_call",
        "nodeName": "xxx工具调用",
        "status": true,
        "timeStamp": 1680000002000
      },
      {
        "stepOrder": 3,
        "nodeType": "reply",
        "nodeName": "回复用户",
        "status": false,
        "timeStamp": 1680000003000
      }
    ]
  }
}
```

**实现逻辑**:
```java
// TurnTraceService.java
public TurnTraceResponse getTurnTrace(String turnId) {
    // 1. 从 session_turn 表获取 Turn 信息
    SessionTurn turn = turnMapper.selectByTurnId(turnId);
    if (turn == null) {
        throw new NotFoundException("Turn not found: " + turnId);
    }
    
    // 2. 从 session_message_detail 表查询该 Turn 范围内的所有消息
    List<SessionMessage> messages = messageMapper.selectByMessageRange(
        turn.getFirstMessageId(), 
        turn.getLastMessageId()
    );
    
    // 3. 构建执行链路节点
    List<TraceNode> nodes = new ArrayList<>();
    int stepOrder = 0;
    
    for (SessionMessage msg : messages) {
        TraceNode node = new TraceNode();
        node.setStepOrder(stepOrder++);
        node.setTimeStamp(msg.getTimestamp().getTime());
        
        // 根据 role 和 content 确定节点类型
        if ("user".equals(msg.getRole())) {
            node.setNodeType("user_input");
            node.setNodeName("用户输入");
            node.setStatus(true);
        } else if ("assistant".equals(msg.getRole())) {
            // 检查是否有技能/工具调用
            if (msg.getSkillName() != null) {
                node.setNodeType("skill_call");
                node.setNodeName(msg.getSkillName() + "技能调用");
                node.setStatus(msg.getStopReason() == null || !"error".equals(msg.getStopReason()));
            } else if (msg.getToolName() != null) {
                node.setNodeType("tool_call");
                node.setNodeName(msg.getToolName() + "工具调用");
                node.setStatus(msg.getStopReason() == null || !"error".equals(msg.getStopReason()));
            } else {
                node.setNodeType("reply");
                node.setNodeName("回复用户");
                node.setStatus(msg.getStopReason() == null || !"error".equals(msg.getStopReason()));
            }
        }
        
        nodes.add(node);
    }
    
    // 4. 组装响应
    TurnTraceResponse response = new TurnTraceResponse();
    response.setTurnId(turnId);
    response.setNodes(nodes);
    
    return response;
}
```

**SQL 查询示例**:
```sql
-- 查询 Turn 范围内的所有消息
SELECT 
    message_id,
    parent_message_id,
    role,
    content_summary,
    skill_name,
    tool_name,
    timestamp,
    stop_reason
FROM session_message_detail
WHERE session_id = :sessionId
  AND timestamp >= (
    SELECT start_timestamp FROM session_turn WHERE turn_id = :turnId
  )
  AND timestamp <= (
    SELECT COALESCE(end_timestamp, NOW()) FROM session_turn WHERE turn_id = :turnId
  )
ORDER BY timestamp ASC;
```

**说明**: 
- 数据源: `session_turn` + `session_message_detail` 联合查询
- 通过 `first_message_id` 和 `last_message_id` 确定消息范围
- 根据 `role`、`skill_name`、`tool_name` 等字段判断节点类型
- `status` 根据 `stop_reason` 判断(success/error)

---

## 八、性能优化与监控

### 8.1 Collector 增量处理策略

**核心思路**: 基于 `last_message_id` 实现增量处理，避免重复解析整个文件


#### 基于last_message_id的增量解析

```go
// processFileIncrementally 增量解析文件
func (c *Collector) processFileIncrementally(filePath string, lastMessageID string) error {
    file, err := os.Open(filePath)
    if err != nil {
        return err
    }
    defer file.Close()
    
    scanner := bufio.NewScanner(file)
    scanner.Buffer(make([]byte, 0, 1024*1024), 10*1024*1024)
    
    var newEntries []SessionLogEntry
    foundLastMessage := false
    
    // 如果没有last_message_id，说明是首次处理
    if lastMessageID == "" {
        foundLastMessage = true
    }
    
    for scanner.Scan() {
        var entry SessionLogEntry
        if err := json.Unmarshal(scanner.Bytes(), &entry); err != nil {
            log.Warn("Failed to parse line: %v", err)
            continue
        }
        
        // 找到上次处理的位置
        if !foundLastMessage {
            if entry.ID == lastMessageID {
                foundLastMessage = true
            }
            continue  // 跳过已处理的消息
        }
        
        // 处理新消息
        newEntries = append(newEntries, entry)
    }
    
    if len(newEntries) == 0 {
        return nil  // 没有新消息
    }
    
    // 聚合指标和消息明细
    metrics, messageDetails := aggregateEntries(newEntries)
    
    // 计算性能指标 (仅统计assistant消息)
    // 方法: assistant.timestamp - previous_message.timestamp
    var totalDuration float64
    var maxDuration float64
    var assistantCount int
    
    for i, entry := range newEntries {
        // 只统计assistant消息
        if entry.Message.Role == "assistant" && i > 0 {
            // 获取上一条消息的时间戳
            prevEntry := newEntries[i-1]
            
            // 解析时间戳
            currentTs, err1 := time.Parse(time.RFC3339, entry.Timestamp)
            prevTs, err2 := time.Parse(time.RFC3339, prevEntry.Timestamp)
            
            if err1 == nil && err2 == nil {
                durationMs := currentTs.Sub(prevTs).Milliseconds()
                
                // 过滤异常值 (负数或超过5分钟)
                if durationMs > 0 && durationMs < 300000 {
                    totalDuration += float64(durationMs)
                    if float64(durationMs) > maxDuration {
                        maxDuration = float64(durationMs)
                    }
                    assistantCount++
                }
            }
        }
    }
    
    var avgDuration float64
    if assistantCount > 0 {
        avgDuration = totalDuration / float64(assistantCount)
    }
    
    // 将性能指标添加到metrics中
    metrics.AvgDurationMs = avgDuration
    metrics.MaxDurationMs = maxDuration
    
    // 推送到后端
    payload := BatchPushPayload{
        CollectorID:    c.config.CollectorID,
        Timestamp:      time.Now().UTC(),
        Metrics:        []SessionMetrics{*metrics},
        MessageDetails: messageDetails,
    }
    
    if err := c.pushMetrics(payload); err != nil {
        return err  // 推送失败，不更新缓存
    }
    
    // 推送成功后更新缓存
    lastEntry := newEntries[len(newEntries)-1]
    fileInfo, _ := os.Stat(filePath)
    
    c.cache.Update(filePath, FileFingerprint{
        FilePath:       filePath,
        FileSize:       fileInfo.Size(),
        ModTime:        fileInfo.ModTime(),
        LastMessageID:  lastEntry.ID,  // 关键：更新last_message_id
        ProcessedLines: c.cache.Get(filePath).ProcessedLines + len(newEntries),
        ProcessedAt:    time.Now(),
        SessionID:      extractSessionID(fileInfo.Name()),
        Status:         "processed",
        FileStatus:     extractFileStatus(fileInfo.Name()),
    })
    
    // 定期保存缓存到磁盘
    if err := c.cache.save(); err != nil {
        log.Error("Failed to save cache: %v", err)
    }
    
    return nil
}
```

**关键优势**:
1. ✅ **高性能**: 只读取新消息，避免重复解析整个文件
2. ✅ **可靠性**: OpenClaw是append-only，基于message_id增量处理安全可靠
3. ✅ **容错性**: 推送失败时不更新last_message_id，下次重试
4. ✅ **简洁性**: Collector端不需要主动去重，天然不重复

---

## 九、项目结构

```
monitoring-dashboard/
├── collector/                    # Go 数据采集
│   ├── main.go
│   ├── config.go
│   ├── scanner.go               # JSONL 扫描
│   ├── parser.go                # JSONL 解析
│   ├── pusher.go                # 批量推送
│   └── go.mod
│
├── backend/                      # SpringBoot 后端
│   ├── pom.xml
│   └── src/main/java/com/openclaw/
│       ├── MonitoringApplication.java
│       ├── controller/
│       │   └── MonitoringController.java
│       ├── service/
│       │   ├── DashboardService.java
│       │   ├── SessionLogService.java
│       │   └── impl/
│       ├── mapper/
│       │   ├── SessionLogMapper.java
│       │   └── ...
│       ├── dto/
│       │   ├── ApiResponse.java
│       │   ├── BatchPushPayload.java
│       │   ├── DashboardSummaryDTO.java
│       │   ├── SessionSearchDTO.java
│       │   └── ...
│       └── entity/
│           ├── SessionLogMetadata.java
│           └── ...
│
├── database/                     # SQL 脚本
│   ├── schema.sql
│   └── init-data.sql
│
└── docs/
    └── api.md                   # API 文档
```

---

## 十、部署说明

### 10.1 环境要求

- **Go**: 1.21+
- **Java**: 17+
- **OceanBase**: MySQL 兼容模式
- **Redis**: 7+
- **NAS**: Session Log 挂载目录

### 10.2 Collector 配置

```yaml
# collector/config.yaml
collector_id: "collector-001"
center_service_url: "http://backend:8080"

# NAS 挂载路径
session_log_base_path: "/mnt/nas/openclaw/sessions"

# 扫描配置
scan_interval: 5m              # 扫描间隔
push_interval: 5m              # 推送间隔
max_concurrent_files: 10       # 并发解析文件数

# 重试配置
max_retries: 3
retry_interval: 30s
```

### 10.3 Backend 配置

```yaml
# backend/src/main/resources/application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://oceanbase-host:3306/monitoring
    username: root
    password: ${DB_PASSWORD}
  redis:
    host: redis-host
    port: 6379

collector:
  batch-max-size: 100          # 批量大小
  gzip-enabled: true           # 启用压缩
```

---

## 十一、注意事项

### 11.1 Session Log 解析

- JSONL 文件可能很大,使用流式解析避免内存溢出
- 文件编码假设为 UTF-8
- 单行可能超过 1MB,需要调整 Scanner buffer

### 11.2 数据一致性

- Collector 推送失败时,下次扫描应跳过已处理的文件
- 基于文件名 + 修改时间判断文件是否已处理
- 数据库使用唯一索引防止重复插入

### 11.3 性能优化

- 批量推送减少网络开销
- Gzip 压缩减少带宽
- Redis 缓存热点数据
- 聚合表使用 UNIQUE KEY 支持 UPSERT

### 11.4 扩展性

- 当前设计支持水平扩展
- 多个 Collector 可同时工作
- Backend 可部署多个实例 + 负载均衡

---

**文档结束**
