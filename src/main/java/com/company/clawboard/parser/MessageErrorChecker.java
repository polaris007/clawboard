package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;

/**
 * 消息错误判断工具类
 * 
 * 提供统一的消息错误判断逻辑，确保 dashboard_message 和 dashboard_execution_trace
 * 的错误判定标准完全一致。
 */
public class MessageErrorChecker {

    /**
     * 判断消息是否有错误
     * 
     * 判断标准（按优先级）：
     * 1. msg.isError() 直接标记为错误
     * 2. errorMessage 字段非空
     * 3. stopReason 包含 "error"、"timeout" 或 "failure"
     * 
     * @param msg 消息记录
     * @return true 表示有错误，false 表示正常
     */
    public static boolean hasError(MessageRecord msg) {
        if (msg == null) {
            return false;
        }
        
        // 1. Direct error flag
        if (msg.isError()) {
            return true;
        }
        
        // 2. Error message content
        if (msg.errorMessage() != null && !msg.errorMessage().isEmpty()) {
            return true;
        }
        
        // 3. Stop reason indicates error
        if (msg.stopReason() != null) {
            String reason = msg.stopReason().toLowerCase();
            if (reason.contains("error") || 
                reason.contains("timeout") ||
                reason.contains("failure")) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 获取消息的错误信息
     * 
     * @param msg 消息记录
     * @return 错误信息，如果没有错误则返回 null
     */
    public static String getErrorMessage(MessageRecord msg) {
        if (msg == null) {
            return null;
        }
        
        // 只有在确认有错误时才返回 errorMessage
        if (hasError(msg)) {
            return msg.errorMessage();
        }
        
        return null;
    }
}
