package com.company.clawboard.service;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportFileService {

    private final ClawboardProperties properties;
    
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String saveReport(String content, String fileName) {
        try {
            String reportsDir = properties.getReports().getOutputDir();
            String dateStr = LocalDateTime.now(BEIJING_ZONE).format(DATE_FORMATTER);
            Path reportDir = Paths.get(reportsDir, dateStr);
            Files.createDirectories(reportDir);

            Path reportPath = reportDir.resolve(fileName);
            
            // Handle file name conflicts
            if (Files.exists(reportPath)) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                String extension = fileName.substring(fileName.lastIndexOf('.'));
                String newFileName = baseName + "-" + timestamp + extension;
                reportPath = reportDir.resolve(newFileName);
                log.info("Report file already exists, appending timestamp: {}", newFileName);
            }

            Files.writeString(reportPath, content);
            log.info("Report saved successfully: {}", reportPath.toAbsolutePath());
            return reportPath.toAbsolutePath().toString();
        } catch (Exception e) {
            log.error("Failed to save report: {}", fileName, e);
            throw new RuntimeException("Failed to save report", e);
        }
    }
}
