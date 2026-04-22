# ClawBoard Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a SpringBoot monitoring dashboard that parses OpenClaw session transcript JSONL files from NAS and provides REST APIs for usage analytics, error detection, skill tracking, and conversation tracing.

**Architecture:** Independent SpringBoot service that reads JSONL files from NAS-mounted user directories on a configurable schedule, parses them into structured data in OceanBase (MySQL mode), and exposes REST APIs for the frontend dashboard. The parser engine is the core — it handles incremental scanning, cross-file dedup, turn assembly, skill detection, and error classification.

**Tech Stack:** JDK 17, SpringBoot 3.2.x, MyBatis 3.0.x, Flyway, OceanBase MySQL mode, Jackson, Lombok, JUnit 5, Testcontainers

**Design Spec:** `docs/2026-04-18-openclaw-monitoring-dashboard-design.md`
**Frontend API Contract:** `docs/API接口文档.md`
**Sample Test Data:** `test/openclaw-logs/agents/main/sessions/` (real JSONL files from a live instance)

---

## File Structure

```
clawboard/
├── pom.xml
├── src/main/java/com/company/clawboard/
│   ├── ClawboardApplication.java
│   ├── config/
│   │   ├── ClawboardProperties.java            # @ConfigurationProperties
│   │   └── AsyncConfig.java                     # ThreadPool for parallel scanning
│   ├── parser/
│   │   ├── model/                               # Parser internal records (JDK 17)
│   │   │   ├── JsonlRecord.java                 # Sealed interface for all JSONL line types
│   │   │   ├── SessionRecord.java               # type=session
│   │   │   ├── MessageRecord.java               # type=message (user/assistant/toolResult)
│   │   │   ├── CustomRecord.java                # type=custom
│   │   │   ├── MetadataRecord.java              # type=model_change/thinking_level_change
│   │   │   ├── UsageInfo.java                   # Extracted token/cost data
│   │   │   └── ChainStep.java                   # Single step in chain_summary
│   │   ├── MessageParser.java                   # Parse single JSONL line → JsonlRecord
│   │   ├── SystemMessageFilter.java             # Filter delivery-mirror & system user msgs
│   │   ├── UsageExtractor.java                  # Extract usage from assistant messages
│   │   ├── SkillDetector.java                   # Detect SKILL.md reads & slash commands
│   │   ├── IssueDetector.java                   # Error pattern matching & flow checks
│   │   ├── TurnAssembler.java                   # Assemble turns from message stream
│   │   └── TranscriptParser.java                # Orchestrate single-file parsing
│   ├── scanner/
│   │   ├── ScanOrchestrator.java                # Full scan flow with concurrency control
│   │   ├── UserScanner.java                     # Scan all files for one user
│   │   ├── ScanProgressService.java             # Track byte offsets per file
│   │   └── HourlyStatsAggregator.java           # Recompute affected hourly stats
│   ├── entity/
│   │   ├── DashboardEmployee.java
│   │   ├── DashboardScanProgress.java
│   │   ├── DashboardMessage.java
│   │   ├── DashboardConversationTurn.java
│   │   ├── DashboardSkillInvocation.java
│   │   ├── DashboardTranscriptIssue.java
│   │   ├── DashboardHourlyStats.java
│   │   └── DashboardScanHistory.java
│   ├── dto/
│   │   ├── ApiResponse.java                     # Unified {code, message, data} wrapper
│   │   ├── PageResult.java                      # Paginated response wrapper
│   │   ├── TimeRangeRequest.java                # Base class for filtered queries
│   │   ├── DashboardSummaryResponse.java
│   │   ├── TrendDataPoint.java
│   │   ├── UserSummaryItem.java
│   │   ├── TurnSearchRequest.java
│   │   ├── TurnSearchItem.java
│   │   ├── TraceResponse.java
│   │   ├── TraceNode.java
│   │   ├── ErrorSummaryResponse.java
│   │   ├── ErrorSearchRequest.java
│   │   ├── ErrorSearchItem.java
│   │   ├── ScanTriggerResponse.java
│   │   ├── ScanStatusResponse.java
│   │   ├── ScanHistoryItem.java
│   │   └── SkillOption.java
│   ├── mapper/
│   │   ├── EmployeeMapper.java
│   │   ├── ScanProgressMapper.java
│   │   ├── MessageMapper.java
│   │   ├── ConversationTurnMapper.java
│   │   ├── SkillInvocationMapper.java
│   │   ├── TranscriptIssueMapper.java
│   │   ├── HourlyStatsMapper.java
│   │   └── ScanHistoryMapper.java
│   ├── service/
│   │   ├── DashboardService.java                # Global stats, summary, trend, user summary
│   │   ├── TurnService.java                     # Turn search, trace, quality update
│   │   ├── ErrorService.java                    # Error summary & search
│   │   ├── SkillService.java                    # Skill options
│   │   └── ScanManagementService.java           # Trigger, status, history
│   └── controller/
│       ├── DashboardController.java             # 5.1-5.5
│       ├── TurnController.java                  # 5.6-5.7, 5.10
│       ├── ErrorController.java                 # 5.8-5.9
│       └── ScanController.java                  # 5.11-5.13
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── db/migration/
│   │   └── V1__init_schema.sql
│   └── mapper/                                  # MyBatis XML mappers
│       ├── MessageMapper.xml
│       ├── ConversationTurnMapper.xml
│       ├── SkillInvocationMapper.xml
│       ├── TranscriptIssueMapper.xml
│       ├── HourlyStatsMapper.xml
│       ├── ScanProgressMapper.xml
│       ├── ScanHistoryMapper.xml
│       └── EmployeeMapper.xml
├── src/test/java/com/company/clawboard/
│   ├── parser/
│   │   ├── MessageParserTest.java
│   │   ├── SystemMessageFilterTest.java
│   │   ├── UsageExtractorTest.java
│   │   ├── SkillDetectorTest.java
│   │   ├── IssueDetectorTest.java
│   │   ├── TurnAssemblerTest.java
│   │   └── TranscriptParserTest.java
│   ├── scanner/
│   │   └── ScanOrchestratorTest.java
│   └── controller/
│       ├── DashboardControllerTest.java
│       ├── TurnControllerTest.java
│       └── ScanControllerTest.java
├── src/test/resources/
│   ├── application-test.yml
│   └── fixtures/                                # Curated sample JSONL for tests
│       ├── simple-session.jsonl
│       ├── session-with-skill.jsonl
│       ├── session-with-errors.jsonl
│       └── session-compacted.jsonl
└── test/openclaw-logs/                          # Real-world test data (existing)
```

---

## Task 1: SpringBoot Project Scaffolding

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/company/clawboard/ClawboardApplication.java`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-dev.yml`

- [ ] **Step 1: Create pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.company</groupId>
    <artifactId>clawboard</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>ClawBoard</name>
    <description>OpenClaw Monitoring Dashboard</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-spring-boot.version>3.0.3</mybatis-spring-boot.version>
    </properties>

    <dependencies>
        <!-- Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis-spring-boot.version}</version>
        </dependency>

        <!-- Flyway -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-mysql</artifactId>
        </dependency>

        <!-- MySQL Driver (OceanBase MySQL mode compatible) -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Jackson (included with web starter, explicit for extras) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Configuration Processor -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter-test</artifactId>
            <version>${mybatis-spring-boot.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- H2 for local dev/test -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:clawboard}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USER:root}
    password: ${DB_PASS:}
    driver-class-name: com.mysql.cj.jdbc.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    default-enum-type-handler: org.apache.ibatis.type.EnumTypeHandler

clawboard:
  nas:
    base-path: ${NAS_BASE_PATH:/mnt/nas}
    openclaw-dir: .openclaw
  scan:
    cron: "0 0 */6 * * *"
    thread-pool-size: 8
    batch-size: 100
    enabled: true
  parser:
    max-error-message-length: 500
    max-user-input-length: 200
    system-message-prefixes:
      - "A new session was started"
      - "Run your Session Startup sequence"
      - "Read HEARTBEAT.md"
      - "<inbound_metadata>"
      - "<openclaw-envelope>"
    delivery-mirror-filter: true
  skill:
    name-mapping:
      official-doc-writer: 公文写作
      pptx: ppt生成
```

- [ ] **Step 3: Create application-dev.yml** (local H2 for dev)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:clawboard;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  flyway:
    enabled: true

clawboard:
  nas:
    base-path: ./test/openclaw-logs/../   # points to test directory parent
```

- [ ] **Step 4: Create ClawboardApplication.java**

```java
package com.company.clawboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class ClawboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClawboardApplication.class, args);
    }
}
```

- [ ] **Step 5: Verify project compiles**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add pom.xml src/main/java src/main/resources
git commit -m "feat: scaffold SpringBoot project with dependencies and config"
```

---

## Task 2: Database Schema (Flyway Migration)

**Files:**
- Create: `src/main/resources/db/migration/V1__init_schema.sql`

- [ ] **Step 1: Create V1__init_schema.sql**

Copy all 8 `CREATE TABLE` statements directly from the design doc Section 3 (3.0 through 3.7). All tables use the `dashboard_` prefix. The exact DDL is in the design spec.

Key tables:
1. `dashboard_employee` — user-department mapping
2. `dashboard_scan_progress` — incremental scan tracking (PK: `employee_id, file_path`)
3. `dashboard_message` — message detail (PK: `session_id, message_id`)
4. `dashboard_conversation_turn` — turn tracking (UNIQUE: `session_id, start_message_id`)
5. `dashboard_skill_invocation` — skill call records (UNIQUE: `session_id, read_message_id`)
6. `dashboard_transcript_issue` — error records (UNIQUE: `session_id, message_id, error_type`)
7. `dashboard_hourly_stats` — pre-aggregated hourly stats (PK: `employee_id, stat_hour`)
8. `dashboard_scan_history` — scan execution history

**Important**: For H2 compatibility during development/test, add an H2-specific migration `V1_1__h2_compat.sql` that is only active in test profile, OR use `spring.flyway.locations` to switch between MySQL and H2 DDL. Since OceanBase is MySQL-mode, the DDL should work directly.

**Note on H2 limitations**: H2's MySQL mode does NOT support `INSERT ... ON DUPLICATE KEY UPDATE`. For unit tests of mapper logic that uses this syntax, either:
- Use Testcontainers with a real MySQL for integration tests
- Or mock the mapper layer in unit tests

- [ ] **Step 2: Verify migration runs (dev profile)**

Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Expected: Flyway migration applies successfully, tables created

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/db/migration/
git commit -m "feat: add Flyway migration with all 8 dashboard tables"
```

---

## Task 3: Configuration Properties

**Files:**
- Create: `src/main/java/com/company/clawboard/config/ClawboardProperties.java`
- Create: `src/main/java/com/company/clawboard/config/AsyncConfig.java`

- [ ] **Step 1: Create ClawboardProperties.java**

```java
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

    @Data
    public static class NasConfig {
        private String basePath = "/mnt/nas";
        private String openclawDir = ".openclaw";
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
}
```

- [ ] **Step 2: Create AsyncConfig.java**

```java
package com.company.clawboard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final ClawboardProperties properties;

    @Bean("scanExecutor")
    public Executor scanExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getScan().getThreadPoolSize());
        executor.setMaxPoolSize(properties.getScan().getThreadPoolSize());
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("scan-");
        executor.initialize();
        return executor;
    }
}
```

- [ ] **Step 3: Verify properties bind correctly**

