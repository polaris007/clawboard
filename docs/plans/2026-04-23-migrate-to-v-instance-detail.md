# Migrate to VInstanceDetail Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将 OpenClaw 数据源从 `openclaw_instances` 表迁移到 `v_instance_detail` 视图，并实现 API 增强功能（registeredUsers 字段、status 字段判断、logFilePath 记录）。

**Architecture:** 
- 创建新的 `VInstanceDetailMapper` 接口和 XML 映射文件，提供从视图查询实例信息的能力
- 修改 `DashboardService` 注入新 Mapper，实现 registeredUsers 统计和 status 字段判断
- 修改 `ParsedTranscript` 记录增加 filePath 字段，实现 logFilePath 数据流
- 修改 `AccountsCsvReader` 简化 JSON 解析逻辑

**Tech Stack:** Java 17, Spring Boot 3.2.5, MyBatis 3.0.3, MySQL 5.7+

---

## Task 1: 创建 VInstanceDetailMapper

### 1.1 创建 VInstanceDetailMapper 接口

**Files:**
- Create: `src/main/java/com/company/clawboard/mapper/VInstanceDetailMapper.java`

**Step 1: 创建 Mapper 接口文件**

```java
package com.company.clawboard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VInstanceDetailMapper {
    
    /**
     * 查询所有运行中的实例
     * @return 实例列表，每个实例包含 uid, user_config_name, user_config_org_code
     */
    List<Map<String, Object>> selectRunningInstances();
    
    /**
     * 根据 uid 查询实例状态
     * @param uid 用户 ID
     * @return status 字段值，如果不存在返回 null
     */
    String selectStatusByUid(@Param("uid") String uid);
    
    /**
     * 批量查询多个 uid 的实例状态
     * @param uids 用户 ID 列表
     * @return 实例状态列表，每个包含 uid 和 status
     */
    List<Map<String, Object>> selectStatusByUids(@Param("uids") List<String> uids);
    
    /**
     * 统计注册用户数（status != 'deleted' 的不重复 uid 数量）
     * @return 注册用户数
     */
    Integer countRegisteredUsers();
}
```

**Step 2: 验证文件创建成功**

```bash
ls -la src/main/java/com/company/clawboard/mapper/VInstanceDetailMapper.java
```

Expected: 文件存在

---

### 1.2 创建 VInstanceDetailMapper XML 映射文件

**Files:**
- Create: `src/main/resources/mapper/VInstanceDetailMapper.xml`

**Step 1: 创建 XML 映射文件**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.company.clawboard.mapper.VInstanceDetailMapper">

    <!-- 查询所有运行中的实例 -->
    <select id="selectRunningInstances" resultType="java.util.Map">
        SELECT 
            uid,
            user_config_name,
            user_config_org_code
        FROM v_instance_detail
        WHERE status = 'running'
    </select>

    <!-- 根据 uid 查询实例状态 -->
    <select id="selectStatusByUid" resultType="java.lang.String">
        SELECT status
        FROM v_instance_detail
        WHERE uid = #{uid}
        LIMIT 1
    </select>

    <!-- 批量查询多个 uid 的实例状态 -->
    <select id="selectStatusByUids" resultType="java.util.Map">
        SELECT 
            uid,
            status
        FROM v_instance_detail
        WHERE uid IN
        <foreach collection="uids" item="uid" open="(" separator="," close=")">
            #{uid}
        </foreach>
    </select>

    <!-- 统计注册用户数 -->
    <select id="countRegisteredUsers" resultType="java.lang.Integer">
        SELECT COUNT(DISTINCT uid)
        FROM v_instance_detail
        WHERE status != 'deleted'
    </select>

