package com.company.clawboard.parser;

import com.company.clawboard.parser.IssueDetector.DetectedIssue;
import com.company.clawboard.parser.model.MessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Check message flow integrity within a session
 * Detects broken conversation flows like missing replies, missing tool results, etc.
 */
@Slf4j
@Component
public class FlowIntegrityChecker {

    /**
     * Check flow integrity for a list of messages in a session
     * @param messages Ordered list of messages in the session
     * @return List of detected flow integrity issues
     */
    public List<DetectedIssue> checkFlowIntegrity(List<MessageRecord> messages) {
        List<DetectedIssue> issues = new ArrayList<>();
        
        if (messages == null || messages.isEmpty()) {
            return issues;
        }

        for (int i = 0; i < messages.size(); i++) {
            MessageRecord current = messages.get(i);
            MessageRecord next = (i + 1 < messages.size()) ? messages.get(i + 1) : null;

            // Rule 1: After user message, expect assistant message
            if ("user".equals(current.role())) {
                // Skip system-generated user messages
                if (isSystemGeneratedMessage(current)) {
                    continue;
                }

                if (next == null) {
                    // File ends after user message
                    issues.add(new DetectedIssue(
                        "flow_integrity_no_reply",
                        "high",
                        "用户提问后没有任何回复（文件在此结束）",
                        "Expected assistant message after user message, but reached end of file\n" +
                        "Line: " + current.id() + ", Timestamp: " + current.epochMs(),
                        "message", null, null, null, null, null, current.lineNumber()
                    ));
                } else if (!"assistant".equals(next.role())) {
                    // Next message is not assistant
                    // Skip if next is an error event (will be counted separately)
                    if (!isErrorMessage(next)) {
                        issues.add(new DetectedIssue(
                            "flow_integrity_no_reply",
                            "high",
                            "用户提问后的下一条消息角色是\"" + next.role() + "\"，而非预期的assistant",
                            "Expected \"assistant\" after \"user\", but got \"" + next.role() + "\"\n" +
                            "Line: " + current.id() + ", Timestamp: " + current.epochMs(),
                            "message", null, null, null, null, null, current.lineNumber()
                        ));
                    }
                }
            }

            // Rule 2: After toolCall, expect toolResult
            if ("assistant".equals(current.role()) && !current.toolCalls().isEmpty()) {
                if (next == null) {
                    // File ends after toolCall
                    issues.add(new DetectedIssue(
                        "flow_integrity_missing_tool_result",
                        "high",
                        "Assistant调用了工具但没有收到工具执行结果（文件在此结束）",
                        "Expected toolResult after toolCall, but reached end of file\n" +
                        "Tool: " + current.toolCalls().get(0).name() + ", Line: " + current.id(),
                        "message", null, null, null, null, null, current.lineNumber()
                    ));
                } else if (!"toolResult".equals(next.role())) {
                    issues.add(new DetectedIssue(
                        "flow_integrity_missing_tool_result",
                        "high",
                        "Assistant调用工具后的下一条消息角色是\"" + next.role() + "\"，而非预期的toolResult",
                        "Expected \"toolResult\" after \"toolCall\", but got \"" + next.role() + "\"\n" +
                        "Tool: " + current.toolCalls().get(0).name() + ", Line: " + current.id(),
                        "message", null, null, null, null, null, current.lineNumber()
                    ));
                }
            }

            // Rule 3: After toolResult, expect assistant final answer or another toolResult
            if ("toolResult".equals(current.role())) {
                if (next == null) {
                    // File ends after toolResult
                    issues.add(new DetectedIssue(
                        "flow_integrity_missing_final_answer",
                        "medium",
                        "工具执行完成后没有Assistant的最终回复（文件在此结束）",
                        "Expected assistant message after toolResult, but reached end of file\n" +
                        "Line: " + current.id(),
                        "message", null, null, null, null, null, current.lineNumber()
                    ));
                } else if (!"assistant".equals(next.role()) && !"toolResult".equals(next.role())) {
                    issues.add(new DetectedIssue(
                        "flow_integrity_missing_final_answer",
                        "medium",
                        "工具执行完成后的下一条消息角色是\"" + next.role() + "\"，而非预期的assistant最终回复或另一个toolResult",
                        "Expected \"assistant\" or \"toolResult\" after \"toolResult\", but got \"" + next.role() + "\"\n" +
                        "Line: " + current.id(),
                        "message", null, null, null, null, null, current.lineNumber()
                    ));
                }
            }
        }

        return issues;
    }

    /**
     * Check if a message is system-generated (should be skipped in flow checks)
     * Matching Python's is_system_generated_user_message function
     */
    private boolean isSystemGeneratedMessage(MessageRecord msg) {
        String content = msg.textContent();
        if (content == null || content.isEmpty()) {
            return false;
        }
        
        // System message patterns (matching Python script)
        if (content.contains("A new session was started via /new or /reset")) {
            return true;
        }
        if (content.contains("Run your Session Startup sequence")) {
            return true;
        }
        if (content.contains("Read HEARTBEAT.md if it exists")) {
            return true;
        }
        if (content.contains("<<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>")) {
            return true;
        }
        if (content.startsWith("System: [")) {
            return true;
        }
        
        return false;
    }

    /**
     * Check if a message is an error message
     */
    private boolean isErrorMessage(MessageRecord msg) {
        return msg.isError() || 
               (msg.errorMessage() != null && !msg.errorMessage().isEmpty()) ||
               (msg.textContent() != null && msg.textContent().contains("error"));
    }
}
