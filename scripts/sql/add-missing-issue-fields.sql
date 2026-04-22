-- Add missing fields to dashboard_transcript_issue table
ALTER TABLE dashboard_transcript_issue 
ADD COLUMN user_input TEXT COMMENT '用户输入内容' AFTER error_message,
ADD COLUMN cause_analysis VARCHAR(500) COMMENT '原因分析' AFTER user_input,
ADD COLUMN file_path VARCHAR(500) COMMENT '文件路径' AFTER cause_analysis,
ADD COLUMN error_line_content TEXT COMMENT '错误行内容' AFTER file_path,
ADD COLUMN next_line_content TEXT COMMENT '下一行内容' AFTER error_line_content;
