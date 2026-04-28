package com.company.clawboard.service;

/**
 * 数据入库状态枚举
 */
public enum IngestionStatus {
    /**
     * 成功处理（读取并入库）
     */
    PROCESSED,
    
    /**
     * 因 mtime 未变化而跳过
     */
    SKIPPED_MTIME,
    
    /**
     * 文件为空或无有效数据
     */
    SKIPPED_EMPTY,
    
    /**
     * 处理失败
     */
    FAILED
}
