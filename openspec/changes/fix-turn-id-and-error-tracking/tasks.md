## 1. Database Migration

- [ ] 1.1 Create database migration script `V3__add_turn_id_to_message.sql` to add `turn_id` column to `dashboard_message` table
- [ ] 1.2 Add index `idx_turn_id` on the new `turn_id` column for query performance
- [ ] 1.3 Verify migration script runs successfully on development database

## 2. Entity and Mapper Updates

- [ ] 2.1 Add `turnId` field to `DashboardMessage.java` entity class with getter/setter
- [ ] 2.2 Update `MessageMapper.xml` to include `turn_id` in INSERT statements
- [ ] 2.3 Update `MessageMapper.xml` to include `turn_id` in SELECT statements and result mappings
- [ ] 2.4 Add `selectByTurnId(Long turnId)` method to `MessageMapper.java` interface
- [ ] 2.5 Add corresponding SQL query in `MessageMapper.xml` for filtering by turn_id
- [ ] 2.6 Add `countBySessionId(String sessionId)` method to check for existing sessions

## 3. TranscriptParser Enhancement

- [ ] 3.1 Modify `ParsedTranscript` record to include `Map<String, Integer> messageIdToTurnIndex` field
- [ ] 3.2 Update `TranscriptParser.parseFile()` to build message-to-turn mapping during parsing
- [ ] 3.3 Ensure all message IDs are mapped to their correct turn indices
- [ ] 3.4 Add unit tests for message-to-turn mapping logic

## 4. DataIngestionService Refactoring

- [ ] 4.1 Implement `hasMessageError(MessageRecord msg)` method with comprehensive error detection (check isError, errorMessage, toolCalls, stopReason)
- [ ] 4.2 Implement `buildTurnMapping(List<DashboardConversationTurn> turns)` to create startMessageId → turnId map
- [ ] 4.3 Implement `buildTurnLineNumberMapping(List<MessageRecord> messages, List<DashboardConversationTurn> turns)` for line number to turn ID mapping
- [ ] 4.4 Implement `convertToMessagesWithTurnId()` method that sets both turn_id and is_error correctly
- [ ] 4.5 Implement `convertToIssuesWithTurnId()` method that populates turn_id based on line number mapping
- [ ] 4.6 Refactor `ingestParsedTranscript()` to follow new order: Turns → Build Mappings → Messages → Issues
- [ ] 4.7 Add session existence check at the beginning of `ingestParsedTranscript()` to skip duplicate scans
- [ ] 4.8 Update logging to track skipped sessions and insertion statistics

## 5. Testing and Validation

- [ ] 5.1 Write unit test for `hasMessageError()` covering all error scenarios (direct error, error message, tool errors, stop reason)
- [ ] 5.2 Write unit test for `buildTurnMapping()` verifying correct ID mapping
- [ ] 5.3 Write integration test for complete ingestion flow with turn_id population
- [ ] 5.4 Test duplicate scan scenario to verify session skipping works correctly
- [ ] 5.5 Run existing test suite to ensure no regressions

## 6. Verification and Documentation

- [ ] 6.1 Execute migration script on clean database and verify schema changes
- [ ] 6.2 Perform a test scan with sample JSONL files and verify turn_id is populated in messages
- [ ] 6.3 Verify is_error accuracy by comparing with dashboard_transcript_issue records
- [ ] 6.4 Run diagnostic SQL query to identify any remaining data inconsistencies
- [ ] 6.5 Update API documentation if any query endpoints benefit from turn_id filtering
- [ ] 6.6 Document the change in project changelog or release notes
