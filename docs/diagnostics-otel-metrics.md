# OpenClaw Diagnostics-OTEL 插件指标说明

## 概述

`diagnostics-otel` 是 OpenClaw 的官方插件，用于将诊断事件导出到 **OpenTelemetry** 兼容的可观测性平台。该插件支持三种数据类型：

- **Metrics（指标）**：计数器、直方图等聚合数据
- **Traces（链路追踪）**：分布式追踪 Span
- **Logs（日志）**：结构化日志导出

---

## 配置选项

```typescript
{
  diagnostics: {
    enabled: boolean,           // 总开关
    otel: {
      enabled: boolean,         // OTEL 开关
      protocol: "http/protobuf", // 协议（仅支持此值）
      endpoint: string,         // OTLP Collector 地址
      headers?: object,         // 认证头（如 API Key）
      serviceName?: string,     // 服务名称（默认: openclaw）
      sampleRate?: number,      // 采样率（0-1，默认: 全量）
      traces?: boolean,         // 是否启用 Traces（默认: true）
      metrics?: boolean,        // 是否启用 Metrics（默认: true）
      logs?: boolean,           // 是否启用 Logs（默认: false）
      flushIntervalMs?: number  // 刷新间隔（毫秒，最小: 1000）
    }
  }
}
```

### 环境变量支持

- `OTEL_EXPORTER_OTLP_PROTOCOL`：协议类型
- `OTEL_EXPORTER_OTLP_ENDPOINT`：Collector 端点
- `OTEL_SERVICE_NAME`：服务名称

---

## 一、Metrics（指标）

共 **16 个指标**，分为 5 大类：

### 1. Token 和成本指标

| 指标名称 | 类型 | 单位 | 说明 | 维度（Attributes） |
|---------|------|------|------|-------------------|
| `openclaw.tokens` | Counter | 1 | Token 使用量 | - `openclaw.channel`: 渠道<br>- `openclaw.provider`: 提供商<br>- `openclaw.model`: 模型<br>- `openclaw.token`: token类型 (input/output/cache_read/cache_write/prompt/total) |
| `openclaw.cost.usd` | Counter | USD | 模型调用成本 | - `openclaw.channel`<br>- `openclaw.provider`<br>- `openclaw.model` |
| `openclaw.context.tokens` | Histogram | 1 | 上下文窗口大小和使用情况 | - `openclaw.channel`<br>- `openclaw.provider`<br>- `openclaw.model`<br>- `openclaw.context`: 类型 (limit/used) |

**示例查询（Prometheus）**：
```promql
# 查询某模型的总输入 Token
sum(openclaw_tokens{openclaw_token="input", openclaw_model="gpt-4"})

# 查询平均上下文使用率
avg(openclaw_context_tokens{openclaw_context="used"}) / avg(openclaw_context_tokens{openclaw_context="limit"})
```

---

### 2. Agent 运行指标

| 指标名称 | 类型 | 单位 | 说明 | 维度 |
|---------|------|------|------|------|
| `openclaw.run.duration_ms` | Histogram | ms | Agent 运行时长 | - `openclaw.channel`<br>- `openclaw.provider`<br>- `openclaw.model` |
| `openclaw.run.attempt` | Counter | 1 | 运行尝试次数 | - `openclaw.attempt`: 尝试次数 |

**用途**：
- 监控 Agent 响应时间分布
- 检测重试频率，识别不稳定的会话

---

### 3. Webhook 指标

| 指标名称 | 类型 | 单位 | 说明 | 维度 |
|---------|------|------|------|------|
| `openclaw.webhook.received` | Counter | 1 | 接收到的 Webhook 请求数 | - `openclaw.channel`<br>- `openclaw.webhook`: Webhook类型 |
| `openclaw.webhook.error` | Counter | 1 | Webhook 处理错误数 | - `openclaw.channel`<br>- `openclaw.webhook` |
| `openclaw.webhook.duration_ms` | Histogram | ms | Webhook 处理时长 | - `openclaw.channel`<br>- `openclaw.webhook` |

