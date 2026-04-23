# /turns/search 接口工号模糊匹配功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 `/api/v1/turns/search` 接口新增 `userIdLike` 参数,支持通过部分工号进行模糊匹配查询(如输入 "181" 查询所有包含该数字的工号记录)。

**Architecture:** 在现有精确匹配基础上,通过 MyBatis `<choose>` 标签实现条件分支逻辑:`userId` 优先用于精确匹配,为空时检查 `userIdLike` 执行模糊匹配(`LIKE '%xxx%'`)。保持向后兼容,不影响现有调用方。

**Tech Stack:** Java 17, Spring Boot 3.2.5, MyBatis 3.0.3, PageHelper 2.1.0, MySQL 5.7, JUnit 5, Mockito

---

## 文件结构概览

### 需要修改的文件

1. **DTO 层**
   - `src/main/java/com/company/clawboard/dto/TurnSearchRequest.java` - 新增 `userIdLike` 字段

2. **Mapper 接口层**
   - `src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java` - 方法签名增加参数

3. **Mapper XML 配置**
   - `src/main/resources/mapper/ConversationTurnMapper.xml` - 修改 `selectTurnsWithFilters` 查询逻辑

4. **API 文档**
   - `docs/api/API接口文档v1.2.md` - 补充参数说明和示例

5. **测试文件**
   - `src/test/java/com/company/clawboard/service/TurnErrorServiceTest.java` - 新增测试用例

### 文件依赖关系

```
TurnSearchRequest (DTO) 
    ↓ 传递给
ConversationTurnMapper.selectTurnsWithFilters (接口)
    ↓ 实现于
ConversationTurnMapper.xml (SQL)
    ↓ 被调用
TurnErrorService.searchTurns (业务逻辑)
    ↓ 暴露为
TurnErrorController.searchTurns (REST API)
```

---

## 前置准备

### 环境确认

- [ ] 确认 JDK 17 已安装: `java -version` (路径: D:\Program\JDK\jdk-17.0.18)
- [ ] 确认 Maven 已安装: `mvn -version` (路径: D:\Program\Maven\apache-maven-3.9.15)
- [ ] 确认 MySQL 运行中: `mysql -h 127.0.0.1 -P 3306 -u clawboard -pClqc@1234 -e "SELECT 1"`
- [ ] 确认当前分支是 worktree (由 brainstorming 创建)

### 阅读相关文档

- [ ] 阅读 OpenSpec proposal: `openspec/changes/add-userid-fuzzy-search/proposal.md`
- [ ] 阅读设计文档: `openspec/changes/add-userid-fuzzy-search/design.md`
- [ ] 阅读规格说明: `openspec/changes/add-userid-fuzzy-search/specs/userid-fuzzy-search/spec.md`
- [ ] 查看当前 API 文档: `docs/api/API接口文档v1.2.md` (第278-299行)

---

## Task 1: DTO 层修改 - 新增 userIdLike 字段

### Files:
- Modify: `src/main/java/com/company/clawboard/dto/TurnSearchRequest.java`

### 步骤

- [ ] **Step 1: 查看当前 TurnSearchRequest 结构**

读取文件了解现有字段:

```bash
cat src/main/java/com/company/clawboard/dto/TurnSearchRequest.java
```

预期看到:
```java
public class TurnSearchRequest {
    private String userId;     // 工号（employee_id）精确匹配
    private String startTime;  // 开始时间，格式 YYYY-MM-DD HH:mm:ss
    private String endTime;    // 结束时间，格式 YYYY-MM-DD HH:mm:ss
    private String skillId;    // 技能ID精确匹配
    private Integer page;      // 默认 1
    private Integer pageSize;  // 默认 10
    
    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
}
```

- [ ] **Step 2: 添加 userIdLike 字段**

在 `userId` 字段下方新增一行:

```java
public class TurnSearchRequest {
    private String userId;      // 工号（employee_id）精确匹配
    private String userIdLike;  // 工号模糊匹配（对应 employee_id LIKE '%xxx%'）
    private String startTime;   // 开始时间，格式 YYYY-MM-DD HH:mm:ss
    private String endTime;     // 结束时间，格式 YYYY-MM-DD HH:mm:ss
    private String skillId;     // 技能ID精确匹配
    private Integer page;       // 默认 1
    private Integer pageSize;   // 默认 10
    
    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
}
```

