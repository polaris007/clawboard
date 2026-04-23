-- V4__add_turn_id_to_message.sql
-- Add turn_id column to dashboard_message table for turn association

ALTER TABLE dashboard_message 
ADD COLUMN turn_id BIGINT COMMENT '关联轮次ID' AFTER session_id;

-- Add index for query performance
ALTER TABLE dashboard_message 
ADD INDEX idx_turn_id (turn_id);
