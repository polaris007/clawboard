package com.company.clawboard.parser;

import com.company.clawboard.parser.model.ChainStep;
import com.company.clawboard.parser.model.MessageRecord;
import com.company.clawboard.parser.SystemMessageFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        long startTime,
        long endTime,
        boolean isSystemTurn,
        // 新增统计字段
        long totalInputTokens,
        long totalOutputTokens,
        long totalTokens,
        int toolCallsCount,
        int toolCallsSuccess,
        int toolCallsError
    ) {}

    public AssembledTurn assembleTurn(List<MessageRecord> messages) {
        if (messages == null || messages.isEmpty()) {
            return new AssembledTurn(null, List.of(), false, false, "incomplete", null, null, 0, 0, false, 0, 0, 0, 0, 0, 0);
        }

        // Find user input (first user message)
        String userInput = messages.stream()
            .filter(m -> "user".equals(m.role()))
            .map(MessageRecord::textContent)
            .findFirst()
            .orElse(null);

        // 初始化统计变量
        long totalInputTokens = 0;
        long totalOutputTokens = 0;
        long totalTokens = 0;
        int toolCallsCount = 0;
        int toolCallsError = 0;

        // 构建 toolCall -> toolResult 映射
        Map<String, MessageRecord> toolResultMap = new HashMap<>();
        for (MessageRecord msg : messages) {
            if ("toolResult".equals(msg.role()) && msg.toolCallId() != null) {
                toolResultMap.put(msg.toolCallId(), msg);
            }
        }

        // Build chain steps
        List<ChainStep> chainSteps = buildChainSteps(messages);

        // 统计 tokens 和 toolCalls
        for (MessageRecord msg : messages) {
            // 只统计 assistant message 的 usage
            if ("assistant".equals(msg.role())) {
                // 统计 tokens
                if (msg.usage() != null) {
                    totalInputTokens += msg.usage().inputTokens();
                    totalOutputTokens += msg.usage().outputTokens();
                    totalTokens += msg.usage().totalTokens();
                }
                
                // 统计 toolCalls
                if (msg.toolCalls() != null && !msg.toolCalls().isEmpty()) {
                    for (MessageRecord.ToolCallInfo tc : msg.toolCalls()) {
                        toolCallsCount++;
                        
                        boolean hasError = false;
                        
                        // 检查是否有对应的 toolResult
                        MessageRecord toolResult = toolResultMap.get(tc.id());
                        if (toolResult == null) {
                            hasError = true;  // 缺少 toolResult
                        } else if (toolResult.isError() || 
                                   (toolResult.errorMessage() != null && !toolResult.errorMessage().isEmpty())) {
                            hasError = true;  // toolResult 有错误
                        }
                        
                        // 检查 stopReason
                        String stopReason = msg.stopReason();
                        if ("error".equals(stopReason) || "aborted".equals(stopReason)) {
                            hasError = true;
                        }
                        
                        if (hasError) {
                            toolCallsError++;
                        }
                    }
                }
            }
        }

        int toolCallsSuccess = toolCallsCount - toolCallsError;

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
        
        // Get start and end times
        long startTime = messages.get(0).epochMs();
        long endTime = messages.get(messages.size() - 1).epochMs();

        return new AssembledTurn(
            userInput, chainSteps, isComplete, hasError, status, 
            startMessageId, endMessageId, startTime, endTime, isSystemTurn,
            totalInputTokens, totalOutputTokens, totalTokens,
            toolCallsCount, toolCallsSuccess, toolCallsError
        );
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
