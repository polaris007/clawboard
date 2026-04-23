## ADDED Requirements

### Requirement: Message table shall have turn_id column
The `dashboard_message` table SHALL include a `turn_id` column that references `dashboard_conversation_turn.id` to establish a direct relationship between messages and conversation turns.

#### Scenario: Database schema includes turn_id column
- **WHEN** the database migration script is executed
- **THEN** the `dashboard_message` table has a `turn_id` BIGINT column
- **AND** an index `idx_turn_id` exists on the `turn_id` column
- **AND** the column allows NULL values for backward compatibility

#### Scenario: New messages are inserted with turn_id
- **WHEN** a new session is scanned and messages are inserted
- **THEN** each message record has its `turn_id` field populated with the correct conversation turn ID
- **AND** messages belonging to the same turn share the same `turn_id` value

### Requirement: System shall build message-to-turn mapping during parsing
The transcript parser SHALL return a mapping from message IDs to their corresponding turn indices, enabling accurate turn_id assignment during data ingestion.

#### Scenario: Parser returns messageIdToTurnIndex mapping
- **WHEN** `TranscriptParser.parseFile()` completes successfully
- **THEN** the returned `ParsedTranscript` includes a `messageIdToTurnIndex` map
- **AND** each message ID in the session maps to its correct turn index (0-based)
- **AND** all messages within the same turn map to the same turn index

#### Scenario: Mapping covers all messages in the session
- **WHEN** a session contains multiple turns with multiple messages each
- **THEN** every message ID appears exactly once in the `messageIdToTurnIndex` map
- **AND** no message ID is missing from the mapping

### Requirement: Data ingestion shall populate turn_id for messages
The `DataIngestionService` SHALL use the message-to-turn mapping to set the correct `turn_id` when converting and inserting message records.

#### Scenario: Messages receive correct turn_id during conversion
- **WHEN** `convertToMessagesWithTurnId()` processes messages
- **THEN** each `DashboardMessage` entity has its `turnId` field set based on the `messageIdToTurnIndex` mapping
- **AND** the turn_id corresponds to the actual `dashboard_conversation_turn.id` after insertion

#### Scenario: Turn ID lookup uses start_message_id mapping
- **WHEN** determining the turn_id for a message
- **THEN** the system looks up the turn's `startMessageId` in the mapping
- **AND** retrieves the corresponding turn entity to get its database ID

### Requirement: Queries shall support filtering by turn_id
The `MessageMapper` SHALL provide methods to query messages filtered by `turn_id`, enabling efficient retrieval of all messages in a conversation turn.

#### Scenario: Query messages by turn_id
- **WHEN** `messageMapper.selectByTurnId(turnId)` is called
- **THEN** all messages with matching `turn_id` are returned
- **AND** messages are ordered by `message_timestamp` ascending
- **AND** the query uses the `idx_turn_id` index for performance

#### Scenario: Count messages per turn
- **WHEN** aggregating statistics for a conversation turn
- **THEN** the system can efficiently count messages using `WHERE turn_id = ?`
- **AND** the query executes in O(log n) time due to indexing