使用 Lombok 的 `@Data` 注解会自动生成 getter/setter,无需手动添加。

- [ ] **Step 3: 验证编译通过**

```bash
cd d:\workplace\github\clawboard
mvn clean compile -DskipTests
```

预期输出: `BUILD SUCCESS`

如果失败,检查语法错误并修正。

- [ ] **Step 4: 提交更改**

```bash
git add src/main/java/com/company/clawboard/dto/TurnSearchRequest.java
git commit -m "feat: add userIdLike field to TurnSearchRequest for fuzzy matching"
```

---

## Task 2: Mapper 接口修改 - 增加方法参数

### Files:
- Modify: `src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java`

### 步骤

- [ ] **Step 1: 查看当前 selectTurnsWithFilters 方法签名**

```bash
grep -A 5 "selectTurnsWithFilters" src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java
```

预期看到:
```java
List<DashboardConversationTurn> selectTurnsWithFilters(
    @Param("userId") String userId,
    @Param("startTime") String startTime,
    @Param("endTime") String endTime
);
```

- [ ] **Step 2: 添加 userIdLike 参数**

修改方法签名为:

```java
/**
 * 根据条件查询非系统轮次（支持分页）
 * @param userId 工号（employee_id）精确匹配
 * @param userIdLike 工号模糊匹配（employee_id LIKE '%xxx%'），userId 优先
 * @param startTime 开始时间，格式 YYYY-MM-DD HH:mm:ss
 * @param endTime 结束时间，格式 YYYY-MM-DD HH:mm:ss
 * @return 对话轮次列表
 */
List<DashboardConversationTurn> selectTurnsWithFilters(
    @Param("userId") String userId,
    @Param("userIdLike") String userIdLike,
    @Param("startTime") String startTime,
    @Param("endTime") String endTime
);
```

注意:
- 参数顺序: `userId`, `userIdLike`, `startTime`, `endTime`
- 更新 JavaDoc 注释,说明 `userIdLike` 用途和优先级规则

- [ ] **Step 3: 验证编译通过**

```bash
mvn clean compile -DskipTests
```

预期输出: `BUILD SUCCESS`

- [ ] **Step 4: 提交更改**

```bash
git add src/main/java/com/company/clawboard/mapper/ConversationTurnMapper.java
git commit -m "feat: add userIdLike parameter to ConversationTurnMapper.selectTurnsWithFilters"
```

---

## Task 3: Mapper XML 修改 - 实现优先级逻辑

### Files:
- Modify: `src/main/resources/mapper/ConversationTurnMapper.xml`

### 步骤

- [ ] **Step 1: 查看当前 selectTurnsWithFilters SQL**

读取文件第111-124行附近:

```bash
sed -n '111,124p' src/main/resources/mapper/ConversationTurnMapper.xml
```

预期看到:
```xml
<select id="selectTurnsWithFilters" resultMap="BaseResultMap">
    SELECT * FROM dashboard_conversation_turn 
    WHERE (system_turn = 0 OR system_turn IS NULL)
    <if test="userId != null and userId != ''">
        AND employee_id = #{userId}
    </if>
    <if test="startTime != null and startTime != ''">
        AND start_time &gt;= #{startTime}
    </if>
    <if test="endTime != null and endTime != ''">
        AND start_time &lt;= #{endTime}
    </if>
    ORDER BY start_time DESC
</select>
```

- [ ] **Step 2: 替换 userId 条件为 choose 标签**

将原有的 `<if test="userId ...">` 块替换为 `<choose>` 结构:

```xml
<select id="selectTurnsWithFilters" resultMap="BaseResultMap">
    SELECT * FROM dashboard_conversation_turn 
    WHERE (system_turn = 0 OR system_turn IS NULL)
    
    <choose>
        <when test="userId != null and userId != ''">
            AND employee_id = #{userId}
        </when>
        <when test="userIdLike != null and userIdLike != ''">
            AND employee_id LIKE CONCAT('%', #{userIdLike}, '%')
        </when>
    </choose>
    
    <if test="startTime != null and startTime != ''">
        AND start_time &gt;= #{startTime}
    </if>
    <if test="endTime != null and endTime != ''">
        AND start_time &lt;= #{endTime}
    </if>
    ORDER BY start_time DESC
</select>
```

