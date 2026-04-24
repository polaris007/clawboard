## Why

当前应用仅在扫描完成后自动生成一次报告，用户无法针对特定时间范围进行灵活的数据分析和报告生成。业务方需要能够：
- 按自定义时间范围统计对话轮次数据
- 生成与扫描报告格式一致的 Markdown 报告
- 快速定位特定时间段的问题和趋势

此功能将提升数据分析的灵活性，支持更精细化的运营决策和问题排查。

## What Changes

- **新增 REST API 端点**：`POST /api/v1/reports/generate-by-time-range`，接受时间范围参数（startTime, endTime）
- **实现时间范围统计逻辑**：基于 `dashboard_conversation_turn.start_time` 字段过滤对话轮次
- **复用现有报告生成器**：调用 `ReportGenerator` 生成相同格式的 Markdown 报告
- **报告存储**：生成的报告保存到 `reports/{yyyy-MM-dd}/` 目录，与扫描报告统一管理
- **异步处理**：报告生成可能耗时较长，采用极简异步（Fire-and-Forget）方式，后台执行完成后直接保存文件

## Capabilities

### New Capabilities
- `time-range-report-generation`: 按时间范围生成对话统计报告的能力，包括 API 接口、统计逻辑、报告生成和存储

### Modified Capabilities
<!-- 无现有能力的需求变更 -->

## Impact

**影响的代码模块**：
- 新增 `ReportController.java`：提供报告生成 API
- 新增 `TimeRangeReportService.java`：实现时间范围统计和报告生成逻辑
- 新增 `AsyncReportTask.java`：异步任务管理
- 复用 `ReportGenerator.java`：生成 Markdown 报告
- 数据库查询：使用 `ConversationTurnMapper.selectTurnsWithFilters()` 按时间过滤

**API 影响**：
- 新增 POST `/api/v1/reports/generate-by-time-range` 接口
- 请求参数：`{ startTime: "yyyy-MM-dd HH:mm:ss", endTime: "yyyy-MM-dd HH:mm:ss" }`
- 响应格式：立即返回确认消息，报告文件保存到 `reports/` 目录

**性能影响**：
- 时间范围查询依赖 `idx_employee_time` 和 `idx_time` 索引，预计 O(log n) 复杂度
- 大数据量时报告生成可能需要 10-30 秒，采用极简异步（@Async）避免阻塞 HTTP 线程
- 报告文件存储在本地磁盘，需注意磁盘空间管理

**数据兼容性**：
- 纯查询操作，不修改任何数据
- 向后兼容，不影响现有扫描报告功能
- 时间参数使用北京时间（Asia/Shanghai）
