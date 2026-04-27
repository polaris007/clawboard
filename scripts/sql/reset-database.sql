-- ============================================
-- ClawBoard Database Reset Script (SQL)
-- Usage: mysql -h localhost -u clawboard -p'Clqc@1234' clawboard < reset-database.sql
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;

-- Truncate all tables in correct order
TRUNCATE TABLE dashboard_transcript_issue;
TRUNCATE TABLE dashboard_skill_invocation;
TRUNCATE TABLE dashboard_conversation_turn;
TRUNCATE TABLE dashboard_message;
TRUNCATE TABLE dashboard_session_summary;
TRUNCATE TABLE dashboard_scan_progress;
TRUNCATE TABLE dashboard_scan_history;
TRUNCATE TABLE dashboard_hourly_stats;
TRUNCATE TABLE dashboard_employee;
TRUNCATE TABLE dashboard_execution_trace;

SET FOREIGN_KEY_CHECKS = 1;

-- Add run_id column if not exists (for custom event issues)
-- MySQL 5.7 compatible: check column existence first
SET @dbname = DATABASE();
SET @tablename = 'dashboard_transcript_issue';
SET @columnname = 'run_id';
SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = @dbname
        AND TABLE_NAME = @tablename
        AND COLUMN_NAME = @columnname
    ) > 0,
    'SELECT 1',
    'ALTER TABLE dashboard_transcript_issue ADD COLUMN run_id VARCHAR(50) COMMENT ''Run ID for custom events'' AFTER event_type'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Verify all tables are empty
SELECT 'dashboard_scan_history' as table_name, COUNT(*) as row_count FROM dashboard_scan_history
UNION ALL
SELECT 'dashboard_message', COUNT(*) FROM dashboard_message
UNION ALL
SELECT 'dashboard_session_summary', COUNT(*) FROM dashboard_session_summary
UNION ALL
SELECT 'dashboard_transcript_issue', COUNT(*) FROM dashboard_transcript_issue
UNION ALL
SELECT 'dashboard_conversation_turn', COUNT(*) FROM dashboard_conversation_turn
UNION ALL
SELECT 'dashboard_skill_invocation', COUNT(*) FROM dashboard_skill_invocation
UNION ALL
SELECT 'dashboard_hourly_stats', COUNT(*) FROM dashboard_hourly_stats
UNION ALL
SELECT 'dashboard_employee', COUNT(*) FROM dashboard_employee
UNION ALL
SELECT 'dashboard_execution_trace', COUNT(*) FROM dashboard_execution_trace;
