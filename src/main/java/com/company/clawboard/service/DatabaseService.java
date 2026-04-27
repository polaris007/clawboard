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
                "dashboard_employee",
                "dashboard_execution_trace"
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
                String trimmedSql = sql.trim();
                log.info("执行SQL: {}", trimmedSql.length() > 100 ? trimmedSql.substring(0, 100) + "..." : trimmedSql);
                
                // 判断是否为SELECT查询
                if (trimmedSql.toUpperCase().startsWith("SELECT")) {
                    // 执行查询并打印结果
                    List<Map<String, Object>> results = jdbcTemplate.queryForList(trimmedSql);
                    log.info("查询返回 {} 条记录", results.size());
                    
                    // 打印查询结果到控制台
                    if (!results.isEmpty()) {
                        printQueryResults(results);
                    }
                } else {
                    // 执行非查询SQL（INSERT, UPDATE, DELETE, DDL等）
                    jdbcTemplate.execute(trimmedSql);
                }
                
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

    /**
     * 打印查询结果到控制台
     * @param results 查询结果列表
     */
    private void printQueryResults(List<Map<String, Object>> results) {
        if (results == null || results.isEmpty()) {
            log.info("查询结果为空");
            return;
        }

        // 获取列名
        List<String> columnNames = new ArrayList<>(results.get(0).keySet());

        // 计算每列的最大宽度
        int[] columnWidths = new int[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i++) {
            columnWidths[i] = columnNames.get(i).length();
        }

        // 遍历所有行，计算最大宽度
        for (Map<String, Object> row : results) {
            for (int i = 0; i < columnNames.size(); i++) {
                String columnName = columnNames.get(i);
                Object value = row.get(columnName);
                String valueStr = value != null ? value.toString() : "NULL";
                columnWidths[i] = Math.max(columnWidths[i], valueStr.length());
            }
        }

        // 限制最大列宽，避免过宽
        final int MAX_COLUMN_WIDTH = 50;
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = Math.min(columnWidths[i], MAX_COLUMN_WIDTH);
        }

        // 构建分隔线
        StringBuilder separator = new StringBuilder("+");
        for (int width : columnWidths) {
            separator.append("-".repeat(width + 2)).append("+");
        }

        // 打印表头
        log.info("\n{}", separator);
        StringBuilder header = new StringBuilder("|");
        for (int i = 0; i < columnNames.size(); i++) {
            String colName = columnNames.get(i);
            header.append(String.format(" %" + columnWidths[i] + "s |", colName));
        }
        log.info("{}", header);
        log.info("{}", separator);

        // 打印数据行（最多显示100行）
        int maxRows = Math.min(results.size(), 100);
        for (int rowNum = 0; rowNum < maxRows; rowNum++) {
            Map<String, Object> row = results.get(rowNum);
            StringBuilder rowStr = new StringBuilder("|");
            for (int i = 0; i < columnNames.size(); i++) {
                String columnName = columnNames.get(i);
                Object value = row.get(columnName);
                String valueStr = value != null ? value.toString() : "NULL";
                if (valueStr.length() > MAX_COLUMN_WIDTH) {
                    valueStr = valueStr.substring(0, MAX_COLUMN_WIDTH - 3) + "...";
                }
                rowStr.append(String.format(" %" + columnWidths[i] + "s |", valueStr));
            }
            log.info("{}", rowStr);
        }
        log.info("{}", separator);

        if (results.size() > 100) {
            log.info("... 还有 {} 条记录未显示", results.size() - 100);
        }

        log.info("共 {} 条记录", results.size());
    }
}