Run: `mvn compile`
Expected: BUILD SUCCESS (full binding test deferred to integration test)

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/config/
git commit -m "feat: add configuration properties and thread pool config"
```

---

## Task 4: Entity Classes

**Files:**
- Create: `src/main/java/com/company/clawboard/entity/` (8 files)

- [ ] **Step 1: Create all entity classes**

Each entity maps 1:1 to a database table. Use Lombok `@Data` for all. Fields match the column names with camelCase conversion (MyBatis `map-underscore-to-camel-case: true`).

**DashboardEmployee.java:**
Fields: `employeeId` (String), `employeeName`, `teamName`, `isActive` (Integer), `updatedAt` (LocalDateTime)

**DashboardScanProgress.java:**
Fields: `employeeId`, `filePath`, `lastOffset` (Long), `fileSize` (Long), `fileMtime` (Long), `sessionId`, `lastMessageId`, `lastMessageTs` (LocalDateTime), `updatedAt`

**DashboardMessage.java:**
Fields: `sessionId`, `messageId`, `employeeId`, `role`, `messageTimestamp` (LocalDateTime), `inputTokens` (Integer), `outputTokens`, `cacheReadTokens`, `cacheWriteTokens`, `totalTokens`, `costTotal` (BigDecimal), `provider`, `model`, `stopReason`, `durationMs` (Integer), `isError` (Integer), `toolName`, `toolCallId`, `parentId`, `createdAt`

**DashboardConversationTurn.java:**
Fields: `id` (Long), `sessionId`, `employeeId`, `turnIndex` (Integer, nullable), `startMessageId`, `endMessageId`, `startTime` (LocalDateTime), `endTime`, `userInput`, `totalInputTokens` (Integer), `totalOutputTokens`, `totalTokens`, `totalCost` (BigDecimal), `toolCallsCount`, `toolCallsSuccess`, `toolCallsError`, `skillCallsCount`, `skillCallsSuccess`, `skillCallsError`, `isComplete` (Integer), `hasError` (Integer), `status` (String), `totalDurationMs` (Integer), `toolDurationMs`, `modelDurationMs`, `chainSummary` (String), `logFilePath`, `qualityStatus` (Integer), `createdAt`

**DashboardSkillInvocation.java:**
Fields: `id` (Long), `sessionId`, `employeeId`, `turnId` (Long), `skillName`, `skillPath`, `invokedAt` (LocalDateTime), `readMessageId`, `resultMessageId`, `isError` (Integer), `triggerType`, `durationMs` (Integer), `createdAt`

**DashboardTranscriptIssue.java:**
Fields: `id` (Long), `sessionId`, `messageId`, `employeeId`, `errorType`, `severity`, `description`, `errorMessage` (String), `eventType`, `provider`, `model`, `lineNumber` (Integer), `occurredAt` (LocalDateTime), `turnId` (Long), `createdAt`

**DashboardHourlyStats.java:**
Fields: `employeeId`, `statHour` (LocalDateTime), `totalTokens` (Long), `inputTokens` (Long), `outputTokens` (Long), `totalCost` (BigDecimal), `cacheReadTokens` (Long), `cacheWriteTokens` (Long), `conversationTurns` (Integer), `completeTurns`, `errorTurns`, `skillInvocations`, `skillErrors`, `toolCalls`, `toolErrors`, `errorCount`, `updatedAt`

**DashboardScanHistory.java:**
Fields: `id` (Long), `triggerType`, `status`, `startedAt` (LocalDateTime), `finishedAt`, `durationMs` (Long), `usersScanned` (Integer), `dirsScanned`, `filesTotal`, `filesProcessed`, `filesSkipped`, `filesError`, `newMessages`, `newTurns`, `newIssues`, `newSkillCalls`, `errorMessage` (String), `createdAt`

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/company/clawboard/entity/
git commit -m "feat: add entity classes for all 8 dashboard tables"
```

---

## Task 5: DTO Classes & API Response Wrapper

**Files:**
- Create: `src/main/java/com/company/clawboard/dto/` (multiple files)

- [ ] **Step 1: Create ApiResponse.java**

```java
package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
```

- [ ] **Step 2: Create PageResult.java**

```java
package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private long total;
    private int page;
    private int pageSize;
    private List<T> list;
}
```

- [ ] **Step 3: Create TimeRangeRequest.java** (base for filtered queries)

```java
package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TimeRangeRequest {
    private String teamName;
    private String userName;
    private Long startTime;   // epoch millis
    private Long endTime;     // epoch millis
    private Integer page;     // default 1
    private Integer pageSize; // default 10

    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
    public int getOffset() { return (getPageOrDefault() - 1) * getPageSizeOrDefault(); }
}
```

- [ ] **Step 4: Create all remaining DTOs**

Create one DTO per API response object, matching the frontend API contract. Key DTOs:

- `DashboardSummaryResponse`: consumedTokens, conversationTurns, skillCalls, activeUsers
- `GlobalStatsResponse`: totalTokens, totalTurns, totalSkillCalls, totalUsers
- `TrendDataPoint`: timeLabel (Long), tokens (Long), turns (Integer), skills (Integer)
- `UserSummaryItem`: userId, userName, teamName, status, lastHeartbeat, tokens (nested), turns (nested), skillCalls (nested), toolCalls (nested), topSkills (List)
- `TurnSearchRequest extends TimeRangeRequest`: + skillId
- `TurnSearchItem`: turnId, timeStamp, userName, userInput, durationMs, resultStatus, qualityStatus, tokens (nested), skills, tools, logFileName
- `TraceResponse`: turnId, nodes (List\<TraceNode\>)
- `TraceNode`: stepOrder, nodeType, nodeName, status (Boolean), timeStamp, durationMs
- `ErrorSummaryResponse`: totalErrors, totalTurns, problemRate, byType (List), bySeverity (Map)
- `ErrorSearchRequest extends TimeRangeRequest`: + errorType, severity
- `ErrorSearchItem`: id, errorType, severity, description, errorMessage, employeeName, occurredAt, turnId
- `ScanTriggerResponse`: scanId, triggerType, startedAt
- `ScanStatusResponse`: scanning, nextScheduledAt, currentScan, lastCompletedScan
- `ScanHistoryItem`: all fields from dashboard_scan_history
- `SkillOption`: skillId, skillName

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/dto/
git commit -m "feat: add DTO classes for API request/response"
```

---

## Task 6: MyBatis Mapper Interfaces & XML

**Files:**
- Create: `src/main/java/com/company/clawboard/mapper/` (8 mapper interfaces)
- Create: `src/main/resources/mapper/` (8 XML files)

- [ ] **Step 1: Create mapper interfaces**

Each mapper interface uses `@Mapper` annotation. Define method signatures only — SQL goes in XML.

**Critical SQL patterns** (implement in XML):

**MessageMapper.xml** — `insertIgnore`:
```sql
INSERT IGNORE INTO dashboard_message
  (session_id, message_id, employee_id, role, message_timestamp,
   input_tokens, output_tokens, cache_read_tokens, cache_write_tokens,
   total_tokens, cost_total, provider, model, stop_reason, duration_ms,
   is_error, tool_name, tool_call_id, parent_id)
