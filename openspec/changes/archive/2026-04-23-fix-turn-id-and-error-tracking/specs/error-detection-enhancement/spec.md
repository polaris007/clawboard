## ADDED Requirements

### Requirement: Error detection shall check multiple error indicators
The `is_error` field in `dashboard_message` SHALL be determined by checking multiple error indicators, not just the `msg.isError()` flag, to ensure accurate error representation.

#### Scenario: Direct error flag is detected
- **WHEN** a message has `msg.isError() == true`
- **THEN** the `is_error` field is set to 1
- **AND** the message is correctly marked as having an error

#### Scenario: Error message content triggers error flag
- **WHEN** a message has a non-null and non-empty `errorMessage` field
- **THEN** the `is_error` field is set to 1
- **AND** the presence of error details is captured

#### Scenario: Tool call errors are detected
- **WHEN** any tool call in `msg.toolCalls()` has `isError() == true` or non-empty `error` field
- **THEN** the `is_error` field is set to 1
- **AND** tool execution failures are properly reflected

#### Scenario: Stop reason indicates error
- **WHEN** `msg.stopReason()` contains "error", "timeout", or "failure" (case-insensitive)
- **THEN** the `is_error` field is set to 1
- **AND** abnormal termination reasons are captured

### Requirement: Error detection logic shall be centralized
The error detection logic SHALL be implemented in a dedicated method within `DataIngestionService` to ensure consistency and maintainability.

#### Scenario: Single method handles all error checks
- **WHEN** determining if a message has errors
- **THEN** the `hasMessageError(MessageRecord msg)` method is called
- **AND** all error indicators are checked in one place
- **AND** the logic is reusable across different ingestion scenarios

#### Scenario: Method returns boolean result
- **WHEN** `hasMessageError()` is invoked with a message
- **THEN** it returns `true` if any error indicator is present
- **AND** it returns `false` only when no error indicators are found
- **AND** the result is used to set `entity.setIsError()`

### Requirement: Existing messages with incorrect is_error shall be identified
The system SHALL provide a way to identify messages where `is_error` does not match the actual error state, enabling data quality audits.

#### Scenario: Query detects mismatched error flags
- **WHEN** running a diagnostic query comparing `dashboard_message.is_error` with `dashboard_transcript_issue` records
- **THEN** messages with issues but `is_error = 0` are identified
- **AND** the count of mismatches is reported for quality assessment

#### Scenario: Audit log captures discrepancies
- **WHEN** data quality checks are performed
- **THEN** any inconsistencies between `is_error` and actual error presence are logged
- **AND** the audit trail supports future improvements to error detection

### Requirement: Error detection shall align with issue detection
The criteria for setting `is_error` in messages SHALL be consistent with the error types detected by `IssueDetector`, ensuring data coherence across tables.

#### Scenario: Message errors correlate with transcript issues
- **WHEN** a message has `is_error = 1`
- **THEN** there should be at least one corresponding record in `dashboard_transcript_issue` for that message
- **AND** the error types are semantically related (e.g., tool error → toolErrors)

#### Scenario: No false positives in error detection
- **WHEN** a message has no actual errors (no error flag, no error message, no tool errors, normal stop reason)
- **THEN** `is_error` is set to 0
- **AND** no spurious issues are created in `dashboard_transcript_issue`