关键变化:
- 使用 `<choose>` 确保只执行一个分支
- 第一个 `<when>`: 精确匹配 `employee_id = #{userId}`
- 第二个 `<when>`: 模糊匹配 `employee_id LIKE CONCAT('%', #{userIdLike}, '%')`
- 保留 `startTime` 和 `endTime` 的原有逻辑不变

- [ ] **Step 3: 验证 XML 语法**

MyBatis XML 常见错误:
- 标签未闭合
- 特殊字符未转义(`>` 写成 `&gt;`, `<` 写成 `&lt;`)
- 参数名拼写错误

检查修改后的 XML 文件格式正确。

- [ ] **Step 4: 编译验证**

```bash
mvn clean compile -DskipTests
```

预期输出: `BUILD SUCCESS`

- [ ] **Step 5: 提交更改**

```bash
git add src/main/resources/mapper/ConversationTurnMapper.xml
git commit -m "feat: implement userId/userIdLike priority logic using MyBatis choose tag"
```

---

## Task 4: API 文档更新

### Files:
- Modify: `docs/api/API接口文档v1.2.md`

### 步骤

- [ ] **Step 1: 定位到 /turns/search 接口文档**

打开文件,找到第278-299行附近的请求体参数表。

- [ ] **Step 2: 在参数表中新增 userIdLike 行**

原表格(第292-299行):

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `userId` | String | 否 | **工号精确匹配**（对应 employee_id） |
| `startTime` | String | 否 | **开始时间，格式 `YYYY-MM-DD HH:mm:ss`** |
| `endTime` | String | 否 | **结束时间，格式 `YYYY-MM-DD HH:mm:ss`** |
| `skillId` | String | 否 | 技能 ID 精确匹配，取值来自接口 1 |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |

修改后:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `userId` | String | 否 | **工号精确匹配**（对应 employee_id），与 userIdLike 二选一 |
| `userIdLike` | String | 否 | **工号模糊匹配**（对应 employee_id LIKE '%xxx%'），与 userId 二选一，userId 优先 |
| `startTime` | String | 否 | **开始时间，格式 `YYYY-MM-DD HH:mm:ss`** |
| `endTime` | String | 否 | **结束时间，格式 `YYYY-MM-DD HH:mm:ss`** |
| `skillId` | String | 否 | 技能 ID 精确匹配，取值来自接口 1 |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |

- [ ] **Step 3: 更新请求体示例**

原示例(第282-290行):

```json
{
  "userId": "18101142",
  "startTime": "2026-04-21 00:00:00",
  "endTime": "2026-04-21 23:59:59",
  "skillId": "pptx",
  "page": 1,
  "pageSize": 10
}
```

在下方添加模糊匹配示例:

```json
// 模糊匹配示例
{
  "userIdLike": "181",
  "startTime": "2026-04-21 00:00:00",
  "endTime": "2026-04-21 23:59:59",
  "page": 1,
  "pageSize": 10
}
```

- [ ] **Step 4: 验证文档格式**

检查 Markdown 表格对齐,JSON 示例语法正确。

- [ ] **Step 5: 提交更改**

```bash
git add docs/api/API接口文档v1.2.md
git commit -m "docs: add userIdLike parameter documentation to /turns/search API"
```

---

## Task 5: 单元测试编写

### Files:
- Modify: `src/test/java/com/company/clawboard/service/TurnErrorServiceTest.java`

### 步骤

- [ ] **Step 1: 查看现有测试结构**

```bash
cat src/test/java/com/company/clawboard/service/TurnErrorServiceTest.java
```

了解现有的测试方法和 Mock 设置。

- [ ] **Step 2: 添加测试用例 - 仅传入 userIdLike 时执行模糊匹配**

在测试类中添加新方法:

```java
@Test
@DisplayName("Should use fuzzy matching when only userIdLike is provided")
void testSearchTurns_WithUserIdLikeOnly() {
    // Given
    TurnSearchRequest request = new TurnSearchRequest();
    request.setUserIdLike("181");
    request.setPage(1);
    request.setPageSize(10);
    
    List<DashboardConversationTurn> mockTurns = Collections.emptyList();
    when(turnMapper.selectTurnsWithFilters(null, "181", null, null))
        .thenReturn(mockTurns);
    
    // When
    PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
    
    // Then
    verify(turnMapper).selectTurnsWithFilters(null, "181", null, null);
    assertThat(result).isNotNull();
}
```

