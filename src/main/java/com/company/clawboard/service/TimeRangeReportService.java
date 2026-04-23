package com.company.clawboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeRangeReportService {

    private final ReportGenerator reportGenerator;
    private final ReportFileService reportFileService;
    
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    @Async("reportTaskExecutor")
    public void generateReportByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        long startMs = System.currentTimeMillis();
        log.info("Starting report generation for time range: {} - {}", startTime, endTime);
        
        try {
            // Validate time range
            if (endTime.isBefore(startTime)) {
                log.error("Invalid time range: endTime {} is before startTime {}", endTime, startTime);
                return;
            }

            // Generate report content
            String markdown = reportGenerator.generateReportByTimeRange(startTime, endTime);
            
            // Generate filename
            String startStr = startTime.format(FILENAME_FORMATTER);
            String endStr = endTime.format(FILENAME_FORMATTER);
            String fileName = String.format("time-range-report-%s-%s.md", startStr, endStr);
            
            // Save report
            String filePath = reportFileService.saveReport(markdown, fileName);
            
            long duration = System.currentTimeMillis() - startMs;
            log.info("Report generation completed successfully in {}ms. File: {}", duration, filePath);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startMs;
            log.error("Report generation failed after {}ms for time range: {} - {}", 
                duration, startTime, endTime, e);
        }
    }
}
