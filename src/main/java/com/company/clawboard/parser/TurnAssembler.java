package com.company.clawboard.parser;

import com.company.clawboard.parser.model.ChainStep;
import com.company.clawboard.parser.model.MessageRecord;
import com.company.clawboard.parser.SystemMessageFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TurnAssembler {

    private final SystemMessageFilter systemMessageFilter;

    public TurnAssembler(SystemMessageFilter systemMessageFilter) {
        this.systemMessageFilter = systemMessageFilter;
    }

    public record AssembledTurn(
        String userInput,
        List<ChainStep> chainSteps,
        boolean isComplete,
        boolean hasError,
        String status,
        String startMessageId,
        String endMessageId,
        boolean isSystemTurn
    ) {}

    public AssembledTurn assembleTurn(List<MessageRecord> messages) {
        if (messages == null || messages.isEmpty()) {
            return new AssembledTurn(null, List.of(), false, false, "incomplete", null, null, false);
        }

        // Find user input (first user message)
        String userInput = messages.stream()
            .filter(m -> "user".equals(m.role()))
            .map(MessageRecord::textContent)
            .findFirst()
            .orElse(null);

        // Build chain steps
        List<ChainStep> chainSteps = buildChainSteps(messages);

        // Determine completion and errors
        boolean hasError = messages.stream()
            .anyMatch(m -> m.isError() || (m.errorMessage() != null && !m.errorMessage().isEmpty()));

        boolean isComplete = messages.stream()
            .anyMatch(m -> "assistant".equals(m.role()) && m.toolCalls().isEmpty());

        String status = isComplete ? "complete" : "incomplete";
        
        // Check if this is a system turn
        boolean isSystemTurn = false;
        if (userInput != null) {
            isSystemTurn = systemMessageFilter.isSystemGeneratedUserMessage(userInput);
        }
        
        // Get start and end message IDs
        String startMessageId = messages.get(0).id();
        String endMessageId = messages.get(messages.size() - 1).id();

        return new AssembledTurn(userInput, chainSteps, isComplete, hasError, status, startMessageId, endMessageId, isSystemTurn);
    }

    private List<ChainStep> buildChainSteps(List<MessageRecord> messages) {
        List<ChainStep> steps = new ArrayList<>();
        int stepOrder = 0;

        for (MessageRecord msg : messages) {
            if ("user".equals(msg.role())) {
                steps.add(new ChainStep(stepOrder++, "user_input", "User Input", msg.id(), null, true, msg.epochMs(), null));
            } else if ("assistant".equals(msg.role())) {
                if (!msg.toolCalls().isEmpty()) {
                    for (var toolCall : msg.toolCalls()) {
                        steps.add(new ChainStep(stepOrder++, "tool_call", toolCall.name(), msg.id(), toolCall.id(), true, msg.epochMs(), msg.durationMs()));
                    }
                } else {
                    steps.add(new ChainStep(stepOrder++, "reply", "Assistant Reply", msg.id(), null, true, msg.epochMs(), msg.durationMs()));
                }
            }
        }

        return steps;
    }
}