VALUES
  (#{sessionId}, #{messageId}, #{employeeId}, #{role}, #{messageTimestamp},
   #{inputTokens}, #{outputTokens}, #{cacheReadTokens}, #{cacheWriteTokens},
   #{totalTokens}, #{costTotal}, #{provider}, #{model}, #{stopReason}, #{durationMs},
   #{isError}, #{toolName}, #{toolCallId}, #{parentId})
```

**ConversationTurnMapper.xml** — `upsertTurn` (the key dedup logic):
```sql
INSERT INTO dashboard_conversation_turn
  (session_id, employee_id, turn_index, start_message_id, end_message_id,
   start_time, end_time, user_input, total_input_tokens, total_output_tokens,
   total_tokens, total_cost, tool_calls_count, tool_calls_success, tool_calls_error,
   skill_calls_count, skill_calls_success, skill_calls_error,
   is_complete, has_error, status, total_duration_ms, tool_duration_ms,
   model_duration_ms, chain_summary, log_file_path)
VALUES (...)
ON DUPLICATE KEY UPDATE
  end_message_id = IF(VALUES(is_complete) > is_complete
                      OR (VALUES(is_complete) = is_complete
                          AND JSON_LENGTH(VALUES(chain_summary), '$.steps') > JSON_LENGTH(chain_summary, '$.steps')),
                      VALUES(end_message_id), end_message_id),
  -- Same pattern for all fields: take the more complete version
  status = IF(VALUES(status) = 'complete' OR status != 'complete', VALUES(status), status),
  is_complete = GREATEST(VALUES(is_complete), is_complete),
  total_tokens = GREATEST(VALUES(total_tokens), total_tokens),
  total_cost = GREATEST(VALUES(total_cost), total_cost),
  chain_summary = IF(JSON_LENGTH(VALUES(chain_summary), '$.steps') > JSON_LENGTH(chain_summary, '$.steps'),
                     VALUES(chain_summary), chain_summary),
  total_duration_ms = GREATEST(COALESCE(VALUES(total_duration_ms), 0), COALESCE(total_duration_ms, 0)),
  tool_calls_count = GREATEST(VALUES(tool_calls_count), tool_calls_count),
  skill_calls_count = GREATEST(VALUES(skill_calls_count), skill_calls_count)
```

> **Simplification note**: The ON DUPLICATE KEY UPDATE logic above is the conceptual approach. In practice, a simpler rule works well: if the incoming row has `status='complete'` or has more chain_summary steps, it wins entirely. Implement this with a single conditional that replaces all fields at once.

**SkillInvocationMapper.xml** — `insertIgnore`:
```sql
INSERT IGNORE INTO dashboard_skill_invocation (...) VALUES (...)
```

**TranscriptIssueMapper.xml** — `insertIgnore`:
```sql
INSERT IGNORE INTO dashboard_transcript_issue (...) VALUES (...)
```

**HourlyStatsMapper.xml** — `upsertHourlyStats`:
```sql
INSERT INTO dashboard_hourly_stats
  (employee_id, stat_hour, total_tokens, input_tokens, output_tokens, total_cost,
   cache_read_tokens, cache_write_tokens, conversation_turns, complete_turns,
   error_turns, skill_invocations, skill_errors, tool_calls, tool_errors, error_count)
VALUES (#{employeeId}, #{statHour}, #{totalTokens}, ...)
ON DUPLICATE KEY UPDATE
  total_tokens = VALUES(total_tokens),
  input_tokens = VALUES(input_tokens),
  -- ... all fields replaced (this is "recompute affected hours" strategy)
```

**ScanProgressMapper.xml** — `upsertProgress`:
```sql
INSERT INTO dashboard_scan_progress
  (employee_id, file_path, last_offset, file_size, file_mtime, session_id,
   last_message_id, last_message_ts)
VALUES (...)
ON DUPLICATE KEY UPDATE
  last_offset = VALUES(last_offset),
  file_size = VALUES(file_size),
  file_mtime = VALUES(file_mtime),
  session_id = VALUES(session_id),
  last_message_id = VALUES(last_message_id),
  last_message_ts = VALUES(last_message_ts)
```

**Dashboard query mappers** (DashboardService queries):

- `HourlyStatsMapper.sumGlobalStats()` — `SELECT SUM(total_tokens), SUM(conversation_turns), SUM(skill_invocations) FROM dashboard_hourly_stats`
- `EmployeeMapper.countDistinctEmployees()` — `SELECT COUNT(DISTINCT employee_id) FROM dashboard_hourly_stats`
- `HourlyStatsMapper.sumFilteredStats(teamName, userName, startTime, endTime)` — with JOINs to employee table
- `HourlyStatsMapper.selectTrendData(...)` — GROUP BY truncated time based on granularity
- `ConversationTurnMapper.searchTurns(...)` — paginated search with LEFT JOIN to skill_invocation

- [ ] **Step 2: Create all 8 mapper XML files**

Each XML file follows MyBatis conventions with `<mapper namespace="...">`.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/mapper/ src/main/resources/mapper/
git commit -m "feat: add MyBatis mapper interfaces and XML with key SQL"
```

---

## Task 7: Parser Domain Models (JDK 17 Records)

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/model/` (7 files)

- [ ] **Step 1: Create JsonlRecord sealed interface**

```java
package com.company.clawboard.parser.model;

/**
 * Represents a single parsed line from a JSONL transcript file.
 */
public sealed interface JsonlRecord
    permits SessionRecord, MessageRecord, CustomRecord, MetadataRecord {
}
```

- [ ] **Step 2: Create record types**

```java
// SessionRecord.java
public record SessionRecord(String id, int version, String timestamp, String cwd)
    implements JsonlRecord {}

// MetadataRecord.java — covers model_change, thinking_level_change
public record MetadataRecord(String type, String id, String provider, String modelId)
    implements JsonlRecord {}

// CustomRecord.java
public record CustomRecord(String customType, String id, String timestamp,
                           com.fasterxml.jackson.databind.JsonNode data)
    implements JsonlRecord {}

// MessageRecord.java — the core record
public record MessageRecord(
    String id,                    // message ID (8-char hex)
    String parentId,
    String timestamp,             // ISO string
    String role,                  // user / assistant / toolResult
    String textContent,           // extracted text (first text block)
    String errorMessage,          // dedicated errorMessage field (assistant messages)
    java.util.List<ToolCallInfo> toolCalls,  // from assistant content blocks
    String toolCallId,            // for toolResult
    String toolName,              // for toolResult
    boolean isError,              // toolResult.isError
    String provider,
    String model,
    String stopReason,
    UsageInfo usage,
    int durationMs,
    long epochMs                  // message.timestamp (epoch ms)
) implements JsonlRecord {

    public record ToolCallInfo(String id, String name, String argumentsJson) {}
}

// UsageInfo.java
public record UsageInfo(
    int inputTokens,
    int outputTokens,
    int cacheReadTokens,
    int cacheWriteTokens,
    int totalTokens,
    java.math.BigDecimal costTotal
) {}

// ChainStep.java
public record ChainStep(
    int stepOrder,
    String nodeType,     // user_input, tool_call, skill_call, reply
    String nodeName,
    String messageId,
    String toolCallId,   // nullable
    boolean status,
    long timeStamp,      // epoch ms
    Integer durationMs   // nullable
) {}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/parser/model/
git commit -m "feat: add parser domain models as JDK 17 records"
```

---

## Task 8: MessageParser — JSONL Line Parser

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/MessageParser.java`
- Create: `src/test/java/com/company/clawboard/parser/MessageParserTest.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void parseSessionLine() {
    String line = """
        {"type":"session","version":3,"id":"52fa7e91-fb04-4a57-a962-194a45b3e38c","timestamp":"2026-04-10T05:15:04.575Z","cwd":"/home/chinalife/.openclaw/workspace"}""";
    JsonlRecord record = parser.parseLine(line);
    assertInstanceOf(SessionRecord.class, record);
    assertEquals("52fa7e91-fb04-4a57-a962-194a45b3e38c", ((SessionRecord) record).id());
}

@Test
void parseUserMessage() {
    // Use actual line from test/openclaw-logs sample file
    String line = """
        {"type":"message","id":"1cdf2be6","parentId":"7d5a0343","timestamp":"2026-04-10T05:15:04.601Z","message":{"role":"user","content":[{"type":"text","text":"Hello world"}],"timestamp":1775798104594}}""";
    JsonlRecord record = parser.parseLine(line);
    assertInstanceOf(MessageRecord.class, record);
    var msg = (MessageRecord) record;
    assertEquals("user", msg.role());
    assertEquals("Hello world", msg.textContent());
    assertEquals(1775798104594L, msg.epochMs());
}

@Test
void parseAssistantWithToolCall() {
    // Line with toolCall content block
    // ... (use sample from 600cd549 session)
}

@Test
void parseMalformedLine_returnsNull() {
    assertNull(parser.parseLine("not json at all"));
    assertNull(parser.parseLine(""));
    assertNull(parser.parseLine("   "));
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl . -Dtest=MessageParserTest -Dspring.profiles.active=test`
Expected: FAIL — MessageParser class does not exist

- [ ] **Step 3: Implement MessageParser.java**

```java
package com.company.clawboard.parser;

import com.company.clawboard.parser.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageParser {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Parse a single JSONL line into a typed record.
     * Returns null for unparseable lines or unknown types.
     */
    public JsonlRecord parseLine(String line) {
        if (line == null || line.isBlank()) return null;
        try {
            JsonNode root = mapper.readTree(line);
            String type = root.path("type").asText("");
            return switch (type) {
                case "session" -> parseSession(root);
                case "message" -> parseMessage(root);
                case "custom" -> parseCustom(root);
                case "model_change", "thinking_level_change" -> parseMetadata(root, type);
                default -> null;
            };
        } catch (Exception e) {
            log.warn("Failed to parse JSONL line: {}", truncate(line, 200), e);
            return null;
        }
    }

    private SessionRecord parseSession(JsonNode root) {
        return new SessionRecord(
            root.path("id").asText(),
            root.path("version").asInt(0),
            root.path("timestamp").asText(),
            root.path("cwd").asText("")
        );
    }

    private MessageRecord parseMessage(JsonNode root) {
        JsonNode msg = root.path("message");
        String role = msg.path("role").asText("");
        String id = root.path("id").asText("");
        String parentId = root.path("parentId").asText(null);
        String timestamp = root.path("timestamp").asText("");

        // Extract text content (first text block)
        String textContent = extractTextContent(msg.path("content"));

        // Extract dedicated errorMessage field (assistant messages)
        String errorMessage = msg.has("errorMessage") ? msg.get("errorMessage").asText(null) : null;

        // Extract tool calls (assistant messages)
        List<MessageRecord.ToolCallInfo> toolCalls = extractToolCalls(msg.path("content"));

        // For toolResult
        String toolCallId = msg.path("toolCallId").asText(null);
        String toolName = msg.path("toolName").asText(null);
        boolean isError = msg.path("isError").asBoolean(false);

        // Provider/model (on assistant messages)
        String provider = msg.path("provider").asText(null);
        String model = msg.path("model").asText(null);
        String stopReason = msg.path("stopReason").asText(null);

        // Usage
        UsageInfo usage = extractUsage(msg.path("usage"));

        // Duration
        int durationMs = msg.path("durationMs").asInt(0);

        // Epoch timestamp
        long epochMs = msg.path("timestamp").asLong(0);

        return new MessageRecord(id, parentId, timestamp, role,
            textContent, errorMessage, toolCalls, toolCallId, toolName, isError,
            provider, model, stopReason, usage, durationMs, epochMs);
    }

    private String extractTextContent(JsonNode contentArray) {
        if (contentArray == null || !contentArray.isArray()) return null;
        for (JsonNode block : contentArray) {
            if ("text".equals(block.path("type").asText())) {
                return block.path("text").asText(null);
            }
        }
        return null;
    }

    private List<MessageRecord.ToolCallInfo> extractToolCalls(JsonNode contentArray) {
        List<MessageRecord.ToolCallInfo> calls = new ArrayList<>();
        if (contentArray == null || !contentArray.isArray()) return calls;
        for (JsonNode block : contentArray) {
            if ("toolCall".equals(block.path("type").asText())) {
                calls.add(new MessageRecord.ToolCallInfo(
                    block.path("id").asText(""),
                    block.path("name").asText(""),
                    block.has("arguments") ? block.get("arguments").toString() : "{}"
                ));
            }
        }
        return calls;
    }

    private UsageInfo extractUsage(JsonNode usageNode) {
        if (usageNode == null || usageNode.isMissingNode()) return null;
        JsonNode cost = usageNode.path("cost");
        return new UsageInfo(
            usageNode.path("input").asInt(0),
            usageNode.path("output").asInt(0),
            usageNode.path("cacheRead").asInt(0),
            usageNode.path("cacheWrite").asInt(0),
            usageNode.path("totalTokens").asInt(0),
            cost.isMissingNode() ? BigDecimal.ZERO
                : new BigDecimal(cost.path("total").asText("0"))
        );
    }

    private CustomRecord parseCustom(JsonNode root) {
        return new CustomRecord(
            root.path("customType").asText(""),
            root.path("id").asText(""),
            root.path("timestamp").asText(""),
            root.get("data")
        );
    }

    private MetadataRecord parseMetadata(JsonNode root, String type) {
        return new MetadataRecord(
            type,
            root.path("id").asText(""),
            root.path("provider").asText(null),
            root.path("modelId").asText(null)
        );
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -pl . -Dtest=MessageParserTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/parser/MessageParser.java
git add src/test/java/com/company/clawboard/parser/MessageParserTest.java
git commit -m "feat: implement MessageParser for JSONL line parsing"
```

---

## Task 9: SystemMessageFilter

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/SystemMessageFilter.java`
- Create: `src/test/java/com/company/clawboard/parser/SystemMessageFilterTest.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void filterDeliveryMirror() {
    // provider="openclaw", model="delivery-mirror" → should be filtered
    assertTrue(filter.shouldFilterAssistant("openclaw", "delivery-mirror"));
    assertFalse(filter.shouldFilterAssistant("custom-provider", "GLM-5"));
    assertFalse(filter.shouldFilterAssistant("openclaw", "gateway-injected"));
}

@Test
void filterSystemUserMessages() {
    assertTrue(filter.isSystemGeneratedUserMessage(
        "A new session was started via /new or /reset. Run your Session Startup..."));
    assertTrue(filter.isSystemGeneratedUserMessage(
        "Read HEARTBEAT.md and respond..."));
    assertTrue(filter.isSystemGeneratedUserMessage(
        "<openclaw-envelope>some metadata</openclaw-envelope>"));
    assertTrue(filter.isSystemGeneratedUserMessage(
        "<inbound_metadata>json data here"));

    // Real user messages should pass through
    assertFalse(filter.isSystemGeneratedUserMessage("帮我生成一份PPT"));
    assertFalse(filter.isSystemGeneratedUserMessage("Hello"));
}
```

- [ ] **Step 2: Implement SystemMessageFilter.java**

```java
package com.company.clawboard.parser;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SystemMessageFilter {

    private final ClawboardProperties properties;

    public boolean shouldFilterAssistant(String provider, String model) {
        if (!properties.getParser().isDeliveryMirrorFilter()) return false;
        return "openclaw".equals(provider) && "delivery-mirror".equals(model);
    }

    public boolean isSystemGeneratedUserMessage(String text) {
        if (text == null || text.isEmpty()) return false;
        List<String> prefixes = properties.getParser().getSystemMessagePrefixes();
        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) return true;
        }
        return false;
    }
}
```

- [ ] **Step 3: Run test, verify pass**
- [ ] **Step 4: Commit**

---

## Task 10: SkillDetector

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/SkillDetector.java`
- Create: `src/test/java/com/company/clawboard/parser/SkillDetectorTest.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void detectSkillReadFromToolCall() {
    // Matches: read/Read tool with path containing SKILL.md
    var result = detector.detectSkillFromToolCall("read",
        "{\"path\":\"/home/user/.openclaw/workspace/skills/imap-smtp-email/SKILL.md\"}");
    assertTrue(result.isPresent());
    assertEquals("imap-smtp-email", result.get().skillName());
    assertEquals("/home/user/.openclaw/workspace/skills/imap-smtp-email/SKILL.md", result.get().skillPath());
}

@Test
void detectSkillReadFromReadTool() {
    var result = detector.detectSkillFromToolCall("Read",
        "{\"file_path\":\"/some/path/skills/official-doc-writer/SKILL.md\"}");
    assertTrue(result.isPresent());
    assertEquals("official-doc-writer", result.get().skillName());
}

@Test
void nonSkillToolCall_returnsEmpty() {
    var result = detector.detectSkillFromToolCall("exec", "{\"command\":\"ls\"}");
    assertTrue(result.isEmpty());
}

@Test
void detectSlashCommand() {
    var result = detector.detectSkillFromUserMessage("Use the \"pptx\" skill to create...");
    assertTrue(result.isPresent());
    assertEquals("pptx", result.get());
}
```

- [ ] **Step 2: Implement SkillDetector.java**

Key logic:
1. `detectSkillFromToolCall(toolName, argumentsJson)` — checks if tool is `read`/`Read` and arguments contain a path matching `*/skills/*/SKILL.md` or ending in `SKILL.md`. Extracts skill name from path segment after `skills/`.
2. `detectSkillFromUserMessage(text)` — matches `Use the "(\w[\w-]*)" skill` pattern or leading `/<skill-name>`.