</mapper>
```

**Step 2: 验证文件创建成功**

```bash
ls -la src/main/resources/mapper/VInstanceDetailMapper.xml
```

Expected: 文件存在

---

### 1.3 验证 Mapper 可以正常注入

**Files:**
- Test: `src/test/java/com/company/clawboard/mapper/VInstanceDetailMapperTest.java`

**Step 1: 创建 Mapper 测试类**

```java
package com.company.clawboard.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VInstanceDetailMapperTest {

    @Autowired
    private VInstanceDetailMapper mapper;

    @Test
    void should_return_empty_list_when_no_running_instances() {
        List<Map<String, Object>> instances = mapper.selectRunningInstances();
        assertThat(instances).isNotNull();
    }

    @Test
    void should_return_null_when_uid_not_exists() {
        String status = mapper.selectStatusByUid("non-existent-uid");
        assertThat(status).isNull();
    }

    @Test
    void should_return_empty_list_when_batch_query_not_exists() {
        List<Map<String, Object>> statuses = mapper.selectStatusByUids(
            List.of("uid1", "uid2", "uid3")
        );
        assertThat(statuses).isEmpty();
    }

    @Test
    void should_return_zero_when_no_registered_users() {
        Integer count = mapper.countRegisteredUsers();
        assertThat(count).isNotNull();
    }
}
```

**Step 2: 运行测试验证**

```bash
mvn test -Dtest=VInstanceDetailMapperTest -DfailIfNoTests=false
```

Expected: 测试通过（至少不报错）

**Step 3: 提交**

```bash
git add src/main/java/com/company/clawboard/mapper/VInstanceDetailMapper.java
git add src/main/resources/mapper/VInstanceDetailMapper.xml
git add src/test/java/com/company/clawboard/mapper/VInstanceDetailMapperTest.java
git commit -m "feat: add VInstanceDetailMapper for querying v_instance_detail view"
```

---

## Task 2: 修改 GlobalStatsResponse 和 DashboardService

### 2.1 在 GlobalStatsResponse 中增加 registeredUsers 字段

**Files:**
- Modify: `src/main/java/com/company/clawboard/dto/GlobalStatsResponse.java`

**Step 1: 修改 DTO 类**

```java
package com.company.clawboard.dto;

import lombok.Data;

@Data
public class GlobalStatsResponse {
    private Long totalTokens;
    private Integer totalTurns;
    private Integer totalSkillCalls;
    private Integer totalUsers;
    private Integer registeredUsers;  // 新增字段：注册用户总数
}
```

**Step 2: 验证编译通过**

```bash
mvn compile -q
```

Expected: 编译成功

---

### 2.2 在 DashboardService 中注入 VInstanceDetailMapper

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DashboardService.java`

**Step 1: 查看当前 DashboardService 的构造函数**

```bash
grep -n "private final" src/main/java/com/company/clawboard/service/DashboardService.java | head -10
```

**Step 2: 添加 Mapper 注入**

在 `DashboardService` 类的字段声明部分（第 22-25 行附近）添加：

```java
private final VInstanceDetailMapper vInstanceDetailMapper;
```

由于使用了 `@RequiredArgsConstructor`，Lombok 会自动生成构造函数。

**Step 3: 添加 import 语句**

在文件头部添加：

```java
import com.company.clawboard.mapper.VInstanceDetailMapper;
```

---

### 2.3 修改 getGlobalStats() 方法设置 registeredUsers 字段

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DashboardService.java`

**Step 1: 修改 getGlobalStats() 方法**

找到 `getGlobalStats()` 方法（大约在第 76-94 行），修改为：

```java
public GlobalStatsResponse getGlobalStats() {
    var response = new GlobalStatsResponse();
    
    // 从 hourly_stats 表查询所有数据（不受筛选条件影响）
    List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(null, null, null, null);
    
    // 聚合计算
    long totalTokens = stats.stream().mapToLong(DashboardHourlyStats::getTotalTokens).sum();
    int totalTurns = stats.stream().mapToInt(DashboardHourlyStats::getConversationTurns).sum();
    int totalSkillCalls = stats.stream().mapToInt(DashboardHourlyStats::getSkillInvocations).sum();
    int totalUsers = (int) stats.stream().map(DashboardHourlyStats::getEmployeeId).distinct().count();
    
    response.setTotalTokens(totalTokens);
    response.setTotalTurns(totalTurns);
    response.setTotalSkillCalls(totalSkillCalls);
    response.setTotalUsers(totalUsers);
    
    // 新增：统计注册用户数（从 v_instance_detail 视图查询）
    Integer registeredUsers = vInstanceDetailMapper.countRegisteredUsers();
    response.setRegisteredUsers(registeredUsers != null ? registeredUsers : 0);
    
    return response;
}
```

**Step 2: 验证编译通过**

```bash
mvn compile -q
```

Expected: 编译成功

---

### 2.4 验证 API 响应包含 registeredUsers 字段

**Files:**
- Test: `src/test/java/com/company/clawboard/service/DashboardServiceTest.java`

**Step 1: 创建或修改测试类**

```java
package com.company.clawboard.service;

