package com.company.clawboard.service;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.dto.ScanConflictResponse;
import com.company.clawboard.dto.ScanDetail;
import com.company.clawboard.dto.ScanStatusResponse;
import com.company.clawboard.dto.ScanTriggerResponse;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.ScanHistoryMapper;
import com.company.clawboard.scanner.ScanOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {
    
    private final ScanOrchestrator scanOrchestrator;
    private final ScanHistoryMapper scanHistoryMapper;
    private final ClawboardProperties properties;
    
    /**
     * 检查并触发扫描
     * - 如果有 running 记录 → 返回 409 Conflict
     * - 如果没有 → 创建记录，异步执行，返回 200 OK
     */
    public Object checkAndTriggerScan(String triggerType) {
        // 1. 检查是否有 running 状态的记录
        DashboardScanHistory running = scanHistoryMapper.selectByStatus("running");
        if (running != null) {
            log.warn("Scan skipped: previous scan still running (scanId={})", running.getId());
            return ScanConflictResponse.from(running);
        }
        
        // 2. 创建新的扫描记录（status='running'）
        Long scanId = scanOrchestrator.createScanRecord(triggerType);
        
        // 3. 异步执行扫描
        scanOrchestrator.executeScanAsync(scanId, triggerType);
        
        // 4. 立即返回
        ScanTriggerResponse response = new ScanTriggerResponse();
        response.setScanId(scanId);
        response.setTriggerType(triggerType);
        response.setStatus("triggered");
        response.setMessage("Scan triggered successfully");
        response.setStartedAt(LocalDateTime.now());
        
        return response;
    }
    
    /**
     * 获取扫描状态
     */
    public ScanStatusResponse getScanStatus() {
        // 1. 查询当前运行的扫描
        DashboardScanHistory currentScan = scanHistoryMapper.selectByStatus("running");
        
        // 2. 查询最近一次完成的扫描
        DashboardScanHistory lastCompleted = scanHistoryMapper.selectLatestCompleted();
        
        // 3. 计算下次定时任务执行时间
        LocalDateTime nextScheduledAt = calculateNextScheduledTime();
        
        ScanStatusResponse response = new ScanStatusResponse();
        response.setScanning(currentScan != null);
        response.setCurrentScan(ScanDetail.from(currentScan));
        response.setLastCompletedScan(ScanDetail.from(lastCompleted));
        response.setNextScheduledAt(nextScheduledAt);
        
        return response;
    }
    
    /**
     * 根据 cron 表达式计算下次执行时间
     */
    private LocalDateTime calculateNextScheduledTime() {
        try {
            String cron = properties.getScan().getCron();
            if (cron == null || cron.isEmpty()) {
                return null;
            }
            
            CronTrigger trigger = new CronTrigger(cron);
            Date nextDate = trigger.nextExecutionTime(new org.springframework.scheduling.TriggerContext() {
                @Override
                public Instant lastScheduledExecution() {
                    return Instant.now();
                }
                
                @Override
                public Instant lastActualExecution() {
                    return Instant.now();
                }
                
                @Override
                public Instant lastCompletion() {
                    return Instant.now();
                }
            });
            
            if (nextDate != null) {
                return LocalDateTime.ofInstant(nextDate.toInstant(), ZoneId.systemDefault());
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to calculate next scheduled time", e);
            return null;
        }
    }
}
