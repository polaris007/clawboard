package com.company.clawboard.service;

import com.company.clawboard.entity.DashboardTranscriptIssue;
import com.company.clawboard.mapper.TranscriptIssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerator {

    private final TranscriptIssueMapper issueMapper;
    private static final String REPORTS_DIR = "scripts/reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void generateReport(Long scanId, LocalDateTime scanStartTime) {
        try {
            // Query all issues for this scan
            var issues = issueMapper.selectByScanId(scanId);

            // Generate Markdown content
            String markdown = buildMarkdownReport(issues, scanStartTime);

            // Write to file with date-based directory structure
            String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
            Path reportDir = Paths.get(REPORTS_DIR, dateStr);
            Files.createDirectories(reportDir);

            Path reportPath = reportDir.resolve("transcript-comprehensive-issues.md");
            Files.writeString(reportPath, markdown);

            log.info("Report generated: {}", reportPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to generate report for scan {}", scanId, e);
        }
    }

    private String buildMarkdownReport(List<DashboardTranscriptIssue> issues, LocalDateTime scanStartTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Transcript Comprehensive Issues Report\n\n");
        sb.append("**Scan Time**: ").append(scanStartTime).append("\n\n");
        sb.append("**Total Issues**: ").append(issues.size()).append("\n\n");

        if (issues.isEmpty()) {
            sb.append("No issues found in this scan.\n");
            return sb.toString();
        }

        // Group by severity
        sb.append("## Summary by Severity\n\n");
        long highCount = issues.stream().filter(i -> "high".equals(i.getSeverity())).count();
        long mediumCount = issues.stream().filter(i -> "medium".equals(i.getSeverity())).count();
        long lowCount = issues.stream().filter(i -> "low".equals(i.getSeverity())).count();

        sb.append("- **High**: ").append(highCount).append("\n");
        sb.append("- **Medium**: ").append(mediumCount).append("\n");
        sb.append("- **Low**: ").append(lowCount).append("\n\n");

        // Detailed issues
        sb.append("## Detailed Issues\n\n");
        for (int i = 0; i < issues.size(); i++) {
            var issue = issues.get(i);
            sb.append("### Issue #").append(i + 1).append("\n\n");
            sb.append("- **Type**: ").append(issue.getErrorType()).append("\n");
            sb.append("- **Severity**: ").append(issue.getSeverity()).append("\n");
            sb.append("- **Session**").append(issue.getSessionId()).append("\n");
            sb.append("- **Description**: ").append(issue.getDescription()).append("\n");
            if (issue.getErrorMessage() != null) {
                sb.append("- **Error Message**: `").append(issue.getErrorMessage()).append("`\n");
            }
            sb.append("\n---\n\n");
        }

        return sb.toString();
    }
}