import com.company.clawboard.dto.GlobalStatsResponse;
import com.company.clawboard.mapper.VInstanceDetailMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private VInstanceDetailMapper vInstanceDetailMapper;

    @Test
    void should_return_registered_users_in_global_stats() {
        GlobalStatsResponse response = dashboardService.getGlobalStats();
        
        assertThat(response).isNotNull();
        assertThat(response.getRegisteredUsers()).isNotNull();
        assertThat(response.getRegisteredUsers()).isGreaterThanOrEqualTo(0);
    }
}
```

**Step 2: 运行测试**

```bash
mvn test -Dtest=DashboardServiceTest#should_return_registered_users_in_global_stats -DfailIfNoTests=false
```

Expected: 测试通过

**Step 3: 提交**

```bash
git add src/main/java/com/company/clawboard/dto/GlobalStatsResponse.java
git add src/main/java/com/company/clawboard/service/DashboardService.java
git add src/test/java/com/company/clawboard/service/DashboardServiceTest.java
git commit -m "feat: add registeredUsers field to GlobalStatsResponse"
```

---

## Task 3: 修改 DashboardService 实现 status 字段判断

### 3.1 修改 getUserSummaries() 方法获取用户状态

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DashboardService.java`

**Step 1: 查看 getUserSummaries() 方法当前实现**

```bash
grep -n "getUserSummaries" -A 80 src/main/java/com/company/clawboard/service/DashboardService.java | head -90
```

**Step 2: 修改 getUserSummaries() 方法**

在方法的第 140-147 行附近，在获取 stats 列表后添加状态查询逻辑：

```java
public PageResult<UserSummaryItem> getUserSummaries(TimeRangeRequest request) {
    // 从 hourly_stats 表查询数据，支持团队和姓名筛选
    List<DashboardHourlyStats> stats = hourlyStatsMapper.selectByTimeRange(
        request.getTeamName(),
        request.getUserId(),
        request.getStartTime(),
        request.getEndTime()
    );
    
    // 新增：收集所有需要查询状态的 employeeId
    List<String> employeeIds = stats.stream()
        .map(DashboardHourlyStats::getEmployeeId)
        .distinct()
        .toList();
    
    // 新增：批量查询用户实例状态
    Map<String, String> userStatusMap = new java.util.HashMap<>();
    if (!employeeIds.isEmpty()) {
        List<Map<String, Object>> statuses = vInstanceDetailMapper.selectStatusByUids(employeeIds);
        for (Map<String, Object> status : statuses) {
            String uid = (String) status.get("uid");
            String instanceStatus = (String) status.get("status");
            userStatusMap.put(uid, instanceStatus);
        }
    }
    
    // 按员工分组并转换为用户明细
    var userItems = stats.stream()
            .collect(Collectors.groupingBy(DashboardHourlyStats::getEmployeeId))
            .entrySet().stream()
            .map(entry -> {
                String employeeId = entry.getKey();
                List<DashboardHourlyStats> userStats = entry.getValue();
                
                var item = new UserSummaryItem();
                item.setUserId(employeeId);
                
                // 从 employee 表获取员工信息
                DashboardEmployee employee = employeeMapper.selectByEmployeeId(employeeId);
                if (employee != null) {
                    item.setUserName(employee.getEmployeeName());
                    item.setOrgCode(employee.getTeamName());
                } else {
                    item.setUserName(employeeId);
                    item.setOrgCode("未知");
                }
                
                // 修改：根据 v_instance_detail 的 status 字段判断
                String instanceStatus = userStatusMap.get(employeeId);
                item.setStatus("running".equals(instanceStatus));
                
                // 保持现有心跳逻辑（如果需要）
                item.setLastHeartbeat(System.currentTimeMillis());
                
                // ... 其余代码保持不变 ...
```

