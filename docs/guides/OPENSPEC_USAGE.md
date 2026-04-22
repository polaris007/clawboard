# OpenSpec 使用指南

## 📖 什么是 OpenSpec？

OpenSpec 是一个结构化的需求管理和开发工作流工具，帮助团队以规范的方式定义、设计和实现功能。

## 🎯 ClawBoard 项目的 OpenSpec 配置

本项目已配置 OpenSpec，包含：
- **技术栈信息**：Java 17, Spring Boot 3.2.5, MyBatis, MySQL
- **开发规范**：代码风格、数据库设计、API 设计原则
- **领域知识**：OpenClaw 会话结构、数据统计维度、扫描策略
- **环境配置**：开发工具路径、数据库连接信息

## 🚀 快速开始

### 1. 创建新功能提案

```bash
# 使用 /opsx:new 命令创建新的变更
/opsx:new add-user-department-filtering
```

这将创建：
```
openspec/changes/add-user-department-filtering/
├── proposal.md      # 功能提案
├── design.md        # 技术设计
├── specs/           # 详细规格说明
│   ├── api-spec.md
│   └── data-spec.md
└── tasks.md         # 实施任务清单
```

### 2. 探索想法（不确定怎么做时）

```bash
# 使用 /opsx:explore 进入探索模式
/opsx:explore
```

适合场景：
- 需求不明确，需要梳理
- 多个技术方案需要权衡
- 想了解现有系统的相关部分

### 3. 生成完整方案（已知要做什么）

```bash
# 使用 /opsx:propose 快速生成所有产物
/opsx:propose implement-batch-import
```

一次性生成：proposal + design + specs + tasks

### 4. 实施变更

```bash
# 使用 /opsx:apply 开始实施
/opsx:apply add-user-department-filtering
```

这会：
- 读取 tasks.md
- 逐个执行任务
- 在关键节点停下来确认

### 5. 验证实施

```bash
# 使用 /opsx:verify 验证完成度
/opsx:verify add-user-department-filtering
```

检查：
- ✅ 所有任务是否完成
- ✅ 是否符合规格说明
- ✅ 是否有遗漏的测试

### 6. 归档变更

```bash
# 使用 /opsx:archive 归档完成的变更
/opsx:archive add-user-department-filtering
```

将变更移动到 `openspec/changes/archive/`

## 📋 OpenSpec 工作流

```
提议 (Propose) → 设计 (Design) → 规格 (Specs) → 任务 (Tasks) → 实施 (Apply) → 验证 (Verify) → 归档 (Archive)
```

### 各阶段产出物

