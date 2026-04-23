## 1. Async Configuration

- [ ] 1.1 Create `AsyncConfig` configuration class with @EnableAsync annotation
- [ ] 1.2 Define `ThreadPoolTaskExecutor` bean with corePoolSize=5, maxPoolSize=10, queueCapacity=100
- [ ] 1.3 Set thread name prefix to "report-gen-" for easy identification
- [ ] 1.4 Configure async exception handler to log errors

## 2. Time Range Report Service

- [ ] 2.1 Create `TimeRangeReportService` with @Async method
- [ ] 2.2 Implement `generateReportByTimeRange(LocalDateTime startTime, LocalDateTime endTime)` async method
- [ ] 2.3 Add validation: check endTime > startTime (no maximum limit)
- [ ] 2.4 Query conversation turns: add `selectTurnsByTimeRange()` method to ConversationTurnMapper
- [ ] 2.5 Create SQL in ConversationTurnMapper.xml with WHERE start_time BETWEEN ? AND ?
- [ ] 2.6 Calculate statistics: active users, total turns, success rate, skill usage, error breakdown
- [ ] 2.7 Filter out system messages (is_system = 0) from all calculations

## 3. Report Generator Enhancement

- [ ] 3.1 Refactor existing `ReportGenerator.generateReport(scanId)` to extract common logic
- [ ] 3.2 Create private method `buildReportContent(statistics, timeRangeLabel)` that generates Markdown
- [ ] 3.3 Modify original method to call new method with scan-based statistics
- [ ] 3.4 Add public method `generateReportByTimeRange(startTime, endTime)` that calls buildReportContent
- [ ] 3.5 Ensure report header includes "Time Range: {startTime} - {endTime}"
- [ ] 3.6 Generate filename: `time-range-report-{startTime}-{endTime}.md` (replace spaces/colons with dashes)

## 4. Report File Management

- [ ] 4.1 Create `ReportFileService` to handle file I/O operations
- [ ] 4.2 Implement `saveReport(content, fileName)` that creates reports/{yyyy-MM-dd}/ directory
- [ ] 4.3 Handle file name conflicts by appending Unix timestamp if file exists
- [ ] 4.4 Return absolute file path after successful save
- [ ] 4.5 Add logging for file operations (info level for success, warn for conflicts)

## 5. REST API Controller

- [ ] 5.1 Create `ReportController` with @RestController and @RequestMapping("/api/v1/reports")
- [ ] 5.2 Implement POST `/generate-by-time-range` endpoint accepting TimeRangeRequest DTO
- [ ] 5.3 Create `TimeRangeRequest` DTO with startTime, endTime fields and validation annotations
- [ ] 5.4 Validate request parameters using @Valid (@NotNull, custom date format validator)
- [ ] 5.5 Call TimeRangeReportService to start async task (fire-and-forget)
- [ ] 5.6 Return HTTP 200 immediately with confirmation message
- [ ] 5.7 Add proper error handling for validation failures (HTTP 400)

## 6. Error Handling and Logging

- [ ] 6.1 Add try-catch in async method to catch all exceptions
- [ ] 6.2 Log ERROR with full stack trace on failure
- [ ] 6.3 Add structured logging: time range, duration, result (success/failure)
- [ ] 6.4 Handle edge cases: no data found, empty result set
- [ ] 6.5 Log INFO when report generation starts and completes

## 7. Integration Testing

- [ ] 7.1 Write test for valid time range request → returns 200 immediately
- [ ] 7.2 Write test for invalid date format → returns 400
- [ ] 7.3 Write test for endTime < startTime → returns 400
- [ ] 7.4 Write integration test: submit task → wait → verify report file created
- [ ] 7.5 Test with large time range (> 90 days) → should work without limit
- [ ] 7.6 Verify report format matches scan-generated reports

## 8. Documentation and Verification

- [ ] 8.1 Test end-to-end flow: API → async task → report file created
- [ ] 8.2 Check database query performance with EXPLAIN (should use index)
- [ ] 8.3 Monitor thread pool metrics during load test
- [ ] 8.4 Document API usage examples in comments or README
- [ ] 8.5 Add note about checking reports/ directory for generated files
