# 📄 报告生成配置指南

## 🎯 概述

ClawBoard 的报告生成功能支持**完全可配置的输出目录**。您可以自定义报告保存的位置和目录结构。

## ⚙️ 配置方式

### 1. 在 application.yml 中配置

```yaml
clawboard:
  reports:
    output-dir: scripts/reports  # 自定义报告输出目录
```

### 2. 在 application-dev.yml 中配置（开发环境）

```yaml
clawboard:
  reports:
    output-dir: ./my-reports  # 开发环境使用不同的目录
```

### 3. 在 application-prod.yml 中配置（生产环境）

```yaml
clawboard:
  reports:
    output-dir: /var/reports/clawboard  # 生产环境使用绝对路径
```

## 📁 目录结构

报告按**日期分层**存储：

```
{output-dir}/
└── {yyyy-MM-dd}/
    └── transcript-comprehensive-issues.md
```

### 示例

如果配置 `output-dir: scripts/reports`，则生成的报告路径为：
```
scripts/reports/
└── 2026-04-18/
    └── transcript-comprehensive-issues.md
```

如果配置 `output-dir: /data/reports`，则生成的报告路径为：
```
/data/reports/
└── 2026-04-18/
    └── transcript-comprehensive-issues.md
```

## 🔧 配置项说明

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `clawboard.reports.output-dir` | String | `scripts/reports` | 报告输出的根目录 |

## 💡 使用场景

### 场景 1: 开发环境使用相对路径

```yaml
clawboard:
  reports:
    output-dir: ./dev-reports
```

### 场景 2: 生产环境使用绝对路径

```yaml
clawboard:
  reports:
    output-dir: /opt/clawboard/reports
```

### 场景 3: 按项目分类存储

```yaml
clawboard:
  reports:
    output-dir: reports/project-a
```

### 场景 4: 网络共享存储

```yaml
clawboard:
  reports:
    output-dir: //fileserver/reports/clawboard
```

## 🔄 动态修改

您可以在不重启应用的情况下修改配置文件，但需要**重新启动应用**才能使新配置生效。

## 📝 注意事项

1. **目录自动创建**: 如果配置的目录不存在，系统会自动创建
2. **权限要求**: 确保应用有写入配置目录的权限
3. **相对路径**: 相对路径基于应用的工作目录（通常是项目根目录）
4. **绝对路径**: 推荐使用绝对路径以避免路径混淆
5. **Windows 路径**: Windows 环境下可以使用正斜杠 `/` 或反斜杠 `\`

## 🧪 测试配置

修改配置后，触发一次扫描来验证报告是否生成到正确的目录：

```bash
# 触发扫描
curl -X POST http://localhost:8080/api/scan/trigger

# 检查报告目录
ls -la {your-configured-dir}/{current-date}/
```

## 📊 实现细节

- **配置类**: `ClawboardProperties.ReportsConfig`
- **服务类**: `ReportGenerator`
- **调用时机**: 每次扫描完成后自动调用
- **失败处理**: 报告生成失败不会影响扫描流程

## 🎨 报告格式

生成的报告是 **Markdown 格式**，包含：
- 扫描时间
- 问题总数
- 按严重程度分类统计
- 每个问题的详细信息（类型、会话ID、描述等）

---

**配置完成！** 现在您可以根据需要自定义报告存储位置了。🚀