| 阶段 | 文件 | 内容 | AI 辅助规则 |
|------|------|------|-------------|
| **Proposal** | proposal.md | 背景、目标、范围、非目标 | 考虑 API/数据模型影响，评估性能 |
| **Design** | design.md | 架构、数据流、技术选型 | 说明权衡取舍，考虑容错机制 |
| **Specs** | specs/*.md | 详细的输入输出契约 | 具体、可测试、无歧义 |
| **Tasks** | tasks.md | 分解的实施步骤 | 每个任务 ≤ 4 小时，明确依赖 |

## 💡 最佳实践

### 1. 何时使用 OpenSpec？

✅ **应该使用**：
- 新增重要功能（如：添加用户部门过滤）
- 重构核心模块（如：优化扫描性能）
- 架构调整（如：引入缓存层）
- 跨多个文件的改动

❌ **不需要使用**：
- 修复简单的 bug
- 修改配置文件
- 更新文档
- 小范围的代码清理

### 2. 编写高质量的 Proposal

**好的示例**：
```markdown
## Background
当前 dashboard/summary 接口返回的 activeUsers 统计包含所有员工，
但业务方希望按部门维度查看活跃用户分布。

## Goals
- 新增 /dashboard/department-summary 接口
- 支持按 department_id 过滤统计数据
- 保持与现有 summary 接口的性能相当（< 500ms）

## Non-goals
- 不修改现有的 /dashboard/summary 接口
- 不支持实时部门变更（依赖定时同步）
```

**不好的示例**：
```markdown
## Goal
添加部门统计功能
```

### 3. 定义清晰的 Specs

**API Spec 示例**：
```markdown
## Endpoint: GET /api/v1/dashboard/department-summary

### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| startTime | String | Yes | 起始时间 "yyyy-MM-dd HH:mm:ss" |
| endTime | String | Yes | 结束时间 "yyyy-MM-dd HH:mm:ss" |
| departmentId | String | No | 部门 ID，不传则返回所有部门 |

### Response
```json
{
  "code": 200,
  "data": {
    "departments": [
      {
        "departmentId": "DEPT001",
        "departmentName": "技术部",
        "activeUsers": 45,
        "conversationTurns": 1234,
        "taskSuccessRate": 87.5
      }
    ]
  }
}
```

### Edge Cases
- 空结果：返回空数组 []
- 无效时间范围：返回 400 错误
- 部门不存在：该部门不在返回结果中
```

### 4. 任务分解技巧

**好的任务分解**：
```markdown
## Tasks

### 1. 数据库层 (2h)
- [ ] 创建 DashboardDepartmentStatsMapper 接口
- [ ] 编写 SQL：按部门聚合统计
- [ ] 单元测试：验证 SQL 正确性

### 2. Service 层 (2h)
- [ ] 创建 DepartmentDashboardService
- [ ] 实现 getDepartmentSummary() 方法
- [ ] 集成测试：模拟不同部门数据

### 3. Controller 层 (1h)
- [ ] 添加 /dashboard/department-summary 端点
- [ ] 参数校验：startTime, endTime 必填
- [ ] 集成测试：验证 HTTP 响应

### 4. 文档和测试 (1h)
- [ ] 更新 API 文档
- [ ] 添加 Swagger 注解
- [ ] 编写集成测试用例
```

**不好的任务分解**：
```markdown
## Tasks
- [ ] 实现部门统计功能
```

## 🔧 常用命令速查

| 命令 | 用途 | 示例 |
|------|------|------|
| `/opsx:new <name>` | 创建新变更 | `/opsx:new add-caching` |
| `/opsx:propose <desc>` | 快速生成完整方案 | `/opsx:propose optimize-scan-performance` |
| `/opsx:explore` | 探索模式（头脑风暴） | `/opsx:explore` |
| `/opsx:continue` | 继续上一个变更 | `/opsx:continue` |
| `/opsx:apply <name>` | 实施变更 | `/opsx:apply add-caching` |
| `/opsx:verify <name>` | 验证实施 | `/opsx:verify add-caching` |
| `/opsx:archive <name>` | 归档变更 | `/opsx:archive add-caching` |
| `/opsx:ff <desc>` | 快速生成并准备实施 | `/opsx:ff fix-pagination-bug` |

## 📁 目录结构

```
openspec/
├── config.yaml              # 项目配置（已配置）
├── changes/
│   ├── archive/             # 已归档的变更
│   │   └── 2026-04-xx-add-feature/
│   └── active/              # 进行中的变更（可选）
└── specs/                   # 全局规格（可选）
    └── common/
```

## ⚠️ 注意事项

1. **变更名称规范**：
   - 使用 kebab-case：`add-user-filtering`
   - 动词开头：`add-`, `fix-`, `refactor-`, `optimize-`
   - 简洁明了：避免过长的名称

2. **及时归档**：
   - 完成的功能立即归档
   - 归档前确保所有测试通过
   - 归档后不再修改

3. **保持同步**：
   - 实施过程中如有重大调整，更新 specs
   - 记录决策原因和权衡过程
   - 保持文档与代码一致

4. **团队协作**：
   - 大型变更前先 review proposal
   - 关键设计决策记录在 design.md
   - 任务分配清晰，避免冲突

## 🎓 学习资源

- [OpenSpec 官方文档](https://github.com/openspec-framework/openspec)
- [项目配置说明](config.yaml)
- [示例变更](changes/archive/) - 查看历史变更记录

## 💬 常见问题

### Q: 什么时候应该用 `/opsx:propose` vs `/opsx:new`？

**A**: 
- `/opsx:propose` - 你已经清楚要做什么，想快速生成所有文档
- `/opsx:new` - 你想逐步创建，每步都审查

### Q: 任务分解多细合适？

**A**: 每个任务 2-4 小时工作量。太粗无法跟踪进度，太细增加管理成本。

### Q: 可以跳过某些阶段吗？

**A**: 不建议。每个阶段都有价值：
- Proposal 确保方向正确
- Design 避免技术债务
- Specs 保证质量
- Tasks 便于跟踪

### Q: 如何处理紧急修复？

**A**: 仍然创建变更，但可以简化：
```bash
/opsx:ff fix-critical-bug
```
然后快速实施和归档。

---

**最后更新**: 2026-04-22  
**维护者**: ClawBoard 开发团队
