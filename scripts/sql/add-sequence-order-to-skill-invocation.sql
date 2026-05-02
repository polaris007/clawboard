-- 为 dashboard_skill_invocation 表添加 sequence_order 字段
-- 用于记录同一 turn 中多个 skill 调用的先后顺序
-- 执行前请确保已备份数据库

-- 检查字段是否已存在，如果不存在则添加
SET @dbname = DATABASE();
SET @tablename = 'dashboard_skill_invocation';
SET @columnname = 'sequence_order';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT NOT NULL DEFAULT 1 COMMENT ''同一turn中的skill调用顺序''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 验证字段添加
DESCRIBE dashboard_skill_invocation;
