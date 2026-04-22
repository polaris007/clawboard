# 项目目录快速参考

## 📁 主要目录

```
clawboard/
├── docs/          # 📚 文档（设计、API、指南、报告）
├── scripts/       # 🛠️ 脚本（Python、SQL、启动脚本）
├── test/          # 🧪 测试（测试代码、测试数据）
├── reports/       # 📊 运行时报告（自动生成）
├── src/           # 💻 源代码
└── target/        # 📦 构建输出
```

## 🔍 快速查找

### 我想看...

| 我要找... | 去这里 |
|----------|--------|
| **API 文档** | `docs/api/` |
| **设计文档** | `docs/design/` |
| **使用指南** | `docs/guides/` |
| **历史报告** | `docs/reports/archive/` |
| **启动应用** | `scripts/run-app-optimized.bat` |
| **重置数据库** | `scripts/reset-database.ps1` |
| **Python 脚本** | `scripts/python/` |
| **SQL 脚本** | `scripts/sql/` |
| **测试代码** | `test/*.java` |
| **测试数据** | `test/session-transcript/` |
| **扫描报告** | `reports/{date}/` |

## 🚀 常用命令

### 启动应用
```bash
scripts\run-app-optimized.bat
```

### 重置数据库
```powershell
scripts\reset-database.ps1
```

### 运行测试
```powershell
scripts\run-tests.ps1
```

### 构建项目
```bash
mvn clean package "-Dmaven.test.skip=true"
```

## 📝 重要文件

- [README.md](docs/README.md) - 项目介绍
- [DATABASE_INIT.md](docs/guides/DATABASE_INIT.md) - 数据库初始化
- [TESTING.md](docs/guides/TESTING.md) - 测试指南
- [PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md) - 详细目录结构

---

**最后更新**: 2026-04-22