- [ ] **Step 3: 添加测试用例 - 同时传入 userId 和 userIdLike 时使用精确匹配**

```java
@Test
@DisplayName("Should prefer exact match when both userId and userIdLike are provided")
void testSearchTurns_UserIdTakesPriorityOverUserIdLike() {
    // Given
    TurnSearchRequest request = new TurnSearchRequest();
    request.setUserId("18101142");
    request.setUserIdLike("181");
    request.setPage(1);
    request.setPageSize(10);
    
    List<DashboardConversationTurn> mockTurns = Collections.emptyList();
    when(turnMapper.selectTurnsWithFilters("18101142", "181", null, null))
        .thenReturn(mockTurns);
    
    // When
    PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
    
    // Then
    // userId 应该被使用,userIdLike 被忽略(但会传递到 Mapper)
    verify(turnMapper).selectTurnsWithFilters("18101142", "181", null, null);
    assertThat(result).isNotNull();
}
```

- [ ] **Step 4: 添加测试用例 - userIdLike 为空字符串时不添加过滤条件**

```java
@Test
@DisplayName("Should not apply filter when userIdLike is empty string")
void testSearchTurns_EmptyUserIdLike() {
    // Given
    TurnSearchRequest request = new TurnSearchRequest();
    request.setUserIdLike("");
    request.setPage(1);
    request.setPageSize(10);
    
    List<DashboardConversationTurn> mockTurns = Collections.emptyList();
    when(turnMapper.selectTurnsWithFilters(null, "", null, null))
        .thenReturn(mockTurns);
    
    // When
    PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
    
    // Then
    verify(turnMapper).selectTurnsWithFilters(null, "", null, null);
    assertThat(result).isNotNull();
}
```

- [ ] **Step 5: 添加测试用例 - userIdLike 与其他筛选条件组合**

```java
@Test
@DisplayName("Should combine userIdLike with other filters")
void testSearchTurns_UserIdLikeWithTimeRangeAndSkill() {
    // Given
    TurnSearchRequest request = new TurnSearchRequest();
    request.setUserIdLike("181");
    request.setStartTime("2026-04-21 00:00:00");
    request.setEndTime("2026-04-21 23:59:59");
    request.setSkillId("pptx");
    request.setPage(1);
    request.setPageSize(10);
    
    List<DashboardConversationTurn> mockTurns = Collections.emptyList();
    when(turnMapper.selectTurnsWithFilters(null, "181", "2026-04-21 00:00:00", "2026-04-21 23:59:59"))
        .thenReturn(mockTurns);
    
    // When
    PageResult<TurnSearchItem> result = turnErrorService.searchTurns(request);
    
    // Then
    verify(turnMapper).selectTurnsWithFilters(null, "181", "2026-04-21 00:00:00", "2026-04-21 23:59:59");
    assertThat(result).isNotNull();
}
```

- [ ] **Step 6: 运行单元测试**

```bash
mvn test -Dtest=TurnErrorServiceTest
```

预期输出: 所有测试通过 (包括新增的4个测试)

如果失败,检查:
- Mock 设置是否正确
- 参数顺序是否匹配
- 断言逻辑是否准确

- [ ] **Step 7: 提交更改**

```bash
git add src/test/java/com/company/clawboard/service/TurnErrorServiceTest.java
git commit -m "test: add unit tests for userIdLike fuzzy matching functionality"
```

---

## Task 6: 集成测试 - 端到端验证

### 前置条件

- [ ] 清空数据库并重新扫描生成测试数据

```bash
# 清空数据库
mysql -h 127.0.0.1 -P 3306 -u clawboard -pClqc@1234 clawboard < scripts/reset-database.sql

# 启动应用(dev profile)
# 在 IDE 中运行 ClawboardApplication,或使用命令:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 触发扫描(假设已有扫描接口)
# 等待扫描完成,生成测试数据
```

### 步骤

- [ ] **Step 1: 测试精确匹配(userId)**

使用 curl 或 Postman 发送请求:

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "18101142",
    "page": 1,
    "pageSize": 10
  }'
