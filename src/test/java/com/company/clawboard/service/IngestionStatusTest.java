package com.company.clawboard.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * IngestionStatus 枚举单元测试
 */
class IngestionStatusTest {

    @Test
    void testAllEnumValuesExist() {
        // 验证所有枚举值存在
        assertNotNull(IngestionStatus.PROCESSED);
        assertNotNull(IngestionStatus.SKIPPED_MTIME);
        assertNotNull(IngestionStatus.SKIPPED_EMPTY);
        assertNotNull(IngestionStatus.FAILED);
    }

    @Test
    void testEnumValueOf() {
        // 验证 valueOf 方法正常工作
        assertEquals(IngestionStatus.PROCESSED, IngestionStatus.valueOf("PROCESSED"));
        assertEquals(IngestionStatus.SKIPPED_MTIME, IngestionStatus.valueOf("SKIPPED_MTIME"));
        assertEquals(IngestionStatus.SKIPPED_EMPTY, IngestionStatus.valueOf("SKIPPED_EMPTY"));
        assertEquals(IngestionStatus.FAILED, IngestionStatus.valueOf("FAILED"));
    }

    @Test
    void testEnumValuesCount() {
        // 验证枚举值数量
        assertEquals(4, IngestionStatus.values().length);
    }
}
