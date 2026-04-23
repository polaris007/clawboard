## ADDED Requirements

### Requirement: API shall accept time range parameters for report generation
The system SHALL provide a REST endpoint that accepts `startTime` and `endTime` parameters in "yyyy-MM-dd HH:mm:ss" format (Beijing timezone) to generate conversation statistics reports.

#### Scenario: Valid time range request
- **WHEN** client sends POST request to `/api/v1/reports/generate-by-time-range` with valid time range
- **THEN** system returns HTTP 202 with `taskId` and `status: "PENDING"`
- **AND** the async report generation task is queued

#### Scenario: Invalid time format rejected
- **WHEN** client sends request with invalid date format (e.g., "2026/04/20")
- **THEN** system returns HTTP 400 with error message "Invalid date format. Expected: yyyy-MM-dd HH:mm:ss"

#### Scenario: End time before start time rejected
- **WHEN** client sends request where `endTime < startTime`
- **THEN** system returns HTTP 400 with error message "End time must be after start time"

#### Scenario: Time range exceeds maximum limit
- **WHEN** client requests time range greater than 90 days
- **THEN** system returns HTTP 400 with error message "Time range cannot exceed 90 days"

### Requirement: System shall query conversation turns by time range
The system SHALL query `dashboard_conversation_turn` table filtering by `start_time` within the specified range, utilizing existing database indexes for performance.

#### Scenario: Query turns within time range
- **WHEN** report generation starts with time range [2026-04-20 00:00:00, 2026-04-23 23:59:59]
- **THEN** system executes SQL: `SELECT * FROM dashboard_conversation_turn WHERE start_time >= ? AND start_time <= ? ORDER BY start_time ASC`
- **AND** uses index `idx_employee_time` or `idx_time` for efficient query

#### Scenario: No turns found in range
- **WHEN** no conversation turns exist within the specified time range
- **THEN** system generates empty report with zero statistics
- **AND** logs warning: "No conversation turns found in time range [...]"

### Requirement: Report shall be generated with consistent format
The system SHALL reuse the existing `ReportGenerator` logic to produce Markdown reports with identical structure and formatting as scan-triggered reports.

#### Scenario: Generate report with same template
- **WHEN** time range report is generated
- **THEN** report includes sections: Executive Summary, Active Users, Conversation Turns, Task Success Rate, Skill Usage, Error Analysis
- **AND** formatting matches scan-generated reports (headers, tables, charts)

#### Scenario: Report includes time range metadata
- **WHEN** report header is generated
- **THEN** report displays "Time Range: 2026-04-20 00:00:00 - 2026-04-23 23:59:59"
- **AND** report filename includes time range: `time-range-report-2026-04-20-00-00-00-2026-04-23-23-59-59.md`

### Requirement: Report shall be saved to standard directory
The system SHALL save generated reports to `reports/{yyyy-MM-dd}/` directory using the current date (Beijing timezone), maintaining consistency with scan-generated reports.

#### Scenario: Save report to date-based directory
- **WHEN** report is generated on 2026-04-23
- **THEN** report is saved to `reports/2026-04-23/time-range-report-{startTime}-{endTime}.md`
- **AND** directory is created if it doesn't exist

#### Scenario: Handle file name conflicts
- **WHEN** report with same time range already exists
- **THEN** system appends timestamp: `time-range-report-{range}-{unixTimestamp}.md`
- **AND** logs info: "Report file already exists, appending timestamp"

### Requirement: Statistics shall exclude system messages
The system SHALL filter out system-generated messages when calculating conversation turn counts and success rates, consistent with scan report logic.

#### Scenario: Exclude system turns from count
- **WHEN** calculating total conversation turns
- **THEN** only non-system turns are counted (`is_system = 0`)
- **AND** system turns are excluded from success rate calculation

#### Scenario: Calculate metrics correctly
- **WHEN** computing task success rate
- **THEN** formula: `(turns without errors) / (total non-system turns) * 100%`
- **AND** active users count uses distinct `employee_id` values