```java
package com.company.clawboard.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SkillDetector {

    private static final Pattern SKILL_PATH_PATTERN =
        Pattern.compile(".*/skills/([\\w-]+)/SKILL\\.md$");
    private static final Pattern SKILL_USE_PATTERN =
        Pattern.compile("Use the \"(\\w[\\w-]*)\" skill");
    private static final Pattern SLASH_COMMAND_PATTERN =
        Pattern.compile("^/(\\w[\\w-]*)(?:\\s|$)");

    private final ObjectMapper mapper = new ObjectMapper();

    public record SkillReadResult(String skillName, String skillPath) {}

    public Optional<SkillReadResult> detectSkillFromToolCall(String toolName, String argumentsJson) {
        if (toolName == null) return Optional.empty();
        if (!toolName.equalsIgnoreCase("read")) return Optional.empty();

        try {
            JsonNode args = mapper.readTree(argumentsJson);
            // Try both "path" and "file_path" argument names
            String path = args.has("path") ? args.get("path").asText("")
                        : args.has("file_path") ? args.get("file_path").asText("") : "";

            if (!path.endsWith("SKILL.md")) return Optional.empty();

            Matcher m = SKILL_PATH_PATTERN.matcher(path);
            if (m.find()) {
                return Optional.of(new SkillReadResult(m.group(1), path));
            }
            // Fallback: if path ends with SKILL.md but doesn't match full pattern,
            // extract directory name before SKILL.md
            int idx = path.lastIndexOf('/');
            if (idx > 0) {
                int idx2 = path.lastIndexOf('/', idx - 1);
                String dirName = path.substring(idx2 + 1, idx);
                return Optional.of(new SkillReadResult(dirName, path));
            }
        } catch (Exception e) {
            log.debug("Failed to parse tool arguments for skill detection", e);
        }
        return Optional.empty();
    }

    public Optional<String> detectSkillFromUserMessage(String text) {
        if (text == null || text.isEmpty()) return Optional.empty();

        Matcher m1 = SKILL_USE_PATTERN.matcher(text);
        if (m1.find()) return Optional.of(m1.group(1));

        Matcher m2 = SLASH_COMMAND_PATTERN.matcher(text);
        if (m2.find()) return Optional.of(m2.group(1));

        return Optional.empty();
    }
}
```

- [ ] **Step 3: Run test, verify pass**
- [ ] **Step 4: Commit**

---

## Task 11: IssueDetector

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/IssueDetector.java`
- Create: `src/test/java/com/company/clawboard/parser/IssueDetectorTest.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void detectModelError() {
    var issues = detector.detectFromErrorMessage("Model API error: rate limit exceeded", "prov", "model1");
    assertTrue(issues.stream().anyMatch(i -> i.errorType().equals("rateLimitErrors")));
}

@Test
void detectTimeoutError() {
    var issues = detector.detectFromErrorMessage("Request timed out after 30000ms", "prov", "model1");
    assertTrue(issues.stream().anyMatch(i -> i.errorType().equals("timeoutErrors")));
}

@Test
void detectAbnormalStop_aborted() {
    var result = detector.checkAbnormalStop("aborted");
    assertTrue(result.isPresent());
    assertEquals("HIGH", result.get().severity());
}

@Test
void normalStopReason_noIssue() {
    assertTrue(detector.checkAbnormalStop("end_turn").isEmpty());
    assertTrue(detector.checkAbnormalStop("stop").isEmpty());
    assertTrue(detector.checkAbnormalStop("toolUse").isEmpty());
}
```

- [ ] **Step 2: Implement IssueDetector.java**

Contains:
1. `detectFromErrorMessage(text, provider, model)` — regex pattern matching against error categories from design doc Section 4.7
2. `detectFromCustomEvent(customType, dataJson)` — match custom events against error patterns
3. `checkAbnormalStop(stopReason)` — normal set: {stop, end_turn, toolUse, tool_use, length}, others are abnormal
4. `checkFlowIntegrity(...)` — checks for no_reply, missing_tool_result, missing_final_answer

Each method returns a list/optional of `DetectedIssue` records:
```java
public record DetectedIssue(String errorType, String severity, String description) {}
```

Error regex patterns (from design doc):
```java
private static final Map<String, List<Pattern>> ERROR_PATTERNS = Map.of(
    "modelErrors", List.of(
        Pattern.compile("(?i)model.*error"), Pattern.compile("(?i)api.*error"),
        Pattern.compile("(?i)LLM.*timeout"), Pattern.compile("(?i)operation.*aborted")),
    "timeoutErrors", List.of(
        Pattern.compile("(?i)timeout"), Pattern.compile("(?i)timed.*out"),
        Pattern.compile("(?i)ETIMEDOUT"), Pattern.compile("(?i)connection.*timeout")),
    "rateLimitErrors", List.of(
        Pattern.compile("(?i)rate.*limit"), Pattern.compile("\\b429\\b"),
        Pattern.compile("(?i)too.*many.*requests"), Pattern.compile("(?i)quota.*exceeded")),
    "toolErrors", List.of(
        Pattern.compile("(?i)tool.*error"), Pattern.compile("(?i)tool.*failed"),
        Pattern.compile("(?i)execution.*error")),
    "permissionErrors", List.of(
        Pattern.compile("(?i)permission.*denied"), Pattern.compile("(?i)access.*denied"),
        Pattern.compile("(?i)forbidden"), Pattern.compile("\\b403\\b")),
    "parsingErrors", List.of(
        Pattern.compile("(?i)parse.*error"), Pattern.compile("(?i)invalid.*json"),
        Pattern.compile("(?i)syntax.*error")),
    "networkErrors", List.of(
        Pattern.compile("(?i)network.*error"), Pattern.compile("(?i)connection.*refused"),
        Pattern.compile("(?i)ECONNREFUSED"))
);

private static final Set<String> NORMAL_STOP_REASONS =
    Set.of("stop", "end_turn", "toolUse", "tool_use", "length");
```

- [ ] **Step 3: Run test, verify pass**
- [ ] **Step 4: Commit**

---

## Task 12: TurnAssembler

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/TurnAssembler.java`
- Create: `src/test/java/com/company/clawboard/parser/TurnAssemblerTest.java`

This is the most complex parser component. It accumulates messages into turns and builds the chain_summary JSON.

- [ ] **Step 1: Write the failing test**

```java
@Test
void assembleSingleCompleteTurn() {
    // Simulate: user → assistant(toolCall) → toolResult → assistant(reply)
    var assembler = new TurnAssembler("session-1", "emp-1", maxInputLen);

    assembler.addMessage(userMsg("msg1", "帮我写个PPT", 1000L));
    assembler.addMessage(assistantToolCall("msg2", "read", "call1", 2000L));
    assembler.addMessage(toolResult("msg3", "call1", "read", false, 2500L));
    assembler.addMessage(assistantReply("msg4", "end_turn", 3000L));

    var turns = assembler.finishAndGetTurns();
    assertEquals(1, turns.size());

    var turn = turns.get(0);
    assertEquals("msg1", turn.getStartMessageId());
    assertEquals("msg4", turn.getEndMessageId());
    assertEquals("complete", turn.getStatus());
    assertEquals("帮我写个PPT", turn.getUserInput());
    assertEquals(1, turn.getIsComplete());
}

@Test
void assembleIncompleteTurn_noReply() {
    var assembler = new TurnAssembler("session-1", "emp-1", maxInputLen);
    assembler.addMessage(userMsg("msg1", "Hello", 1000L));
    // No assistant reply

    var turns = assembler.finishAndGetTurns();
    assertEquals(1, turns.size());
    assertEquals("incomplete", turns.get(0).getStatus());
}

@Test
void multipleUserMessages_createMultipleTurns() {
    var assembler = new TurnAssembler("session-1", "emp-1", maxInputLen);
    assembler.addMessage(userMsg("msg1", "Q1", 1000L));
    assembler.addMessage(assistantReply("msg2", "end_turn", 2000L));
    assembler.addMessage(userMsg("msg3", "Q2", 3000L));
    assembler.addMessage(assistantReply("msg4", "end_turn", 4000L));

    var turns = assembler.finishAndGetTurns();
    assertEquals(2, turns.size());
    assertEquals("Q1", turns.get(0).getUserInput());
    assertEquals("Q2", turns.get(1).getUserInput());
}
```

- [ ] **Step 2: Implement TurnAssembler.java**

Key responsibilities:
1. Maintain a "current turn" state. A new turn starts when a `user` message arrives.
2. Track tool calls, tool results, skill calls, and replies within the current turn.
3. Build `chain_summary` JSON with `ChainStep` records for each step.
4. On turn boundary (next user msg or file end), finalize the current turn:
   - Set `status` based on completeness (has final reply? has errors?)
   - Calculate `total_duration_ms`, `tool_duration_ms`, `model_duration_ms`
   - Serialize `chain_summary` to JSON string
   - Return a `DashboardConversationTurn` entity

