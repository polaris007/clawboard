package com.company.clawboard.parser.model;

public record ChainStep(
    int stepOrder,
    String nodeType,     // user_input, tool_call, skill_call, reply
    String nodeName,
    String messageId,
    String toolCallId,   // nullable
    boolean status,
    long timeStamp,      // epoch ms
    Integer durationMs   // nullable
) {}
