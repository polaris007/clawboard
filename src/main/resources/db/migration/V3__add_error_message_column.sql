-- Add error_message column to dashboard_message table for storing detailed error information

ALTER TABLE dashboard_message 
ADD COLUMN error_message TEXT COMMENT '错误消息内容' AFTER is_error;
