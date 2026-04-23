## ADDED Requirements

### Requirement: Issue table shall have turn_id populated
The `dashboard_transcript_issue` table's `turn_id` column SHALL be populated during data ingestion based on the issue's line number and its corresponding conversation turn.

#### Scenario: Issues receive correct turn_id during insertion
- **WHEN** `convertToIssuesWithTurnId()` processes detected issues
- **THEN** each `DashboardTranscriptIssue` entity has its `turnId` field set
- **AND** the turn_id matches the conversation turn that contains the error

#### Scenario: Turn ID is derived from line number mapping
- **WHEN** an issue has a `lineNumber` field
- **THEN** the system looks up which turn contains that line number
- **AND** sets `turn_id` to the corresponding `dashboard_conversation_turn.id`

### Requirement: System shall build line-number-to-turn mapping
The data ingestion service SHALL construct a mapping from JSONL file line numbers to conversation turn IDs, enabling accurate turn assignment for issues.

#### Scenario: Mapping covers all lines in the session
- **WHEN** `buildTurnLineNumberMapping()` is called with messages and turns
- **THEN** a map is created where each line number maps to a turn ID
- **AND** all lines belonging to the same turn map to the same turn ID
- **AND** the mapping covers the full range from first to last message in each turn

#### Scenario: Line number ranges are calculated correctly
- **WHEN** determining the line range for a turn
- **THEN** the start line is found by looking up the turn's `startMessageId`
- **AND** the end line is found by looking up the turn's `endMessageId`
- **AND** all intermediate line numbers are mapped to the turn ID

### Requirement: Issues without line numbers shall handle gracefully
The system SHALL handle issues that do not have a `lineNumber` field, either by attempting alternative matching or leaving `turn_id` as NULL.

#### Scenario: Issue has no line number
- **WHEN** an issue's `lineNumber` is null
- **THEN** the `turn_id` field is set to NULL
- **AND** the issue is still inserted into the database
- **AND** a warning is logged for investigation

#### Scenario: Alternative matching by message ID
- **WHEN** an issue has no line number but has a `messageId`
- **THEN** the system attempts to find the turn containing that message
- **AND** sets `turn_id` if a match is found
- **AND** leaves it NULL if no match is found

### Requirement: Queries shall support filtering issues by turn_id
The `TranscriptIssueMapper` SHALL provide methods to query issues filtered by `turn_id`, enabling efficient retrieval of all problems in a conversation turn.

#### Scenario: Query issues by turn_id
- **WHEN** `issueMapper.selectByTurnId(turnId)` is called
- **THEN** all issues with matching `turn_id` are returned
- **AND** issues are ordered by `occurred_at` ascending
- **AND** the query uses an index on `turn_id` for performance

#### Scenario: Aggregate errors per turn
- **WHEN** calculating error statistics for a conversation turn
- **THEN** the system can count issues using `WHERE turn_id = ?`
- **AND** the query efficiently retrieves error counts by severity and type

### Requirement: Turn-level error analysis shall be enabled
With `turn_id` populated, the system SHALL support turn-level error analysis, including error rates and patterns per conversation turn.

#### Scenario: Calculate error rate per turn
- **WHEN** analyzing conversation quality
- **THEN** the system can calculate `error_count / total_messages` for each turn
- **AND** turns with high error rates are identified for review

#### Scenario: Identify error-prone turns
- **WHEN** searching for problematic conversation patterns
- **THEN** turns with multiple issues can be queried efficiently
- **AND** common error types per turn are aggregated for pattern detection
