-- 修复 session_id 字段长度问题
-- 将所有表的 session_id 字段从 VARCHAR(36) 扩展为 VARCHAR(100)

USE clawboard;

ALTER TABLE dashboard_session_summary 
    MODIFY COLUMN session_id VARCHAR(100) NOT NULL COMMENT 'Session UUID/ID';

ALTER TABLE dashboard_message 
    MODIFY COLUMN session_id VARCHAR(100) NOT NULL COMMENT 'Session UUID/ID';

ALTER TABLE dashboard_conversation_turn 
    MODIFY COLUMN session_id VARCHAR(100) NOT NULL COMMENT 'Session UUID/ID';

ALTER TABLE dashboard_skill_invocation 
    MODIFY COLUMN session_id VARCHAR(100) NOT NULL COMMENT 'Session UUID/ID';

ALTER TABLE dashboard_transcript_issue 
    MODIFY COLUMN session_id VARCHAR(100) NOT NULL COMMENT 'Session UUID/ID';

ALTER TABLE dashboard_scan_progress 
    MODIFY COLUMN session_id VARCHAR(100) COMMENT '当前Session UUID';

SELECT 'session_id field length updated successfully' AS status;