**示例查询**：
```promql
# Webhook 错误率
rate(openclaw_webhook_error[5m]) / rate(openclaw_webhook_received[5m])

# P95 处理延迟
histogram_quantile(0.95, rate(openclaw_webhook_duration_ms_bucket[5m]))
```

---

### 4. 消息队列指标

| 指标名称 | 类型 | 单位 | 说明 | 维度 |
|---------|------|------|------|------|
| `openclaw.message.queued` | Counter | 1 | 排队等待处理的消息数 | - `openclaw.channel`<br>- `openclaw.source`: 来源 |
| `openclaw.message.processed` | Counter | 1 | 已处理的消息数 | - `openclaw.channel`<br>- `openclaw.outcome`: 结果 (completed/skipped/error) |
| `openclaw.message.duration_ms` | Histogram | ms | 消息处理时长 | - `openclaw.channel`<br>- `openclaw.outcome` |
| `openclaw.queue.depth` | Histogram | 1 | 队列深度 | - `openclaw.channel` 或 `openclaw.lane` |
| `openclaw.queue.wait_ms` | Histogram | ms | 队列等待时间 | - `openclaw.lane`: 队列通道 |
| `openclaw.queue.lane.enqueue` | Counter | 1 | 命令队列入队事件 | - `openclaw.lane` |
| `openclaw.queue.lane.dequeue` | Counter | 1 | 命令队列出队事件 | - `openclaw.lane` |

**用途**：
- 监控系统负载和吞吐量
- 检测队列积压问题
- 分析不同通道的处理性能

---

### 5. Session 状态指标

| 指标名称 | 类型 | 单位 | 说明 | 维度 |
|---------|------|------|------|------|
| `openclaw.session.state` | Counter | 1 | Session 状态转换次数 | - `openclaw.state`: 状态 (idle/processing/waiting)<br>- `openclaw.reason`: 转换原因 |
| `openclaw.session.stuck` | Counter | 1 | 卡住的 Session 数量 | - `openclaw.state` |
| `openclaw.session.stuck_age_ms` | Histogram | ms | 卡住 Session 的时长 | - `openclaw.state` |

**告警示例**：
```promql
# 检测卡住超过 5 分钟的 Session
openclaw_session_stuck_age_ms > 300000
```

---

## 二、Traces（链路追踪）

当 `tracesEnabled = true` 时，会创建以下 **5 种 Span**：

### 1. Model Usage Span

- **Span 名称**: `openclaw.model.usage`
- **记录内容**: 
  - Token 使用详情（input/output/cache_read/cache_write/total）
  - 会话信息（sessionKey, sessionId）
  - 渠道、提供商、模型信息
  - 耗时（durationMs）

**Attributes**:
```json
{
  "openclaw.channel": "telegram",
  "openclaw.provider": "openai",
  "openclaw.model": "gpt-4",
  "openclaw.sessionKey": "user123",
  "openclaw.sessionId": "abc-def-ghi",
  "openclaw.tokens.input": 150,
  "openclaw.tokens.output": 300,
  "openclaw.tokens.cache_read": 0,
  "openclaw.tokens.cache_write": 50,
  "openclaw.tokens.total": 500
}
```

---

### 2. Webhook Processed Span

- **Span 名称**: `openclaw.webhook.processed`
- **记录内容**:
  - 渠道、Webhook 类型
  - Chat ID
  - 处理时长

**Attributes**:
```json
{
  "openclaw.channel": "telegram",
  "openclaw.webhook": "message",
  "openclaw.chatId": "123456789"
}
```

---

### 3. Webhook Error Span

- **Span 名称**: `openclaw.webhook.error`
- **状态**: `ERROR`
- **记录内容**:
  - 错误信息（已脱敏）
  - 渠道、Webhook 类型
  - Chat ID

**Attributes**:
```json
{
  "openclaw.channel": "telegram",
  "openclaw.webhook": "message",
  "openclaw.chatId": "123456789",
  "openclaw.error": "[REDACTED] Authentication failed"
}
```

