package com.company.clawboard.service;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardTranscriptIssue;
import com.company.clawboard.mapper.SessionSummaryMapper;
import com.company.clawboard.mapper.TranscriptIssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerator {

    private final TranscriptIssueMapper issueMapper;
    private final SessionSummaryMapper sessionSummaryMapper;
    private final ClawboardProperties properties;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void generateReport(Long scanId, LocalDateTime scanStartTime) {
        try {
            // Query issues for this scan
            var issues = issueMapper.selectByScanId(scanId);

            // Generate Markdown content
            String markdown = buildMarkdownReport(issues, scanStartTime, scanId);

            // Get reports directory from configuration
            String reportsDir = properties.getReports().getOutputDir();
            
            // Write to file with date-based directory structure and scanId in filename
            String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
            Path reportDir = Paths.get(reportsDir, dateStr);
            Files.createDirectories(reportDir);

            Path reportPath = reportDir.resolve("transcript-comprehensive-issues-scan-" + scanId + ".md");
            Files.writeString(reportPath, markdown);

            log.info("Report generated: {}", reportPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to generate report for scan {}", scanId, e);
        }
    }

    private String buildMarkdownReport(List<DashboardTranscriptIssue> issues, LocalDateTime scanStartTime, Long scanId) {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("# OpenClaw Session Transcript 综合错误检测报告\n\n");
        String nowStr = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .format(java.time.LocalDateTime.now());
        sb.append("**生成时间**: " + nowStr + "\n\n");

        if (issues.isEmpty()) {
            sb.append("No errors found in this scan.\n");
            return sb.toString();
        }

        // Statistics
        sb.append("## 📊 统计概览\n\n");
        
        // Count by type
        java.util.Map<String, Long> byType = issues.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                DashboardTranscriptIssue::getErrorType, 
                java.util.stream.Collectors.counting()
            ));
        
        // Get total conversation turns and problematic turns from database
        int totalConversationTurns = sessionSummaryMapper.selectTotalTurnsByScanId(scanId);
        // Calculate problematic turns by counting unique sessionId-lineNumber combinations (like Python does)
        // Note: Python includes all issues, even those without line numbers
        // We need to handle null line numbers by using a fallback value
        int totalProblematicTurns = (int) issues.stream()
            .map(issue -> {
                Integer lineNumber = issue.getLineNumber();
                // For issues without line numbers, use a combination of sessionId and errorType as fallback
                // This ensures that each unique issue is counted as a separate problematic turn
                return lineNumber != null ? issue.getSessionId() + "-" + lineNumber : (issue.getSessionId() + "-" + issue.getErrorType()).hashCode();
            })
            .distinct()
            .count();
        
        sb.append("- **总错误数**: ").append(issues.size()).append("\n");
        sb.append("- **总对话轮数**: ").append(totalConversationTurns).append(" （排除系统消息）\n");
        sb.append("- **有错误轮数**: ").append(totalProblematicTurns).append(" （存在任何类型错误的轮次）\n");
        if (totalConversationTurns > 0) {
            double problemRate = ((double) totalProblematicTurns / totalConversationTurns) * 100;
            sb.append(String.format("- **错误率**: %.2f%% （有错误轮数 / 总对话轮数）\n", problemRate));
        }
        sb.append("\n");

        // Error type distribution table
        sb.append("### 错误类型分布\n\n");
        sb.append("| 错误类型 | 数量 | 说明 |\n");
        sb.append("|---------|------|------|\n");
        
        java.util.Map<String, String> typeDescriptions = new java.util.HashMap<>();
        typeDescriptions.put("flow_integrity_no_reply", "用户提问后无回复");
        typeDescriptions.put("flow_integrity_missing_tool_result", "工具调用后无执行结果");
        typeDescriptions.put("flow_integrity_missing_final_answer", "工具执行后无最终回复");
        typeDescriptions.put("modelErrors", "模型API错误");
        typeDescriptions.put("timeoutErrors", "超时错误");
        typeDescriptions.put("rateLimitErrors", "速率限制错误");
        typeDescriptions.put("toolErrors", "工具执行错误");
        typeDescriptions.put("permissionErrors", "权限错误");
        typeDescriptions.put("parsingErrors", "解析错误");
        typeDescriptions.put("networkErrors", "网络错误");
        typeDescriptions.put("abnormal_stop", "异常停止");
        typeDescriptions.put("ASSISTANT_ERROR", "助手错误");
        typeDescriptions.put("TOOL_ERROR", "工具错误");
        
        // Sort by count descending
        byType.entrySet().stream()
            .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> {
                String desc = typeDescriptions.getOrDefault(entry.getKey(), entry.getKey());
                // No escaping for underscores - use raw error type
                sb.append(String.format("| %s | %d | %s |\n", entry.getKey(), entry.getValue(), desc));
            });
        
        sb.append("\n---\n\n");

        // Group issues by type
        java.util.Map<String, List<DashboardTranscriptIssue>> groupedByType = issues.stream()
            .collect(java.util.stream.Collectors.groupingBy(DashboardTranscriptIssue::getErrorType));
        
        // Sort groups by size descending
        List<java.util.Map.Entry<String, List<DashboardTranscriptIssue>>> sortedGroups = 
            new ArrayList<>(groupedByType.entrySet());
        sortedGroups.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));
        
        int globalIssueNumber = 1;
        
        for (var entry : sortedGroups) {
            String errorType = entry.getKey();
            List<DashboardTranscriptIssue> typeIssues = entry.getValue();
            String typeDesc = typeDescriptions.getOrDefault(errorType, errorType);
            
            sb.append(String.format("## %s - %s (%d)\n\n", errorType, typeDesc, typeIssues.size()));
            
            for (DashboardTranscriptIssue issue : typeIssues) {
                sb.append(String.format("### 错误 #%d\n\n", globalIssueNumber++));
                sb.append("- **事件类型**: `").append(issue.getEventType() != null ? issue.getEventType() : "message").append("`\n");
                sb.append("- **描述**: ").append(issue.getDescription()).append("\n");
                
                // Employee info (if available)
                if (issue.getEmployeeId() != null && !issue.getEmployeeId().isEmpty()) {
                    sb.append("- **工号**: ").append(issue.getEmployeeId()).append("\n");
                }
                
                // User input (if available) - truncated to 200 chars like Python
                if (issue.getUserInput() != null && !issue.getUserInput().isEmpty()) {
                    String userInput = issue.getUserInput();
                    String truncatedInput = userInput.length() > 200 ? userInput.substring(0, 200) + "..." : userInput;
                    String escapedInput = truncatedInput.replace("`", "\\`");
                    sb.append("- **用户输入**: `").append(escapedInput).append("`\n");
                }
                
                // Error message with code block
                if (issue.getErrorMessage() != null && !issue.getErrorMessage().isEmpty()) {
                    sb.append("- **错误信息**: \n````\n")
                      .append(issue.getErrorMessage())
                      .append("\n````\n");
                }
                
                // If error message contains "list index out of range", show full line record
                if (issue.getErrorMessage() != null && issue.getErrorMessage().toLowerCase().contains("list index out of range")) {
                    if (issue.getErrorLineContent() != null && !issue.getErrorLineContent().isEmpty()) {
                        sb.append("- **完整行记录**: \n```json\n")
                          .append(issue.getErrorLineContent())
                          .append("\n```\n");
                    }
                }
                
                // Cause analysis
                String causeAnalysis = issue.getCauseAnalysis();
                if (causeAnalysis == null || causeAnalysis.isEmpty()) {
                    causeAnalysis = getCauseAnalysis(issue.getErrorType());
                }
                sb.append("- **原因分析**: ").append(causeAnalysis).append("\n");
                
                // File path (if available) - show relative path like Python does
                if (issue.getFilePath() != null && !issue.getFilePath().isEmpty()) {
                    // Show relative path like Python does
                    String displayPath = issue.getFilePath();
                    sb.append("- **文件位置**: `").append(displayPath).append("`\n");
                } else {
                    sb.append("- **文件位置**: `[数据未存储]`\n");
                }
                
                // Session ID
                sb.append("- **Session ID**: `").append(issue.getSessionId()).append("`\n");
                
                // Line number
                if (issue.getLineNumber() != null) {
                    sb.append("- **行号**: ").append(issue.getLineNumber()).append("\n");
                } else {
                    sb.append("- **行号**: [未记录]\n");
                }
                
                // Timestamp
                if (issue.getOccurredAt() != null) {
                    sb.append("- **时间戳**: ").append(issue.getOccurredAt()).append("\n");
                }
                
                // Run ID
                if (issue.getRunId() != null && !issue.getRunId().isEmpty()) {
                    sb.append("- **Run ID**: `").append(issue.getRunId()).append("`\n");
                }
                
                // For flow integrity errors, show error line and next line content
                if (issue.getErrorType() != null && issue.getErrorType().startsWith("flow_integrity_")) {
                    if (issue.getErrorLineContent() != null && !issue.getErrorLineContent().isEmpty()) {
                        sb.append("- **错误行内容**: \n````\n")
                          .append(issue.getErrorLineContent())
                          .append("\n````\n");
                    }
                    if (issue.getNextLineContent() != null && !issue.getNextLineContent().isEmpty()) {
                        sb.append("- **下一行内容**: \n````\n")
                          .append(issue.getNextLineContent())
                          .append("\n````\n");
                    }
                }
                
                sb.append("\n---\n\n");
            }
        }

        return sb.toString();
    }
    
    private String getCauseAnalysis(String errorType) {
        return switch (errorType) {
            case "flow_integrity_no_reply" -> "可能的原因：1) 会话被意外中断；2) 系统崩溃导致回复丢失；3) 网络断开；4) 用户主动终止会话但未记录";
            case "flow_integrity_missing_tool_result" -> "可能的原因：1) 工具执行超时；2) 外部服务不可用；3) 权限不足；4) 参数错误";
            case "flow_integrity_missing_final_answer" -> "可能的原因：1) 工具执行后会话被中断；2) 系统在处理结果时崩溃；3) 达到最大token限制";
            case "modelErrors", "ASSISTANT_ERROR" -> "助手返回了错误消息，可能是模型API调用失败或参数错误";
            case "abnormal_stop" -> "会话异常停止，可能是网络中断、系统崩溃或用户主动终止";
            case "TOOL_ERROR", "toolErrors" -> "工具执行失败，可能是权限不足、参数错误或外部服务不可用";
            case "timeoutErrors" -> "请求超时，可能是网络延迟、服务器负载过高或请求处理时间过长";
            case "rateLimitErrors" -> "达到API速率限制，需要降低请求频率或升级API配额";
            case "permissionErrors" -> "权限不足，检查API密钥、访问令牌或资源权限配置";
            case "parsingErrors" -> "JSON解析错误，响应格式不正确或包含非法字符";
            case "networkErrors" -> "网络连接错误，检查网络稳定性、DNS解析或防火墙配置";
            default -> "未知错误类型，需要进一步分析";
        };
    }
}
