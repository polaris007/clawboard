# 扫描文件记录与统计逻辑优化实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复扫描文件记录不准确和统计逻辑错误，确保 `java-scanned-files-scan-X.txt` 只包含实际处理的文件，且 `files_processed + files_skipped + files_error = files_total`

**Architecture:** 采用混合方案：在 ScanOrchestrator 层前置检查 mtime（避免解析开销），DataIngestionService 返回 IngestionResult（用于精确统计），统一状态管理确保数据一致性

**Tech Stack:** Java 17, Spring Boot, MyBatis, Lombok

---

## 文件结构映射

### 新增文件
- `src/main/java/com/company/clawboard/service/IngestionStatus.java` - 入库状态枚举
- `src/main/java/com/company/clawboard/service/IngestionResult.java` - 入库结果记录类

### 修改文件
- `src/main/java/com/company/clawboard/service/DataIngestionService.java`
  - 修改 `ingestParsedTranscript` 方法签名和实现
  - 返回 `IngestionResult` 而非 void
  
- `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`
  - 添加前置 mtime 检查逻辑
  - 根据 `IngestionResult` 分类统计
  - 修正 `usersScanned` 计算
  - 添加统计等式验证

### 测试文件（可选）
- `src/test/java/com/company/clawboard/service/DataIngestionServiceTest.java` - 单元测试

---

## Phase 1: 基础数据结构

### Task 1: 创建 IngestionStatus 枚举

**Files:**
- Create: `src/main/java/com/company/clawboard/service/IngestionStatus.java`

- [ ] **Step 1: 创建枚举文件**

```java
package com.company.clawboard.service;

/**
 * 数据入库状态枚举
 */
public enum IngestionStatus {
    /**
     * 成功处理（读取并入库）
     */
    PROCESSED,
    
    /**
     * 因 mtime 未变化而跳过
     */
    SKIPPED_MTIME,
    
    /**
     * 文件为空或无有效数据
     */
    SKIPPED_EMPTY,
    
    /**
     * 处理失败
     */
    FAILED
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/service/IngestionStatus.java
git commit -m "feat: add IngestionStatus enum for scan processing states"
```

---

### Task 2: 创建 IngestionResult 记录类

**Files:**
- Create: `src/main/java/com/company/clawboard/service/IngestionResult.java`

- [ ] **Step 1: 创建记录类文件**

```java
package com.company.clawboard.service;

/**
 * 数据入库结果
 * 
 * @param status 处理状态
 * @param messageCount 消息数量
 * @param turnCount 对话轮次数量
 * @param issueCount 问题数量
 * @param skillCount Skill 调用数量
 * @param errorMessage 错误信息（如果有）
 */
public record IngestionResult(
    IngestionStatus status,
    int messageCount,
    int turnCount,
    int issueCount,
    int skillCount,
    String errorMessage
) {
    /**
     * 创建成功结果
     */
    public static IngestionResult success(int messageCount, int turnCount, 
                                          int issueCount, int skillCount) {
        return new IngestionResult(IngestionStatus.PROCESSED, messageCount, 
                                   turnCount, issueCount, skillCount, null);
    }
    
    /**
     * 创建跳过结果
     */
    public static IngestionResult skipped(IngestionStatus status, String reason) {
        return new IngestionResult(status, 0, 0, 0, 0, reason);
    }
    
    /**
     * 创建失败结果
     */
    public static IngestionResult failed(String errorMessage) {
        return new IngestionResult(IngestionStatus.FAILED, 0, 0, 0, 0, errorMessage);
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/clawboard/service/IngestionResult.java
git commit -m "feat: add IngestionResult record for scan processing results"
```

---

## Phase 2: 修改 DataIngestionService

### Task 3: 修改 ingestParsedTranscript 方法签名

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 添加 import**

在文件顶部添加：
```java
import com.company.clawboard.service.IngestionStatus;
import com.company.clawboard.service.IngestionResult;
```

- [ ] **Step 2: 修改方法签名**

找到第 61 行：
```java
// 当前
@Transactional
public void ingestParsedTranscript(Long scanId, String employeeId, TranscriptParser.ParsedTranscript parsed) {

// 修改为
@Transactional
public IngestionResult ingestParsedTranscript(Long scanId, String employeeId, TranscriptParser.ParsedTranscript parsed) {
```

