package com.company.clawboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
