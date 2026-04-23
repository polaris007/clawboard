package com.company.clawboard.parser;

import com.company.clawboard.parser.IssueDetector.DetectedIssue;
import com.company.clawboard.parser.model.MessageRecord;
import com.company.clawboard.parser.model.CustomRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FlowIntegrityChecker {

    private static final List<String> NORMAL_STOP_REASONS = List.of("stop", "toolUse", "length");

    public List<DetectedIssue> checkFlowIntegrity(List<Object> allEvents, List<MessageRecord> messages, String employeeId) {
        List<DetectedIssue> issues = new ArrayList<>();

        if (allEvents == null || allEvents.isEmpty()) {
            return issues;
        }

        for (int i = 0; i < allEvents.size(); i++) {
            Object currentObj = allEvents.get(i);
            Object nextObj = (i + 1 < allEvents.size()) ? allEvents.get(i + 1) : null;

            if (currentObj instanceof MessageRecord currentMsg) {
                String currentRole = currentMsg.role();

                if ("user".equals(currentRole)) {
                    if (isSystemGeneratedMessage(currentMsg)) {
                        continue;
                    }

                    if (nextObj == null) {
                        issues.add(new DetectedIssue(
                            "flow_integrity_no_reply",
                            "high",
                            "用户提问后没有任何回复（文件在此结束）",
                            "Expected assistant message after user message, but reached end of file\n" +
                            "Line: " + currentMsg.id() + ", Timestamp: " + currentMsg.epochMs(),
                            "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                            employeeId
                        ));
                    } else if (nextObj instanceof MessageRecord nextMsg) {
                        if (!"assistant".equals(nextMsg.role())) {
                            if (nextMsg.textContent() == null || !nextMsg.textContent().toLowerCase().contains("prompt-error")) {
                                issues.add(new DetectedIssue(
                                    "flow_integrity_no_reply",
                                    "high",
                                    "用户提问后的下一条消息角色是\"" + nextMsg.role() + "\"，而非预期的assistant",
                                    "Expected \"assistant\" after \"user\", but got \"" + nextMsg.role() + "\"\n" +
                                    "Line: " + currentMsg.id() + ", Timestamp: " + currentMsg.epochMs(),
                                    "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                                    employeeId
                                ));
                            }
                        }
                    } else if (nextObj instanceof CustomRecord nextCustom) {
                        if (nextCustom.customType() == null || !nextCustom.customType().contains("prompt-error")) {
                            issues.add(new DetectedIssue(
                                "flow_integrity_no_reply",
                                "high",
                                "用户提问后的下一条消息是custom事件\"" + nextCustom.customType() + "\"，而非预期的assistant",
                                "Expected \"assistant\" after \"user\", but got \"custom:" + nextCustom.customType() + "\"\n" +
                                "Line: " + currentMsg.id() + ", Timestamp: " + currentMsg.epochMs(),
                                "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                                employeeId
                            ));
                        }
                    }
                }

                if ("assistant".equals(currentRole) && !currentMsg.toolCalls().isEmpty()) {
                    String stopReason = currentMsg.stopReason();
                    if (stopReason != null && ("aborted".equals(stopReason) || "error".equals(stopReason))) {
                        continue;
                    }

                    if (nextObj == null) {
                        issues.add(new DetectedIssue(
                            "flow_integrity_missing_tool_result",
                            "high",
                            "Assistant调用了工具但没有收到工具执行结果（文件在此结束）",
                            "Expected toolResult after toolCall, but reached end of file\n" +
                            "Tool: " + currentMsg.toolCalls().get(0).name() + ", Line: " + currentMsg.id(),
                            "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                            employeeId
                        ));
                    } else if (nextObj instanceof MessageRecord nextMsg) {
                        if (!"toolResult".equals(nextMsg.role())) {
                            issues.add(new DetectedIssue(
                                "flow_integrity_missing_tool_result",
                                "high",
                                "Assistant调用工具后的下一条消息角色是\"" + nextMsg.role() + "\"，而非预期的toolResult",
                                "Expected \"toolResult\" after \"toolCall\", but got \"" + nextMsg.role() + "\"\n" +
                                "Tool: " + currentMsg.toolCalls().get(0).name() + ", Line: " + currentMsg.id(),
                                "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                                employeeId
                            ));
                        }
                    } else if (nextObj instanceof CustomRecord nextCustom) {
                        if (nextCustom.customType() == null || !nextCustom.customType().contains("prompt-error")) {
                            issues.add(new DetectedIssue(
                                "flow_integrity_missing_tool_result",
                                "high",
                                "Assistant调用工具后的下一条消息是custom事件\"" + nextCustom.customType() + "\"，而非预期的toolResult",
                                "Expected \"toolResult\" after \"toolCall\", but got \"custom:" + nextCustom.customType() + "\"\n" +
                                "Tool: " + currentMsg.toolCalls().get(0).name() + ", Line: " + currentMsg.id(),
                                "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                                employeeId
                            ));
                        }
                    }
                }

                if ("toolResult".equals(currentRole)) {
                    if ("sessions_yield".equals(currentMsg.toolName())) {
                        continue;
                    }

                    if (nextObj == null) {
                        issues.add(new DetectedIssue(
                            "flow_integrity_missing_final_answer",
                            "medium",
                            "工具执行完成后没有Assistant的最终回复（文件在此结束）",
                            "Expected assistant message after toolResult, but reached end of file\n" +
                            "Line: " + currentMsg.id(),
                            "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                            employeeId
                        ));
                    } else if (nextObj instanceof MessageRecord nextMsg) {
                        if (!"assistant".equals(nextMsg.role()) && !"toolResult".equals(nextMsg.role())) {
                            if (nextMsg.textContent() == null || !nextMsg.textContent().toLowerCase().contains("prompt-error")) {
                                issues.add(new DetectedIssue(
                                    "flow_integrity_missing_final_answer",
                                    "medium",
                                    "工具执行完成后的下一条消息角色是\"" + nextMsg.role() + "\"，而非预期的assistant最终回复或另一个toolResult",
                                    "Expected \"assistant\" or \"toolResult\" after \"toolResult\", but got \"" + nextMsg.role() + "\"\n" +
                                    "Line: " + currentMsg.id(),
                                    "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                                    employeeId
                                ));
                            }
                        }
                    } else if (nextObj instanceof CustomRecord nextCustom) {
                        if (nextCustom.customType() == null || !nextCustom.customType().toLowerCase().contains("prompt-error")) {
                            issues.add(new DetectedIssue(
                                "flow_integrity_missing_final_answer",
                                "medium",
                                "工具执行完成后出现custom事件\"" + nextCustom.customType() + "\"，而非预期的assistant最终回复",
                                "Expected \"assistant\" after \"toolResult\", but got \"custom:" + nextCustom.customType() + "\"\n" +
                                "Line: " + currentMsg.id(),
                                "message", null, null, null, null, null, currentMsg.lineNumber(), null, null, null, currentMsg.id(), currentMsg.epochMs(),
                                employeeId
                            ));
                        }
                    }
                }
            }
        }

        return issues;
    }

    private boolean isSystemGeneratedMessage(MessageRecord msg) {
        String content = msg.textContent();
        if (content == null || content.isEmpty()) {
            return false;
        }

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
}