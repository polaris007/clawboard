package com.company.clawboard.parser.model;

import java.util.List;

public record MessageRecord(
    String id,                    // message ID (8-char hex)
    String parentId,
    String timestamp,             // ISO string
    String role,                  // user / assistant / toolResult
    String textContent,           // extracted text (first text block)
    String errorMessage,          // dedicated errorMessage field (assistant messages)
    List<ToolCallInfo> toolCalls,  // from assistant content blocks
    String toolCallId,            // for toolResult
    String toolName,              // for toolResult
    boolean isError,              // toolResult.isError
    String provider,
    String model,
    String stopReason,
    UsageInfo usage,
    int durationMs,
    long epochMs                  // message.timestamp (epoch ms)
) implements JsonlRecord {

    public record ToolCallInfo(String id, String name, String argumentsJson) {}
}
