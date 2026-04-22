# 数据库初始化指南

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS clawboard 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 2. 执行初始化脚本

使用 MySQL 命令行工具执行完整的初始化脚本：

```bash
mysql -h localhost -u root -p clawboard < src/main/resources/db/init-complete.sql
```

或在 Windows PowerShell 中：

```powershell
Get-Content src\main\resources\db\init-complete.sql | mysql -h localhost -u root -p clawboard
```

### 3. 验证表结构

```sql
USE clawboard;
SHOW TABLES;
```

应该看到以下 8 个表：
- dashboard_employee
- dashboard_scan_history
- dashboard_scan_progress
- dashboard_message
- dashboard_conversation_turn
- dashboard_skill_invocation
- dashboard_transcript_issue
- dashboard_hourly_stats

## 表结构说明

### dashboard_employee - 员工信息表
存储员工的基本信息，用于将 session 映射到具体员工。

**关键字段：**
- `employee_id`: 工号（主键）
- `name`: 姓名
- `department`: 部门

### dashboard_scan_history - 扫描历史记录表
记录每次扫描的执行情况和统计信息。

**关键字段：**
- `id`: 扫描记录ID（自增主键）
- `status`: 状态（running/completed/failed）
- `users_scanned`: 扫描用户数
- `files_processed`: 已处理文件数
- `new_messages`: 新增消息数
- `new_issues`: 新增问题数

### dashboard_scan_progress - 扫描进度表
实时跟踪扫描进度（每条扫描记录一行）。

**关键字段：**
- `scan_id`: 关联扫描ID（唯一索引）
- `processed_files`: 已处理文件数
- `total_files`: 总文件数

### dashboard_message - 消息明细表
存储所有解析出的消息记录。

**关键字段：**
- `session_id` + `message_id`: 联合主键
- `scan_id`: 关联扫描ID
- `role`: 角色（user/assistant/toolResult）
- `error_message`: 错误消息内容（如果有）
- `is_error`: 是否报错

**索引：**
- `idx_scan_id`: 按扫描ID查询
- `idx_employee_time`: 按员工和时间查询
- `idx_time`: 按时间查询

### dashboard_conversation_turn - 对话轮次表
将用户消息和助手回复组装成对话轮次。

**关键字段：**
- `id`: 对话轮次ID（自增主键）
- `session_id` + `turn_number`: 唯一索引
- `user_input`: 用户输入
- `assistant_response`: 助手回复
- `has_error`: 是否有错误

### dashboard_skill_invocation - 技能调用记录表
记录所有技能/工具的调用情况。

**关键字段：**
- `id`: 技能调用ID（自增主键）
- `skill_name`: 技能名称
- `arguments`: 调用参数（JSON）
- `result`: 执行结果
- `is_error`: 是否执行失败

### dashboard_transcript_issue - 错误/问题记录表
存储检测到的各种问题（模型错误、超时、异常停止等）。

**关键字段：**
- `id`: 问题ID（自增主键）
- `error_type`: 错误分类（ASSISTANT_ERROR, modelErrors, abnormal_stop 等）
- `severity`: 严重程度（HIGH/MEDIUM/LOW）
- `error_message`: 原始错误信息
- `session_id` + `message_id` + `error_type`: 唯一索引（避免重复）

### dashboard_hourly_stats - 小时统计数据表
按小时聚合的统计数据，用于仪表盘展示。

**关键字段：**
- `employee_id` + `stat_hour`: 联合主键
- `total_messages`: 总消息数
- `total_issues`: 总问题数
- `avg_response_time`: 平均响应时间

## 注意事项

1. **字符集**: 所有表使用 `utf8mb4` 字符集，支持完整的 Unicode 字符（包括 emoji）
2. **引擎**: 使用 InnoDB 引擎，支持事务和外键
3. **时间精度**: 时间戳字段使用 `DATETIME(3)`，精确到毫秒
4. **索引优化**: 已为常用查询模式创建索引，确保查询性能

## 数据清理

如需清空所有数据（保留表结构）：

```sql
TRUNCATE TABLE dashboard_hourly_stats;
TRUNCATE TABLE dashboard_transcript_issue;
TRUNCATE TABLE dashboard_skill_invocation;
TRUNCATE TABLE dashboard_conversation_turn;
TRUNCATE TABLE dashboard_message;
TRUNCATE TABLE dashboard_scan_progress;
TRUNCATE TABLE dashboard_scan_history;
-- 注意：dashboard_employee 通常不清空，除非要重置员工映射
```

## 备份与恢复

### 备份
```bash
mysqldump -h localhost -u root -p clawboard > clawboard_backup.sql
```

### 恢复
```bash
mysql -h localhost -u root -p clawboard < clawboard_backup.sql
```