```

预期结果:
- 返回的列表中所有记录的 userName 对应的 employee_id 都是 "18101142"
- 如果该工号不存在,返回空列表

- [ ] **Step 2: 测试模糊匹配(userIdLike)**

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userIdLike": "181",
    "page": 1,
    "pageSize": 10
  }'
```

预期结果:
- 返回的列表中所有记录的 userName 对应的 employee_id 都包含 "181"(如 18101142, 18102233, 20181001 等)
- 可能返回多条记录

- [ ] **Step 3: 测试优先级(同时传入 userId 和 userIdLike)**

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "18101142",
    "userIdLike": "181",
    "page": 1,
    "pageSize": 10
  }'
```

预期结果:
- 行为与 Step 1 相同(使用精确匹配)
- 仅返回 employee_id = "18101142" 的记录
- userIdLike 被忽略

- [ ] **Step 4: 测试组合筛选(userIdLike + 时间范围 + skillId)**

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userIdLike": "181",
    "startTime": "2026-04-21 00:00:00",
    "endTime": "2026-04-21 23:59:59",
    "skillId": "pptx",
    "page": 1,
    "pageSize": 10
  }'
```

预期结果:
- 返回满足所有条件的记录:
  - employee_id 包含 "181"
  - start_time 在指定时间范围内
  - 使用了 pptx 技能

- [ ] **Step 5: 验证分页功能**

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userIdLike": "181",
    "page": 1,
    "pageSize": 5
  }'
```

预期结果:
- 响应中包含 `total`, `page`, `pageSize` 字段
- `total` 表示总记录数
- `list` 数组长度 ≤ 5
- 修改 `page` 为 2,应返回下一页数据

- [ ] **Step 6: 测试边界情况 - 特殊字符**

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userIdLike": "181%",
    "page": 1,
    "pageSize": 10
  }'
```

预期结果:
- `%` 被视为普通字符,不是 SQL 通配符
- 返回 employee_id 包含字面量 "181%" 的记录(通常为空)
- 不应报错或执行恶意 SQL

- [ ] **Step 7: 测试边界情况 - SQL 注入尝试**

```bash
curl -X POST http://localhost:8080/api/v1/turns/search \
  -H "Content-Type: application/json" \
  -d '{
    "userIdLike": "'\''; DROP TABLE dashboard_conversation_turn; --",
    "page": 1,
    "pageSize": 10
  }'
```

预期结果:
- 系统将该字符串作为普通文本处理
- 不会执行 DROP TABLE 语句
- 返回空列表或包含该字面量的记录
- 数据库表未被删除(可再次查询验证)

- [ ] **Step 8: 记录测试结果**

将所有测试的请求、响应、预期vs实际结果记录到测试报告中。

---

## Task 7: 代码审查与优化

### 步骤

- [ ] **Step 1: 检查代码规范**

逐项检查:
- [ ] Lombok 注解使用正确(`@Data` 自动生成 getter/setter)
- [ ] 命名符合驼峰规范(`userIdLike` 而非 `user_id_like`)
- [ ] 注释完整且准确(JavaDoc, 字段注释)
- [ ] 无魔法数字或硬编码字符串

- [ ] **Step 2: 确认 SQL 注入防护**

检查 `ConversationTurnMapper.xml`:
- [ ] 使用 `#{userIdLike}` 而非 `${userIdLike}`
- [ ] 使用 `CONCAT('%', #{userIdLike}, '%')` 而非字符串拼接
- [ ] 无动态 SQL 拼接风险

- [ ] **Step 3: 检查冗余代码**

- [ ] 无未使用的 import 语句
- [ ] 无重复的代码逻辑
- [ ] 无注释掉的废弃代码

- [ ] **Step 4: 验证日志记录(可选)**

根据项目规范,检查是否需要添加日志:
```java
log.debug("Searching turns with userId={}, userIdLike={}", userId, userIdLike);
```

如果项目要求 DEBUG 级别日志记录查询参数,则添加。

- [ ] **Step 5: 运行静态代码分析(可选)**

```bash
mvn checkstyle:check
```

修复任何警告或错误。

---

## Task 8: 文档与交付

### 步骤

- [ ] **Step 1: 确认 OpenSpec artifacts 完整**

