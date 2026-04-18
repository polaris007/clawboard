package com.company.clawboard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "clawboard")
public class ClawboardProperties {

    private NasConfig nas = new NasConfig();
    private ScanConfig scan = new ScanConfig();
    private ParserConfig parser = new ParserConfig();
    private SkillConfig skill = new SkillConfig();
    private ReportsConfig reports = new ReportsConfig();

    @Data
    public static class NasConfig {
        private String basePath = "/mnt/nas";
        private String openclawDir = ".openclaw";
        private String accountsCsvPath = "scripts/accounts.csv";
    }

    @Data
    public static class ScanConfig {
        private String cron = "0 0 */6 * * *";
        private int threadPoolSize = 8;
        private int batchSize = 100;
        private boolean enabled = true;
    }

    @Data
    public static class ParserConfig {
        private int maxErrorMessageLength = 500;
        private int maxUserInputLength = 200;
        private List<String> systemMessagePrefixes = List.of(
            "A new session was started",
            "Run your Session Startup sequence",
            "Read HEARTBEAT.md",
            "<inbound_metadata>",
            "<openclaw-envelope>"
        );
        private boolean deliveryMirrorFilter = true;
    }

    @Data
    public static class SkillConfig {
        private Map<String, String> nameMapping = Map.of();
    }

    @Data
    public static class ReportsConfig {
        private String outputDir = "scripts/reports";
    }
}