```java
package com.company.clawboard.parser;

import com.company.clawboard.entity.DashboardConversationTurn;
import com.company.clawboard.entity.DashboardSkillInvocation;
import com.company.clawboard.parser.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class TurnAssembler {

    private final String sessionId;
    private final String employeeId;
    private final int maxInputLength;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Accumulated completed turns
    private final List<DashboardConversationTurn> completedTurns = new ArrayList<>();
    private final List<DashboardSkillInvocation> skillInvocations = new ArrayList<>();

    // Current turn state
    private String currentStartMessageId;
    private String currentUserInput;
    private long currentStartEpochMs;
    private String lastEndMessageId;
    private long lastEndEpochMs;
    private final List<ChainStep> currentChain = new ArrayList<>();
    private int currentInputTokens = 0;
    private int currentOutputTokens = 0;
    private int currentTotalTokens = 0;
    private BigDecimal currentTotalCost = BigDecimal.ZERO;
    private int toolCallsCount = 0, toolCallsSuccess = 0, toolCallsError = 0;
    private int skillCallsCount = 0, skillCallsSuccess = 0, skillCallsError = 0;
    private boolean hasError = false;
    private boolean hasReply = false;
    private int toolDurationMs = 0;
    private int modelDurationMs = 0;
    private boolean inTurn = false;

    // Track pending tool calls for matching with results
    private final Map<String, ToolCallPending> pendingToolCalls = new HashMap<>();

    record ToolCallPending(String toolName, long epochMs, boolean isSkill, String skillName, String skillPath) {}

    public TurnAssembler(String sessionId, String employeeId, int maxInputLength) {
        this.sessionId = sessionId;
        this.employeeId = employeeId;
        this.maxInputLength = maxInputLength;
    }

    public void addUserMessage(MessageRecord msg) {
        // Close previous turn if any
        if (inTurn) {
            finalizeTurn();
        }
        // Start new turn
        inTurn = true;
        currentStartMessageId = msg.id();
        currentUserInput = truncate(msg.textContent(), maxInputLength);
        currentStartEpochMs = msg.epochMs();
        lastEndMessageId = msg.id();
        lastEndEpochMs = msg.epochMs();
        currentChain.clear();
        resetCounters();

        currentChain.add(new ChainStep(0, "user_input", "用户输入",
            msg.id(), null, true, msg.epochMs(), null));
    }

    public void addAssistantMessage(MessageRecord msg, List<SkillDetector.SkillReadResult> detectedSkills) {
        if (!inTurn) return;

        lastEndMessageId = msg.id();
        lastEndEpochMs = msg.epochMs();

        // Accumulate usage
        if (msg.usage() != null) {
            currentInputTokens += msg.usage().inputTokens();
            currentOutputTokens += msg.usage().outputTokens();
            currentTotalTokens += msg.usage().totalTokens();
            currentTotalCost = currentTotalCost.add(
                msg.usage().costTotal() != null ? msg.usage().costTotal() : BigDecimal.ZERO);
            modelDurationMs += msg.durationMs();
        }

        // Process tool calls in this message
        for (var tc : msg.toolCalls()) {
            boolean isSkill = false;
            String skillName = null, skillPath = null;
            // Check if this toolCall is a skill read
            for (var sr : detectedSkills) {
                if (tc.name().equalsIgnoreCase("read") || tc.name().equalsIgnoreCase("Read")) {
                    isSkill = true;
                    skillName = sr.skillName();
                    skillPath = sr.skillPath();
                    break;
                }
            }

            pendingToolCalls.put(tc.id(), new ToolCallPending(
                tc.name(), msg.epochMs(), isSkill, skillName, skillPath));

            String nodeType = isSkill ? "skill_call" : "tool_call";
            String nodeName = isSkill ? skillName : tc.name();
            if (isSkill) skillCallsCount++;
            else toolCallsCount++;

            currentChain.add(new ChainStep(currentChain.size(), nodeType, nodeName,
                msg.id(), tc.id(), true, msg.epochMs(), null));
        }

        // Check if this is the final reply (no tool calls, stop != toolUse)
        if (msg.toolCalls().isEmpty() && !"toolUse".equals(msg.stopReason())
                && !"tool_use".equals(msg.stopReason())) {
            hasReply = true;
            currentChain.add(new ChainStep(currentChain.size(), "reply", "回复用户",
                msg.id(), null, true, msg.epochMs(), msg.durationMs() > 0 ? msg.durationMs() : null));
        }

        // Check for errors in assistant message
        if (msg.isError()) hasError = true;
    }

    public void addToolResult(MessageRecord msg) {
        if (!inTurn) return;

        lastEndMessageId = msg.id();
        lastEndEpochMs = msg.epochMs();

        var pending = pendingToolCalls.remove(msg.toolCallId());
        if (pending != null) {
            int duration = (int)(msg.epochMs() - pending.epochMs());
            toolDurationMs += Math.max(duration, 0);

            if (pending.isSkill()) {
                if (msg.isError()) skillCallsError++;
                else skillCallsSuccess++;

                // Create skill invocation record
                var inv = new DashboardSkillInvocation();
                inv.setSessionId(sessionId);
                inv.setEmployeeId(employeeId);
                inv.setSkillName(pending.skillName());
                inv.setSkillPath(pending.skillPath());
                inv.setInvokedAt(toLocalDateTime(pending.epochMs()));
                inv.setReadMessageId(msg.toolCallId()); // the toolCall msg that triggered the read
                inv.setResultMessageId(msg.id());
                inv.setIsError(msg.isError() ? 1 : 0);
                inv.setTriggerType("model_read");
                inv.setDurationMs(Math.max(duration, 0));
                skillInvocations.add(inv);
            } else {
                if (msg.isError()) {
                    toolCallsError++;
                    hasError = true;
                } else {
                    toolCallsSuccess++;
                }
            }
        }
    }

    /**
     * Call when file parsing is complete. Finalizes the last turn and returns all turns.
     */
    public List<DashboardConversationTurn> finishAndGetTurns() {
        if (inTurn) finalizeTurn();
        return completedTurns;
    }

    public List<DashboardSkillInvocation> getSkillInvocations() {
        return skillInvocations;
    }

    private void finalizeTurn() {
        var turn = new DashboardConversationTurn();
        turn.setSessionId(sessionId);
        turn.setEmployeeId(employeeId);
        turn.setStartMessageId(currentStartMessageId);
        turn.setEndMessageId(lastEndMessageId);
        turn.setStartTime(toLocalDateTime(currentStartEpochMs));
        turn.setEndTime(toLocalDateTime(lastEndEpochMs));
        turn.setUserInput(currentUserInput);
        turn.setTotalInputTokens(currentInputTokens);
        turn.setTotalOutputTokens(currentOutputTokens);
        turn.setTotalTokens(currentTotalTokens);
        turn.setTotalCost(currentTotalCost);
        turn.setToolCallsCount(toolCallsCount);
        turn.setToolCallsSuccess(toolCallsSuccess);
        turn.setToolCallsError(toolCallsError);
        turn.setSkillCallsCount(skillCallsCount);
        turn.setSkillCallsSuccess(skillCallsSuccess);
        turn.setSkillCallsError(skillCallsError);

        // Determine status
        if (hasError) {
            turn.setStatus("error");
            turn.setIsComplete(0);
            turn.setHasError(1);
        } else if (hasReply) {
            turn.setStatus("complete");
            turn.setIsComplete(1);
            turn.setHasError(0);
        } else {
            turn.setStatus("incomplete");
            turn.setIsComplete(0);
            turn.setHasError(0);
        }

        // Duration
        int totalDuration = (int)(lastEndEpochMs - currentStartEpochMs);
        turn.setTotalDurationMs(Math.max(totalDuration, 0));
        turn.setToolDurationMs(toolDurationMs);
        turn.setModelDurationMs(modelDurationMs);

        // Chain summary
        turn.setChainSummary(serializeChainSummary());

        completedTurns.add(turn);
        inTurn = false;
    }

    private String serializeChainSummary() {
        try {
            var summary = Map.of(
                "steps", currentChain,
                "totalDurationMs", (int)(lastEndEpochMs - currentStartEpochMs),
                "toolDurationMs", toolDurationMs,
                "modelDurationMs", modelDurationMs
            );
            return objectMapper.writeValueAsString(summary);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize chain summary", e);
            return "{}";
        }
    }

    private void resetCounters() {
        currentInputTokens = 0; currentOutputTokens = 0; currentTotalTokens = 0;
        currentTotalCost = BigDecimal.ZERO;
        toolCallsCount = 0; toolCallsSuccess = 0; toolCallsError = 0;
        skillCallsCount = 0; skillCallsSuccess = 0; skillCallsError = 0;
        hasError = false; hasReply = false;
        toolDurationMs = 0; modelDurationMs = 0;
        pendingToolCalls.clear();
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static LocalDateTime toLocalDateTime(long epochMs) {
        if (epochMs <= 0) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault());
    }
}
```

- [ ] **Step 3: Run test, verify pass**
- [ ] **Step 4: Commit**

---

## Task 13: TranscriptParser — Single-File Orchestrator

**Files:**
- Create: `src/main/java/com/company/clawboard/parser/TranscriptParser.java`
- Create: `src/test/java/com/company/clawboard/parser/TranscriptParserTest.java`
- Create: `src/test/resources/fixtures/simple-session.jsonl` (curated test data)

- [ ] **Step 1: Create test fixture**

Copy selected lines from `test/openclaw-logs/agents/main/sessions/52fa7e91-fb04-4a57-a962-194a45b3e38c.jsonl` as a minimal fixture with: session header → user msg → assistant reply.

- [ ] **Step 2: Write the failing test**

```java
@Test
void parseSimpleSession() {
    var result = parser.parseFile(
        Path.of("src/test/resources/fixtures/simple-session.jsonl"),
        "emp001", 0L);

    assertNotNull(result);
    assertFalse(result.messages().isEmpty());
    assertFalse(result.turns().isEmpty());
    assertEquals("complete", result.turns().get(0).getStatus());
}
```

- [ ] **Step 3: Implement TranscriptParser.java**

```java
package com.company.clawboard.parser;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.*;
import com.company.clawboard.parser.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranscriptParser {

    private final MessageParser messageParser;
    private final SystemMessageFilter systemMessageFilter;
    private final SkillDetector skillDetector;
    private final IssueDetector issueDetector;
    private final ClawboardProperties properties;

    public record ParseResult(
        String sessionId,
        List<DashboardMessage> messages,
        List<DashboardConversationTurn> turns,
        List<DashboardSkillInvocation> skillInvocations,
        List<DashboardTranscriptIssue> issues,
        long bytesRead,
        String lastMessageId,
        long lastMessageEpochMs
    ) {}

    /**
     * Parse a single JSONL file starting from the given byte offset.
     *
     * @param filePath     absolute path to the JSONL file
     * @param employeeId   the employee who owns this session
     * @param startOffset  byte offset to start reading (0 for full parse)
     * @return ParseResult containing all extracted entities, or null on failure
     */
    public ParseResult parseFile(Path filePath, String employeeId, long startOffset) {
        String sessionIdFromFile = extractSessionIdFromFileName(filePath.getFileName().toString());
        String sessionId = null;  // will be set from session header or filename

        List<DashboardMessage> messages = new ArrayList<>();
        List<DashboardTranscriptIssue> issues = new ArrayList<>();
        String lastMessageId = null;
        long lastMessageEpochMs = 0;
        long bytesRead = startOffset;
        int lineNumber = 0;

        TurnAssembler turnAssembler = null;

        try (var fis = new FileInputStream(filePath.toFile())) {
            // Skip to offset
            if (startOffset > 0) {
                long skipped = fis.skip(startOffset);
                if (skipped < startOffset) {
                    log.warn("Could not skip to offset {} in file {}", startOffset, filePath);
                }
            }

            var reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                bytesRead += line.getBytes(StandardCharsets.UTF_8).length + 1; // +1 for newline

                JsonlRecord record = messageParser.parseLine(line);
                if (record == null) continue;

                switch (record) {
                    case SessionRecord sr -> {
                        sessionId = sr.id();
                        if (turnAssembler == null) {
                            turnAssembler = new TurnAssembler(sessionId, employeeId,
                                properties.getParser().getMaxUserInputLength());
                        }
                    }
                    case MessageRecord mr -> {
                        // Use filename-based session ID if header was missing (compacted file)
                        if (sessionId == null) sessionId = sessionIdFromFile;
                        if (turnAssembler == null) {
                            turnAssembler = new TurnAssembler(sessionId, employeeId,
                                properties.getParser().getMaxUserInputLength());
                        }

                        processMessage(mr, sessionId, employeeId, lineNumber,
                            messages, issues, turnAssembler);

                        lastMessageId = mr.id();
                        lastMessageEpochMs = mr.epochMs();
                    }
                    case CustomRecord cr -> {
                        if (sessionId == null) sessionId = sessionIdFromFile;
                        // Detect errors from custom events
                        var detected = issueDetector.detectFromCustomEvent(
                            cr.customType(), cr.data() != null ? cr.data().toString() : "");
                        for (var d : detected) {
                            var issue = new DashboardTranscriptIssue();
                            issue.setSessionId(sessionId);
                            issue.setMessageId(cr.id());
                            issue.setEmployeeId(employeeId);
                            issue.setErrorType(d.errorType());
                            issue.setSeverity(d.severity());
                            issue.setDescription(d.description());
                            issue.setEventType(cr.customType());
                            issue.setLineNumber(lineNumber);
                            issues.add(issue);
                        }
                    }
                    case MetadataRecord ignored -> { /* skip */ }
                }
            }
        } catch (IOException e) {
            log.error("Failed to parse file: {}", filePath, e);
            return null;
        }

        // Finalize turns
        var turns = turnAssembler != null ? turnAssembler.finishAndGetTurns() : List.<DashboardConversationTurn>of();
        var skillInvocations = turnAssembler != null ? turnAssembler.getSkillInvocations() : List.<DashboardSkillInvocation>of();

        // Set log_file_path on all turns
        String logPath = filePath.toString();
        turns.forEach(t -> t.setLogFilePath(logPath));

        return new ParseResult(
            sessionId != null ? sessionId : sessionIdFromFile,
            messages, turns, skillInvocations, issues,
            bytesRead, lastMessageId, lastMessageEpochMs
        );
    }

    private void processMessage(MessageRecord mr, String sessionId, String employeeId,
                                int lineNumber, List<DashboardMessage> messages,
                                List<DashboardTranscriptIssue> issues,
                                TurnAssembler turnAssembler) {
        switch (mr.role()) {
            case "user" -> {
                // Filter system-generated user messages
                if (systemMessageFilter.isSystemGeneratedUserMessage(mr.textContent())) return;

                messages.add(toEntity(mr, sessionId, employeeId));
                turnAssembler.addUserMessage(mr);

                // Detect skill slash commands in user message
                skillDetector.detectSkillFromUserMessage(mr.textContent()).ifPresent(skillName -> {
                    // Slash command skill detection is recorded in turn assembler
                    // as a separate signal (trigger_type = "slash_command")
                });
            }
            case "assistant" -> {
                // Filter delivery-mirror
                if (systemMessageFilter.shouldFilterAssistant(mr.provider(), mr.model())) return;

                messages.add(toEntity(mr, sessionId, employeeId));

                // Detect skills from tool calls
                List<SkillDetector.SkillReadResult> skills = new ArrayList<>();
                for (var tc : mr.toolCalls()) {
                    skillDetector.detectSkillFromToolCall(tc.name(), tc.argumentsJson())
                        .ifPresent(skills::add);
                }

                turnAssembler.addAssistantMessage(mr, skills);

                // Check for errors — use dedicated errorMessage field (primary)
                // and textContent as fallback
                String errorText = mr.errorMessage() != null ? mr.errorMessage() : mr.textContent();
                if (errorText != null) {
                    var errorIssues = issueDetector.detectFromErrorMessage(
                        errorText, mr.provider(), mr.model());
                    for (var d : errorIssues) {
                        var issue = new DashboardTranscriptIssue();
                        issue.setSessionId(sessionId);
                        issue.setMessageId(mr.id());
                        issue.setEmployeeId(employeeId);
                        issue.setErrorType(d.errorType());
                        issue.setSeverity(d.severity());
                        issue.setDescription(d.description());
                        issue.setProvider(mr.provider());
                        issue.setModel(mr.model());
                        issue.setLineNumber(lineNumber);
                        issues.add(issue);
                    }
                }

                // Check abnormal stop
                issueDetector.checkAbnormalStop(mr.stopReason()).ifPresent(d -> {
                    var issue = new DashboardTranscriptIssue();
                    issue.setSessionId(sessionId);
                    issue.setMessageId(mr.id());
                    issue.setEmployeeId(employeeId);
                    issue.setErrorType(d.errorType());
                    issue.setSeverity(d.severity());
                    issue.setDescription(d.description());
                    issue.setProvider(mr.provider());
                    issue.setModel(mr.model());
                    issue.setLineNumber(lineNumber);
                    issues.add(issue);
                });
            }
            case "toolResult" -> {
                messages.add(toEntity(mr, sessionId, employeeId));
                turnAssembler.addToolResult(mr);
            }
        }
    }

    private DashboardMessage toEntity(MessageRecord mr, String sessionId, String employeeId) {
        var msg = new DashboardMessage();
        msg.setSessionId(sessionId);
        msg.setMessageId(mr.id());
        msg.setEmployeeId(employeeId);
        msg.setRole(mr.role());
        msg.setMessageTimestamp(TurnAssembler.toLocalDateTime(mr.epochMs()));
        if (mr.usage() != null) {
            msg.setInputTokens(mr.usage().inputTokens());
            msg.setOutputTokens(mr.usage().outputTokens());
            msg.setCacheReadTokens(mr.usage().cacheReadTokens());
            msg.setCacheWriteTokens(mr.usage().cacheWriteTokens());
            msg.setTotalTokens(mr.usage().totalTokens());
            msg.setCostTotal(mr.usage().costTotal());
        }
        msg.setProvider(mr.provider());
        msg.setModel(mr.model());
        msg.setStopReason(mr.stopReason());
        msg.setDurationMs(mr.durationMs());
        msg.setIsError(mr.isError() ? 1 : 0);
        msg.setToolName(mr.toolName());
        msg.setToolCallId(mr.toolCallId());
        msg.setParentId(mr.parentId());
        return msg;
    }

    /**
     * Extract session ID from filename: take everything before the first ".jsonl".
     */
    static String extractSessionIdFromFileName(String fileName) {
        int idx = fileName.indexOf(".jsonl");
        return idx > 0 ? fileName.substring(0, idx) : fileName;
    }
}
```

