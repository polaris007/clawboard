# OpenSpec 配置说明

**配置日期**: 2026-04-22  
**配置文件**: [openspec/config.yaml](../openspec/config.yaml)

---

## 📋 配置概览

本项目已完整配置 OpenSpec，包含以下核心内容：

### 1. **技术栈信息** (Tech Stack)

```yaml
Language: Java 17 (Records, Sealed Classes, Text Blocks)
Framework: Spring Boot 3.2.5
ORM: MyBatis 3.0.3 + PageHelper 2.1.0
Database: MySQL 5.7+ / OceanBase MySQL 模式
Build Tool: Maven 3.6+
Testing: JUnit 5, Mockito, AssertJ, H2, Testcontainers
```

### 2. **项目概述** (Project Overview)

ClawBoard 是 OpenClaw AI Agent 监控仪表板系统，核心功能：
- 📊 扫描和解析 JSONL 会话转录文件
- 🔍 提取对话轮次、技能调用、错误信息、Token 消耗
- 📈 提供 REST API 供前端展示统计数据
- 🎯 支持多维度聚合（用户/部门/时间）

### 3. **开发规范** (Key Conventions)

#### 代码规范
- ✅ 使用 Lombok (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`)
- ✅ 遵循 Spring Boot 最佳实践
- ✅ 所有表名使用 `dashboard_` 前缀
- ✅ 日期时间统一使用北京时间（Asia/Shanghai, UTC+8）

#### 数据库规范
- 🔑 主键：自增 BIGINT
- 🔒 复合唯一键：`(session_id, message_id)` 防止跨 session ID 碰撞
- 📥 批量插入使用 `INSERT IGNORE INTO` 实现去重
- ⏰ 时区字段存储为 LocalDateTime（服务器本地时区）

#### 扫描与解析
- 🧵 多线程扫描：用户级别并行（默认 8 线程），文件级别串行
- 📁 扫描所有包含 `.jsonl` 的文件（包括 .bak, .reset, .deleted, .checkpoint）
- 🚫 过滤 delivery-mirror 消息（provider=openclaw, model=delivery-mirror）
- 🚫 过滤系统生成的 user 消息

#### API 设计
- 🌐 RESTful 风格，路径前缀 `/api/v1/`
- 📦 统一响应格式：`{ code, message, data }`
- 📄 分页参数：page, pageSize（使用 PageHelper）
- 🕐 时间参数格式：`"yyyy-MM-dd HH:mm:ss"`

#### 测试规范
- 🎯 单元测试覆盖率目标：核心业务逻辑 > 80%
- 💾 集成测试使用 H2 内存数据库
- 📝 测试类命名：`*Test.java`
- 🔤 测试方法命名：`should_[预期行为]_when_[条件]`

### 4. **领域知识** (Domain Knowledge)

#### OpenClaw 会话结构
- 👤 每个用户有独立的 OpenClaw 实例，目录通过 NAS 挂载
- 📂 会话文件位置：`{basePath}/{hash}/agents/{agentName}/sessions/*.jsonl`
- 🆔 Session ID 从文件名提取：取第一个 `.jsonl` 之前的部分
- 🔢 Message ID 是 8 位十六进制，仅在单个 session 内唯一

#### 数据统计维度
- 👥 **活跃用户**：指定时间范围内有对话记录的去重员工数
- 💬 **对话轮次**：排除系统消息后的真实用户消息数
- ✅ **任务成功率**：无错误的完整对话轮次 / 总非系统轮次
- 🛠️ **Skill 调用**：检测 toolUse/toolResult 事件
- ❌ **错误分类**：modelErrors, timeoutErrors, rateLimitErrors, toolErrors 等

#### 文件扫描策略
- 🔄 增量扫描：只处理新文件或修改过的文件
- 🔐 并发控制：AtomicBoolean 防止重叠扫描
- 📊 进度跟踪：DashboardScanProgress 表记录扫描状态
- 📝 报告生成：每次扫描后生成 Markdown 格式的问题检测报告

### 5. **开发环境** (Development Environment)

```
JDK 路径:     D:\Program\JDK\jdk-17.0.18
Maven 路径:   D:\Program\Maven\apache-maven-3.9.15
Maven 仓库:   D:\m2
MySQL:        127.0.0.1:3306, 数据库 clawboard
Python 2.7:   D:\Python27（用于对比脚本）
开发 Profile: application-dev.yml
启动前操作:   scripts/reset-database.sql
```

### 6. **产物规则** (Per-artifact Rules)

#### Proposal（提案）
- ✅ 应包含：背景、目标、技术方案、风险评估、实施计划
- ✅ 明确标注与非目标（Non-goals）的边界
- ✅ 考虑对现有 API 和数据模型的影响
- ✅ 评估性能影响和扩展性

#### Specs（规格说明）
- ✅ 应具体、可测试、无歧义
- ✅ 包含正常场景和异常场景
- ✅ 定义清晰的输入输出契约
- ✅ 考虑向后兼容性

#### Tasks（任务清单）
- ✅ 任务分解粒度：每个任务不超过 4 小时工作量
- ✅ 明确任务的依赖关系和执行顺序
- ✅ 包含验证步骤（如何确认任务完成）
- ✅ 优先实现核心功能，再处理边缘情况

#### Design（设计文档）
- ✅ 应包含：架构图、数据流、关键决策点
- ✅ 说明技术选型理由和权衡取舍
- ✅ 考虑错误处理和容错机制
- ✅ 预留扩展点和配置项

---

## 🎯 配置特点

### 1. **全面的技术上下文**
AI 在创建任何产物时都能了解：
- 项目的技术栈和版本
- 编码规范和最佳实践
- 数据库设计和 API 约定
- 领域特定的业务知识

### 2. **明确的开发规范**
确保生成的代码和文档符合项目标准：
- 统一的命名和格式
- 一致的架构模式
- 规范的测试方法

### 3. **实用的任务指导**
帮助 AI 合理分解工作：
- 合适的任务粒度（≤ 4 小时）
- 清晰的依赖关系
- 明确的验证标准

### 4. **完整的领域知识**
让 AI 理解业务背景：
- OpenClaw 的工作原理
- 数据统计的业务含义
- 扫描策略的设计考量

---

## 📖 相关文档

- [OpenSpec 使用指南](OPENSPEC_USAGE.md) - 如何使用 OpenSpec 工作流
- [项目结构说明](PROJECT_STRUCTURE.md) - 项目目录组织
- [README](../docs/README.md) - 项目介绍

---

## 🔗 OpenSpec 命令速查

| 命令 | 用途 | 示例 |
|------|------|------|
| `/opsx:new <name>` | 创建新变更 | `/opsx:new add-department-filter` |
| `/opsx:propose <desc>` | 快速生成完整方案 | `/opsx:propose optimize-scan` |
| `/opsx:explore` | 探索模式 | `/opsx:explore` |
| `/opsx:apply <name>` | 实施变更 | `/opsx:apply add-department-filter` |
| `/opsx:verify <name>` | 验证实施 | `/opsx:verify add-department-filter` |
| `/opsx:archive <name>` | 归档变更 | `/opsx:archive add-department-filter` |

---

**维护者**: ClawBoard 开发团队  
**最后更新**: 2026-04-22