---

### 4. Message Processed Span

- **Span 名称**: `openclaw.message.processed`
- **状态**: 如果 outcome="error" 则标记为 `ERROR`
- **记录内容**:
  - 渠道、处理结果（outcome）
  - Session 信息、Chat ID、Message ID
  - 处理时长、失败原因

**Attributes**:
```json
{
  "openclaw.channel": "telegram",
  "openclaw.outcome": "completed",
  "openclaw.sessionKey": "user123",
  "openclaw.sessionId": "abc-def-ghi",
  "openclaw.chatId": "123456789",
  "openclaw.messageId": "msg-001"
}
```

---

### 5. Session Stuck Span

- **Span 名称**: `openclaw.session.stuck`
- **状态**: `ERROR`
- **记录内容**:
  - Session 状态、Session 信息
  - 队列深度、卡住时长（ageMs）

**Attributes**:
```json
{
  "openclaw.state": "processing",
  "openclaw.sessionKey": "user123",
  "openclaw.sessionId": "abc-def-ghi",
  "openclaw.queueDepth": 5,
  "openclaw.ageMs": 600000
}
```

---

## 三、Logs（日志）

当 `logsEnabled = true` 时，会将 OpenClaw 的所有日志通过 OTLP 导出。

### 日志级别映射

| 级别 | Severity Number | 说明 |
|------|----------------|------|
| TRACE | 1 | 最详细调试信息 |
| DEBUG | 5 | 调试信息 |
| INFO | 9 | 一般信息 |
| WARN | 13 | 警告 |
| ERROR | 17 | 错误 |
| FATAL | 21 | 致命错误 |

### 日志属性（Attributes）

每条日志包含以下结构化属性：

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `openclaw.log.level` | string | 日志级别 |
| `openclaw.logger` | string | Logger 名称 |
| `openclaw.logger.parents` | string | Logger 父级路径（点号分隔） |
| `code.filepath` | string | 代码文件路径 |
| `code.lineno` | number | 代码行号 |
| `code.function` | string | 函数名 |
| `openclaw.code.location` | string | 代码位置（文件:行号） |
| `openclaw.*` | dynamic | 动态绑定属性（bindings） |
| `openclaw.log.args` | string | 日志参数（JSON 字符串） |

### 安全特性

- **自动脱敏**：所有字符串字段都会经过敏感信息脱敏处理
- **结构化输出**：支持 JSON 格式的 bindings 解析

**示例日志**：
```json
{
  "body": "Processing message from user",
  "severityText": "INFO",
  "severityNumber": 9,
  "attributes": {
    "openclaw.log.level": "INFO",
    "openclaw.logger": "message-handler",
    "openclaw.logger.parents": "openclaw.core.handlers",
    "code.filepath": "/src/handlers/message.ts",
    "code.lineno": 123,
    "code.function": "processMessage",
    "openclaw.code.location": "/src/handlers/message.ts:123",
    "openclaw.channel": "telegram",
    "openclaw.chatId": "123456789"
  },
  "timestamp": "2026-04-27T05:30:00.000Z"
}
```

---

## 四、支持的事件类型

插件监听 **12 种诊断事件**：

| 事件类型 | 说明 | 触发时机 |
|---------|------|---------|
| `model.usage` | 模型使用情况 | 每次 LLM 调用完成后 |
| `webhook.received` | Webhook 接收 | 收到外部 Webhook 请求时 |
| `webhook.processed` | Webhook 处理完成 | Webhook 处理成功后 |
| `webhook.error` | Webhook 处理错误 | Webhook 处理失败时 |
| `message.queued` | 消息入队 | 消息加入处理队列时 |
| `message.processed` | 消息处理完成 | 消息处理完成后 |
| `session.state` | Session 状态变化 | Session 状态转换时 |
| `session.stuck` | Session 卡住 | 检测到 Session 长时间无响应时 |
| `queue.lane.enqueue` | 队列入队 | 命令加入特定队列通道时 |
| `queue.lane.dequeue` | 队列出队 | 命令从队列通道取出时 |
| `run.attempt` | 运行尝试 | Agent 开始运行时 |
| `diagnostic.heartbeat` | 心跳检测 | 定期心跳检查时 |

