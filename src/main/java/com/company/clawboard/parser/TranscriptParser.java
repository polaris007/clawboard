package com.company.clawboard.parser;

import com.company.clawboard.parser.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JsonlRecord record = messageParser.parseLine(line);

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

        // Detect issues
        List<IssueDetector.DetectedIssue> allIssues = new ArrayList<>();
        for (MessageRecord msg : messages) {
            allIssues.addAll(issueDetector.detectIssues(msg));
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