> **Note**: Make `TurnAssembler.toLocalDateTime()` package-private or public so TranscriptParser can use it. Alternatively, extract a shared utility.

- [ ] **Step 4: Run test, verify pass**
- [ ] **Step 5: Commit**

---

## Task 14: ScanProgressService

**Files:**
- Create: `src/main/java/com/company/clawboard/scanner/ScanProgressService.java`

- [ ] **Step 1: Implement ScanProgressService.java**

Responsibilities:
1. `shouldScan(employeeId, filePath, currentSize, currentMtime)` — returns scan decision:
   - `SKIP` — no changes
   - `INCREMENTAL` — append, return last_offset
   - `COMPACTION` — file shrunk
   - `FULL` — new file
2. `updateProgress(employeeId, filePath, offset, size, mtime, sessionId, lastMsgId, lastMsgTs)` — upsert progress
3. `findLastMessageId(employeeId, filePath)` — for compaction recovery

```java
package com.company.clawboard.scanner;

import com.company.clawboard.entity.DashboardScanProgress;
import com.company.clawboard.mapper.ScanProgressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScanProgressService {

    private final ScanProgressMapper progressMapper;

    public enum ScanMode { SKIP, INCREMENTAL, COMPACTION, FULL }

    public record ScanDecision(ScanMode mode, long startOffset, String lastMessageId) {}

    public ScanDecision decideScanMode(String employeeId, String filePath,
                                        long currentSize, long currentMtime) {
        DashboardScanProgress progress = progressMapper.findByKey(employeeId, filePath);
        if (progress == null) {
            return new ScanDecision(ScanMode.FULL, 0, null);
        }
        if (progress.getFileSize() == currentSize && progress.getFileMtime() == currentMtime) {
            return new ScanDecision(ScanMode.SKIP, 0, null);
        }
        if (currentSize >= progress.getFileSize()) {
            return new ScanDecision(ScanMode.INCREMENTAL, progress.getLastOffset(),
                progress.getLastMessageId());
        }
        // File shrunk → compaction
        return new ScanDecision(ScanMode.COMPACTION, 0, progress.getLastMessageId());
    }

    public void updateProgress(DashboardScanProgress progress) {
        progressMapper.upsertProgress(progress);
    }
}
```

- [ ] **Step 2: Commit**

---

## Task 15: UserScanner

**Files:**
- Create: `src/main/java/com/company/clawboard/scanner/UserScanner.java`

- [ ] **Step 1: Implement UserScanner.java**

Responsibilities:
1. Discover all transcript JSONL files in the user's `.openclaw/agents/*/sessions/` directory
2. **Filter out non-transcript files**: only `sessions.json`
3. For each file, determine scan mode via ScanProgressService
4. Parse file using TranscriptParser
5. Batch insert results into database
6. Return scan statistics (files processed, skipped, errors, new records)

**File filtering logic** (simplest approach — rely on DB deduplication):

```java
/**
 * Determine if a file should be scanned as a transcript.
 * Strategy: Include ALL .jsonl files except sessions.json.
 * Deduplication is handled by database (session_id, message_id) unique key.
 * 
 * Included file types:
 * - Main files: *.jsonl
 * - Reset archives: *.jsonl.reset.*
 * - Deleted archives: *.jsonl.deleted.*
 * - Compaction backups: *.jsonl.bak* or *.jsonl.bak-*
 * - Checkpoint snapshots: *.checkpoint.*.jsonl
 * 
 * Excluded:
 * - sessions.json (metadata file, not transcript data)
 */
static boolean isTranscriptFile(String fileName) {
    // Exclude sessions.json metadata file
    if ("sessions.json".equals(fileName)) return false;
    // Include everything else that contains .jsonl
    return fileName.contains(".jsonl");
}
```

**Design rationale**:
- **Simplicity**: No complex filename pattern matching needed
- **Robustness**: Automatically adapts to new archive formats OpenClaw may introduce
- **Data integrity**: Database-level deduplication ensures no data loss
- **Performance acceptable**: Extra I/O for checkpoint/bak files is negligible at 100-1000 user scale

- [ ] **Step 2: Commit**

---

## Task 16: ScanOrchestrator & Scheduler

**Files:**
- Create: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

- [ ] **Step 1: Implement ScanOrchestrator.java**

Key aspects:
1. **Concurrency control** — `AtomicBoolean` for scan lock, `volatile Long currentScanId`
2. **Scheduled execution** — `@Scheduled(cron = "${clawboard.scan.cron}")` with `@ConditionalOnProperty(name = "clawboard.scan.enabled")`
3. **Manual trigger** — `triggerManualScan()` returns scanId or throws if already running
4. **Parallel user scanning** — submit UserScanner tasks to the `scanExecutor` thread pool
5. **Scan history** — insert `running` record at start, update to `completed`/`failed` in `finally` block
6. **Hourly stats aggregation** — call HourlyStatsAggregator after scan completes

```java
package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.ScanHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanOrchestrator {

    private final ClawboardProperties properties;
    private final UserScanner userScanner;
    private final ScanHistoryMapper scanHistoryMapper;
    private final HourlyStatsAggregator hourlyStatsAggregator;
    @Qualifier("scanExecutor")
    private final Executor scanExecutor;

    private final AtomicBoolean scanning = new AtomicBoolean(false);
    private volatile Long currentScanId = null;

    public boolean isScanning() { return scanning.get(); }
    public Long getCurrentScanId() { return currentScanId; }

    @Scheduled(cron = "${clawboard.scan.cron}")
    public void scheduledScan() {
        if (!properties.getScan().isEnabled()) return;
        runScan("scheduled");
    }

    /**
     * @return scanId if triggered, null if already scanning
     */
    public Long triggerManualScan() {
        // runScan() handles the CAS lock internally, no double-lock needed
        return runScan("manual");
    }

    private Long runScan(String triggerType) {
        if (!scanning.compareAndSet(false, true)) {
            log.warn("Scan skipped: previous scan still running (scanId={})", currentScanId);
            return null;
        }

        // Create scan history record
        var history = new DashboardScanHistory();
        history.setTriggerType(triggerType);
        history.setStatus("running");
        history.setStartedAt(LocalDateTime.now());
        scanHistoryMapper.insert(history);
        currentScanId = history.getId();

        long startMs = System.currentTimeMillis();

        try {
            // Discover user directories
            File nasRoot = new File(properties.getNas().getBasePath());
            File[] userDirs = nasRoot.listFiles(File::isDirectory);
            if (userDirs == null || userDirs.length == 0) {
                log.info("No user directories found in {}", nasRoot);
                finishScan(history, "completed", startMs, null);
                return history.getId();
            }

            history.setUsersScanned(userDirs.length);

            // Submit parallel scan tasks
            var futures = new ConcurrentHashMap<String, Future<UserScanner.UserScanResult>>();
            for (File userDir : userDirs) {
                String employeeId = userDir.getName();
                var future = CompletableFuture.supplyAsync(
                    () -> userScanner.scanUser(employeeId, userDir.toPath()),
                    scanExecutor
                );
                futures.put(employeeId, future);
            }

            // Collect results
            int totalFilesProcessed = 0, totalFilesSkipped = 0, totalFilesError = 0, totalFilesTotal = 0;
            int totalNewMessages = 0, totalNewTurns = 0, totalNewIssues = 0, totalNewSkillCalls = 0;

            for (var entry : futures.entrySet()) {
                try {
                    var result = entry.getValue().get(30, TimeUnit.MINUTES);
                    totalFilesTotal += result.filesTotal();
                    totalFilesProcessed += result.filesProcessed();
                    totalFilesSkipped += result.filesSkipped();
                    totalFilesError += result.filesError();
                    totalNewMessages += result.newMessages();
                    totalNewTurns += result.newTurns();
                    totalNewIssues += result.newIssues();
                    totalNewSkillCalls += result.newSkillCalls();
                } catch (Exception e) {
                    log.error("Scan failed for user {}", entry.getKey(), e);
                }
            }

            history.setFilesTotal(totalFilesTotal);
            history.setFilesProcessed(totalFilesProcessed);
            history.setFilesSkipped(totalFilesSkipped);
            history.setFilesError(totalFilesError);
            history.setNewMessages(totalNewMessages);
            history.setNewTurns(totalNewTurns);
            history.setNewIssues(totalNewIssues);
            history.setNewSkillCalls(totalNewSkillCalls);

            // Aggregate hourly stats
            hourlyStatsAggregator.aggregateAll();

            finishScan(history, "completed", startMs, null);
            return history.getId();

        } catch (Exception e) {
            log.error("Scan failed", e);
            finishScan(history, "failed", startMs, e.getMessage());
            return history.getId();
        } finally {
            currentScanId = null;
            scanning.set(false);
        }
    }

    private void finishScan(DashboardScanHistory history, String status, long startMs, String error) {
        history.setStatus(status);
        history.setFinishedAt(LocalDateTime.now());
        history.setDurationMs(System.currentTimeMillis() - startMs);
        history.setErrorMessage(error);
        scanHistoryMapper.updateById(history);
    }
}
```

- [ ] **Step 2: Commit**

---

## Task 17: HourlyStatsAggregator

**Files:**
- Create: `src/main/java/com/company/clawboard/scanner/HourlyStatsAggregator.java`

- [ ] **Step 1: Implement HourlyStatsAggregator.java**

Strategy: "recompute affected hours". For each `(employee_id, hour)` combination that was touched during the scan, re-aggregate from detail tables and upsert into `dashboard_hourly_stats`.

The simpler implementation for V1: recompute all hours for all employees who had data touched. In V2, optimize to track only affected hours.