- [ ] **Step 3: 修改早期返回逻辑**

找到第 62-65 行：
```java
// 当前
if (parsed == null || parsed.sessionId() == null) {
    log.warn("Cannot ingest null or invalid parsed transcript");
    return;
}

// 修改为
if (parsed == null || parsed.sessionId() == null) {
    log.warn("Cannot ingest null or invalid parsed transcript");
    return IngestionResult.skipped(IngestionStatus.SKIPPED_EMPTY, "Invalid parsed transcript");
}
```

- [ ] **Step 4: 修改 mtime 检查返回**

找到第 80-82 行：
```java
// 当前
if (progress != null && fileMtime <= progress.getFileMtime()) {
    log.debug("File not modified (mtime: {}), skipping session {}", fileMtime, sessionId);
    return;
}

// 修改为
if (progress != null && fileMtime <= progress.getFileMtime()) {
    log.debug("File not modified (mtime: {}), skipping session {}", fileMtime, sessionId);
    return IngestionResult.skipped(IngestionStatus.SKIPPED_MTIME, null);
}
```

- [ ] **Step 5: 编译验证（预期失败）**

Run: `mvn compile -DskipTests`
Expected: COMPILATION ERROR（因为方法末尾缺少 return 语句）

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "refactor: change ingestParsedTranscript to return IngestionResult"
```

---

### Task 4: 添加成功返回逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/service/DataIngestionService.java`

- [ ] **Step 1: 找到方法末尾**

定位到 `updateSessionSummary` 调用之后（约第 208 行）

- [ ] **Step 2: 添加成功返回**

在 `updateSessionSummary` 调用后添加：
```java
// ✅ 收集本次扫描的小时信息（用于增量聚合）
collectHoursFromMessages(parsed.messages());

// ✅ 返回成功结果
return IngestionResult.success(
    parsed.messages().size(),
    parsed.turns().size(),
    parsed.issues().size(),
    parsed.skillInvocations().size()
);
```

- [ ] **Step 3: 添加异常捕获和失败返回**

找到 try-catch 块，修改 catch 部分：
```java
} catch (Exception e) {
    log.error("Failed to ingest transcript for session {}", sessionId, e);
    return IngestionResult.failed(e.getMessage());
}
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/service/DataIngestionService.java
git commit -m "feat: return IngestionResult with processing statistics"
```

---

## Phase 3: 修改 ScanOrchestrator

### Task 5: 添加前置 mtime 检查

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

- [ ] **Step 1: 添加 import**

在文件顶部确认已有：
```java
import com.company.clawboard.service.IngestionStatus;
import com.company.clawboard.service.IngestionResult;
```

- [ ] **Step 2: 找到文件处理循环**

定位到第 358 行附近：
```java
for (Path jsonlFile : jsonlFiles) {
    try {
        log.debug("Processing file: {} (employee: {})", jsonlFile.getFileName(), resolvedEmployeeId);
```

- [ ] **Step 3: 在解析前添加 mtime 检查**

在 `log.debug` 之前插入：
```java
// ✅ 前置检查：文件 mtime
boolean shouldSkip = false;
String skipReason = null;

try {
    long fileMtime = Files.getLastModifiedTime(jsonlFile).toMillis();
    var progress = scanProgressMapper.selectByEmployeeAndFile(resolvedEmployeeId, jsonlFile.toString());
    
    if (progress != null && fileMtime <= progress.getFileMtime()) {
        shouldSkip = true;
        skipReason = "File not modified (mtime: " + fileMtime + ")";
        log.debug("Skipping unmodified file: {}", jsonlFile.getFileName());
    }
} catch (Exception e) {
    log.warn("Failed to check file mtime for {}, proceeding with parse", jsonlFile.getFileName(), e);
}

if (shouldSkip) {
    // ✅ 计入 skipped，不解析文件
    skippedFiles.put(jsonlFile.toString(), skipReason);
    skippedFilesCount++;
    continue;
}
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java
git commit -m "feat: add pre-parse mtime check to skip unmodified files"
```

---

### Task 6: 修改入库调用和统计逻辑

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

- [ ] **Step 1: 修改入库调用**

