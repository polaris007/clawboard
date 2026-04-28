package com.company.clawboard.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * IngestionResult 记录类单元测试
 */
class IngestionResultTest {

    @Test
    void testSuccessFactoryMethod() {
        // 测试成功结果创建
        IngestionResult result = IngestionResult.success(10, 5, 2, 3);
        
        assertEquals(IngestionStatus.PROCESSED, result.status());
        assertEquals(10, result.messageCount());
        assertEquals(5, result.turnCount());
        assertEquals(2, result.issueCount());
        assertEquals(3, result.skillCount());
        assertNull(result.errorMessage());
    }

    @Test
    void testSkippedMtimeFactoryMethod() {
        // 测试 mtime 跳过结果创建
        IngestionResult result = IngestionResult.skipped(
            IngestionStatus.SKIPPED_MTIME, 
            "File not modified"
        );
        
        assertEquals(IngestionStatus.SKIPPED_MTIME, result.status());
        assertEquals(0, result.messageCount());
        assertEquals(0, result.turnCount());
        assertEquals(0, result.issueCount());
        assertEquals(0, result.skillCount());
        assertEquals("File not modified", result.errorMessage());
    }

    @Test
    void testSkippedEmptyFactoryMethod() {
        // 测试空文件跳过结果创建
        IngestionResult result = IngestionResult.skipped(
            IngestionStatus.SKIPPED_EMPTY, 
            "Invalid parsed transcript"
        );
        
        assertEquals(IngestionStatus.SKIPPED_EMPTY, result.status());
        assertEquals(0, result.messageCount());
        assertEquals(0, result.turnCount());
        assertEquals(0, result.issueCount());
        assertEquals(0, result.skillCount());
        assertEquals("Invalid parsed transcript", result.errorMessage());
    }

    @Test
    void testFailedFactoryMethod() {
        // 测试失败结果创建
        String errorMsg = "Database connection failed";
        IngestionResult result = IngestionResult.failed(errorMsg);
        
        assertEquals(IngestionStatus.FAILED, result.status());
        assertEquals(0, result.messageCount());
        assertEquals(0, result.turnCount());
        assertEquals(0, result.issueCount());
        assertEquals(0, result.skillCount());
        assertEquals(errorMsg, result.errorMessage());
    }

    @Test
    void testProcessedStatusHasNoErrorMessage() {
        // 验证成功状态没有错误信息
        IngestionResult result = IngestionResult.success(100, 50, 10, 20);
        assertNull(result.errorMessage());
    }

    @Test
    void testSkippedStatusHasZeroCounts() {
        // 验证跳过状态的统计数字都是 0
        IngestionResult result = IngestionResult.skipped(
            IngestionStatus.SKIPPED_MTIME, 
            "test reason"
        );
        
        assertEquals(0, result.messageCount());
        assertEquals(0, result.turnCount());
        assertEquals(0, result.issueCount());
        assertEquals(0, result.skillCount());
    }

    @Test
    void testFailedStatusHasZeroCounts() {
        // 验证失败状态的统计数字都是 0
        IngestionResult result = IngestionResult.failed("error");
        
        assertEquals(0, result.messageCount());
        assertEquals(0, result.turnCount());
        assertEquals(0, result.issueCount());
        assertEquals(0, result.skillCount());
    }

    @Test
    void testRecordImmutability() {
        // 验证记录的不可变性（record 是 immutable 的）
        IngestionResult result1 = IngestionResult.success(10, 5, 2, 3);
        IngestionResult result2 = IngestionResult.success(10, 5, 2, 3);
        
        // 相同值应该相等
        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    void testDifferentStatusAreNotEqual() {
        // 验证不同状态的记录不相等
        IngestionResult success = IngestionResult.success(10, 5, 2, 3);
        IngestionResult failed = IngestionResult.failed("error");
        
        assertNotEquals(success, failed);
    }
}