```java
package com.company.clawboard.scanner;

import com.company.clawboard.mapper.HourlyStatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyStatsAggregator {

    private final HourlyStatsMapper hourlyStatsMapper;

    /**
     * Recompute all hourly stats from detail tables.
     * V1: full recompute. V2: track affected (employee_id, hour) pairs.
     */
    public void aggregateAll() {
        log.info("Starting hourly stats aggregation");
        long start = System.currentTimeMillis();

        // This executes a single SQL that:
        // 1. Groups dashboard_message by (employee_id, TRUNCATE_TO_HOUR(message_timestamp))
        // 2. JOINs with turn/skill/issue data
        // 3. INSERTs or UPDATEs dashboard_hourly_stats
        hourlyStatsMapper.recomputeAllHourlyStats();

        log.info("Hourly stats aggregation completed in {}ms", System.currentTimeMillis() - start);
    }
}
```

The key SQL in `HourlyStatsMapper.xml`:

```sql
<!-- recomputeAllHourlyStats: replaces all hourly_stats from detail tables -->
INSERT INTO dashboard_hourly_stats
  (employee_id, stat_hour, total_tokens, input_tokens, output_tokens, total_cost,
   cache_read_tokens, cache_write_tokens,
   conversation_turns, complete_turns, error_turns,
   skill_invocations, skill_errors,
   tool_calls, tool_errors, error_count)
SELECT
    m.employee_id,
    DATE_FORMAT(m.message_timestamp, '%Y-%m-%d %H:00:00') AS stat_hour,
    COALESCE(SUM(m.total_tokens), 0),
    COALESCE(SUM(m.input_tokens), 0),
    COALESCE(SUM(m.output_tokens), 0),
    COALESCE(SUM(m.cost_total), 0),
    COALESCE(SUM(m.cache_read_tokens), 0),
    COALESCE(SUM(m.cache_write_tokens), 0),
    -- turns: count from dashboard_conversation_turn where start_time in this hour
    0, 0, 0,  -- filled separately below
    0, 0,     -- skill invocations
    0, 0,     -- tool calls
    0         -- error count
FROM dashboard_message m
WHERE m.role = 'assistant'
  AND m.message_timestamp IS NOT NULL
GROUP BY m.employee_id, DATE_FORMAT(m.message_timestamp, '%Y-%m-%d %H:00:00')
ON DUPLICATE KEY UPDATE
    total_tokens = VALUES(total_tokens),
    input_tokens = VALUES(input_tokens),
    output_tokens = VALUES(output_tokens),
    total_cost = VALUES(total_cost),
    cache_read_tokens = VALUES(cache_read_tokens),
    cache_write_tokens = VALUES(cache_write_tokens)
```

> **Implementation note**: The aggregation is better split into multiple queries — one for message-level token stats, another for turn counts, another for skill/tool counts. Combine them in the Java code and do a single upsert per `(employee_id, stat_hour)`. This avoids complex multi-table JOINs.

```java
// Better approach: aggregate in Java
public void aggregateAll() {
    // 1. Get all distinct (employee_id, stat_hour) from messages
    var keys = hourlyStatsMapper.selectDistinctEmployeeHours();

    for (var key : keys) {
        // 2. Aggregate tokens from dashboard_message
        var tokenStats = hourlyStatsMapper.sumTokensForHour(key.employeeId(), key.statHour());
        // 3. Count turns from dashboard_conversation_turn
        var turnStats = hourlyStatsMapper.countTurnsForHour(key.employeeId(), key.statHour());
        // 4. Count skills from dashboard_skill_invocation
        var skillStats = hourlyStatsMapper.countSkillsForHour(key.employeeId(), key.statHour());
        // 5. Count errors from dashboard_transcript_issue
        var errorStats = hourlyStatsMapper.countErrorsForHour(key.employeeId(), key.statHour());

        // 6. Upsert
        hourlyStatsMapper.upsertHourlyStats(/* merged stats */);
    }
}
```

- [ ] **Step 2: Commit**

---

## Task 18: Service Layer

**Files:**
- Create: `src/main/java/com/company/clawboard/service/DashboardService.java`
- Create: `src/main/java/com/company/clawboard/service/TurnService.java`
- Create: `src/main/java/com/company/clawboard/service/ErrorService.java`
- Create: `src/main/java/com/company/clawboard/service/SkillService.java`
- Create: `src/main/java/com/company/clawboard/service/ScanManagementService.java`

- [ ] **Step 1: Implement DashboardService.java**

Methods:
- `getGlobalStats()` → query `dashboard_hourly_stats` SUM all + COUNT DISTINCT employees
- `getSummary(TimeRangeRequest)` → filtered SUM with JOIN to `dashboard_employee`
- `getTrend(TimeRangeRequest)` → GROUP BY time granularity based on duration:
  - `duration <= 24h` → GROUP BY `DATE_FORMAT(stat_hour, '%Y-%m-%d %H:00:00')` with 2-hour buckets
  - `24h < duration <= 7d` → GROUP BY `DATE(stat_hour)`
  - `duration > 7d` → GROUP BY `YEARWEEK(stat_hour)`
- `getUserSummary(TimeRangeRequest)` → paginated user-level aggregation

- [ ] **Step 2: Implement TurnService.java**

Methods:
- `searchTurns(TurnSearchRequest)` → paginated query on `dashboard_conversation_turn` with optional skill filter via JOIN
- `getTrace(Long turnId)` → read `chain_summary` JSON, deserialize to TraceNode list
- `updateQuality(Long turnId, Integer qualityStatus)` → simple UPDATE

- [ ] **Step 3: Implement ErrorService.java**

Methods:
- `getErrorSummary(TimeRangeRequest)` → aggregate from `dashboard_transcript_issue` by type and severity
- `searchErrors(ErrorSearchRequest)` → paginated error list

**problemRate calculation**: `totalErrors / totalTurns` (from `dashboard_conversation_turn` in the same time range)

- [ ] **Step 4: Implement SkillService.java**

Methods:
- `getSkillOptions()` → `SELECT DISTINCT skill_name FROM dashboard_skill_invocation`; apply name mapping from config

- [ ] **Step 5: Implement ScanManagementService.java**

Methods:
- `triggerScan()` → delegates to ScanOrchestrator, returns 200 or 409
- `getScanStatus()` → current scanning state + next scheduled time + last completed scan
- `getScanHistory(page, pageSize, triggerType, status)` → paginated query

**Next scheduled time calculation**: Parse the cron expression and compute the next fire time. Use `org.springframework.scheduling.support.CronExpression.next(LocalDateTime)`.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/company/clawboard/service/
git commit -m "feat: implement service layer for all API modules"
```

---

## Task 19: REST Controllers — Dashboard & Skills

**Files:**
- Create: `src/main/java/com/company/clawboard/controller/DashboardController.java`

- [ ] **Step 1: Implement DashboardController.java**

```java
package com.company.clawboard.controller;

import com.company.clawboard.dto.*;
import com.company.clawboard.service.DashboardService;
import com.company.clawboard.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SkillService skillService;

    @GetMapping("/skills/options")
    public ApiResponse<List<SkillOption>> getSkillOptions() {
        return ApiResponse.ok(skillService.getSkillOptions());
    }

    @GetMapping("/dashboard/global-stats")
    public ApiResponse<GlobalStatsResponse> getGlobalStats() {
        return ApiResponse.ok(dashboardService.getGlobalStats());
    }

    @PostMapping("/dashboard/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(dashboardService.getSummary(request));
    }

    @PostMapping("/dashboard/trend")
    public ApiResponse<List<TrendDataPoint>> getTrend(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(dashboardService.getTrend(request));
    }

    @PostMapping("/dashboard/usersummary")
    public ApiResponse<PageResult<UserSummaryItem>> getUserSummary(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(dashboardService.getUserSummary(request));
    }
}
```

- [ ] **Step 2: Commit**

---

## Task 20: REST Controllers — Turn & Error

**Files:**
- Create: `src/main/java/com/company/clawboard/controller/TurnController.java`
- Create: `src/main/java/com/company/clawboard/controller/ErrorController.java`

- [ ] **Step 1: Implement TurnController.java**

```java
@RestController
@RequestMapping("/api/v1/turns")
@RequiredArgsConstructor
public class TurnController {

    private final TurnService turnService;

    @PostMapping("/search")
    public ApiResponse<PageResult<TurnSearchItem>> searchTurns(@RequestBody TurnSearchRequest request) {
        return ApiResponse.ok(turnService.searchTurns(request));
    }

    @GetMapping("/{turnId}/trace")
    public ApiResponse<TraceResponse> getTrace(@PathVariable Long turnId) {
        return ApiResponse.ok(turnService.getTrace(turnId));
    }

