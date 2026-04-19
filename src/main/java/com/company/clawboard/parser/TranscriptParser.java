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
import java.util.List;

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
        List<SkillInvocation> skillInvocations
    ) {}

    public record SkillInvocation(String skillName, String toolCallId, long invokedAt) {}

    // Record to store custom record with its line number
    private record CustomRecordWithLineNumber(CustomRecord customRecord, int lineNumber) {}

    public ParsedTranscript parseFile(Path filePath, String employeeId) {
        List<MessageRecord> messages = new ArrayList<>();
        List<CustomRecordWithLineNumber> customRecords = new ArrayList<>();
        List<Object> allEvents = new ArrayList<>();
        String sessionId = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath.toFile()), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                JsonlRecord record = messageParser.parseLine(line, lineNumber);

                if (record instanceof SessionRecord session) {
                    sessionId = session.id();
                } else if (record instanceof MessageRecord msg) {
                    if ("assistant".equals(msg.role()) &&
                        systemMessageFilter.shouldFilterAssistant(msg.provider(), msg.model())) {
                        continue;
                    }

                    if ("user".equals(msg.role()) &&
                        systemMessageFilter.isSystemGeneratedUserMessage(msg.textContent())) {
                        continue;
                    }

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
            return new ParsedTranscript(null, List.of(), List.of(), List.of(), List.of());
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
                IssueDetector.DetectedIssue enriched = enrichIssue(issue, filePathStr, userInput, null, null);
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
                IssueDetector.DetectedIssue enriched = new IssueDetector.DetectedIssue(
                    issue.errorType(),
                    issue.severity(),
                    issue.description(),
                    issue.errorMessage(),
                    issue.eventType(),
                    null,
                    issue.causeAnalysis(),
                    filePathStr,
                    null,
                    null,
                    lineNumber,
                    issue.runId(),
                    issue.provider(),
                    issue.model(),
                    issue.messageId()
                );
                allIssues.add(enriched);
            }
        }


        List<IssueDetector.DetectedIssue> flowIssues = flowIntegrityChecker.checkFlowIntegrity(allEvents, messages);
        for (var issue : flowIssues) {
            String errorLineContent = extractLineContent(messages, issue);
            String nextLineContent = extractNextLineContent(messages, issue);
            allIssues.add(enrichIssue(issue, filePathStr, null, errorLineContent, nextLineContent));
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

        return new ParsedTranscript(sessionId, messages, turns, allIssues, skillInvocations);
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

    private String extractLineContent(List<MessageRecord> messages, IssueDetector.DetectedIssue issue) {
        if (messages == null || issue == null) {
            return null;
        }

        String errorMsg = issue.errorMessage();
        if (errorMsg == null) {
            return null;
        }

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Line:\\s*(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(errorMsg);
        if (!matcher.find()) {
            return null;
        }

        String lineId = matcher.group(1);
        for (MessageRecord msg : messages) {
            if (msg.id().equals(lineId)) {
                return formatMessageForDisplay(msg);
            }
        }

        return null;
    }

    private String extractNextLineContent(List<MessageRecord> messages, IssueDetector.DetectedIssue issue) {
        if (messages == null || issue == null) {
            return null;
        }

        String errorMsg = issue.errorMessage();
        if (errorMsg == null) {
            return null;
        }

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Line:\\s*(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(errorMsg);
        if (!matcher.find()) {
            return null;
        }

        String lineId = matcher.group(1);
        for (int i = 0; i < messages.size() - 1; i++) {
            if (messages.get(i).id().equals(lineId)) {
                return formatMessageForDisplay(messages.get(i + 1));
            }
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
            String nextLineContent) {

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
            original.messageId()
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
            }
            currentTurnMessages.add(msg);
        }

        if (!currentTurnMessages.isEmpty()) {
            turns.add(turnAssembler.assembleTurn(currentTurnMessages));
        }

        return turns;
    }
}
