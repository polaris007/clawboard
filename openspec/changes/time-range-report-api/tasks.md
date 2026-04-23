## 1. Async Task Infrastructure

- [ ] 1.1 Create `ReportTaskStatus` enum with values: PENDING, RUNNING, COMPLETED, FAILED
- [ ] 1.2 Create `ReportTaskRecord` record class with fields: taskId, status, startTime, endTime, reportPath, errorMessage
- [ ] 1.3 Create `AsyncReportTaskManager` service with ConcurrentHashMap<String, ReportTaskRecord> for task tracking
- [ ] 1.4 Implement `createTask()` method that generates UUID and initializes PENDING task
- [ ] 1.5 Implement `updateTaskStatus()` method for status transitions (thread-safe)
- [ ] 1.6 Implement `getTaskStatus()` method to query task by ID
- [ ] 1.7 Add scheduled cleanup job (@Scheduled) to remove tasks older than 24 hours

## 2. Thread Pool Configuration

- [ ] 2.1 Create `AsyncConfig` configuration class with @EnableAsync annotation
- [ ] 2.2 Define `ThreadPoolTaskExecutor` bean with corePoolSize=5, maxPoolSize=10, queueCapacity=100
- [ ] 2.3 Configure rejection handler that logs warning when queue is full
- [ ] 2.4 Set thread name prefix to "report-gen-" for easy identification

## 3. Time Range Report Service

- [ ] 3.1 Create `TimeRangeReportService` with @Async methods
- [ ] 3.2 Implement `generateReportByTimeRange(LocalDateTime startTime, LocalDateTime endTime)` async method
- [ ] 3.3 Add validation logic: check time range <= 90 days, endTime > startTime
- [ ] 3.4 Query conversation turns: add `selectTurnsByTimeRange()` method to ConversationTurnMapper
- [ ] 3.5 Create SQL in ConversationTurnMapper.xml with WHERE start_time BETWEEN ? AND ?
- [ ] 3.6 Calculate statistics: active users, total turns, success rate, skill usage, error breakdown
- [ ] 3.7 Filter out system messages (is_system = 0) from all calculations

## 4. Report Generator Enhancement

- [ ] 4.1 Refactor existing `ReportGenerator.generateReport(scanId)` to extract common logic
- [ ] 4.2 Create private method `buildReportContent(statistics, timeRangeLabel)` that generates Markdown
- [ ] 4.3 Modify original method to call new method with scan-based statistics
- [ ] 4.4 Add public method `generateReportByTimeRange(startTime, endTime)` that calls buildReportContent
- [ ] 4.5 Ensure report header includes "Time Range: {startTime} - {endTime}"
- [ ] 4.6 Generate filename: `time-range-report-{startTime}-{endTime}.md` (replace spaces/colons with dashes)

## 5. Report File Management

- [ ] 5.1 Create `ReportFileService` to handle file I/O operations
- [ ] 5.2 Implement `saveReport(content, fileName)` that creates reports/{yyyy-MM-dd}/ directory
- [ ] 5.3 Handle file name conflicts by appending Unix timestamp if file exists
- [ ] 5.4 Return absolute file path after successful save
- [ ] 5.5 Add logging for file operations (info level for success, warn for conflicts)

## 6. REST API Controller

- [ ] 6.1 Create `ReportController` with @RestController and @RequestMapping("/api/v1/reports")
- [ ] 6.2 Implement POST `/generate-by-time-range` endpoint accepting TimeRangeRequest DTO
- [ ] 6.3 Create `TimeRangeRequest` DTO with startTime, endTime fields and validation annotations
- [ ] 6.4 Validate request parameters using @Valid and custom validators
- [ ] 6.5 Call TimeRangeReportService to start async task
- [ ] 6.6 Return HTTP 202 with taskId in response body
- [ ] 6.7 Implement GET `/tasks/{taskId}/status` endpoint to query task status
- [ ] 6.8 Return appropriate HTTP status codes: 200 (found), 404 (not found), 429 (too many tasks)

## 7. Concurrency Control

- [ ] 7.1 Add Semaphore(5) to TimeRangeReportService to limit concurrent tasks
- [ ] 7.2 Acquire semaphore before starting async task, release in finally block
- [ ] 7.3 Return HTTP 429 immediately if semaphore.acquire() fails (tryAcquire with timeout=0)
- [ ] 7.4 Log warning when task is rejected due to concurrency limit

## 8. Error Handling and Logging

- [ ] 8.1 Add try-catch in async method to catch all exceptions
- [ ] 8.2 Update task status to FAILED with error message on exception
- [ ] 8.3 Log full stack trace at ERROR level for debugging
- [ ] 8.4 Add structured logging: task ID, time range, duration, result
- [ ] 8.5 Handle edge cases: no data found, empty result set

## 9. Integration Testing

- [ ] 9.1 Write test for valid time range request → returns taskId
- [ ] 9.2 Write test for invalid date format → returns 400
- [ ] 9.3 Write test for endTime < startTime → returns 400
- [ ] 9.4 Write test for time range > 90 days → returns 400
- [ ] 9.5 Write integration test: submit task → poll status → verify COMPLETED
- [ ] 9.6 Test concurrent submissions: verify semaphore limits to 5 tasks
- [ ] 9.7 Test task cleanup: verify old tasks are removed after 24 hours

## 10. Documentation and Verification

- [ ] 10.1 Test end-to-end flow: API → async task → report file created
- [ ] 10.2 Verify report format matches scan-generated reports
- [ ] 10.3 Check database query performance with EXPLAIN (should use index)
- [ ] 10.4 Monitor thread pool metrics during load test
- [ ] 10.5 Document API usage examples in comments or README
