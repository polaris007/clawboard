package com.company.clawboard.parser;

import com.company.clawboard.parser.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranscriptParser {

    private final MessageParser messageParser;
    private final SystemMessageFilter systemMessageFilter;
    private final SkillDetector skillDetector;
    private final IssueDetector issueDetector;
    private final TurnAssembler turnAssembler;
    private final FlowIntegrityChecker flowIntegrityChecker;

    public record ParsedTranscript(
        String sessionId,
        List<MessageRecord> messages,
        List<TurnAssembler.AssembledTurn> turns,
        List<IssueDetector.DetectedIssue> issues,
        List<SkillInvocation> skillInvocations,
        Map<String, Integer> messageIdToTurnIndex  // NEW: maps message ID to turn index (0-based)
    ) {}

    public record SkillInvocation(String skillName, String toolCallId, long invokedAt) {}

    // Record to store custom record with its line number
    private record CustomRecordWithLineNumber(CustomRecord customRecord, int lineNumber) {}

    public ParsedTranscript parseFile(Path filePath, String employeeId) {
        List<MessageRecord> messages = new ArrayList<>();
        List<CustomRecordWithLineNumber> customRecords = new ArrayList<>();
        List<Object> allEvents = new ArrayList<>();
        List<String> rawLines = new ArrayList<>(); // Store raw lines for error context
        String sessionId = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath.toFile()), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                rawLines.add(line); // Store raw line
                JsonlRecord record = messageParser.parseLine(line, lineNumber);

                if (record instanceof SessionRecord session) {
                    sessionId = session.id();
                } else if (record instanceof MessageRecord msg) {
                    if ("assistant".equals(msg.role()) &&
                        systemMessageFilter.shouldFilterAssistant(msg.provider(), msg.model())) {
                        continue;
                    }

                    // 不再过滤系统消息，而是在后续处理中标记为系统消息
                    // 这样系统消息也能被正确处理并入库


                    messages.add(msg);
                    allEvents.add(msg);
                } else if (record instanceof CustomRecord custom) {
                    // Store custom record with its line number
                    customRecords.add(new CustomRecordWithLineNumber(custom, lineNumber));
                    allEvents.add(custom);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read transcript file: {}", filePath, e);
            return new ParsedTranscript(null, List.of(), List.of(), List.of(), List.of(), Map.of());
        }

        if (sessionId == null) {
            String fileName = filePath.getFileName().toString();
            sessionId = fileName.contains(".jsonl") ?
                fileName.substring(0, fileName.indexOf(".jsonl")) :
                fileName;
            log.debug("No session ID found in file: {}, using sessionId: {}", filePath, sessionId);
        }

        List<IssueDetector.DetectedIssue> allIssues = new ArrayList<>();
        String filePathStr = filePath.toString();
        log.debug("Processing transcript file: {}, messages: {}, customRecords: {}", filePathStr, messages.size(), customRecords.size());

        for (MessageRecord msg : messages) {
            List<IssueDetector.DetectedIssue> msgIssues = issueDetector.detectIssues(msg);
            for ( var issue : msgIssues) {
                String userInput = extractUserInput(messages, msg);
                IssueDetector.DetectedIssue enriched = enrichIssue(issue, filePathStr, userInput, null, null, employeeId);
                allIssues.add(enriched);
            }
        }

        for (CustomRecordWithLineNumber customWithLine : customRecords) {
            CustomRecord custom = customWithLine.customRecord();
            int lineNumber = customWithLine.lineNumber();
            String dataJson = custom.data() != null ? custom.data().toString() : null;
            
            List<IssueDetector.DetectedIssue> customIssues = issueDetector.detectCustomEventIssues(
                custom.customType(), dataJson, custom.id(), custom.timestamp(), lineNumber);
            if (!customIssues.isEmpty()) {
                log.debug("Custom event detected issues: type={}, issues={}", custom.customType(), customIssues.size());
            }
            for ( var issue : customIssues) {
                // Extract user input for custom events (like Python does)
                String userInput = extractUserInputForCustomEvent(messages, lineNumber);
                IssueDetector.DetectedIssue enriched = new IssueDetector.DetectedIssue(
                    issue.errorType(),
                    issue.severity(),
                    issue.description(),
                    issue.errorMessage(),
                    issue.eventType(),
                    userInput,
                    issue.causeAnalysis(),
                    filePathStr,
                    null,
                    null,
                    lineNumber,
                    issue.runId(),
                    issue.provider(),
                    issue.model(),
                    issue.messageId(),
                    employeeId
                );
                allIssues.add(enriched);
            }
        }


        List<IssueDetector.DetectedIssue> flowIssues = flowIntegrityChecker.checkFlowIntegrity(allEvents, messages, employeeId);
        for (var issue : flowIssues) {
            String errorLineContent = extractLineContent(rawLines, issue);
            String nextLineContent = extractNextLineContent(rawLines, issue);
            allIssues.add(enrichIssue(issue, filePathStr, null, errorLineContent, nextLineContent, employeeId));
        }

        List<SkillInvocation> skillInvocations = new ArrayList<>();
        for (MessageRecord msg : messages) {
            if (msg.toolCalls() != null) {
                for (var tc : msg.toolCalls()) {
                    if (skillDetector.isSkillToolCall(tc.name())) {
                        String skillName = skillDetector.extractSkillName(tc.name());
                        skillInvocations.add(new SkillInvocation(skillName, tc.id(), msg.epochMs()));
                    }
                }
            }
        }

        List<TurnAssembler.AssembledTurn> turns = assembleTurns(messages);

        // Build message-to-turn mapping
        Map<String, Integer> messageIdToTurnIndex = buildMessageToTurnMapping(messages, turns);

        return new ParsedTranscript(sessionId, messages, turns, allIssues, skillInvocations, messageIdToTurnIndex);
    }

    private String extractUserInput(List<MessageRecord> messages, MessageRecord currentMsg) {
        if (messages == null || currentMsg == null) {
            return null;
        }

        int currentIndex = -1;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id().equals(currentMsg.id())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex <= 0) {
            return null;
        }

        for (int i = currentIndex - 1; i >= 0; i--) {
            MessageRecord msg = messages.get(i);
            if ("user".equals(msg.role()) && msg.textContent() != null) {
                String content = msg.textContent();
                return content.length() > 200 ? content.substring(0, 200) + "..." : content;
            }
        }

        return null;
    }

    private String extractUserInputForCustomEvent(List<MessageRecord> messages, int lineNumber) {
        if (messages == null) {
            return null;
        }

        // Find the message that is closest to the custom event line number (but before it)
        MessageRecord closestMsg = null;
        int closestLineDiff = Integer.MAX_VALUE;

        for (MessageRecord msg : messages) {
            if (msg.lineNumber() < lineNumber) {
                int diff = lineNumber - msg.lineNumber();
                if (diff < closestLineDiff) {
                    closestLineDiff = diff;
                    closestMsg = msg;
                }
            }
        }

        // If we found a message, extract user input from it or earlier messages
        if (closestMsg != null) {
            return extractUserInput(messages, closestMsg);
        }

        return null;
    }

    private String extractLineContent(List<String> rawLines, IssueDetector.DetectedIssue issue) {
        if (rawLines == null || issue == null || issue.lineNumber() == null) {
            return null;
        }

        int lineNum = issue.lineNumber() - 1; // Convert to 0-based index
        if (lineNum >= 0 && lineNum < rawLines.size()) {
            return rawLines.get(lineNum);
        }

        return null;
    }

    private String extractNextLineContent(List<String> rawLines, IssueDetector.DetectedIssue issue) {
        if (rawLines == null || issue == null || issue.lineNumber() == null) {
            return null;
        }

        int lineNum = issue.lineNumber(); // Convert to 0-based index for next line
        if (lineNum >= 0 && lineNum < rawLines.size()) {
            return rawLines.get(lineNum);
        }

        return null;
    }

    private String formatMessageForDisplay(MessageRecord msg) {
        if (msg == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(msg.role()).append("]");

        if (msg.textContent() != null && !msg.textContent().isEmpty()) {
            String content = msg.textContent();
            if (content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            sb.append(" ").append(content.replace("\n", " "));
        }

        if (msg.toolCalls() != null && !msg.toolCalls().isEmpty()) {
            sb.append(" [ToolCall: ").append(msg.toolCalls().get(0).name()).append("]");
        }

        if (msg.isError()) {
            sb.append(" [ERROR]");
        }

        return sb.toString();
    }

    private IssueDetector.DetectedIssue enrichIssue(
            IssueDetector.DetectedIssue original,
            String filePath,
            String userInput,
            String errorLineContent,
            String nextLineContent,
            String employeeId) {

        return new IssueDetector.DetectedIssue(
            original.errorType(),
            original.severity(),
            original.description(),
            original.errorMessage(),
            original.eventType(),
            userInput != null ? userInput : original.userInput(),
            original.causeAnalysis(),
            filePath != null ? filePath : original.filePath(),
            errorLineContent != null ? errorLineContent : original.errorLineContent(),
            nextLineContent != null ? nextLineContent : original.nextLineContent(),
            original.lineNumber(),
            original.runId(),
            original.provider(),
            original.model(),
            original.messageId(),
            employeeId
        );
    }

    private List<TurnAssembler.AssembledTurn> assembleTurns(List<MessageRecord> messages) {
        List<TurnAssembler.AssembledTurn> turns = new ArrayList<>();
        List<MessageRecord> currentTurnMessages = new ArrayList<>();

        for (MessageRecord msg : messages) {
            if ("user".equals(msg.role())) {
                if (!currentTurnMessages.isEmpty()) {
                    turns.add(turnAssembler.assembleTurn(currentTurnMessages));
                }
                currentTurnMessages.clear();
                currentTurnMessages.add(msg);
            } else if (!currentTurnMessages.isEmpty()) {
                currentTurnMessages.add(msg);
            }
            // 跳过以assistant消息开始的轮次
        }

        if (!currentTurnMessages.isEmpty()) {
            turns.add(turnAssembler.assembleTurn(currentTurnMessages));
        }

        return turns;
    }

    /**
     * Build a mapping from message IDs to their turn indices.
     * Each turn contains a range of messages, and we map each message ID to its turn index.
     *
     * @param messages All messages in the session
     * @param turns Assembled conversation turns
     * @return Map from message ID to turn index (0-based)
     */
    private Map<String, Integer> buildMessageToTurnMapping(
            List<MessageRecord> messages,
            List<TurnAssembler.AssembledTurn> turns) {
        
        Map<String, Integer> mapping = new HashMap<>();
        
        // Create a map from message ID to its index in the messages list
        Map<String, Integer> messageIdToIndex = new HashMap<>();
        for (int i = 0; i < messages.size(); i++) {
            messageIdToIndex.put(messages.get(i).id(), i);
        }
        
        // For each turn, map all its messages to the turn index
        for (int turnIndex = 0; turnIndex < turns.size(); turnIndex++) {
            TurnAssembler.AssembledTurn turn = turns.get(turnIndex);
            
            // Find start and end message indices
            Integer startIndex = messageIdToIndex.get(turn.startMessageId());
            Integer endIndex = turn.endMessageId() != null ? 
                messageIdToIndex.get(turn.endMessageId()) : startIndex;
            
            if (startIndex != null && endIndex != null) {
                // Map all messages in this turn's range
                for (int i = startIndex; i <= endIndex && i < messages.size(); i++) {
                    mapping.put(messages.get(i).id(), turnIndex);
                }
            }
        }
        
        return mapping;
    }
}
