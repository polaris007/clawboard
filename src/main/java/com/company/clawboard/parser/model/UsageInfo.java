package com.company.clawboard.parser.model;

import java.util.List;

public record UsageInfo(
    int inputTokens,
    int outputTokens,
    int cacheReadTokens,
    int cacheWriteTokens,
    int totalTokens,
    java.math.BigDecimal costTotal
) {}
