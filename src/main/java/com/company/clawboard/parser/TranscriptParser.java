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

    public ParsedTranscript parseFile(Path filePath, String employeeId) {
        List<MessageRecord> messages = new ArrayList<>();
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
                    // Filter delivery-mirror messages
                    if ("assistant".equals(msg.role()) &&
                        systemMessageFilter.shouldFilterAssistant(msg.provider(), msg.model())) {
                        continue;
                    }

                    // Filter system-generated user messages
                    if ("user".equals(msg.role()) &&
                        systemMessageFilter.isSystemGeneratedUserMessage(msg.textContent())) {
                        continue;
                    }

                    messages.add(msg);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read transcript file: {}", filePath, e);
            return new ParsedTranscript(null, List.of(), List.of(), List.of(), List.of());
        }

        // If no session ID found, use filename as fallback (matching Python behavior)
        if (sessionId == null) {
            String fileName = filePath.getFileName().toString();
            // Extract base filename without extensions (e.g., "xxx.jsonl.reset.2026-04-13" -> "xxx")
            sessionId = fileName.contains(".jsonl") ? 
                fileName.substring(0, fileName.indexOf(".jsonl")) : 
                fileName;
            log.debug("No session ID found in file: {}, using filename as sessionId: {}", filePath, sessionId);
        }

        // Detect issues (single message level)
        List<IssueDetector.DetectedIssue> allIssues = new ArrayList<>();
        String filePathStr = filePath.toString();
        log.debug("Processing transcript file: {}", filePathStr);
        for (MessageRecord msg : messages) {
            List<IssueDetector.DetectedIssue> msgIssues = issueDetector.detectIssues(msg);
            // Enrich issues with file path and user input
            for (var issue : msgIssues) {
                String userInput = extractUserInput(messages, msg);
                IssueDetector.DetectedIssue enriched = enrichIssue(issue, filePathStr, userInput, null, null);
                if (log.isDebugEnabled()) {
                    log.debug("Enriched issue - type: {}, filePath: {}, userInput length: {}", 
                        enriched.errorType(), 
                        enriched.filePath() != null ? enriched.filePath().substring(Math.max(0, enriched.filePath().length() - 50)) : "null",
                        enriched.userInput() != null ? enriched.userInput().length() : 0);
                }
                allIssues.add(enriched);
            }
        }

        // Check flow integrity (session level)
        List<IssueDetector.DetectedIssue> flowIssues = flowIntegrityChecker.checkFlowIntegrity(messages);
        // Enrich flow issues with file path and line content
        for (var issue : flowIssues) {
            String enrichedFilePath = filePathStr;
            String errorLineContent = extractLineContent(messages, issue);
            String nextLineContent = extractNextLineContent(messages, issue);
            allIssues.add(enrichIssue(issue, enrichedFilePath, null, errorLineContent, nextLineContent));
        }

        // Detect skill invocations
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

        // Assemble turns (group by parent-child relationships)
        List<TurnAssembler.AssembledTurn> turns = assembleTurns(messages);

        return new ParsedTranscript(sessionId, messages, turns, allIssues, skillInvocations);
    }

    /**
     * Extract user input from the most recent user message before the current message
     */
    private String extractUserInput(List<MessageRecord> messages, MessageRecord currentMsg) {
        if (messages == null || currentMsg == null) {
            return null;
        }
        
        // Find the index of current message
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
        
        // Search backwards for the most recent user message
        for (int i = currentIndex - 1; i >= 0; i--) {
            MessageRecord msg = messages.get(i);
            if ("user".equals(msg.role()) && msg.textContent() != null) {
                // Truncate to 200 chars
                String content = msg.textContent();
                return content.length() > 200 ? content.substring(0, 200) + "..." : content;
            }
        }
        
        return null;
    }

    /**
     * Extract error line content for flow integrity issues
     */
    private String extractLineContent(List<MessageRecord> messages, IssueDetector.DetectedIssue issue) {
        if (messages == null || issue == null) {
            return null;
        }
        
        // Try to find the message by ID in the error message
        String errorMsg = issue.errorMessage();
        if (errorMsg == null) {
            return null;
        }
        
        // Extract line ID from error message (e.g., "Line: xxx")
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

    /**
     * Extract next line content for flow integrity issues
     */
    private String extractNextLineContent(List<MessageRecord> messages, IssueDetector.DetectedIssue issue) {
        if (messages == null || issue == null) {
            return null;
        }
        
        String errorMsg = issue.errorMessage();
        if (errorMsg == null) {
            return null;
        }
        
        // For flow errors, we need to find the next message after the error line
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Line:\\s*(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(errorMsg);
        if (!matcher.find()) {
            return null;
        }
        
        String lineId = matcher.group(1);
        for (int i = 0; i < messages.size() - 1; i++) {
            if (messages.get(i).id().equals(lineId)) {
                // Return the next message
                return formatMessageForDisplay(messages.get(i + 1));
            }
        }
        
        return null;
    }

    /**
     * Format a message for display in error reports
     */
    private String formatMessageForDisplay(MessageRecord msg) {
        if (msg == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(msg.role()).append("]");
        
        if (msg.textContent() != null && !msg.textContent().isEmpty()) {
            String content = msg.textContent();
            // Truncate long content
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

    /**
     * Enrich an issue with additional context information
     */
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
            original.lineNumber()
        );
    }

    private List<TurnAssembler.AssembledTurn> assembleTurns(List<MessageRecord> messages) {
        // Simple grouping: each user message starts a new turn
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
