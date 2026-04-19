# 数据库重置指南

## 快速开始

### 方法 1: 使用 PowerShell 脚本（推荐）

```powershell
cd G:\Workplace\github\clawboard\scripts
.\reset-database.ps1
```

**特点**：
- ✅ 交互式确认，防止误操作
- ✅ 彩色输出，清晰显示进度
- ✅ 自动验证清空结果
- ⚠️ 需要允许执行 PowerShell 脚本

### 方法 2: 使用 SQL 脚本

```powershell
Get-Content G:\Workplace\github\clawboard\scripts\reset-database.sql | mysql -h localhost -u clawboard -p'Clqc@1234' clawboard
```

或直接在 MySQL 命令行中：

```bash
mysql -h localhost -u clawboard -p'Clqc@1234' clawboard < G:\Workplace\github\clawboard\scripts\reset-database.sql
```

**特点**：
- ✅ 简单直接
- ✅ 可在任何支持 MySQL 的环境中使用
- ⚠️ 无确认提示，需谨慎使用

## 清空的数据表

脚本会清空以下所有表（按依赖顺序）：

1. `dashboard_transcript_issue` - 问题记录
2. `dashboard_skill_invocation` - 技能调用
3. `dashboard_conversation_turn` - 对话轮次
4. `dashboard_message` - 消息明细
5. `dashboard_session_summary` - Session 汇总
6. `dashboard_scan_progress` - 扫描进度
7. `dashboard_scan_history` - 扫描历史
8. `dashboard_hourly_stats` - 小时统计
9. `dashboard_employee` - 员工信息

## 注意事项

⚠️ **重要提醒**：
- 此操作**不可恢复**，所有数据将被永久删除
- 表结构保持不变，只清空数据
- 自增 ID 会重置为 1
- 建议在重新扫描前执行

✅ **安全特性**：
- 使用 `TRUNCATE TABLE` 而非 `DROP TABLE`，保留表结构
- 临时禁用外键检查，确保清空顺序正确
- 清空后自动验证所有表是否为空

## 完整工作流程

```powershell
# 1. 停止正在运行的应用（如果有）
Get-Process -Name java | Stop-Process -Force

# 2. 清空数据库
cd G:\Workplace\github\clawboard\scripts
.\reset-database.ps1

# 3. 重新启动应用
$env:DB_USER="clawboard"; $env:DB_PASS="Clqc@1234"
java -jar ..\target\clawboard-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev

# 4. 触发新的扫描
Invoke-WebRequest -Uri http://localhost:8080/api/scan/trigger -Method POST
```

## 常见问题

### Q: 如果只想清空部分表怎么办？

A: 手动执行 SQL 命令，例如只清空扫描相关数据：

```sql
TRUNCATE TABLE dashboard_transcript_issue;
TRUNCATE TABLE dashboard_skill_invocation;
TRUNCATE TABLE dashboard_conversation_turn;
TRUNCATE TABLE dashboard_message;
TRUNCATE TABLE dashboard_session_summary;
TRUNCATE TABLE dashboard_scan_progress;
TRUNCATE TABLE dashboard_scan_history;
-- 保留 dashboard_employee 和 dashboard_hourly_stats
```

### Q: 清空后需要重新创建表吗？

A: 不需要。`TRUNCATE TABLE` 只清空数据，表结构和索引都保持不变。

### Q: 如何备份当前数据？

A: 在清空前执行：

```powershell
mysqldump -h localhost -u clawboard -p'Clqc@1234' clawboard > backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql
```

### Q: PowerShell 脚本执行被阻止？

A: 需要设置执行策略：

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

## 相关文件

- [reset-database.ps1](./reset-database.ps1) - PowerShell 交互式脚本
- [reset-database.sql](./reset-database.sql) - 纯 SQL 脚本
- [init-complete.sql](../src/main/resources/db/init-complete.sql) - 完整的数据库初始化脚本（包含表结构）
