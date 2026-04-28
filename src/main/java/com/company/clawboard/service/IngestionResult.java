package com.company.clawboard.service;

/**
 * 数据入库结果
 * 
 * @param status 处理状态
 * @param messageCount 消息数量
 * @param turnCount 对话轮次数量
 * @param issueCount 问题数量
 * @param skillCount Skill 调用数量
 * @param errorMessage 错误信息（如果有）
 */
public record IngestionResult(
    IngestionStatus status,
    int messageCount,
    int turnCount,
    int issueCount,
    int skillCount,
    String errorMessage
) {
    /**
     * 创建成功结果
     */
    public static IngestionResult success(int messageCount, int turnCount, 
                                          int issueCount, int skillCount) {
        return new IngestionResult(IngestionStatus.PROCESSED, messageCount, 
                                   turnCount, issueCount, skillCount, null);
    }
    
    /**
     * 创建跳过结果
     */
    public static IngestionResult skipped(IngestionStatus status, String reason) {
        return new IngestionResult(status, 0, 0, 0, 0, reason);
    }
    
    /**
     * 创建失败结果
     */
    public static IngestionResult failed(String errorMessage) {
        return new IngestionResult(IngestionStatus.FAILED, 0, 0, 0, 0, errorMessage);
    }
}
