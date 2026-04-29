package com.company.clawboard.scanner;

import com.company.clawboard.entity.DashboardHourlyStats;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HourlyStatsAggregatorTest {

    private final HourlyStatsAggregator aggregator = new HourlyStatsAggregator(null, null, null);

    @Test
    void testIsAllFieldsZero_AllZeros_ReturnsTrue() {
        DashboardHourlyStats stats = createAllZeroStats();
        
        // 使用反射调用私有方法
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertTrue(result, "All fields are zero, should return true");
    }

    @Test
    void testIsAllFieldsZero_TotalTokensNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setTotalTokens(1000L);
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "totalTokens is non-zero, should return false");
    }

    @Test
    void testIsAllFieldsZero_ConversationTurnsNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setConversationTurns(5);
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "conversationTurns is non-zero, should return false");
    }

    @Test
    void testIsAllFieldsZero_ErrorCountNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setErrorCount(1);
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "errorCount is non-zero, should return false");
    }

    @Test
    void testIsAllFieldsZero_TotalCostNonZero_ReturnsFalse() {
        DashboardHourlyStats stats = createAllZeroStats();
        stats.setTotalCost(new BigDecimal("0.01"));
        
        boolean result = invokeIsAllFieldsZero(stats);
        
        assertFalse(result, "totalCost is non-zero, should return false");
    }

    /**
     * 创建全 0 的统计数据对象
     */
    private DashboardHourlyStats createAllZeroStats() {
        DashboardHourlyStats stats = new DashboardHourlyStats();
        stats.setEmployeeId("test-user");
        stats.setStatHour(LocalDateTime.now());
        stats.setTotalTokens(0L);
        stats.setInputTokens(0L);
        stats.setOutputTokens(0L);
        stats.setTotalCost(BigDecimal.ZERO);
        stats.setCacheReadTokens(0L);
        stats.setCacheWriteTokens(0L);
        stats.setConversationTurns(0);
        stats.setCompleteTurns(0);
        stats.setErrorTurns(0);
        stats.setSkillInvocations(0);
        stats.setSkillErrors(0);
        stats.setToolCalls(0);
        stats.setToolErrors(0);
        stats.setErrorCount(0);
        stats.setUpdatedAt(LocalDateTime.now());
        return stats;
    }

    /**
     * 使用反射调用私有的 isAllFieldsZero 方法
     */
    private boolean invokeIsAllFieldsZero(DashboardHourlyStats stats) {
        try {
            java.lang.reflect.Method method = HourlyStatsAggregator.class.getDeclaredMethod("isAllFieldsZero", DashboardHourlyStats.class);
            method.setAccessible(true);
            return (boolean) method.invoke(aggregator, stats);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke isAllFieldsZero", e);
        }
    }
}
