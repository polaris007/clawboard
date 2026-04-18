package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class IssueDetector {

    public record DetectedIssue(String errorType, String severity, String description, String errorMessage) {}

    public List<DetectedIssue> detectIssues(MessageRecord msg) {
        List<DetectedIssue> issues = new ArrayList<>();

        // Check for assistant errors
        if ("assistant".equals(msg.role()) && msg.errorMessage() != null && !msg.errorMessage().isEmpty()) {
            issues.add(new DetectedIssue(
                "ASSISTANT_ERROR",
                "high",
                "Assistant returned an error message",
                truncate(msg.errorMessage(), 500)
            ));
        }

        // Check for tool errors
        if (msg.isError()) {
            issues.add(new DetectedIssue(
                "TOOL_ERROR",
                "medium",
                "Tool execution failed: " + msg.toolName(),
                msg.textContent()
            ));
        }

        return issues;
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
