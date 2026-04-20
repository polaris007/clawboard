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
        Integer lineNumber,
        String runId,
        String provider,
        String model,
        String messageId,
        String employeeId
    ) {}

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

    private static final List<String> NORMAL_STOP_REASONS = List.of("stop", "toolUse", "length");

    private record ErrorPattern(String category, List<Pattern> patterns) {}

    public List<DetectedIssue> detectIssues(MessageRecord msg) {
        List<DetectedIssue> issues = new ArrayList<>();

        if ("assistant".equals(msg.role())) {
            if (msg.errorMessage() != null && !msg.errorMessage().isEmpty()) {
                String errorMsg = msg.errorMessage();

                for (ErrorPattern ep : ERROR_PATTERNS) {
                    boolean categoryMatched = false;
                    for (Pattern pattern : ep.patterns()) {
                        if (pattern.matcher(errorMsg).find()) {
                            issues.add(new DetectedIssue(
                                ep.category(),
                                "high",
                                "在message事件中检测到" + getCategoryDescription(ep.category()),
                                truncate(errorMsg, 500),
                                "message",
                                null,
                                analyzeCause(ep.category(), errorMsg),
                                null,
                                null,
                                null,
                                msg.lineNumber(),
                                null,
                                msg.provider(),
                                msg.model(),
                                msg.id(),
                                null
                            ));
                            categoryMatched = true;
                            break;
                        }
                    }
                    if (categoryMatched) {
                        continue;
                    }
                }
            }
        }

        if (msg.stopReason() != null && !msg.stopReason().isEmpty()) {
            String stopReason = msg.stopReason();

            if (!NORMAL_STOP_REASONS.contains(stopReason)) {
                String errorMsg = msg.errorMessage();
                String severity = ("aborted".equals(stopReason) || "error".equals(stopReason)) ? "high" : "medium";

                issues.add(new DetectedIssue(
                    "abnormal_stop",
                    severity,
                    "检测到异常停止原因: " + stopReason,
                    truncate(errorMsg != null ? errorMsg : "Unexpected stop reason: " + stopReason, 500),
                    "message",
                    null,
                    analyzeCause("modelErrors", errorMsg),
                    null,
                    null,
                    null,
                    msg.lineNumber(),
                    null,
                    msg.provider(),
                    msg.model(),
                    msg.id(),
                    null
                ));
            }
        }

        return issues;
    }

    public List<DetectedIssue> detectCustomEventIssues(String customType, String dataJson, String lineId, String timestamp, Integer lineNumber) {
        List<DetectedIssue> issues = new ArrayList<>();
        if (customType == null || customType.isEmpty()) {
            return issues;
        }

        String customTypeLower = customType.toLowerCase();
        String dataStr = (dataJson != null ? dataJson : "").toLowerCase();
        log.debug("detectCustomEventIssues: customType={}, customTypeLower={}, dataJson={}, dataStr={}", customType, customTypeLower, dataJson, dataStr);

        for (ErrorPattern ep : ERROR_PATTERNS) {
            for (Pattern pattern : ep.patterns()) {
                if (pattern.matcher(customTypeLower).find() || pattern.matcher(dataStr).find()) {
                    String errorMessage = dataJson != null ? dataJson : customType;
                    issues.add(new DetectedIssue(
                        ep.category(),
                        "high",
                        "检测到" + getCategoryDescription(ep.category()) + "事件",
                        truncate(errorMessage, 500),
                        customType,
                        null,
                        analyzeCause(ep.category(), errorMessage),
                        null,
                        null,
                        null,
                        lineNumber,
                        extractRunId(dataJson),
                        extractProvider(dataJson),
                        extractModel(dataJson),
                        lineId != null ? lineId : "custom-" + System.currentTimeMillis(),
                        null
                    ));
                    break;
                }
            }
        }

        return issues;
    }

    private String extractRunId(String dataJson) {
        if (dataJson == null) return null;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"runId\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(dataJson);
        return m.find() ? m.group(1) : null;
    }

    private String extractProvider(String dataJson) {
        if (dataJson == null) return null;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"provider\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(dataJson);
        return m.find() ? m.group(1) : null;
    }

    private String extractModel(String dataJson) {
        if (dataJson == null) return null;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"model\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(dataJson);
        return m.find() ? m.group(1) : null;
    }

    private String analyzeCause(String errorType, String errorMessage) {
        String msg = errorMessage != null ? errorMessage.toLowerCase() : "";
        return switch (errorType) {
            case "modelErrors" -> {
                if (msg.contains("timeout") || msg.contains("idle"))
                    yield "模型服务响应超时，可能原因：1) 网络延迟或不稳定；2) 模型服务端负载过高；3) Prompt过长导致处理时间增加；4) 前置工具执行失败导致模型等待用户输入";
                if (msg.contains("aborted") || msg.contains("cancel"))
                    yield "请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消";
                if (msg.contains("context") || msg.contains("token"))
                    yield "上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积";
                yield "模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完";
            }
            case "timeoutErrors" -> msg.contains("idle")
                ? "空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输"
                : "请求超时，可能原因：1) 网络延迟过高；2) 服务端处理缓慢；3) 防火墙或代理拦截；4) DNS解析超时";
            case "rateLimitErrors" -> "触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制";
            case "toolErrors" -> (msg.contains("permission") || msg.contains("denied"))
                ? "工具执行权限不足，可能原因：1) 文件系统权限限制；2) 沙箱环境约束；3) 需要sudo权限但未配置；4) 访问受限资源"
                : "工具执行失败，可能原因：1) 命令不存在或路径错误；2) 依赖未安装；3) 输入参数不正确；4) 工具内部逻辑错误";
            case "permissionErrors" -> "权限验证失败，可能原因：1) API密钥无效；2) OAuth token过期；3) IP白名单限制；4) 账户被禁用或欠费";
            case "parsingErrors" -> "数据解析失败，可能原因：1) JSON格式不正确；2) 编码问题；3) 数据结构与预期不符；4) 特殊字符未转义";
            case "networkErrors" -> {
                if (msg.contains("refused"))
                    yield "连接被拒绝，可能原因：1) 目标服务未启动；2) 端口未开放；3) 防火墙阻止；4) 服务地址配置错误";
                if (msg.contains("notfound") || msg.contains("dns"))
                    yield "DNS解析失败，可能原因：1) 域名拼写错误；2) DNS服务器故障；3) 网络连接中断；4) hosts文件配置问题";
                yield "网络连接错误，可能原因：1) 网络不稳定；2) 代理配置错误；3) SSL证书问题；4) 服务端重启或维护";
            }
            default -> "未知错误类型，需要人工分析具体原因";
        };
    }

    private String getCategoryDescription(String category) {
        return switch (category) {
            case "modelErrors" -> "模型API错误";
            case "timeoutErrors" -> "超时错误";
            case "rateLimitErrors" -> "速率限制错误";
            case "toolErrors" -> "工具执行错误";
            case "permissionErrors" -> "权限错误";
            case "parsingErrors" -> "解析错误";
            case "networkErrors" -> "网络错误";
            default -> category;
        };
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
