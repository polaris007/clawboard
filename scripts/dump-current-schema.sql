-- ============================================
-- Dump current database schema
-- ============================================

SET NAMES utf8mb4;

-- Show all tables
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- Show create table for each table
-- We'll use SHOW CREATE TABLE in separate queries
