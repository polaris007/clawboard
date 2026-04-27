package com.company.clawboard.parser;

import com.company.clawboard.config.ClawboardProperties;
import com.company.clawboard.parser.model.MessageRecord;
import com.company.clawboard.parser.model.UsageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnAssemblerTest {
    
    private TurnAssembler turnAssembler;
    
    @BeforeEach
    void setUp() {
        // 创建 mock 的 ClawboardProperties
        ClawboardProperties properties = new ClawboardProperties();
        properties.getParser().setDeliveryMirrorFilter(false);
        properties.getParser().setSystemMessagePrefixes(List.of());
        
        // SystemMessageFilter 是必需的依赖
        turnAssembler = new TurnAssembler(new SystemMessageFilter(properties));
    }
    
    private MessageRecord createUserMessage(String id, String content) {
        return new MessageRecord(
            id, null, "2026-04-22T10:00:00Z", "user", content,
            null, List.of(), null, null, false,
            null, null, null, null, 0, 1713780000000L, 1
        );
    }
    
    private MessageRecord createAssistantMessage(String id, UsageInfo usage, List<MessageRecord.ToolCallInfo> toolCalls, String stopReason) {
        return new MessageRecord(
            id, null, "2026-04-22T10:00:01Z", "assistant", null,
            null, toolCalls, null, null, false,
            "custom-provider", "Qwen3.5", stopReason, usage, 100, 1713780001000L, 2
        );
    }
    
    private MessageRecord createToolResultMessage(String id, String toolCallId, String toolName, boolean isError, String errorMessage) {
        return new MessageRecord(
            id, null, "2026-04-22T10:00:02Z", "toolResult", null,
            errorMessage, List.of(), toolCallId, toolName, isError,
            null, null, null, null, 0, 1713780002000L, 3
        );
    }
    
    private UsageInfo createUsage(int input, int output, int total) {
        return new UsageInfo(input, output, 0, 0, total, BigDecimal.ZERO);
    }
    
    @Test
    void testTurnWithoutToolCalls() {
        // 准备：user → assistant（纯文本回复）
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "帮我写报告"),
            createAssistantMessage("msg2", createUsage(100, 200, 300), List.of(), null)
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证
        assertEquals(100, turn.totalInputTokens());
        assertEquals(200, turn.totalOutputTokens());
        assertEquals(300, turn.totalTokens());
        assertEquals(0, turn.toolCallsCount());
        assertEquals(0, turn.toolCallsSuccess());
        assertEquals(0, turn.toolCallsError());
        assertTrue(turn.isComplete());
        assertFalse(turn.hasError());
    }
    
    @Test
    void testSingleToolCallSuccess() {
        // 准备：user → assistant(toolCall) → toolResult → assistant(reply)
        MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
            "call-1", "exec", "{\"command\":\"date\"}"
        );
        
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "执行命令"),
            createAssistantMessage("msg2", createUsage(50, 30, 80), List.of(toolCall), "toolUse"),
            createToolResultMessage("msg3", "call-1", "exec", false, null),
            createAssistantMessage("msg4", createUsage(20, 40, 60), List.of(), null)
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证
        assertEquals(70, turn.totalInputTokens());   // 50 + 20
        assertEquals(70, turn.totalOutputTokens());  // 30 + 40
        assertEquals(140, turn.totalTokens());       // 80 + 60
        assertEquals(1, turn.toolCallsCount());
        assertEquals(1, turn.toolCallsSuccess());
        assertEquals(0, turn.toolCallsError());
        assertTrue(turn.isComplete());
    }
    
    @Test
    void testMissingToolResult() {
        // 准备：user → assistant(toolCall) → (文件结束，缺少 toolResult)
        MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
            "call-1", "read_file", "{\"path\":\"/etc/hosts\"}"
        );
        
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "读取文件"),
            createAssistantMessage("msg2", createUsage(60, 20, 80), List.of(toolCall), "toolUse")
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证
        assertEquals(60, turn.totalInputTokens());
        assertEquals(20, turn.totalOutputTokens());
        assertEquals(80, turn.totalTokens());
        assertEquals(1, turn.toolCallsCount());
        assertEquals(0, turn.toolCallsSuccess());
        assertEquals(1, turn.toolCallsError());  // 缺少 toolResult，计为错误
        assertFalse(turn.isComplete());
    }
    
    @Test
    void testToolResultWithError() {
        // 准备：user → assistant(toolCall) → toolResult(error)
        MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
            "call-1", "exec", "{\"command\":\"invalid_command\"}"
        );
        
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "执行命令"),
            createAssistantMessage("msg2", createUsage(50, 30, 80), List.of(toolCall), "toolUse"),
            createToolResultMessage("msg3", "call-1", "exec", true, "Command not found")
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证
        assertEquals(1, turn.toolCallsCount());
        assertEquals(0, turn.toolCallsSuccess());
        assertEquals(1, turn.toolCallsError());  // toolResult 有错误
    }
    
    @Test
    void testAssistantStopReasonError() {
        // 准备：user → assistant(toolCall, stopReason="error")
        MessageRecord.ToolCallInfo toolCall = new MessageRecord.ToolCallInfo(
            "call-1", "exec", "{\"command\":\"date\"}"
        );
        
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "执行命令"),
            createAssistantMessage("msg2", createUsage(50, 0, 50), List.of(toolCall), "error")
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证
        assertEquals(1, turn.toolCallsCount());
        assertEquals(0, turn.toolCallsSuccess());
        assertEquals(1, turn.toolCallsError());  // stopReason 为 error
    }
    
    @Test
    void testMultipleToolCallsPartialSuccess() {
        // 准备：user → assistant(2个toolCall) → toolResult(success) → toolResult(error)
        MessageRecord.ToolCallInfo toolCall1 = new MessageRecord.ToolCallInfo(
            "call-1", "read_file", "{\"path\":\"/etc/hosts\"}"
        );
        MessageRecord.ToolCallInfo toolCall2 = new MessageRecord.ToolCallInfo(
            "call-2", "exec", "{\"command\":\"invalid\"}"
        );
        
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "读取并执行"),
            createAssistantMessage("msg2", createUsage(80, 40, 120), List.of(toolCall1, toolCall2), "toolUse"),
            createToolResultMessage("msg3", "call-1", "read_file", false, null),
            createToolResultMessage("msg4", "call-2", "exec", true, "Command failed")
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证
        assertEquals(2, turn.toolCallsCount());
        assertEquals(1, turn.toolCallsSuccess());  // call-1 成功
        assertEquals(1, turn.toolCallsError());    // call-2 失败
    }
    
    @Test
    void testNullUsage() {
        // 准备：assistant message 的 usage 为 null
        List<MessageRecord> messages = List.of(
            createUserMessage("msg1", "测试"),
            createAssistantMessage("msg2", null, List.of(), null)
        );
        
        // 执行
        TurnAssembler.AssembledTurn turn = turnAssembler.assembleTurn(messages);
        
        // 验证：应该当作 0 处理，不抛出异常
        assertEquals(0, turn.totalInputTokens());
        assertEquals(0, turn.totalOutputTokens());
        assertEquals(0, turn.totalTokens());
    }
}
