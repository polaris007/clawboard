# time-range-report-generation

## Purpose

提供按时间范围生成对话统计报告的能力，支持灵活的数据分析和趋势定位。

---

## Requirements

### Requirement: API shall accept time range parameters for report generation

系统 SHALL 提供一个 REST 端点，接受 "yyyy-MM-dd HH:mm:ss" 格式（北京时间）的 `startTime` 和 `endTime` 参数来生成对话统计报告。

#### Scenario: Valid time range request
- **WHEN** 客户端发送 POST 请求到 `/api/v1/reports/generate-by-time-range` 并提供有效的时间范围
- **THEN** 系统立即返回 HTTP 200 和确认消息
- **AND** 异步报告生成任务在后台启动

#### Scenario: Time range format validation
- **WHEN** 客户端发送的请求包含无效的日期格式（例如 "2026/04/20"）
- **THEN** 系统返回 HTTP 400 和错误消息 "Invalid date format. Expected: yyyy-MM-dd HH:mm:ss"

---

### Requirement: System shall query conversation turns by time range

系统 SHALL 查询 `dashboard_conversation_turn` 表，按 `start_time` 在指定范围内过滤，利用现有数据库索引提高性能。

#### Scenario: Query turns within time range
- **WHEN** 报告生成开始，时间范围为 [2026-04-20 00:00:00, 2026-04-23 23:59:59]
- **THEN** 系统执行 SQL: `SELECT * FROM dashboard_conversation_turn WHERE start_time >= ? AND start_time <= ? ORDER BY start_time ASC`
- **AND** 使用索引 `idx_employee_time` 或 `idx_time` 进行高效查询

#### Scenario: No turns found in range
- **WHEN** 指定时间范围内没有对话轮次
- **THEN** 系统生成零统计的空报告
- **AND** 记录警告日志: "No conversation turns found in time range [...]"

---

### Requirement: Report shall be generated with consistent format

系统 SHALL 复用现有的 `ReportGenerator` 逻辑生成 Markdown 报告，确保与扫描触发的报告具有相同的结构和格式。

#### Scenario: Generate report with same template
- **WHEN** 生成时间范围报告
- **THEN** 报告包含以下部分：Executive Summary, Active Users, Conversation Turns, Task Success Rate, Skill Usage, Error Analysis
- **AND** 格式与扫描生成的报告匹配（标题、表格、图表）

#### Scenario: Report includes time range metadata
- **WHEN** 生成报告头部
- **THEN** 报告显示 "Time Range: 2026-04-20 00:00:00 - 2026-04-23 23:59:59"
- **AND** 报告文件名包含时间范围: `time-range-report-2026-04-20-00-00-00-2026-04-23-23-59-59.md`

---

### Requirement: Report shall be saved to standard directory

系统 SHALL 将生成的报告保存到 `reports/{yyyy-MM-dd}/` 目录（使用当前日期，北京时间），与扫描生成的报告保持一致。

#### Scenario: Save report to date-based directory
- **WHEN** 报告在 2026-04-23 生成
- **THEN** 报告保存到 `reports/2026-04-23/time-range-report-{startTime}-{endTime}.md`
- **AND** 如果目录不存在则创建

#### Scenario: Handle file name conflicts
- **WHEN** 相同时间范围的报告已存在
- **THEN** 系统附加时间戳: `time-range-report-{range}-{unixTimestamp}.md`
- **AND** 记录信息日志: "Report file already exists, appending timestamp"

---

### Requirement: Statistics shall exclude system messages

系统 SHALL 在计算对话轮次数和成功率时过滤掉系统生成的消息，与扫描报告逻辑保持一致。

#### Scenario: Exclude system turns from count
- **WHEN** 计算总对话轮次数
- **THEN** 只统计非系统轮次（`is_system = 0`）
- **AND** 系统轮次不计入成功率计算

#### Scenario: Calculate metrics correctly
- **WHEN** 计算任务成功率
- **THEN** 公式: `(无错误的轮次数) / (总非系统轮次数) * 100%`
- **AND** 活跃用户数使用不同的 `employee_id` 值计数
