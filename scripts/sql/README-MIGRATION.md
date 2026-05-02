# 数据库迁移脚本说明

## 本次变更概述

为 `dashboard_skill_invocation` 表添加 `sequence_order` 字段，用于记录同一 turn 中多个 skill 调用的先后顺序。

## 相关文件

### 1. 迁移脚本
- **文件路径**: `scripts/sql/add-sequence-order-to-skill-invocation.sql`
- **功能**: 为现有数据库添加 `sequence_order` 字段
- **特点**: 
  - ✅ 幂等性设计（可重复执行）
  - ✅ 自动检查字段是否存在
  - ✅ 包含验证步骤

### 2. Schema 定义文件
- **文件路径**: `src/main/resources/db/init-complete.sql`
- **修改内容**: 在 `dashboard_skill_invocation` 表的 CREATE TABLE 语句中添加 `sequence_order` 字段定义
- **用途**: 全新部署时的初始 schema

### 3. 实体类
- **文件路径**: `src/main/java/com/company/clawboard/entity/DashboardSkillInvocation.java`
- **修改内容**: 添加 `private Integer sequenceOrder;` 字段
- **用途**: Java 代码中的数据库映射

## 执行方式

### 方式 1: 使用迁移脚本（推荐用于现有环境）

```bash
# Windows PowerShell
Get-Content scripts\sql\add-sequence-order-to-skill-invocation.sql | mysql -h 127.0.0.1 -P 3306 -u clawboard -p'Clqc@1234' clawboard

# Linux/Mac
mysql -h 127.0.0.1 -P 3306 -u clawboard -p'Clqc@1234' clawboard < scripts/sql/add-sequence-order-to-skill-invocation.sql
```

**预期输出**:
```
Field           Type            Null    Key     Default         Extra
...
duration_ms     int(11)         YES             NULL
sequence_order  int(11)         NO              1
created_at      datetime        NO              CURRENT_TIMESTAMP
```

### 方式 2: 全新部署

如果是全新部署，直接执行 `init-complete.sql` 即可，无需单独执行迁移脚本：

```bash
mysql -h 127.0.0.1 -P 3306 -u clawboard -p'Clqc@1234' clawboard < src/main/resources/db/init-complete.sql
```

## 验证步骤

### 1. 检查字段是否存在

```sql
DESCRIBE dashboard_skill_invocation;
```

应该看到 `sequence_order` 字段。

### 2. 检查字段属性

```sql
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'clawboard'
  AND TABLE_NAME = 'dashboard_skill_invocation'
  AND COLUMN_NAME = 'sequence_order';
```

**预期结果**:
```
COLUMN_NAME     | DATA_TYPE | IS_NULLABLE | COLUMN_DEFAULT | COLUMN_COMMENT
sequence_order  | int       | NO          | 1              | 同一turn中的skill调用顺序
```

### 3. 测试数据插入

```sql
INSERT INTO dashboard_skill_invocation (
    session_id, employee_id, skill_name, invoked_at, 
    read_message_id, result_message_id, is_error, 
    trigger_type, duration_ms, sequence_order
) VALUES (
    'test-session', 'test-user', 'test-skill', NOW(),
    'msg-1', 'msg-2', 0,
    'model_read', 100, 1
);

SELECT * FROM dashboard_skill_invocation WHERE session_id = 'test-session';

-- 清理测试数据
DELETE FROM dashboard_skill_invocation WHERE session_id = 'test-session';
```

## 回滚方案

如果需要回滚此变更：

```sql
ALTER TABLE dashboard_skill_invocation DROP COLUMN sequence_order;
```

**注意**: 回滚前请确保没有代码依赖此字段，否则会导致运行时错误。

## Git Commit 历史

本次数据库变更相关的 commits:

1. `343d208` - db: add sequence_order column to dashboard_skill_invocation table
   - 创建迁移脚本
   - 更新 init-complete.sql

2. `f78f17e` - feat: enable sequenceOrder field in DashboardSkillInvocation entity and DataIngestionService
   - 更新实体类
   - 启用数据填充逻辑

3. `b35c429` - improve: make database migration script idempotent with existence check
   - 改进迁移脚本，添加幂等性检查

## 注意事项

1. **备份优先**: 执行迁移脚本前，建议先备份数据库
2. **环境同步**: 确保开发、测试、生产环境的 schema 保持一致
3. **代码兼容**: 数据库变更后，需要重新编译和部署应用
4. **幂等性**: 迁移脚本可以安全地重复执行，不会产生副作用

## 相关文档

- 设计文档: `docs/superpowers/specs/2026-04-23-skill-invocation-chain-detection-design.md`
- 实现计划: `docs/superpowers/plans/2026-04-23-skill-invocation-chain-detection.md`
