# ClawBoard 实现计划制定 — 会话记录

> 日期: 2026-04-18
> 产出物: `docs/2026-04-18-clawboard-implementation-plan.md`

---

## 1. 会话背景

本次会话是设计文档评审通过后的下一步：基于已完成的设计文档制定详细的实现计划。

### 前序工作（前几次会话完成）

- 完成了 OpenClaw 监控统计面板的完整设计文档 `docs/2026-04-18-openclaw-monitoring-dashboard-design.md`
- 设计文档经过自动化 spec review 和人工评审，状态为 **Approved**
- 关键设计决策已锁定：
  - 扫描 `.bak` 文件（方案 B），通过数据库 `(session_id, start_message_id)` 去重
  - `turn_index` 从唯一键降级为排序辅助字段，改用 `(session_id, start_message_id)` 作为唯一键
  - JDK 17、SpringBoot、MyBatis、OceanBase MySQL 模式
  - 8 张数据库表、14 个 REST API 端点

### 用户指令

用户将设计文档迁移到新目录 `G:\Workplace\github\clawboard\docs\`，并指示：

> "我已经将设计文档放到 G:\Workplace\github\clawboard\docs\2026-04-18-openclaw-monitoring-dashboard-design.md，后续的开发工作会在新的目录 G:\Workplace\github\clawboard 下面进行，现在开始制定实现计划，相关文档都放到 G:\Workplace\github\clawboard\docs\ 目录下"

使用 `/writing-plans` skill 启动实现计划制定流程。

---

## 2. 信息收集阶段

### 2.1 读取设计文档

完整读取了 `docs/2026-04-18-openclaw-monitoring-dashboard-design.md`（1264 行），包含 9 个章节：

1. 背景与目标（技术选型含 JDK 17）
2. 数据源分析（JSONL 文件结构、Message ID 唯一性、过滤规则、数据生命周期）
3. 数据库设计（8 张表的完整 DDL）
4. 解析引擎设计（架构图、调度流程、并发控制、TranscriptParser 流程、轮次边界、Skill 识别、错误检测）
5. REST API 设计（14 个端点，严格对齐前端 API 接口文档）
6. 配置项
7. 待定功能
8. 风险与应对
9. SpringBoot 模块结构建议

### 2.2 探索项目目录结构

项目目录 `G:\Workplace\github\clawboard` 包含：

```
clawboard/
├── .git/
├── .gitignore          # Java 标准 gitignore
├── README.md
├── docs/               # 设计文档、API 文档
├── openspec/           # config.yaml (spec-driven schema)
└── test/
    └── openclaw-logs/  # 真实的 OpenClaw session transcript 测试数据
        └── agents/main/sessions/
            ├── *.jsonl                    # 活跃 session
            ├── *.jsonl.deleted.*          # 删除归档
            ├── *.jsonl.reset.*            # reset 归档
            ├── *.jsonl.bak-*              # compaction 备份
            ├── *.checkpoint.*.jsonl       # checkpoint 文件
            └── sessions.json              # session store 元数据
