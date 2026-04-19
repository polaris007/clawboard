package com.company.clawboard.parser;

import com.company.clawboard.parser.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageParser {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Parse a single JSONL line into a typed record.
     * Returns null for unparseable lines or unknown types.
     * @param line The JSONL line to parse
     * @param lineNumber The line number in the file (1-based), use -1 if unknown
     */
    public JsonlRecord parseLine(String line, int lineNumber) {
        if (line == null || line.isBlank()) return null;
        try {
            JsonNode root = mapper.readTree(line);
            String type = root.path("type").asText("");
            return switch (type) {
                case "session" -> parseSession(root);
                case "message" -> parseMessage(root, lineNumber);
                case "custom" -> parseCustom(root);
                case "model_change", "thinking_level_change" -> parseMetadata(root, type);
                default -> null;
            };
        } catch (Exception e) {
            log.debug("Failed to parse JSONL line (data quality issue): {}", truncate(line, 100));
            return null;
        }
    }

    private SessionRecord parseSession(JsonNode root) {
        return new SessionRecord(
            root.path("id").asText(),
            root.path("version").asInt(0),
            root.path("timestamp").asText(),
            root.path("cwd").asText("")
        );
    }

    private MessageRecord parseMessage(JsonNode root, int lineNumber) {
        JsonNode msg = root.path("message");
        String role = msg.path("role").asText("");
        String id = root.path("id").asText("");
        String parentId = root.path("parentId").asText(null);
        String timestamp = root.path("timestamp").asText("");

        // Extract text content (first text block)
        String textContent = extractTextContent(msg.path("content"));

        // Extract dedicated errorMessage field (assistant messages)
        String errorMessage = msg.has("errorMessage") ? msg.get("errorMessage").asText(null) : null;

        // Extract tool calls (assistant messages)
        List<MessageRecord.ToolCallInfo> toolCalls = extractToolCalls(msg.path("content"));

        // For toolResult
        String toolCallId = msg.path("toolCallId").asText(null);
        String toolName = msg.path("toolName").asText(null);
        boolean isError = msg.path("isError").asBoolean(false);

        // Provider/model (on assistant messages)
        String provider = msg.path("provider").asText(null);
        String model = msg.path("model").asText(null);
        String stopReason = msg.path("stopReason").asText(null);

        // Usage
        UsageInfo usage = extractUsage(msg.path("usage"));

        // Duration
        int durationMs = msg.path("durationMs").asInt(0);

        // Epoch timestamp
        long epochMs = msg.path("timestamp").asLong(0);

        return new MessageRecord(id, parentId, timestamp, role,
            textContent, errorMessage, toolCalls, toolCallId, toolName, isError,
            provider, model, stopReason, usage, durationMs, epochMs, lineNumber);
    }

    private String extractTextContent(JsonNode contentArray) {
        if (contentArray == null || !contentArray.isArray()) return null;
        for (JsonNode block : contentArray) {
            if ("text".equals(block.path("type").asText())) {
                return block.path("text").asText(null);
            }
        }
        return null;
    }

    private List<MessageRecord.ToolCallInfo> extractToolCalls(JsonNode contentArray) {
        List<MessageRecord.ToolCallInfo> calls = new ArrayList<>();
        if (contentArray == null || !contentArray.isArray()) return calls;
        for (JsonNode block : contentArray) {
            if ("toolCall".equals(block.path("type").asText())) {
                calls.add(new MessageRecord.ToolCallInfo(
                    block.path("id").asText(""),
                    block.path("name").asText(""),
                    block.has("arguments") ? block.get("arguments").toString() : "{}"
                ));
            }
        }
        return calls;
    }

    private UsageInfo extractUsage(JsonNode usageNode) {
        if (usageNode == null || usageNode.isMissingNode()) return null;
        JsonNode cost = usageNode.path("cost");
        return new UsageInfo(
            usageNode.path("input").asInt(0),
            usageNode.path("output").asInt(0),
            usageNode.path("cacheRead").asInt(0),
            usageNode.path("cacheWrite").asInt(0),
            usageNode.path("totalTokens").asInt(0),
            cost.isMissingNode() ? BigDecimal.ZERO
                : new BigDecimal(cost.path("total").asText("0"))
        );
    }

    private CustomRecord parseCustom(JsonNode root) {
        return new CustomRecord(
            root.path("customType").asText(""),
            root.path("id").asText(""),
            root.path("timestamp").asText(""),
            root.get("data")
        );
    }

    private MetadataRecord parseMetadata(JsonNode root, String type) {
        return new MetadataRecord(
            type,
            root.path("id").asText(""),
            root.path("provider").asText(null),
            root.path("modelId").asText(null)
        );
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