找到第 375 行附近：
```java
// 当前
dataIngestionService.ingestParsedTranscript(scanId, resolvedEmployeeId, parsed);

// 修改为
var result = dataIngestionService.ingestParsedTranscript(scanId, resolvedEmployeeId, parsed);
```

- [ ] **Step 2: 添加结果处理逻辑**

在入库调用后替换原有的统计代码（第 416-428 行）：
```java
if (result.status() == IngestionStatus.PROCESSED) {
    // ✅ 只有实际处理的文件才记录到 scannedFilePaths
    String relativePath = basePathObj.relativize(jsonlFile).toString();
    scannedFilePaths.add(relativePath);
    
    // 累加统计数据
    totalMessages += result.messageCount();
    totalTurns += result.turnCount();
    totalIssues += result.issueCount();
    totalSkills += result.skillCount();
    processedFiles++;
    
    log.debug("Parsed file: {} messages, {} turns, {} issues, {} skills",
        result.messageCount(), result.turnCount(), result.issueCount(), result.skillCount());
    
} else if (result.status() == IngestionStatus.SKIPPED_MTIME) {
    // ✅ mtime 未变化（兜底检查触发）
    skippedFiles.put(jsonlFile.toString(), "File not modified (checked in ingestion service)");
    skippedFilesCount++;
    
} else if (result.status() == IngestionStatus.FAILED) {
    // ✅ 处理失败
    String errorMsg = "Ingestion failed: " + result.errorMessage();
    skippedFiles.put(jsonlFile.toString(), errorMsg);
    errorFiles++;
    log.error("{}", errorMsg);
}
```

- [ ] **Step 3: 删除旧的统计代码**

删除原来的：
```java
int msgCount = parsed.messages().size();
int turnCount = parsed.turns().size();
int issueCount = parsed.issues().size();
int skillCount = parsed.skillInvocations().size();

totalMessages += msgCount;
totalTurns += turnCount;
totalIssues += issueCount;
totalSkills += skillCount;
processedFiles++;
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java
git commit -m "feat: process IngestionResult and update statistics accordingly"
```

---

### Task 7: 修正 usersScanned 计算

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

- [ ] **Step 1: 找到统计收集部分**

定位到第 191-192 行：
```java
// Update scan history with results
history.setDirsScanned(users.size());
```

- [ ] **Step 2: 添加实际用户数计算**

在第 191 行之前插入：
```java
// ✅ 计算实际扫描的用户数（有 processed 或 skipped 文件的用户）
int actualUsersScanned = 0;
for (var entry : futures.entrySet()) {
    try {
        UserScanResult result = entry.getValue().get(30, TimeUnit.MINUTES);
        if (result.filesProcessed() > 0 || result.filesSkipped() > 0 || result.filesError() > 0) {
            actualUsersScanned++;
        }
    } catch (Exception e) {
        log.error("Failed to get result for user {}", entry.getKey(), e);
    }
}
```

- [ ] **Step 3: 修改设置逻辑**

将：
```java
history.setDirsScanned(users.size());
```

修改为：
```java
history.setUsersScanned(actualUsersScanned);
history.setDirsScanned(actualUsersScanned);  // 每个用户对应一个目录
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java
git commit -m "fix: calculate actual users scanned instead of total users found"
```

---

### Task 8: 添加统计等式验证

**Files:**
- Modify: `src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java`

- [ ] **Step 1: 找到 finishScan 调用前**

定位到第 202 行附近：
```java
finishScan(scanId, history, "completed", startTime, null);
```

- [ ] **Step 2: 添加验证逻辑**

在 `finishScan` 调用之前插入：
```java
// ✅ 验证统计等式
int calculatedTotal = processedFiles + skippedFilesCount + errorFiles;
if (calculatedTotal != totalFiles) {
    log.warn("Statistics mismatch: processed({}) + skipped({}) + error({}) = {} != total({})",
        processedFiles, skippedFilesCount, errorFiles, calculatedTotal, totalFiles);
    // 不抛出异常，但记录警告以便调试
}
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/clawboard/scanner/ScanOrchestrator.java
git commit -m "feat: add statistics equation validation with warning log"
```

---

## Phase 4: 测试与验证

### Task 9: 运行应用并执行扫描

**Files:**
- No code changes

- [ ] **Step 1: 清空数据库**