检查以下文件存在且内容正确:
- [ ] `openspec/changes/add-userid-fuzzy-search/proposal.md`
- [ ] `openspec/changes/add-userid-fuzzy-search/design.md`
- [ ] `openspec/changes/add-userid-fuzzy-search/specs/userid-fuzzy-search/spec.md`
- [ ] `openspec/changes/add-userid-fuzzy-search/tasks.md`

- [ ] **Step 2: 更新 CLAUDE.md(如需要)**

如果项目约定在 CLAUDE.md 中记录重要变更,添加简短说明:

```markdown
## Recent Changes

### 2026-04-23: Add userIdLike fuzzy matching to /turns/search
- New optional parameter `userIdLike` for fuzzy employee_id matching
- Priority: userId (exact) > userIdLike (fuzzy)
- Implementation: MyBatis <choose> tag in ConversationTurnMapper.xml
```

- [ ] **Step 3: 准备发布说明**

创建发布说明文档 `docs/releases/2026-04-23-userid-fuzzy-search.md`:

```markdown
# Release: userIdLike Fuzzy Matching

## Date
2026-04-23

## Summary
Added optional `userIdLike` parameter to `/api/v1/turns/search` endpoint for fuzzy employee ID matching.

## Changes
- **API**: New optional parameter `userIdLike` in `/turns/search` request body
- **Behavior**: When `userIdLike` is provided, performs LIKE query on employee_id field
- **Priority**: `userId` (exact match) takes precedence over `userIdLike` (fuzzy match)
- **Backward Compatibility**: Fully compatible - existing calls unaffected

## Example Usage
```json
{
  "userIdLike": "181",
  "page": 1,
  "pageSize": 10
}
```

## Notes
- Fuzzy matching uses `LIKE '%xxx%'` which may be slower on large datasets
- Consider frontend validation to require minimum 2-3 characters
```

- [ ] **Step 4: 通知前端团队**

通过邮件或即时通讯工具通知前端开发者:

```
主题: API 变更通知 - /turns/search 接口新增 userIdLike 参数

各位前端同事,

后端已在 /api/v1/turns/search 接口中新增可选参数 userIdLike,支持工号模糊匹配。

主要特性:
- 参数名: userIdLike (String, 可选)
- 功能: 对 employee_id 进行模糊匹配(LIKE '%xxx%')
- 优先级: userId(精确) > userIdLike(模糊)
- 向后兼容: 不影响现有调用

示例:
{
  "userIdLike": "181",
  "page": 1,
  "pageSize": 10
}

详细文档见: docs/api/API接口文档v1.2.md

如有疑问请联系后端团队。
```

- [ ] **Step 5: 最终提交**

```bash
git add .
git commit -m "chore: complete userIdLike fuzzy matching feature implementation"
```

---

## 验收标准

所有任务完成后,应满足以下条件:

1. ✅ 代码编译通过,无语法错误
2. ✅ 单元测试全部通过(至少4个新增测试)
3. ✅ 集成测试验证所有场景(精确匹配、模糊匹配、优先级、组合筛选、边界情况)
4. ✅ API 文档已更新,包含参数说明和示例
5. ✅ OpenSpec artifacts 完整
6. ✅ 向后兼容,现有调用不受影响
7. ✅ SQL 注入防护措施到位
8. ✅ 代码符合项目规范

---

## 回滚计划

如果发现严重问题需要回滚:

```bash
# 回滚到最后一次提交前
git reset --hard HEAD~1

# 或者回滚到特定 commit
git reset --hard <commit-hash>

# 重启应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

由于 `userIdLike` 是可选参数,不回滚也不会影响现有功能,只是新功能不可用。

---

## 后续优化建议(非本次范围)

1. **性能优化**: 如果数据量达到百万级,考虑:
   - 添加 `employee_id` 前缀索引
   - 使用全文索引(FULLTEXT INDEX)
   - 缓存热门查询结果

2. **前端优化**: 
   - 限制最小字符数(至少2-3个字符)
   - 添加防抖(debounce)避免频繁请求
   - 显示匹配结果数量提示

3. **监控告警**:
   - 记录慢查询日志
   - 监控模糊匹配使用频率
   - 设置超时阈值

---

**Plan Complete.** Ready for implementation via subagent-driven-development or executing-plans.
