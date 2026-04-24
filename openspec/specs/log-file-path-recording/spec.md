# log-file-path-recording

## Purpose

在对话轮次数据中记录 transcript 文件路径，便于问题追踪和日志定位。

---

## Requirements

### Requirement: ParsedTranscript 包含 filePath 字段

系统 SHALL 在 `ParsedTranscript` 记录中包含 transcript 文件的路径信息。

#### Scenario: 解析文件时记录路径
- **WHEN** 调用 `TranscriptParser.parseFile(Path filePath, String employeeId)` 方法
- **THEN** 返回的 `ParsedTranscript` 对象包含 `filePath` 字段，值为传入的 `filePath.toString()`

#### Scenario: 文件路径用于后续处理
- **WHEN** `DataIngestionService` 处理 `ParsedTranscript`
- **THEN** 可以从 `ParsedTranscript.filePath` 获取文件路径

---

### Requirement: DashboardConversationTurn 记录 logFilePath

系统 SHALL 在入库对话轮次时，将 transcript 文件路径记录到 `DashboardConversationTurn.logFilePath` 字段。

#### Scenario: 入库时设置 logFilePath
- **WHEN** 调用 `DataIngestionService.convertToTurns()` 方法
- **THEN** 每个 `DashboardConversationTurn` 实体的 `logFilePath` 字段设置为传入的 filePath 参数

#### Scenario: 与 DashboardTranscriptIssue 一致的处理逻辑
- **WHEN** 记录 logFilePath 时
- **THEN** 使用与 `DashboardTranscriptIssue.filePath` 相同的处理逻辑（完整文件路径）

---

### Requirement: logFilePath 数据流

系统 SHALL 确保 logFilePath 从文件解析到入库的完整数据流。

#### Scenario: 完整数据流
- **WHEN** 扫描 transcript 文件时
- **THEN** 数据流为：`ScanOrchestrator` → `TranscriptParser.parseFile()` → `ParsedTranscript.filePath` → `DataIngestionService.ingestParsedTranscript()` → `convertToTurns()` → `DashboardConversationTurn.logFilePath`

#### Scenario: 历史数据兼容
- **WHEN** 查询历史数据的 logFilePath 字段
- **THEN** 允许字段为空字符串或 null（历史数据可能未填充）