Run: `.\scripts\reset-database.ps1`
Expected: Database tables truncated successfully

- [ ] **Step 2: 启动应用**

Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Expected: Application started on port 8080

- [ ] **Step 3: 触发扫描**

Run: `Invoke-WebRequest -Uri http://localhost:8080/api/scan/trigger -Method POST -UseBasicParsing`
Expected: HTTP 200 OK, scan started

- [ ] **Step 4: 等待扫描完成**

监控日志输出，等待 "Scan completed" 消息
Expected: Scan completes without errors

- [ ] **Step 5: 检查统计等式**

查看日志中是否有 "Statistics mismatch" 警告
Expected: No warning (or warning with explanation if edge cases exist)

---

### Task 10: 验证文件列表准确性

**Files:**
- No code changes

- [ ] **Step 1: 找到生成的文件列表**

检查 `reports/<date>/` 目录：
```powershell
Get-ChildItem reports\*\java-scanned-files-scan-*.txt | Select-Object -First 1
Get-ChildItem reports\*\java-skipped-files-scan-*.txt | Select-Object -First 1
```

- [ ] **Step 2: 检查 scanned 文件内容**

```powershell
Get-Content reports\<date>\java-scanned-files-scan-<id>.txt | Measure-Object -Line
```

Expected: Line count matches `files_processed` from database

- [ ] **Step 3: 检查 skipped 文件内容**

```powershell
Get-Content reports\<date>\java-skipped-files-scan-<id>.txt | Select-Object -First 5
```

Expected: Contains files with reasons like "File not modified" or error messages

- [ ] **Step 4: 查询数据库验证**

Run: 
```sql
SELECT files_total, files_processed, files_skipped, files_error,
       (files_processed + files_skipped + files_error) as calculated_total
FROM dashboard_scan_history 
ORDER BY id DESC LIMIT 1;
```

Expected: `files_total = calculated_total`

---

### Task 11: 性能对比测试（可选）

**Files:**
- No code changes

- [ ] **Step 1: 记录扫描时间**

查看日志中的扫描开始和结束时间：
```
Scan started at: ...
Scan completed in X ms
```

- [ ] **Step 2: 检查跳过的文件数量**

```sql
SELECT COUNT(*) as skipped_count 
FROM dashboard_scan_progress 
WHERE file_mtime = (SELECT MAX(file_mtime) FROM dashboard_scan_progress);
```

Expected: Significant number of files skipped (if running second scan)

- [ ] **Step 3: 对比优化前后性能**

如果有之前的扫描日志，对比相同数据量下的扫描时间
Expected: Faster scan due to pre-parse filtering

---

## Phase 5: 文档更新

### Task 12: 更新相关文档

**Files:**
- Modify: `docs/API接口文档.md` (如果需要)

- [ ] **Step 1: 检查 API 文档是否需要更新**

查看 `/api/scan/history` 接口的响应字段说明
Expected: Fields are already documented correctly

- [ ] **Step 2: 如有需要，更新字段说明**

如果文档中有错误的统计字段说明，修正它

- [ ] **Step 3: Commit**

```bash
git add docs/
git commit -m "docs: update API documentation for scan statistics fields"
```

---

## 完成检查清单

- [ ] 所有任务已完成
- [ ] 编译通过无错误
- [ ] 应用启动成功
- [ ] 扫描执行成功
- [ ] 统计等式成立：`processed + skipped + error = total`
- [ ] `java-scanned-files-scan-X.txt` 只包含实际处理的文件
- [ ] `java-skipped-files-scan-X.txt` 包含所有跳过的文件
- [ ] `users_scanned` 反映实际扫描的用户数
- [ ] 无性能回归（或性能提升）
- [ ] 日志清晰，无误导性警告

---

## 回滚方案

如果实施后出现问题：

1. **恢复 DataIngestionService**：
   ```bash
   git revert <commit-hash-of-task-3-4>
   ```

2. **恢复 ScanOrchestrator**：
   ```bash
   git revert <commit-hash-of-task-5-8>
   ```

3. **删除新文件**：
   ```bash
   git rm src/main/java/com/company/clawboard/service/IngestionStatus.java
   git rm src/main/java/com/company/clawboard/service/IngestionResult.java
   ```

4. **重新编译和测试**