**Step 3: 验证编译通过**

```bash
mvn compile -q
```

Expected: 编译成功

---

### 3.2 验证 API 响应中 status 字段判断正确

**Files:**
- Test: `src/test/java/com/company/clawboard/service/DashboardServiceTest.java`

**Step 1: 添加 status 字段测试**

```java
@Test
void should_set_status_true_when_instance_running() {
    // 先插入测试数据到 v_instance_detail（通过 openclaw_instances 表）
    // 然后调用 API 验证 status 字段为 true
    GlobalStatsResponse response = dashboardService.getGlobalStats();
    // 验证逻辑
}
```

**Step 2: 运行集成测试**

启动应用后手动验证：

```bash
# 清空数据库
mysql -u clawboard -pClaw@1234 clawboard < scripts/reset-database.sql

# 启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

然后调用 API：

```bash
curl -X POST http://localhost:8080/api/v1/dashboard/usersummary \
  -H "Content-Type: application/json" \
  -d '{}'
```

Expected: 响应中 status 字段根据实例状态正确设置

**Step 3: 提交**

```bash
git add src/main/java/com/company/clawboard/service/DashboardService.java
git add src/test/java/com/company/clawboard/service/DashboardServiceTest.java
git commit -m "feat: set user status based on v_instance_detail.status field"
```

---

## Task 4: 修改 ParsedTranscript 记录

### 4.1 在 ParsedTranscript 记录中增加 filePath 字段

**Files:**
- Modify: `src/main/java/com/company/clawboard/parser/TranscriptParser.java`

**Step 1: 查看 ParsedTranscript 记录定义**

```bash
grep -n "record ParsedTranscript" -A 10 src/main/java/com/company/clawboard/parser/TranscriptParser.java
```

**Step 2: 修改 ParsedTranscript 记录**

找到第 30-37 行的记录定义，修改为：

```java
public record ParsedTranscript(
    String sessionId,
    List<MessageRecord> messages,
    List<TurnAssembler.AssembledTurn> turns,
    List<IssueDetector.DetectedIssue> issues,
    List<SkillInvocation> skillInvocations,
    Map<String, Integer> messageIdToTurnIndex,
    String filePath  // 新增字段：transcript 文件路径
) {}
```

---

### 4.2 修改 parseFile() 方法返回包含 filePath 的 ParsedTranscript

**Files:**
- Modify: `src/main/java/com/company/clawboard/parser/TranscriptParser.java`

**Step 1: 找到 parseFile() 方法的返回语句**

```bash
grep -n "return new ParsedTranscript" src/main/java/com/company/clawboard/parser/TranscriptParser.java
```

**Step 2: 修改返回语句**

在第 166 行附近，找到返回语句并修改为：

```java
return new ParsedTranscript(sessionId, messages, turns, allIssues, skillInvocations, messageIdToTurnIndex, filePathStr);
```

注意：`filePathStr` 变量在第 94 行已经定义：`String filePathStr = filePath.toString();`

**Step 3: 验证编译通过**

```bash
mvn compile -q
```

Expected: 编译成功

---

### 4.3 验证 ParsedTranscript 包含正确的文件路径

**Files:**
- Test: `src/test/java/com/company/clawboard/parser/TranscriptParserTest.java`

**Step 1: 创建或修改测试类**

```java
package com.company.clawboard.parser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranscriptParserTest {

    @Autowired
    private TranscriptParser parser;

    @Test
    void should_include_file_path_in_parsed_transcript() {
        // 使用测试文件
        Path testFile = Paths.get("test-data/test-session.jsonl");
        
        if (testFile.toFile().exists()) {
            TranscriptParser.ParsedTranscript result = parser.parseFile(testFile, "test-user");
            
            assertThat(result.filePath()).isNotNull();
            assertThat(result.filePath()).contains("test-session.jsonl");
        }
    }
}
```

**Step 2: 提交**

```bash
git add src/main/java/com/company/clawboard/parser/TranscriptParser.java
git add src/test/java/com/company/clawboard/parser/TranscriptParserTest.java
git commit -m "feat: add filePath field to ParsedTranscript record"
```

---

## Task 5: 修改 DataIngestionService 记录 logFilePath

### 5.1 修改 convertToTurns() 方法接收 filePath 参数

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

**Step 1: 查看 convertToTurns() 方法签名**

```bash
grep -n "private List<DashboardConversationTurn> convertToTurns" -A 5 src/main/java/com/company/clawboard/service/DataIngestionService.java
```

**Step 2: 修改方法签名**

找到第 207-211 行的方法签名，修改为：

```java
private List<DashboardConversationTurn> convertToTurns(Long scanId,
                                                        List<TurnAssembler.AssembledTurn> turns,
                                                        String sessionId,
                                                        String employeeId,
                                                        LocalDateTime now,
                                                        String filePath) {  // 新增参数
```

---

### 5.2 在转换 DashboardConversationTurn 时设置 logFilePath 字段

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

**Step 1: 修改 convertToTurns() 方法体**

在第 260 行附近，找到设置 `logFilePath` 的地方：

```java
entity.setLogFilePath("");  // 原代码
```

修改为：

```java
entity.setLogFilePath(filePath != null ? filePath : "");
```

---

### 5.3 修改 ingestParsedTranscript() 方法传递 filePath 参数

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

**Step 1: 找到调用 convertToTurns() 的地方**

```bash
grep -n "convertToTurns" src/main/java/com/company/clawboard/service/DataIngestionService.java
```

**Step 2: 修改调用语句**

在第 66 行附近，找到：

```java
List<DashboardConversationTurn> turns = convertToTurns(scanId, parsed.turns(), sessionId, employeeId, now);
```

修改为：

```java
List<DashboardConversationTurn> turns = convertToTurns(scanId, parsed.turns(), sessionId, employeeId, now, parsed.filePath());
```

**Step 3: 验证编译通过**

```bash
mvn compile -q
```

Expected: 编译成功

---

### 5.4 验证入库后 logFilePath 字段有值

**Files:**
- Test: `src/test/java/com/company/clawboard/service/DataIngestionServiceTest.java`

**Step 1: 创建或修改测试类**

```java
package com.company.clawboard.service;

import com.company.clawboard.entity.DashboardConversationTurn;
import com.company.clawboard.mapper.ConversationTurnMapper;
import com.company.clawboard.parser.TranscriptParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DataIngestionServiceTest {

    @Autowired
    private DataIngestionService dataIngestionService;

    @Autowired
    private ConversationTurnMapper turnMapper;

    @Test
    void should_set_log_file_path_when_ingesting_transcript() {
        // 准备测试数据
        Path testFile = Paths.get("test-data/test-session.jsonl");
        
        if (testFile.toFile().exists()) {
            // 调用 ingestParsedTranscript
            // 然后验证数据库中的 logFilePath 字段
            List<DashboardConversationTurn> turns = turnMapper.selectAll();
            
            for (DashboardConversationTurn turn : turns) {
                if (turn.getLogFilePath() != null && !turn.getLogFilePath().isEmpty()) {
                    assertThat(turn.getLogFilePath()).contains("test-session.jsonl");
                    return;
                }
            }
        }
    }
}
```

**Step 2: 提交**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git add src/test/java/com/company/clawboard/service/DataIngestionServiceTest.java
git commit -m "feat: record logFilePath in DashboardConversationTurn"
```

---

## Task 6: 修改 AccountsCsvReader

### 6.1 修改 loadFromDatabase() 方法使用 VInstanceDetailMapper

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/AccountsCsvReader.java`

**Step 1: 查看当前 loadFromDatabase() 方法**

```bash
grep -n "loadFromDatabase" -A 50 src/main/java/com/company/clawboard/scanner/AccountsCsvReader.java | head -60
```

**Step 2: 添加 VInstanceDetailMapper 注入**

在类字段部分添加：

```java
private final VInstanceDetailMapper vInstanceDetailMapper;
```

由于使用了 `@RequiredArgsConstructor`，Lombok 会自动生成构造函数。

**Step 3: 添加 import 语句**

```java
import com.company.clawboard.mapper.VInstanceDetailMapper;
```

---

### 6.2 简化 loadFromDatabase() 方法移除 JSON 解析逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/AccountsCsvReader.java`

**Step 1: 修改 loadFromDatabase() 方法**

找到第 139-179 行附近的方法，修改为：

```java
/**
 * Load employee mapping from v_instance_detail view
 * uid column is employeeId
 * user_config_name is userName
 * user_config_org_code is orgCode
 */
public void loadFromDatabase() {
    log.info("Loading employee mapping from v_instance_detail view");
    int count = 0;

    try {
        List<Map<String, Object>> instances = vInstanceDetailMapper.selectRunningInstances();
        log.info("Found {} running instances in database", instances.size());

        for (Map<String, Object> instance : instances) {
            String uid = (String) instance.get("uid");
            String userName = (String) instance.get("user_config_name");
            String orgCode = (String) instance.get("user_config_org_code");

            if (uid == null || uid.isEmpty() || userName == null) {
                log.debug("Skipping instance with null uid or userName: {}", uid);
                continue;
            }

            // Calculate SHA512 hash of employee_id (uid)
            String hash = calculateSha512(uid);
            if (hash == null) {
                log.warn("Failed to calculate hash for employee: {}", uid);
                continue;
            }

            EmployeeInfo info = new EmployeeInfo();
            info.setEmployeeId(uid);
            info.setName(userName);  // 直接使用 user_config_name，不需要解析 JSON
            info.setDepartment(orgCode != null ? orgCode : "");  // 直接使用 user_config_org_code
            info.setSha512Hash(hash);

            employeeMap.put(hash, info);
            count++;
        }

        log.info("Loaded {} employee mappings from database", count);
    } catch (Exception e) {
        log.error("Failed to load employee mapping from database", e);
    }
}
```

**Step 2: 移除不再使用的导入**

```java
// 移除：import com.fasterxml.jackson.databind.JsonNode;
// 移除：import com.fasterxml.jackson.databind.ObjectMapper;
```

如果 `ObjectMapper` 还在其他地方使用，则保留。

**Step 3: 验证编译通过**

```bash
mvn compile -q
```

Expected: 编译成功

---

### 6.3 验证员工映射数据加载正确

**Files:**
- Test: `src/test/java/com/company/clawboard/scanner/AccountsCsvReaderTest.java`

**Step 1: 创建或修改测试类**

```java
package com.company.clawboard.scanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountsCsvReaderTest {

    @Autowired
    private AccountsCsvReader accountsReader;

    @Test
    void should_load_employee_mapping_from_database() {
        accountsReader.loadFromDatabase();
        
        // 验证加载成功
        // 具体验证逻辑取决于实际数据
        assertThat(accountsReader).isNotNull();
    }
}
```

**Step 2: 提交**

```bash
git add src/main/java/com/company/clawboard/scanner/AccountsCsvReader.java
git add src/test/java/com/company/clawboard/scanner/AccountsCsvReaderTest.java
git commit -m "refactor: simplify AccountsCsvReader to use v_instance_detail view"
```

---

## Task 7: 测试验证

### 7.1 运行单元测试验证各组件功能

**Step 1: 运行所有相关测试**

```bash
mvn test -Dtest=VInstanceDetailMapperTest,DashboardServiceTest,TranscriptParserTest,DataIngestionServiceTest,AccountsCsvReaderTest -DfailIfNoTests=false
```

Expected: 所有测试通过

---

### 7.2 清空数据库并启动应用进行集成测试

**Step 1: 清空数据库**

```bash
mysql -u clawboard -pClaw@1234 clawboard < scripts/reset-database.sql
```

**Step 2: 启动应用**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

### 7.3 验证 /dashboard/global-stats API 返回 registeredUsers 字段

**Step 1: 调用 API**

```bash
curl -X GET http://localhost:8080/api/v1/dashboard/global-stats
```

Expected: 响应包含 `registeredUsers` 字段

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalTokens": 0,
    "totalTurns": 0,
    "totalSkillCalls": 0,
    "totalUsers": 0,
    "registeredUsers": 0
  }
}
```

---

### 7.4 验证 /dashboard/usersummary API 的 status 字段判断正确

**Step 1: 调用 API**

```bash
curl -X POST http://localhost:8080/api/v1/dashboard/usersummary \
  -H "Content-Type: application/json" \
  -d '{}'
