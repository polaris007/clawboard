package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageErrorChecker 单元测试
 * 
 * 验证错误判断逻辑的正确性，确保与 dashboard_message.is_error 的判断标准一致
 */
class MessageErrorCheckerTest {

    @Test
    void testHasError_WithDirectErrorFlag() {
        // 测试直接标记为错误的消息
        MessageRecord msg = new MessageRecord(
            "msg1", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", "Error occurred", null,
            null, null, true,  // isError = true
            "openai", "gpt-4", "error", null, 0, 0L, 1
        );
        
        assertTrue(MessageErrorChecker.hasError(msg), "isError=true 应该判定为有错误");
    }

    @Test
    void testHasError_WithErrorMessage() {
        // 测试有 errorMessage 字段的消息
        MessageRecord msg = new MessageRecord(
            "msg2", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", "Tool execution failed", null,
            null, null, false,  // isError = false
            "openai", "gpt-4", null, null, 0, 0L, 2
        );
        
        assertTrue(MessageErrorChecker.hasError(msg), "errorMessage 非空应该判定为有错误");
    }

    @Test
    void testHasError_WithStopReasonError() {
        // 测试 stopReason 包含 "error" 的消息
        MessageRecord msg = new MessageRecord(
            "msg3", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", null, null,
            null, null, false,  // isError = false
            "openai", "gpt-4", "content_filter_error", null, 0, 0L, 3
        );
        
        assertTrue(MessageErrorChecker.hasError(msg), "stopReason 包含 'error' 应该判定为有错误");
    }

    @Test
    void testHasError_WithStopReasonTimeout() {
        // 测试 stopReason 包含 "timeout" 的消息
        MessageRecord msg = new MessageRecord(
            "msg4", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", null, null,
            null, null, false,
            "openai", "gpt-4", "request_timeout", null, 0, 0L, 4
        );
        
        assertTrue(MessageErrorChecker.hasError(msg), "stopReason 包含 'timeout' 应该判定为有错误");
    }

    @Test
    void testHasError_WithStopReasonFailure() {
        // 测试 stopReason 包含 "failure" 的消息
        MessageRecord msg = new MessageRecord(
            "msg5", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", null, null,
            null, null, false,
            "openai", "gpt-4", "api_failure", null, 0, 0L, 5
        );
        
        assertTrue(MessageErrorChecker.hasError(msg), "stopReason 包含 'failure' 应该判定为有错误");
    }

    @Test
    void testHasError_NoError() {
        // 测试正常消息（无任何错误标志）
        MessageRecord msg = new MessageRecord(
            "msg6", null, "2026-04-29T10:00:00Z", "assistant",
            "Normal response", null, null,
            null, null, false,
            "openai", "gpt-4", "stop", null, 0, 0L, 6
        );
        
        assertFalse(MessageErrorChecker.hasError(msg), "无任何错误标志应该判定为正常");
    }

    @Test
    void testHasError_NullMessage() {
        // 测试 null 消息
        assertFalse(MessageErrorChecker.hasError(null), "null 消息应该返回 false");
    }

    @Test
    void testHasError_EmptyErrorMessage() {
        // 测试 errorMessage 为空字符串的消息
        MessageRecord msg = new MessageRecord(
            "msg7", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", "", null,  // errorMessage = ""
            null, null, false,
            "openai", "gpt-4", null, null, 0, 0L, 7
        );
        
        assertFalse(MessageErrorChecker.hasError(msg), "空字符串 errorMessage 不应该判定为错误");
    }

    @Test
    void testGetErrorMessage_WithError() {
        // 测试有错误时获取 errorMessage
        MessageRecord msg = new MessageRecord(
            "msg8", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", "Detailed error message", null,
            null, null, true,
            "openai", "gpt-4", "error", null, 0, 0L, 8
        );
        
        assertEquals("Detailed error message", MessageErrorChecker.getErrorMessage(msg),
            "有错误时应该返回 errorMessage");
    }

    @Test
    void testGetErrorMessage_NoError() {
        // 测试无错误时获取 errorMessage
        MessageRecord msg = new MessageRecord(
            "msg9", null, "2026-04-29T10:00:00Z", "assistant",
            "Normal response", null, null,
            null, null, false,
            "openai", "gpt-4", "stop", null, 0, 0L, 9
        );
        
        assertNull(MessageErrorChecker.getErrorMessage(msg),
            "无错误时应该返回 null");
    }

    @Test
    void testGetErrorMessage_NullMessage() {
        // 测试 null 消息
        assertNull(MessageErrorChecker.getErrorMessage(null),
            "null 消息应该返回 null");
    }

    @Test
    void testConsistency_BetweenHasErrorAndGetErrorMessage() {
        // 测试 hasError 和 getErrorMessage 的一致性
        MessageRecord errorMsg = new MessageRecord(
            "msg10", null, "2026-04-29T10:00:00Z", "assistant",
            "Some content", "Error details", null,
            null, null, true,
            "openai", "gpt-4", "error", null, 0, 0L, 10
        );
        
        MessageRecord normalMsg = new MessageRecord(
            "msg11", null, "2026-04-29T10:00:00Z", "assistant",
            "Normal response", null, null,
            null, null, false,
            "openai", "gpt-4", "stop", null, 0, 0L, 11
        );
        
        // 有错误时，getErrorMessage 应该返回非 null
        assertTrue(MessageErrorChecker.hasError(errorMsg));
        assertNotNull(MessageErrorChecker.getErrorMessage(errorMsg));
        
        // 无错误时，getErrorMessage 应该返回 null
        assertFalse(MessageErrorChecker.hasError(normalMsg));
        assertNull(MessageErrorChecker.getErrorMessage(normalMsg));
    }
}
