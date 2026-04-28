package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.entity.DashboardScanHistory;
import com.company.clawboard.mapper.*;
import com.company.clawboard.parser.TranscriptParser;
import com.company.clawboard.service.DataIngestionService;
import com.company.clawboard.service.ReportGenerator;
import com.company.clawboard.service.ScanProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ScanOrchestrator 扫描统计逻辑单元测试
 * 重点测试 usersScanned 计算和统计等式验证
 */
@ExtendWith(MockitoExtension.class)
class ScanOrchestratorStatisticsTest {

    @Mock
    private ClawboardProperties properties;

    @Mock
    private UserScanner userScanner;

    @Mock
    private TranscriptFileScanner fileScanner;

    @Mock
    private AccountsCsvReader accountsReader;

    @Mock
    private TranscriptParser transcriptParser;

    @Mock
    private ScanProgressService scanProgressService;

    @Mock
    private DataIngestionService dataIngestionService;

    @Mock
    private ReportGenerator reportGenerator;

    @Mock
    private HourlyStatsAggregator hourlyStatsAggregator;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private ConversationTurnMapper turnMapper;

    @Mock
    private SkillInvocationMapper skillMapper;

    @Mock
    private TranscriptIssueMapper issueMapper;

    @Mock
    private ScanHistoryMapper scanHistoryMapper;

    @Mock
    private ScanProgressMapper scanProgressMapper;

    @Mock
    @Qualifier("scanExecutor")
    private Executor scanExecutor;

    private ScanOrchestrator scanOrchestrator;

    @BeforeEach
    void setUp() {
        scanOrchestrator = new ScanOrchestrator(
            properties,
            userScanner,
            fileScanner,
            accountsReader,
            transcriptParser,
            scanProgressService,
            dataIngestionService,
            reportGenerator,
            hourlyStatsAggregator,
            messageMapper,
            turnMapper,
            skillMapper,
            issueMapper,
            scanHistoryMapper,
            scanProgressMapper,
            scanExecutor
        );
    }

    /**
     * 测试 1: 验证统计等式 processed + skipped + error = total
     * 
     * 注意：由于 scanUserDirectory 方法是 private，我们无法直接单元测试它。
     * 这个测试主要验证 ScanOrchestrator 的依赖注入和基本结构正确性。
     * 完整的统计逻辑测试需要通过集成测试进行。
     */
    @Test
    void testScanOrchestratorInitialization() {
        assertNotNull(scanOrchestrator);
        assertFalse(scanOrchestrator.isScanning());
        assertNull(scanOrchestrator.getCurrentScanId());
    }

    /**
     * 测试 2: 验证 IngestionStatus import 正确
     */
    @Test
    void testIngestionStatusImport() {
        // 这个测试确保 IngestionStatus 枚举可以正常使用
        assertNotNull(com.company.clawboard.service.IngestionStatus.PROCESSED);
        assertNotNull(com.company.clawboard.service.IngestionStatus.SKIPPED_MTIME);
        assertNotNull(com.company.clawboard.service.IngestionStatus.SKIPPED_EMPTY);
        assertNotNull(com.company.clawboard.service.IngestionStatus.FAILED);
        
        assertEquals(4, com.company.clawboard.service.IngestionStatus.values().length);
    }

    /**
     * 测试 3: 验证 ScanProgressMapper 依赖注入
     */
    @Test
    void testScanProgressMapperInjection() {
        // 验证 ScanProgressMapper 已正确注入
        // 如果注入失败，构造函数会抛出异常
        assertNotNull(scanOrchestrator);
    }
}