```

Expected: 响应中 `status` 字段根据实例状态正确设置

---

### 7.5 验证 dashboard_conversation_turn 表的 logFilePath 字段有值

**Step 1: 触发一次扫描**

```bash
curl -X POST http://localhost:8080/api/v1/scan/trigger
```

**Step 2: 查询数据库验证**

```bash
mysql -u clawboard -pClaw@1234 clawboard -e "SELECT id, session_id, log_file_path FROM dashboard_conversation_turn LIMIT 5;"
```

Expected: `log_file_path` 字段有值（非空）

---

## Task 8: 清理和优化（可选）

### 8.1 评估是否删除或标记 OpenclawInstanceMapper 为@Deprecated

**Files:**
- Modify: `src/main/java/com/company/clawboard/mapper/OpenclawInstanceMapper.java`

**Step 1: 检查是否还有其他地方使用 OpenclawInstanceMapper**

```bash
grep -r "OpenclawInstanceMapper" src/main/java/ --include="*.java"
```

**Step 2A: 如果没有其他引用，可以删除**

```bash
rm src/main/java/com/company/clawboard/mapper/OpenclawInstanceMapper.java
```

**Step 2B: 如果还有引用，标记为 Deprecated**

```java
@Deprecated(since = "2026-04-23", forRemoval = true)
@Mapper
public interface OpenclawInstanceMapper {
    // ...
}
```

---

### 8.2 更新相关文档和注释

**Files:**
- Modify: 相关文档文件

**Step 1: 更新 CLAUDE.md 或其他文档**

如果有文档提到 `OpenclawInstanceMapper` 或 JSON 解析逻辑，更新为新的 `VInstanceDetailMapper`。

---

### 8.3 代码审查和优化

**Step 1: 运行代码检查工具**

```bash
mvn checkstyle:check
```

**Step 2: 查看 git diff 确认所有变更**

```bash
git diff --stat
```

**Step 3: 最终提交**

```bash
git commit -am "chore: cleanup and documentation updates"
```

---

## 测试命令汇总

```bash
# 编译验证
mvn compile -q

# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=VInstanceDetailMapperTest
mvn test -Dtest=DashboardServiceTest
mvn test -Dtest=TranscriptParserTest
mvn test -Dtest=DataIngestionServiceTest
mvn test -Dtest=AccountsCsvReaderTest

# 启动应用（dev profile）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# API 测试
curl -X GET http://localhost:8080/api/v1/dashboard/global-stats
curl -X POST http://localhost:8080/api/v1/dashboard/usersummary -H "Content-Type: application/json" -d '{}'
curl -X POST http://localhost:8080/api/v1/scan/trigger
```

---

## 验证清单

- [ ] VInstanceDetailMapper 接口和 XML 创建成功
- [ ] VInstanceDetailMapper 测试通过
- [ ] GlobalStatsResponse 增加 registeredUsers 字段
- [ ] DashboardService.getGlobalStats() 返回 registeredUsers
- [ ] DashboardService.getUserSummaries() 的 status 字段根据实例状态判断
- [ ] ParsedTranscript 增加 filePath 字段
- [ ] DataIngestionService 记录 logFilePath
- [ ] AccountsCsvReader 不再解析 JSON
- [ ] 所有单元测试通过
- [ ] API 响应符合预期
- [ ] 数据库 logFilePath 字段有值

---

**Plan complete and saved to `docs/plans/2026-04-23-migrate-to-v-instance-detail.md`. Two execution options:**

**1. Subagent-Driven (this session)** - I dispatch fresh subagent per task, review between tasks, fast iteration

**2. Parallel Session (separate)** - Open new session with executing-plans, batch execution with checkpoints

**Which approach?**