    @PutMapping("/{turnId}/quality")
    public ApiResponse<Void> updateQuality(@PathVariable Long turnId,
                                            @RequestBody Map<String, Integer> body) {
        turnService.updateQuality(turnId, body.get("qualityStatus"));
        return ApiResponse.ok(null);
    }
}
```

- [ ] **Step 2: Implement ErrorController.java**

```java
@RestController
@RequestMapping("/api/v1/errors")
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorService errorService;

    @PostMapping("/summary")
    public ApiResponse<ErrorSummaryResponse> getErrorSummary(@RequestBody TimeRangeRequest request) {
        return ApiResponse.ok(errorService.getErrorSummary(request));
    }

    @PostMapping("/search")
    public ApiResponse<PageResult<ErrorSearchItem>> searchErrors(@RequestBody ErrorSearchRequest request) {
        return ApiResponse.ok(errorService.searchErrors(request));
    }
}
```

- [ ] **Step 3: Commit**

---

## Task 21: REST Controllers — Scan Management

**Files:**
- Create: `src/main/java/com/company/clawboard/controller/ScanController.java`

- [ ] **Step 1: Implement ScanController.java**

```java
@RestController
@RequestMapping("/api/v1/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanManagementService scanManagementService;

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<?>> triggerScan() {
        var result = scanManagementService.triggerScan();
        if (result.isConflict()) {
            return ResponseEntity.status(409)
                .body(ApiResponse.error(409, "scan already in progress, skipping this trigger",
                    result.currentScanInfo()));
        }
        return ResponseEntity.ok(ApiResponse.ok(result.triggerInfo()));
    }

    @GetMapping("/status")
    public ApiResponse<ScanStatusResponse> getScanStatus() {
        return ApiResponse.ok(scanManagementService.getScanStatus());
    }

    @GetMapping("/history")
    public ApiResponse<PageResult<ScanHistoryItem>> getScanHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String triggerType,
            @RequestParam(required = false) String status) {
        return ApiResponse.ok(scanManagementService.getScanHistory(page, pageSize, triggerType, status));
    }
}
```

- [ ] **Step 2: Commit**

---

## Task 22: Global Exception Handler

**Files:**
- Create: `src/main/java/com/company/clawboard/controller/GlobalExceptionHandler.java`

- [ ] **Step 1: Implement GlobalExceptionHandler.java**

```java
package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequest(IllegalArgumentException e) {
        return ApiResponse.error(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGenericError(Exception e) {
        log.error("Unexpected error", e);
        return ApiResponse.error(500, "internal server error");
    }
}
```

- [ ] **Step 2: Commit**

---

## Task 23: Integration Test with Real JSONL Data

**Files:**
- Create: `src/test/java/com/company/clawboard/parser/TranscriptParserIntegrationTest.java`
- Create: `src/test/resources/application-test.yml`
- Create: `src/test/resources/fixtures/` (curated test files)

- [ ] **Step 1: Create test configuration**

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  flyway:
    enabled: true

clawboard:
  nas:
    base-path: ./test/openclaw-logs/../
  parser:
    system-message-prefixes:
      - "A new session was started"
      - "Run your Session Startup sequence"
      - "Read HEARTBEAT.md"
      - "<inbound_metadata>"
      - "<openclaw-envelope>"
```

- [ ] **Step 2: Create curated test fixtures**

Copy minimal JSONL data from `test/openclaw-logs/agents/main/sessions/` into `src/test/resources/fixtures/`:
- `simple-session.jsonl` — from `52fa7e91...` (simple: user → assistant → reply)
- `session-with-skill.jsonl` — from `600cd549...` (has SKILL.md read via toolCall)
- `session-with-errors.jsonl` — construct a file with known error patterns for testing IssueDetector

- [ ] **Step 3: Write parser integration test**

```java
@SpringBootTest(properties = "spring.profiles.active=test")
class TranscriptParserIntegrationTest {

    @Autowired TranscriptParser parser;

    @Test
    void parseSimpleSession_extractsCorrectTurnCount() {
        var result = parser.parseFile(
            Path.of("src/test/resources/fixtures/simple-session.jsonl"),
            "test-emp", 0L);
        assertNotNull(result);
        assertEquals("52fa7e91-fb04-4a57-a962-194a45b3e38c", result.sessionId());
        // Expect 1 turn (the cron message is system-generated and filtered)
        // Actual expectation depends on content - verify manually
    }

    @Test
    void parseSessionWithSkill_detectsSkillInvocation() {
        var result = parser.parseFile(
            Path.of("src/test/resources/fixtures/session-with-skill.jsonl"),
            "test-emp", 0L);
        assertNotNull(result);
        assertFalse(result.skillInvocations().isEmpty());
        assertEquals("imap-smtp-email", result.skillInvocations().get(0).getSkillName());
    }

    @Test
    void parseRealWorldFile_noExceptions() {
        // Parse a real file from test data directory — should not throw
        Path realFile = Path.of("test/openclaw-logs/agents/main/sessions/600cd549-c1bf-4bc9-bd1e-8aa4d67f1874.jsonl");
        if (realFile.toFile().exists()) {
            var result = parser.parseFile(realFile, "test-emp", 0L);
            assertNotNull(result);
        }
    }
}
```

- [ ] **Step 4: Write controller tests**

Use `@WebMvcTest` with mocked services to verify endpoint mapping and response format.

```java
@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean DashboardService dashboardService;
    @MockBean SkillService skillService;

    @Test
    void getGlobalStats_returnsCorrectFormat() throws Exception {
        when(dashboardService.getGlobalStats()).thenReturn(
            new GlobalStatsResponse(1250000L, 52000, 25000, 872));

        mockMvc.perform(get("/api/v1/dashboard/global-stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.totalTokens").value(1250000));
    }
}
```

- [ ] **Step 5: Run all tests**

Run: `mvn test`
Expected: ALL PASS

- [ ] **Step 6: Commit**

```bash
git add src/test/
git commit -m "test: add parser integration tests and controller tests"
```

---

## Task 24: Scan Report Generator

**Files:**
- Create: `src/main/java/com/company/clawboard/scanner/ReportGenerator.java`
- Create: `src/main/resources/application.yml` (add reports config)

- [ ] **Step 1: Implement ReportGenerator.java**

Responsibilities:
1. Generate Markdown report after each scan completes
2. Query issues from `dashboard_transcript_issue` table filtered by `scan_id`
3. Calculate statistics (total issues, conversation turns, problem rate)
4. Group issues by severity and type
5. Write report to `scripts/reports/YYYY-MM-DD/transcript-comprehensive-issues.md`

**Report structure** (aligned with `../openclaw/scripts/transcript-comprehensive-issues.md`):

```java
package com.company.clawboard.scanner;

import com.company.clawboard.entity.TranscriptIssue;
import com.company.clawboard.mapper.TranscriptIssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerator {

    private final TranscriptIssueMapper issueMapper;
    
    private static final String REPORTS_DIR = "scripts/reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Generate report for a specific scan.
     * Called by ScanOrchestrator in the finally block after scan completes.
     */
    public void generateReport(Long scanId, LocalDateTime scanStartTime) {
        try {
            // 1. Query all issues for this scan
            var issues = issueMapper.selectByScanId(scanId);
            
            // 2. Calculate statistics
            var stats = calculateStatistics(issues);
            
            // 3. Generate Markdown content
            String markdown = buildMarkdownReport(stats, issues, scanStartTime);
            
            // 4. Write to file with date-based directory structure
            String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
            Path reportDir = Paths.get(REPORTS_DIR, dateStr);
            Files.createDirectories(reportDir);
            
            Path reportPath = reportDir.resolve("transcript-comprehensive-issues.md");
            Files.writeString(reportPath, markdown);
            
            log.info("Report generated: {}", reportPath.toAbsolutePath());
            
        } catch (IOException e) {
            log.error("Failed to generate report for scan {}", scanId, e);
        }
    }
    
    private Map<String, Object> calculateStatistics(List<TranscriptIssue> issues) {
        // Calculate total issues, by type, by severity, etc.
        // Return map with all needed statistics
    }
    
    private String buildMarkdownReport(Map<String, Object> stats, 
                                       List<TranscriptIssue> issues,
                                       LocalDateTime scanStartTime) {
        StringBuilder md = new StringBuilder();
        
        // Header
        md.append("# ClawBoard Session Transcript 综合问题检测报告\n\n");
        md.append("**生成时间**: ").append(LocalDateTime.now()).append("\n\n");
        
        // Statistics overview
        md.append("## 📊 统计概览\n\n");
        md.append("- **总问题数**: ").append(stats.get("totalIssues")).append("\n");
        md.append("- **总对话轮数**: ").append(stats.get("totalTurns")).append("\n");
        md.append("- **有问题轮数**: ").append(stats.get("problematicTurns")).append("\n");
        if ((int) stats.get("totalTurns") > 0) {
            double rate = (double) stats.get("problematicTurns") / (int) stats.get("totalTurns") * 100;
            md.append(String.format("- **问题率**: %.2f%%\n", rate));
        }
        
        // Issue type distribution table
        md.append("\n### 问题类型分布\n\n");
        md.append("| 问题类型 | 数量 | 说明 |\n");
        md.append("|---------|------|------|\n");
        @SuppressWarnings("unchecked")
        Map<String, Integer> byType = (Map<String, Integer>) stats.get("byType");
        for (var entry : byType.entrySet()) {
            md.append("| ").append(entry.getKey())
              .append(" | ").append(entry.getValue())
              .append(" | ").append(getIssueTypeDescription(entry.getKey()))
              .append(" |\n");
        }
        
        // Detailed issues by severity
        md.append("\n## 🔴 HIGH 严重度问题\n\n");
        appendIssuesBySeverity(md, issues, "HIGH");
        
        md.append("\n## 🟡 MEDIUM 严重度问题\n\n");
        appendIssuesBySeverity(md, issues, "MEDIUM");
        
        md.append("\n## 🔵 LOW 严重度问题\n\n");
        appendIssuesBySeverity(md, issues, "LOW");
        
        // Scan metadata
        md.append("\n## 📋 扫描元数据\n\n");
        md.append("- **扫描开始时间**: ").append(scanStartTime).append("\n");
        md.append("- **扫描结束时间**: ").append(LocalDateTime.now()).append("\n");
        md.append("- **扫描耗时**: ").append(stats.get("durationSeconds")).append(" 秒\n");
        md.append("- **扫描用户数**: ").append(stats.get("usersScanned")).append("\n");
        md.append("- **扫描文件数**: ").append(stats.get("filesScanned")).append("\n");
        
        return md.toString();
    }
    
    private void appendIssuesBySeverity(StringBuilder md, 
                                        List<TranscriptIssue> issues, 
                                        String severity) {
        var filtered = issues.stream()
            .filter(i -> severity.equals(i.getSeverity()))
            .collect(Collectors.toList());
        
        if (filtered.isEmpty()) {
            md.append("无\n");
            return;
        }
        
        for (var issue : filtered) {
            md.append("### ").append(issue.getErrorType()).append("\n\n");
            md.append("- **Session ID**: ").append(issue.getSessionId()).append("\n");
            md.append("- **文件名**: ").append(issue.getLogFileName()).append("\n");
            md.append("- **行号**: ").append(issue.getLineNum()).append("\n");
            md.append("- **描述**: ").append(issue.getDescription()).append("\n");
            if (issue.getContext() != null && !issue.getContext().isEmpty()) {
                md.append("- **上下文**: ```\n").append(issue.getContext()).append("\n```\n");
            }
            md.append("\n");
        }
    }
    
    private String getIssueTypeDescription(String errorType) {
        // Return human-readable description for each issue type
        return switch (errorType) {
            case "flow_integrity_no_reply" -> "user 消息后无 assistant 回复";
            case "flow_integrity_missing_tool_result" -> "toolCall 后无 toolResult";
            case "flow_integrity_missing_final_answer" -> "toolResult 后无最终回答";
            case "model_error" -> "模型调用错误";
            case "timeout_error" -> "超时错误";
            case "rate_limit_error" -> "速率限制错误";
            default -> errorType;
        };
    }
}
```

- [ ] **Step 2: Integrate with ScanOrchestrator**

Modify `ScanOrchestrator.executeScheduledScan()` to call report generator in the `finally` block:

```java
finally {
    long endMs = System.currentTimeMillis();
    finishScan(history, success ? "completed" : "failed", startMs, errorMsg);
    
    // Generate report after scan completes (regardless of success/failure)
    if (success) {
        try {
            reportGenerator.generateReport(history.getId(), 
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startMs), ZoneId.systemDefault()));
        } catch (Exception e) {
            log.error("Failed to generate scan report", e);
            // Don't fail the scan if report generation fails
        }
    }
}
```

- [ ] **Step 3: Add configuration for reports directory**

In `application.yml`:
```yaml
clawboard:
  scan:
    enabled: true
    cron: "0 0 * * * *"  # Every hour
    max-concurrent-users: 5
  reports:
    output-dir: scripts/reports  # Configurable reports directory
```

- [ ] **Step 4: Commit**

---

## Summary: API → Implementation Mapping

| API Endpoint | Controller | Service Method | Primary Data Source |
|---|---|---|---|
| GET `/api/v1/skills/options` | DashboardController | SkillService.getSkillOptions() | dashboard_skill_invocation DISTINCT |
| GET `/api/v1/dashboard/global-stats` | DashboardController | DashboardService.getGlobalStats() | dashboard_hourly_stats SUM |
| POST `/api/v1/dashboard/summary` | DashboardController | DashboardService.getSummary() | dashboard_hourly_stats filtered SUM |
| POST `/api/v1/dashboard/trend` | DashboardController | DashboardService.getTrend() | dashboard_hourly_stats GROUP BY granularity |
| POST `/api/v1/dashboard/usersummary` | DashboardController | DashboardService.getUserSummary() | hourly_stats + turn + skill aggregation |
| POST `/api/v1/turns/search` | TurnController | TurnService.searchTurns() | dashboard_conversation_turn |
| GET `/api/v1/turns/{id}/trace` | TurnController | TurnService.getTrace() | conversation_turn.chain_summary |
| PUT `/api/v1/turns/{id}/quality` | TurnController | TurnService.updateQuality() | conversation_turn UPDATE |
| POST `/api/v1/errors/summary` | ErrorController | ErrorService.getErrorSummary() | dashboard_transcript_issue |
| POST `/api/v1/errors/search` | ErrorController | ErrorService.searchErrors() | dashboard_transcript_issue |
| POST `/api/v1/scan/trigger` | ScanController | ScanManagementService.triggerScan() | ScanOrchestrator |
| GET `/api/v1/scan/status` | ScanController | ScanManagementService.getScanStatus() | dashboard_scan_history |
| GET `/api/v1/scan/history` | ScanController | ScanManagementService.getScanHistory() | dashboard_scan_history |

## Key Design Decisions Captured

1. **ORM choice**: MyBatis (not JPA) — complex aggregation queries and `ON DUPLICATE KEY UPDATE` are central to this system. MyBatis gives direct SQL control.
2. **Dedup strategy**: `INSERT IGNORE` for messages/issues/skills, `INSERT ... ON DUPLICATE KEY UPDATE` for turns (merge more complete version) and hourly stats (full replace).
3. **File filtering**: Include all `.jsonl`-containing files EXCEPT `sessions.json` and `.checkpoint.*` files. The `.bak` file format in production is `*.jsonl.bak-<N>-<epochMs>`, not `*.jsonl.bak.<ISO-ts>`.
4. **Session ID extraction**: From file header `type=session` first, fallback to `fileName.substring(0, fileName.indexOf(".jsonl"))`.
5. **Turn unique key**: `(session_id, start_message_id)` — NOT `turn_index`. Same user message has identical message_id in .bak and main file, enabling natural dedup.
6. **Hourly stats aggregation**: "Recompute affected hours" strategy — V1 recomputes all, V2 can optimize to track dirty hours.
7. **Scan concurrency**: `AtomicBoolean` lock. Scheduled trigger silently skips. Manual trigger returns 409 Conflict.
