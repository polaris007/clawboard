package com.company.clawboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void truncateAllTables() {
        log.info("开始清空数据库所有表...");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        String[] tables = {
                "dashboard_transcript_issue",
                "dashboard_skill_invocation",
                "dashboard_conversation_turn",
                "dashboard_message",
                "dashboard_session_summary",
                "dashboard_scan_progress",
                "dashboard_scan_history",
                "dashboard_hourly_stats",
                "dashboard_employee"
        };

        for (String table : tables) {
            log.info("清空表: {}", table);
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        log.info("数据库所有表已清空完成");
    }

    @Transactional
    public void executeSqlFile(String filePath) throws IOException {
        log.info("开始执行SQL文件: {}", filePath);

        // 安全检查：确保文件路径在应用目录内，防止目录遍历攻击
        File sqlFile = new File(filePath);
        String canonicalPath = sqlFile.getCanonicalPath();
        String currentDir = new File(".").getCanonicalPath();

        if (!canonicalPath.startsWith(currentDir)) {
            throw new SecurityException("禁止执行应用目录外的SQL文件");
        }

        if (!sqlFile.exists() || !sqlFile.isFile()) {
            throw new IOException("SQL文件不存在或不是一个文件");
        }

        // 读取SQL文件内容（使用UTF-8编码）
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                currentStatement.append(line).append(" ");
                // 当遇到分号时，认为是一条完整的SQL语句
                if (line.endsWith(";")) {
                    sqlStatements.add(currentStatement.toString());
                    currentStatement.setLength(0);
                }
            }
        }

        // 执行所有SQL语句
        int executedCount = 0;
        for (String sql : sqlStatements) {
            try {
                log.info("执行SQL: {}", sql.length() > 100 ? sql.substring(0, 100) + "..." : sql);
                jdbcTemplate.execute(sql);
                executedCount++;
            } catch (Exception e) {
                log.error("执行SQL失败: {}", sql, e);
                throw new RuntimeException("执行SQL语句失败: " + sql, e);
            }
        }

        log.info("SQL文件执行完成，共执行 {} 条语句", executedCount);
    }

    public List<Map<String, Object>> getTableStructure(String tableName) {
        log.info("查询表结构: {}", tableName);
        
        // 使用SHOW CREATE TABLE语句获取表结构
        String sql = "SHOW CREATE TABLE " + tableName;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        
        // 同时获取表的列信息
        String columnsSql = "DESCRIBE " + tableName;
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(columnsSql);
        
        // 将列信息添加到结果中
        if (!result.isEmpty()) {
            result.get(0).put("columns", columns);
        }
        
        return result;
    }
}