⚠️ **注意**：虽然定义了 `DiagnosticToolLoopEvent`（工具循环检测），但在当前版本中**未实现**对该事件的处理。

---

## 五、集成示例

### 1. Grafana + Prometheus + Loki

```yaml
# docker-compose.yml
version: '3'
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
  
  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
  
  loki:
    image: grafana/loki
    ports:
      - "3100:3100"
  
  otel-collector:
    image: otel/opentelemetry-collector-contrib
    volumes:
      - ./otel-config.yaml:/etc/otelcol/config.yaml
    command: ["--config=/etc/otelcol/config.yaml"]
    ports:
      - "4317:4317"  # OTLP gRPC
      - "4318:4318"  # OTLP HTTP
```

### 2. OpenClaw 配置

```json
{
  "diagnostics": {
    "enabled": true,
    "otel": {
      "enabled": true,
      "endpoint": "http://localhost:4318",
      "serviceName": "openclaw-prod",
      "sampleRate": 1.0,
      "traces": true,
      "metrics": true,
      "logs": true,
      "flushIntervalMs": 5000
    }
  }
}
```

### 3. Jaeger（仅 Traces）

```bash
docker run -d --name jaeger \
  -e COLLECTOR_OTLP_ENABLED=true \
  -p 16686:16686 \
  -p 4317:4317 \
  -p 4318:4318 \
  jaegertracing/all-in-one:latest
```

访问 http://localhost:16686 查看追踪数据。

---

## 六、最佳实践

### 1. 采样策略

- **开发环境**：`sampleRate: 1.0`（全量采集）
- **生产环境**：`sampleRate: 0.1`（10% 采样）以降低存储成本

### 2. 刷新间隔

- **高频场景**：`flushIntervalMs: 2000`（2秒）
- **低频场景**：`flushIntervalMs: 10000`（10秒）

### 3. 成本控制

- 禁用不必要的信号类型（如仅需 Metrics 时设置 `traces: false, logs: false`）
- 使用标签过滤，只采集关键渠道的数据
- 定期清理旧数据（建议保留 7-30 天）

### 4. 告警规则

```promql
# Token 成本突增
rate(openclaw_cost_usd[1h]) > 10

# 消息处理错误率高
rate(openclaw_message_processed{openclaw_outcome="error"}[5m]) 
  / rate(openclaw_message_processed[5m]) > 0.1

# Session 卡住
openclaw_session_stuck_age_ms > 300000

# 队列积压
openclaw_queue_depth > 100
```

---

## 七、故障排查

### 问题 1：没有数据上报

**检查项**：
1. 确认 `diagnostics.enabled` 和 `otel.enabled` 均为 `true`
2. 检查 `endpoint` 是否正确且可访问
3. 查看 OpenClaw 日志是否有 `diagnostics-otel` 相关错误

### 问题 2：数据延迟

**解决方案**：
- 减小 `flushIntervalMs` 值
- 检查网络延迟和 Collector 性能
- 确认 Collector 的批量导出配置

### 问题 3：敏感信息泄露

**解决方案**：
- 确保使用最新版本的插件（已内置脱敏功能）
- 在 Collector 层添加额外的过滤规则
- 审查自定义 bindings 中的字段

---

## 八、参考资料

- [OpenTelemetry 官方文档](https://opentelemetry.io/docs/)
- [OTLP Protocol Specification](https://github.com/open-telemetry/opentelemetry-proto)
- [OpenClaw Plugin SDK](https://github.com/openclaw/openclaw/tree/main/src/plugin-sdk)
- [Diagnostics-OTEL 源码](https://github.com/openclaw/openclaw/tree/main/extensions/diagnostics-otel)

---

**文档版本**: v1.0  
**最后更新**: 2026-04-27  
**适用 OpenClaw 版本**: 基于当前 master 分支