```

### 2.3 分析真实测试数据

读取了多个样本 JSONL 文件，发现几个重要细节：

**1. `.bak` 文件实际格式与设计文档不一致：**
- 设计文档描述: `<uuid>.jsonl.bak.<ISO-timestamp>`
- 实际格式: `6bc1ee54-...jsonl.bak-292-1775810216210`（`*.jsonl.bak-<行数>-<epochMs>`）
- 影响：文件过滤逻辑需要兼容两种格式（实际上 `.contains(".jsonl")` 即可覆盖）

**2. `.checkpoint.` 文件未在设计文档中提及：**
- 格式: `<uuid>.checkpoint.<checkpoint-uuid>.jsonl`
- 内容包含 `provider: "openclaw"`, `model: "gateway-injected"` 等内部消息
- 决策：在扫描时排除这些文件，避免重复数据

**3. 主文件 session_id 可能与文件名不一致：**
- `6bc1ee54-...jsonl` 的第一行 session header 中 id 是 `c2b6491d-...`（reset 后的新 session）
- 确认了设计文档的处理策略：优先使用 session header 中的 id，fallback 到文件名解析

**4. 样本文件展示的完整数据结构：**
- `52fa7e91-...jsonl`: 简单 cron 任务（user → assistant → 完成）
- `600cd549-...jsonl`: 含 Skill 调用（read SKILL.md → exec → toolResult → reply）
- 验证了 toolCall 块中 SKILL.md 路径的实际格式

### 2.4 读取前端 API 接口文档

读取了 `docs/API接口文档.md`，确认 7 个核心接口的请求/响应格式：
- 技能列表、全局统计、统计卡片、趋势图、用户明细、对话搜索、链路详情

---

## 3. 计划编写阶段

### 3.1 技术决策

在编写计划过程中做出以下技术决策：

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 构建工具 | Maven | 企业 Java 标准 |
| ORM | MyBatis（非 JPA） | 大量复杂聚合 SQL 和 `ON DUPLICATE KEY UPDATE`，需要直接 SQL 控制 |
| JSON 解析 | Jackson（SpringBoot 内置） | 解析 JSONL 行 |
| 数据库迁移 | Flyway | 标准方案 |
| 代码简化 | Lombok | 减少 Entity 样板代码，Parser 模型使用 JDK 17 Records |
| 测试 | JUnit 5 + Mockito + H2（test） | Parser 纯单元测试不需要数据库 |

### 3.2 计划结构

最终计划包含 **23 个任务**，分为 6 个阶段：

| 阶段 | 任务编号 | 内容 |
|------|---------|------|
| 项目脚手架 | Task 1-5 | SpringBoot 项目、Flyway schema、配置类、Entity、DTO |
| 数据访问层 | Task 6 | MyBatis Mapper 接口 + XML（含关键 SQL） |
| 解析引擎 | Task 7-13 | 7 个组件，TDD 方式开发 |
| 扫描基础设施 | Task 14-17 | 进度管理、用户扫描器、调度器、小时统计聚合 |
| REST API 层 | Task 18-22 | 4 个 Controller + 5 个 Service + 异常处理器 |
| 集成测试 | Task 23 | 使用真实 JSONL 数据的端到端测试 |

### 3.3 关键代码设计

计划中提供了以下核心组件的完整代码：

**MessageParser** — JSONL 行解析器：
- 使用 Jackson `readTree` 解析每行 JSON
- `sealed interface JsonlRecord` + `switch` 表达式分流不同类型
- 提取 text content、tool calls、usage、provider/model 等

**SystemMessageFilter** — 消息过滤器：
- 过滤 delivery-mirror 消息（`provider=="openclaw" && model=="delivery-mirror"`）
- 过滤 5 种系统生成的 user 消息（可配置前缀列表）

**SkillDetector** — Skill 识别器：
- 信号 1: 模型读取 SKILL.md（toolCall name=read/Read, path 匹配 `*/skills/*/SKILL.md`）
- 信号 2: 用户斜杠命令（匹配 `Use the "xxx" skill` 或 `/<skill-name>`）

**IssueDetector** — 错误检测器：
- 7 类错误的正则匹配（modelErrors, timeoutErrors, rateLimitErrors 等）
- 流程完整性检查（no_reply, missing_tool_result, missing_final_answer）
- 异常停止检测（非正常 stopReason）

**TurnAssembler** — 轮次组装器（最复杂的组件）：
- 维护"当前轮次"状态机
- 跟踪 tool calls 与 tool results 的匹配关系
- 构建 chain_summary JSON
- 判断轮次状态（complete/incomplete/error）
- 计算各维度耗时（total/tool/model duration）

**ScanOrchestrator** — 扫描调度器：
- `AtomicBoolean` 全局扫描锁
- `@Scheduled` cron 触发 + 手动触发
- 并行 UserScanner 任务提交到线程池
- finally 块确保异常安全

**关键 SQL**:
- `INSERT IGNORE INTO dashboard_message` — 消息去重
- `INSERT INTO dashboard_conversation_turn ... ON DUPLICATE KEY UPDATE` — 轮次合并（取更完整版本）
- `INSERT INTO dashboard_hourly_stats ... ON DUPLICATE KEY UPDATE` — 小时统计重算

---

## 4. 计划评审阶段

### 4.1 自动化评审

调度了 plan-document-reviewer 子代理进行评审。

**评审结果: Approved**

**发现的 2 个实际问题：**

**Issue 1 — MessageRecord 缺少 errorMessage 字段（Task 7/8/13）：**
- `MessageRecord` 没有提取 JSONL 中 assistant 消息的专用 `errorMessage` 字段
- `TranscriptParser` 用 `mr.textContent()`（content 数组中的第一个 text 块）做错误检测
- 设计文档明确要求匹配 `assistant 消息的 errorMessage 字段`
- 这意味着 `errorMessage` 字段有值但 content 正常时，错误会被漏检

**Issue 2 — triggerManualScan() 竞态条件（Task 16）：**
- 原代码先 CAS 获取锁，立即释放，再调用 `runScan()`（内部再次 CAS）
- 释放和重新获取之间，定时触发可能抢到锁，导致手动触发错误失败

**5 条建议（非阻塞）：**
1. `UsageExtractor` 在文件结构图中列出但没有独立 Task（逻辑内联在 MessageParser 中）
2. Controller 测试应覆盖全部 4 个 Controller（计划中只详细展示了 2 个）
3. `ScanHistoryMapper.insert()` XML 需要 `useGeneratedKeys="true"` 属性
4. `TurnAssembler.toLocalDateTime()` 需要改为 package-private 可见性
5. 设计文档有 5 个 Controller，计划合并为 4 个（Skills 并入 Dashboard）— 刻意简化

### 4.2 问题修复

**Fix 1 — 添加 errorMessage 字段：**
- `MessageRecord` 新增 `String errorMessage` 字段
- `MessageParser.parseMessage()` 新增 `msg.has("errorMessage") ? msg.get("errorMessage").asText(null) : null`
- `TranscriptParser.processMessage()` 错误检测逻辑改为：优先使用 `mr.errorMessage()`，fallback 到 `mr.textContent()`

**Fix 2 — 消除竞态条件：**
- `triggerManualScan()` 简化为直接调用 `runScan("manual")`
- `runScan()` 内部已有 CAS 锁保护，无需外层再加锁

---

## 5. 最终产出

### 实现计划文档

**路径**: `G:\Workplace\github\clawboard\docs\2026-04-18-clawboard-implementation-plan.md`

**规模**: 23 个 Task，覆盖从项目脚手架到集成测试的完整实现路径

**文件结构**: 计划定义了约 60+ 个 Java 源文件的精确路径和职责

**API 映射表**: 13 个 REST 端点 → Controller → Service → 数据源的完整映射

### 关键设计决策汇总

| 决策 | 内容 |
|------|------|
| ORM | MyBatis（直接 SQL 控制） |
| 去重策略 | 消息: INSERT IGNORE; 轮次: ON DUPLICATE KEY UPDATE 取更完整版; 小时统计: 全量替换 |
| 文件过滤 | 包含所有 `.jsonl` 文件，排除 `sessions.json` 和 `.checkpoint.*` |
| .bak 格式 | 兼容 `*.jsonl.bak-<N>-<epochMs>` 实际格式 |
| Session ID | 优先 session header，fallback 文件名解析 |
| 轮次唯一键 | `(session_id, start_message_id)` — 天然跨文件去重 |
| 扫描并发 | AtomicBoolean 锁，定时触发静默跳过，手动触发返回 409 |
| 小时统计 | "重算受影响小时"策略，V1 全量重算 |

### 下一步

用户确认计划后，有两种执行方式可选：
1. **Subagent-Driven（推荐）** — 每个 Task 调度独立子代理，任务间 review
2. **Inline Execution** — 在当前会话中逐 Task 执行
