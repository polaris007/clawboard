-- 为 dashboard_skill_invocation 表添加 sequence_order 字段
-- 用于记录同一 turn 中多个 skill 调用的先后顺序

ALTER TABLE dashboard_skill_invocation 
ADD COLUMN sequence_order INT NOT NULL DEFAULT 1 COMMENT '同一turn中的skill调用顺序';
