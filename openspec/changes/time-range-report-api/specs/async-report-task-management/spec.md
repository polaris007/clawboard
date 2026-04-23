## ADDED Requirements

### Requirement: System shall track async report generation tasks
The system SHALL maintain an in-memory task registry to track the status of report generation tasks, including task ID, status, start time, completion time, and result path.

#### Scenario: Task created on API request
- **WHEN** client submits time range report request
- **THEN** system generates unique UUID as `taskId`
- **AND** creates task entry with status "PENDING" and current timestamp
- **AND** stores task in concurrent HashMap for thread-safe access

#### Scenario: Task status transitions
- **WHEN** task progresses through lifecycle
- **THEN** status changes: PENDING → RUNNING → COMPLETED (or FAILED)
- **AND** each transition updates the task record with timestamp

#### Scenario: Task cleanup after completion
- **WHEN** task completes (success or failure)
- **THEN** task remains in registry for 24 hours for status queries
- **AND** expired tasks are removed by scheduled cleanup job

### Requirement: API shall provide task status endpoint
The system SHALL provide a GET endpoint `/api/v1/reports/tasks/{taskId}/status` that returns the current status and result of a report generation task.

#### Scenario: Query pending task
- **WHEN** client queries task with status "PENDING"
- **THEN** system returns HTTP 200 with `{ taskId, status: "PENDING", progress: null }`

#### Scenario: Query running task
- **WHEN** client queries task with status "RUNNING"
- **THEN** system returns HTTP 200 with `{ taskId, status: "RUNNING", progress: "Querying data..." }`

#### Scenario: Query completed task
- **WHEN** client queries task with status "COMPLETED"
- **THEN** system returns HTTP 200 with `{ taskId, status: "COMPLETED", reportPath: "reports/2026-04-23/xxx.md" }`

#### Scenario: Query failed task
- **WHEN** client queries task with status "FAILED"
- **THEN** system returns HTTP 200 with `{ taskId, status: "FAILED", error: "Database connection timeout" }`

#### Scenario: Task not found
- **WHEN** client queries non-existent or expired taskId
- **THEN** system returns HTTP 404 with error message "Task not found or expired"

### Requirement: Async execution shall use Spring @Async
The system SHALL execute report generation asynchronously using Spring's `@Async` annotation with a dedicated thread pool to avoid blocking HTTP request threads.

#### Scenario: Async method execution
- **WHEN** service method annotated with `@Async` is called
- **THEN** method executes in separate thread from configured thread pool
- **AND** caller receives `CompletableFuture<Void>` immediately

#### Scenario: Thread pool configuration
- **WHEN** application starts
- **THEN** async executor is configured with core pool size 5, max pool size 10, queue capacity 100
- **AND** rejected tasks trigger rejection handler that logs warning

#### Scenario: Exception handling in async task
- **WHEN** exception occurs during async report generation
- **THEN** task status is set to "FAILED" with error message
- **AND** exception is logged with full stack trace
- **AND** HTTP request thread is not affected

### Requirement: System shall limit concurrent report generation tasks
The system SHALL enforce a maximum concurrency limit (default: 5) for report generation tasks to prevent resource exhaustion.

#### Scenario: Concurrent task limit enforced
- **WHEN** 5 tasks are already running and 6th task is submitted
- **THEN** system rejects the 6th task immediately
- **AND** returns HTTP 429 with message "Too many concurrent report generation tasks. Please try again later."

#### Scenario: Task slot released after completion
- **WHEN** a running task completes (success or failure)
- **THEN** task slot is released and becomes available for new tasks
- **AND** semaphore counter is incremented

### Requirement: Task state shall be thread-safe
The system SHALL use thread-safe data structures and synchronization mechanisms to handle concurrent task submissions and status queries.

#### Scenario: Concurrent task submissions
- **WHEN** multiple clients submit requests simultaneously
- **THEN** all tasks are registered without data corruption
- **AND** each task receives unique ID

#### Scenario: Concurrent status queries
- **WHEN** multiple clients query same task status simultaneously
- **THEN** all queries return consistent state
- **AND** no race conditions occur

### Requirement: Scheduled cleanup shall remove expired tasks
The system SHALL run a scheduled job every hour to remove completed/failed tasks older than 24 hours from the task registry.

#### Scenario: Cleanup removes old tasks
- **WHEN** scheduled cleanup runs at top of hour
- **THEN** tasks with completion time > 24 hours ago are removed
- **AND** active tasks (PENDING/RUNNING) are preserved

#### Scenario: Cleanup handles empty registry
- **WHEN** no tasks exist in registry
- **THEN** cleanup completes successfully without errors
- **AND** logs debug message: "No tasks to clean up"
