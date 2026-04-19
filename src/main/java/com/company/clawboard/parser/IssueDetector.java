package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IssueDetector {

    public record DetectedIssue(
        String errorType, 
        String severity, 
        String description, 
        String errorMessage,
        String eventType,
        String userInput,
        String causeAnalysis,
        String filePath,
        String errorLineContent,
        String nextLineContent,
        Integer lineNumber
    ) {}

    // Error pattern definitions (matching Python script)
    private static final List<ErrorPattern> ERROR_PATTERNS = List.of(
        new ErrorPattern("modelErrors", List.of(
            Pattern.compile("model.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("api.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("LLM.*timeout", Pattern.CASE_INSENSITIVE),
            Pattern.compile("operation.*aborted", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\baborted\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("prompt.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("request.*failed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("inference.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("generation.*failed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("model.*unavailable", Pattern.CASE_INSENSITIVE)
        )),
        new ErrorPattern("timeoutErrors", List.of(
            Pattern.compile("timeout", Pattern.CASE_INSENSITIVE),
            Pattern.compile("timed.*out", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ETIMEDOUT", Pattern.CASE_INSENSITIVE),
            Pattern.compile("idle.*timeout", Pattern.CASE_INSENSITIVE),
            Pattern.compile("connection.*timeout", Pattern.CASE_INSENSITIVE),
            Pattern.compile("request.*timeout", Pattern.CASE_INSENSITIVE),
            Pattern.compile("deadline.*exceeded", Pattern.CASE_INSENSITIVE)
        )),
        new ErrorPattern("rateLimitErrors", List.of(
            Pattern.compile("rate.*limit", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b429\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("too.*many.*requests", Pattern.CASE_INSENSITIVE),
            Pattern.compile("quota.*exceeded", Pattern.CASE_INSENSITIVE),
            Pattern.compile("throttl", Pattern.CASE_INSENSITIVE)
        )),
        new ErrorPattern("toolErrors", List.of(
            Pattern.compile("tool.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("tool.*failed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("execution.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("command.*failed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("tool.*timeout", Pattern.CASE_INSENSITIVE)
        )),
        new ErrorPattern("permissionErrors", List.of(
            Pattern.compile("permission.*denied", Pattern.CASE_INSENSITIVE),
            Pattern.compile("access.*denied", Pattern.CASE_INSENSITIVE),
            Pattern.compile("forbidden", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b403\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("unauthorized", Pattern.CASE_INSENSITIVE),
            Pattern.compile("auth.*error", Pattern.CASE_INSENSITIVE)
        )),
        new ErrorPattern("parsingErrors", List.of(
            Pattern.compile("parse.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("invalid.*json", Pattern.CASE_INSENSITIVE),
            Pattern.compile("syntax.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("malformed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("unexpected.*token", Pattern.CASE_INSENSITIVE)
        )),
        new ErrorPattern("networkErrors", List.of(
            Pattern.compile("network.*error", Pattern.CASE_INSENSITIVE),
            Pattern.compile("connection.*refused", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ECONNREFUSED", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ENOTFOUND", Pattern.CASE_INSENSITIVE),
            Pattern.compile("socket.*hang.*up", Pattern.CASE_INSENSITIVE),
            Pattern.compile("fetch.*failed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("dns.*error", Pattern.CASE_INSENSITIVE)
        ))
    );

    // Normal stop reasons (matching Python script exactly)
    private static final List<String> NORMAL_STOP_REASONS = List.of("stop", "toolUse", "length");

    private record ErrorPattern(String category, List<Pattern> patterns) {}

    public List<DetectedIssue> detectIssues(MessageRecord msg) {
        List<DetectedIssue> issues = new ArrayList<>();

        // Check for assistant errors with pattern matching
        boolean hasErrorPattern = false;
        if ("assistant".equals(msg.role()) && msg.errorMessage() != null && !msg.errorMessage().isEmpty()) {
            String errorMsg = msg.errorMessage();
            
            // Try to match error patterns
            for (ErrorPattern ep : ERROR_PATTERNS) {
                for (Pattern pattern : ep.patterns()) {
                    if (pattern.matcher(errorMsg).find()) {
                        issues.add(new DetectedIssue(
                            ep.category(),
                            "high",
                            "Detected " + getCategoryDescription(ep.category()),
                            truncate(errorMsg, 500),
                            "message",
                            null,  // userInput - extracted from context in TranscriptParser
                            analyzeCause(ep.category()),
                            null,  // filePath - set in TranscriptParser
                            null,  // errorLineContent - set in FlowIntegrityChecker
                            null,  // nextLineContent - set in FlowIntegrityChecker
                            msg.lineNumber()
                        ));
                        hasErrorPattern = true;
                        break;
                    }
                }
                if (hasErrorPattern) break;
            }
            
            // If no pattern matched, use generic ASSISTANT_ERROR
            if (!hasErrorPattern) {
                issues.add(new DetectedIssue(
                    "ASSISTANT_ERROR",
                    "high",
                    "Assistant returned an error message",
                    truncate(errorMsg, 500),
                    "message",
                    null,
                    "助手返回了错误消息，可能是模型API调用失败或参数错误",
                    null,
                    null,
                    null,
                    msg.lineNumber()
                ));
                hasErrorPattern = true;  // Mark as detected to avoid double counting with abnormal_stop
            }
        }

        // Check for abnormal stop reasons
        if ("assistant".equals(msg.role()) && msg.stopReason() != null && !msg.stopReason().isEmpty()) {
            String stopReason = msg.stopReason();
            
            if (!NORMAL_STOP_REASONS.contains(stopReason)) {
                String errorMsg = msg.errorMessage() != null ? msg.errorMessage() : "Unexpected stop reason: " + stopReason;
                String severity = ("aborted".equals(stopReason) || "error".equals(stopReason)) ? "high" : "medium";
                
                issues.add(new DetectedIssue(
                    "abnormal_stop",
                    severity,
                    "Detected abnormal stop reason: " + stopReason,
                    truncate(errorMsg, 500),
                    "message",
                    null,
                    "会话异常停止，可能是网络中断、系统崩溃或用户主动终止",
                    null,
                    null,
                    null,
                    msg.lineNumber()
                ));
            }
        }

        // Check for tool errors
        if (msg.isError()) {
            issues.add(new DetectedIssue(
                "TOOL_ERROR",
                "medium",
                "Tool execution failed: " + msg.toolName(),
                msg.textContent(),
                "toolResult",
                null,
                "工具执行失败，可能是权限不足、参数错误或外部服务不可用",
                null,
                null,
                null,
                msg.lineNumber()
            ));
        }

        return issues;
    }
    
    private String analyzeCause(String errorType) {
        return switch (errorType) {
            case "modelErrors" -> "模型API调用失败，可能是网络问题、API配额耗尽或服务端错误";
            case "timeoutErrors" -> "请求超时，可能是网络延迟、服务器负载过高或请求处理时间过长";
            case "rateLimitErrors" -> "达到API速率限制，需要降低请求频率或升级API配额";
            case "toolErrors" -> "工具执行失败，可能是权限不足、参数错误或外部服务不可用";
            case "permissionErrors" -> "权限不足，检查API密钥、访问令牌或资源权限配置";
            case "parsingErrors" -> "JSON解析错误，响应格式不正确或包含非法字符";
            case "networkErrors" -> "网络连接错误，检查网络稳定性、DNS解析或防火墙配置";
            default -> "未知错误类型";
        };
    }

    private String getCategoryDescription(String category) {
        return switch (category) {
            case "modelErrors" -> "Model API error";
            case "timeoutErrors" -> "Timeout error";
            case "rateLimitErrors" -> "Rate limit error";
            case "toolErrors" -> "Tool execution error";
            case "permissionErrors" -> "Permission error";
            case "parsingErrors" -> "Parsing error";
            case "networkErrors" -> "Network error";
            default -> category;
        };
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
