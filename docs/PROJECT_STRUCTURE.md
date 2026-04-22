# 项目结构整理说明

**整理日期**: 2026-04-22

## 📁 整理概览

本次整理将项目根目录的文件按照类型分类到相应的子目录中，使项目结构更加清晰。

---

## 📂 新的目录结构

### 1. **docs/** - 文档目录

```
docs/
├── README.md                          # 项目主文档
├── CLAUDE.md                          # Claude 配置
├── CLAUDE-home.md                     # Claude 家庭环境配置
├── CLAUDE-work.md                     # Claude 工作环境配置
├── env.txt                            # 环境变量说明
├── openclaw-instance_table.txt        # OpenClaw 实例表
├── openclaw-instances_sample-data.txt # 示例数据
│
├── api/                               # API 文档
│   ├── API接口文档.md
│   ├── API接口文档v1.1.md
│   └── 新增接口文档.md
│
├── design/                            # 设计文档
│   ├── 2026-04-18-clawboard-implementation-plan.md
│   ├── 2026-04-18-openclaw-monitoring-dashboard-design.md
│   └── 2026-04-18-implementation-plan-session-log.md
│
├── guides/                            # 使用指南
│   ├── DATABASE_INIT.md               # 数据库初始化
│   ├── TESTING.md                     # 测试指南
│   ├── REPORT-CONFIGURATION.md        # 报告配置
│   └── SCAN-USAGE.md                  # 扫描使用说明
│
└── reports/                           # 报告文档
    └── archive/                       # 历史报告归档
        ├── DATA-INGESTION-COMPLETION.md
        ├── EMPTY_STRING_FIX_REPORT.md
        ├── FINAL-TEST-REPORT.md
        ├── JAVA_VS_PYTHON_COMPARISON.md
        ├── PAGINATION-IMPLEMENTATION-REPORT.md
        ├── SYSTEM_MESSAGE_EXCLUSION_SUMMARY.md
        ├── TEST-RUN-REPORT.md
        ├── TEST-SUMMARY.md
        ├── USERNAME_TO_USERID_MODIFICATION_REPORT.md
        └── VERIFICATION-CHECKLIST.md
```

### 2. **scripts/** - 脚本目录

```
scripts/
├── init-database.ps1                  # 数据库初始化脚本
├── reset-database.ps1                 # 数据库重置脚本
├── reset-database-tmp.ps1             # 临时重置脚本
├── run-app-optimized.bat              # 优化启动脚本
├── run-single-test.bat                # 单个测试运行脚本
├── run-tests.ps1                      # 测试运行脚本
├── run_test.bat                       # 测试运行脚本
├── start-app.bat                      # 应用启动脚本
├── run.txt                            # 运行说明
│
├── python/                            # Python 脚本
│   ├── analyze-python-custom-errors.py
│   ├── compare-specific-file.py
│   ├── compare_errors.py
│   ├── compare_reports.py
│   ├── debug_parse.py
│   ├── detect-all-transcript-issues-org.py
│   ├── detect-all-transcript-issues.py
│   ├── detect-all-transcript-issues.py.new
│   ├── export_issues.py
│   ├── find_missing_errors.py
│   └── test_list_index_error.py
│
├── sql/                               # SQL 脚本
│   ├── add-missing-issue-fields.sql
│   ├── count_error_types.sql
│   ├── describe_issue_table.sql
│   ├── dump-current-schema.sql
│   ├── export_issues.sql
│   ├── openclaw-instances.sql
│   ├── reset-database.sql
│   ├── update-comments-1.sql
│   └── update-comments.sql
│
├── data/                              # 数据文件
│   ├── accounts-bak.csv
│   ├── accounts-test.csv
│   ├── accounts-test.txt
│   └── accounts.xlsx
│
├── docs/                              # 脚本文档
│   ├── OPTIMIZATION_EXAMPLE.md
│   ├── OPTIMIZATION_NOTES.md
│   ├── OPTIMIZATION_SUMMARY.md
│   ├── QUICK_REFERENCE.md
│   ├── README_RESET.md
│   ├── TERMINOLOGY_UPDATE_SUMMARY.md
│   └── VERIFICATION_CHECKLIST.md
│
└── reports/                           # 脚本生成的报告
    └── ... (3 items)
```

### 3. **test/** - 测试目录

```
test/
├── TestPost.java                      # 测试代码
├── TestPost.class
├── TestResetDatabase.java
├── TestResetDatabase.class
├── TestScanTrigger.java
├── TestScanTrigger.class
├── TestUserSummary.java
├── TestUserSummary.class
├── TestFullFlow.java
├── TestFullFlow.class
├── TestSummaryOnly.java
├── TestSummaryOnly.class
├── QueryDatabase.java
├── QueryDatabase.class
│
├── scripts/                           # 测试脚本
│   ├── check-error-message.js
│   └── check-error-message.py
│
├── session-transcript/                # 会话转录数据 (167 items)
├── prod-logs/                         # 生产日志 (2 items)
├── testlogs/                          # 测试日志 (1 items)
├── testsingle/                        # 单项测试 (1 items)
│
├── test_output.txt                    # 测试输出
├── short_output.txt                   # 简短输出
├── test_request.json                  # 测试请求
├── logs.tar.gz                        # 日志压缩包
├── transcript.zi                      # 转录数据
├── transcript-bak.zi                  # 转录备份
└── .gitignore
```

