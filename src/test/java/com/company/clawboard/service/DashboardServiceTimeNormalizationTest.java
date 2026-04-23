package com.company.clawboard.service;

import com.company.clawboard.mapper.ConversationTurnMapper;
import com.company.clawboard.mapper.EmployeeMapper;
import com.company.clawboard.mapper.HourlyStatsMapper;
import com.company.clawboard.mapper.SkillInvocationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DashboardService 时间标准化方法测试
 */
@DisplayName("DashboardService 时间标准化测试")
class DashboardServiceTimeNormalizationTest {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 通过反射调用私有方法 normalizeStartTime
     */
    private String invokeNormalizeStartTime(String startTime) throws Exception {
        DashboardService service = new DashboardService(
            null,  // hourlyStatsMapper
            null,  // employeeMapper
            null,  // skillInvocationMapper
            null   // conversationTurnMapper
        );
        Method method = DashboardService.class.getDeclaredMethod("normalizeStartTime", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, startTime);
    }

    /**
     * 通过反射调用私有方法 normalizeEndTime
     */
    private String invokeNormalizeEndTime(String endTime) throws Exception {
        DashboardService service = new DashboardService(
            null,  // hourlyStatsMapper
            null,  // employeeMapper
            null,  // skillInvocationMapper
            null   // conversationTurnMapper
        );
        Method method = DashboardService.class.getDeclaredMethod("normalizeEndTime", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, endTime);
    }

    @Test
    @DisplayName("startTime 为非整点时应向上取整到下一小时")
    void testNormalizeStartTime_NonZeroMinuteSecond_ShouldRoundUp() throws Exception {
        // Given
        String input = "2026-04-22 00:59:03";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-22 01:00:00");
    }

    @Test
    @DisplayName("startTime 为整点时应保持不变")
    void testNormalizeStartTime_ZeroMinuteSecond_ShouldKeepSame() throws Exception {
        // Given
        String input = "2026-04-22 01:00:00";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-22 01:00:00");
    }

    @Test
    @DisplayName("startTime 只有分钟不为0时应向上取整")
    void testNormalizeStartTime_NonZeroMinuteOnly_ShouldRoundUp() throws Exception {
        // Given
        String input = "2026-04-22 10:30:00";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-22 11:00:00");
    }

    @Test
    @DisplayName("startTime 只有秒不为0时应向上取整")
    void testNormalizeStartTime_NonZeroSecondOnly_ShouldRoundUp() throws Exception {
        // Given
        String input = "2026-04-22 15:00:30";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-22 16:00:00");
    }

    @Test
    @DisplayName("startTime 为 null 时应返回 null")
    void testNormalizeStartTime_NullInput_ShouldReturnNull() throws Exception {
        // Given
        String input = null;
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("startTime 为空字符串时应返回空字符串")
    void testNormalizeStartTime_EmptyInput_ShouldReturnEmpty() throws Exception {
        // Given
        String input = "";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("endTime 应保持不变")
    void testNormalizeEndTime_ShouldKeepSame() throws Exception {
        // Given
        String input = "2026-04-22 23:00:00";
        
        // When
        String result = invokeNormalizeEndTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-22 23:00:00");
    }

    @Test
    @DisplayName("跨天场景：23:59:03 应取整到次日 00:00:00")
    void testNormalizeStartTime_CrossDay_ShouldRoundToNextDay() throws Exception {
        // Given
        String input = "2026-04-22 23:59:03";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-23 00:00:00");
    }

    @Test
    @DisplayName("边界场景：00:00:01 应取整到 01:00:00")
    void testNormalizeStartTime_EarlyMorning_ShouldRoundUp() throws Exception {
        // Given
        String input = "2026-04-22 00:00:01";
        
        // When
        String result = invokeNormalizeStartTime(input);
        
        // Then
        assertThat(result).isEqualTo("2026-04-22 01:00:00");
    }
}
