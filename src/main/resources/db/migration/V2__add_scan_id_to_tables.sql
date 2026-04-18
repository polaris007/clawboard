-- Add scan_id column to tracking tables for better data management

ALTER TABLE dashboard_message 
ADD COLUMN scan_id BIGINT AFTER session_id,
ADD INDEX idx_scan_id (scan_id);

ALTER TABLE dashboard_conversation_turn 
ADD COLUMN scan_id BIGINT AFTER turn_number,
ADD INDEX idx_scan_id (scan_id);

ALTER TABLE dashboard_skill_invocation 
ADD COLUMN scan_id BIGINT AFTER session_id,
ADD INDEX idx_scan_id (scan_id);

ALTER TABLE dashboard_transcript_issue 
ADD COLUMN scan_id BIGINT AFTER session_id,
ADD INDEX idx_scan_id (scan_id);
