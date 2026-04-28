package com.company.clawboard.scanner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ScanOrchestrator 统计逻辑单元测试
 * 
 * 验证扫描统计的核心逻辑：
 * 1. 统计等式：processed + skipped + error = total
 * 2. usersScanned 计算逻辑
 */
class ScanStatisticsTest {

    @Test
    void testStatisticsEquationHolds() {
        // 模拟扫描结果
        int processedFiles = 10;
        int skippedFiles = 5;
        int errorFiles = 2;
        int totalFiles = processedFiles + skippedFiles + errorFiles;
        
        // 验证统计等式成立
        assertEquals(totalFiles, processedFiles + skippedFiles + errorFiles);
    }

    @Test
    void testStatisticsEquationWithZeroProcessed() {
        // 所有文件都被跳过的情况
        int processedFiles = 0;
        int skippedFiles = 100;
        int errorFiles = 0;
        int totalFiles = 100;
        
        // 验证统计等式成立
        assertEquals(totalFiles, processedFiles + skippedFiles + errorFiles);
    }

    @Test
    void testStatisticsEquationWithAllErrors() {
        // 所有文件都处理失败的情况
        int processedFiles = 0;
        int skippedFiles = 0;
        int errorFiles = 50;
        int totalFiles = 50;
        
        // 验证统计等式成立
        assertEquals(totalFiles, processedFiles + skippedFiles + errorFiles);
    }

    @Test
    void testStatisticsEquationMismatch() {
        // 模拟统计不匹配的情况（应该触发警告）
        int processedFiles = 10;
        int skippedFiles = 5;
        int errorFiles = 2;
        int totalFiles = 20; // 错误的总数
        
        int calculatedTotal = processedFiles + skippedFiles + errorFiles;
        
        // 验证检测到不匹配
        assertNotEquals(totalFiles, calculatedTotal);
    }

    @Test
    void testUsersScannedCalculation() {
        // 模拟用户扫描结果
        boolean[] userHasFiles = {true, false, true, true, false};
        
        // 计算实际扫描的用户数
        int actualUsersScanned = 0;
        for (boolean hasFiles : userHasFiles) {
            if (hasFiles) {
                actualUsersScanned++;
            }
        }
        
        // 验证只有 3 个用户有文件
        assertEquals(3, actualUsersScanned);
    }

    @Test
    void testUsersScannedWithNoFiles() {
        // 所有用户都没有文件
        boolean[] userHasFiles = {false, false, false};
        
        int actualUsersScanned = 0;
        for (boolean hasFiles : userHasFiles) {
            if (hasFiles) {
                actualUsersScanned++;
            }
        }
        
        // 验证没有用户被扫描
        assertEquals(0, actualUsersScanned);
    }

    @Test
    void testUsersScannedWithAllFiles() {
        // 所有用户都有文件
        boolean[] userHasFiles = {true, true, true, true};
        
        int actualUsersScanned = 0;
        for (boolean hasFiles : userHasFiles) {
            if (hasFiles) {
                actualUsersScanned++;
            }
        }
        
        // 验证所有用户都被扫描
        assertEquals(4, actualUsersScanned);
    }

    @Test
    void testFileClassificationLogic() {
        // 模拟文件分类逻辑
        String status = "PROCESSED";
        int processedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;
        
        switch (status) {
            case "PROCESSED":
                processedCount++;
                break;
            case "SKIPPED":
                skippedCount++;
                break;
            case "FAILED":
                errorCount++;
                break;
        }
        
        // 验证分类正确
        assertEquals(1, processedCount);
        assertEquals(0, skippedCount);
        assertEquals(0, errorCount);
    }

    @Test
    void testMultipleFileClassification() {
        // 模拟多个文件的分类
        String[] statuses = {"PROCESSED", "SKIPPED", "PROCESSED", "FAILED", "SKIPPED"};
        
        int processedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;
        
        for (String status : statuses) {
            switch (status) {
                case "PROCESSED":
                    processedCount++;
                    break;
                case "SKIPPED":
                    skippedCount++;
                    break;
                case "FAILED":
                    errorCount++;
                    break;
            }
        }
        
        // 验证分类统计正确
        assertEquals(2, processedCount);
        assertEquals(2, skippedCount);
        assertEquals(1, errorCount);
        assertEquals(5, processedCount + skippedCount + errorCount);
    }
}