### 4. **reports/** - 运行时报告目录

```
reports/
├── 2026-04-18/
├── 2026-04-19/
├── 2026-04-20/
├── 2026-04-21/
└── 2026-04-22/
```

> **注意**: 这个目录用于存放运行时生成的扫描报告，按日期自动创建子目录。

---

## 🔄 移动的文件清单

### 从根目录移动到 docs/
- ✅ README.md
- ✅ CLAUDE.md
- ✅ CLAUDE-home.md
- ✅ CLAUDE-work.md
- ✅ DATABASE_INIT.md → docs/guides/
- ✅ TESTING.md → docs/guides/
- ✅ PAGINATION-IMPLEMENTATION-REPORT.md → docs/reports/archive/
- ✅ USERNAME_TO_USERID_MODIFICATION_REPORT.md → docs/reports/archive/
- ✅ EMPTY_STRING_FIX_REPORT.md → docs/reports/archive/

### 从根目录移动到 test/
- ✅ TestPost.java, TestPost.class
- ✅ TestResetDatabase.java, TestResetDatabase.class
- ✅ TestScanTrigger.java, TestScanTrigger.class
- ✅ TestUserSummary.java, TestUserSummary.class
- ✅ TestFullFlow.java, TestFullFlow.class
- ✅ TestSummaryOnly.java, TestSummaryOnly.class
- ✅ QueryDatabase.java, QueryDatabase.class
- ✅ test_output.txt
- ✅ short_output.txt
- ✅ test_request.json

### 从根目录移动到 scripts/
- ✅ init-database.ps1
- ✅ run-app-optimized.bat
- ✅ run-single-test.bat
- ✅ run-tests.ps1
- ✅ run_test.bat
- ✅ start-app.bat
- ✅ check_system_turns.sql → scripts/sql/

### scripts/ 内部整理
- ✅ *.py → scripts/python/
- ✅ *.sql → scripts/sql/
- ✅ *.md → scripts/docs/
- ✅ accounts*.* → scripts/data/
- ✅ transcript-comprehensive-issues*.md → docs/reports/archive/

### test/ 内部整理
- ✅ check-error-message.js → test/scripts/
- ✅ check-error-message.py → test/scripts/

---

## 📋 目录用途说明

| 目录 | 用途 | 示例 |
|------|------|------|
| `docs/` | 项目文档 | API 文档、设计文档、使用指南 |
| `docs/api/` | API 接口文档 | REST API 说明 |
| `docs/design/` | 系统设计文档 | 架构设计、实现计划 |
| `docs/guides/` | 使用指南 | 数据库初始化、测试指南 |
| `docs/reports/archive/` | 历史报告归档 | 测试报告、完成报告 |
| `scripts/` | 运维和工具脚本 | 启动脚本、数据库脚本 |
| `scripts/python/` | Python 工具脚本 | 数据分析、错误检测 |
| `scripts/sql/` | SQL 脚本 | 数据库操作、查询 |
| `scripts/data/` | 数据文件 | CSV、Excel 账户数据 |
| `scripts/docs/` | 脚本文档 | 优化说明、快速参考 |
| `test/` | 测试相关文件 | 测试代码、测试数据 |
| `test/scripts/` | 测试脚本 | JS/Python 测试辅助脚本 |
| `test/session-transcript/` | 测试数据 | 会话转录 JSONL 文件 |
| `reports/` | 运行时报告 | 扫描生成的报告（按日期） |

---

## ⚠️ 注意事项

1. **Git 追踪**: 确保 `.gitignore` 正确配置，避免提交不必要的文件（如 `.class` 文件、大型测试数据）

2. **路径引用**: 如果文档或脚本中有硬编码的路径引用，需要更新为新的路径

3. **CI/CD**: 如果有自动化构建或测试流程，需要更新相关配置中的路径

4. **团队通知**: 通知团队成员目录结构的变化，避免找不到文件

---

## ✨ 整理后的优势

1. **清晰的分类**: 文档、脚本、测试文件各司其职
2. **易于维护**: 相关文件集中管理，便于查找和更新
3. **版本控制友好**: 可以针对不同目录设置不同的 Git 策略
4. **新人友好**: 新成员可以快速了解项目结构
5. **可扩展性**: 子目录结构便于后续添加更多分类

---

## 🔗 相关文档

- [README.md](../docs/README.md) - 项目主文档
- [DATABASE_INIT.md](../docs/guides/DATABASE_INIT.md) - 数据库初始化指南
- [TESTING.md](../docs/guides/TESTING.md) - 测试指南
- [REPORT-CONFIGURATION.md](../docs/guides/REPORT-CONFIGURATION.md) - 报告配置说明
