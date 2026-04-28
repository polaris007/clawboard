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
    private AggregationConfig aggregation = new AggregationConfig();

    @Data
    public static class NasConfig {
        private String basePath = "/datafs/openclaw";
        private String openclawDir = "agents";
        private String accountsCsvPath = "accounts.csv";
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
        private String outputDir = "reports";
    }

    @Data
    public static class AggregationConfig {
        // 是否启用智能混合聚合策略
        private boolean enableSmartAggregation = true;
        
        // 时间窗口大小（小时）
        private int timeWindowHours = 24;
        
        // 是否启用基于文件的增量聚合
        private boolean enableFileBasedIncremental = true;
        
        // 是否启用时间窗口兜底
        private boolean enableTimeWindow = true;
    }
}
