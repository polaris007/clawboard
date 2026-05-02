package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SkillChainDetectorTest {
    
    private SkillChainDetector detector = new SkillChainDetector();
    
    // Helper method to create a mock MessageRecord
    private MessageRecord createMessage(
        String id, String role, long epochMs, String textContent, List<MessageRecord.ToolCallInfo> toolCalls) {
        return new MessageRecord(
            id,                           // id
            null,                         // parentId
            String.valueOf(epochMs),      // timestamp
            role,                         // role
            textContent,                  // textContent
            null,                         // errorMessage
            toolCalls,                    // toolCalls
            null,                         // toolCallId
            null,                         // toolName
            false,                        // isError
            null,                         // provider
            null,                         // model
            null,                         // stopReason
            null,                         // usage
            0,                            // durationMs
            epochMs,                      // epochMs
            0                             // lineNumber
        );
    }
    
    @Test
    @DisplayName("检测成功的 skill 调用链")
    void testDetectSuccessfulSkillChain() {
        // 构造测试数据：assistant(toolCall+"正在使用 X Skill") → toolResult → assistant
        List<MessageRecord> messages = new ArrayList<>();
        Map<String, Integer> messageIdToTurnIndex = new HashMap<>();
        
        // Message 1: Skill 调用起点
        List<MessageRecord.ToolCallInfo> toolCalls1 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_1", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_001", 
            "assistant", 
            1000L, 
            "## 🛠️ 正在使用 docx Skill\n执行中...",
            toolCalls1
        ));
        messageIdToTurnIndex.put("msg_001", 0);
        
        // Message 2: Tool 执行结果
        messages.add(createMessage(
            "msg_002",
            "toolResult",
            1500L,
            null,
            null
        ));
        messageIdToTurnIndex.put("msg_002", 0);
        
        // Message 3: 正常结束
        messages.add(createMessage(
            "msg_003",
            "assistant",
            2000L,
            "任务完成",
            null
        ));
        messageIdToTurnIndex.put("msg_003", 0);
        
        List<TranscriptParser.SkillInvocation> skills = detector.detectSkillChains(
            messages, messageIdToTurnIndex
        );
        
        assertEquals(1, skills.size());
        assertEquals("docx", skills.get(0).skillName());
        assertEquals("msg_001", skills.get(0).readMessageId());
        // 最后一个 skill 的终点是第一个非 toolCall 的 assistant 消息
        assertEquals("msg_003", skills.get(0).resultMessageId());
        assertFalse(skills.get(0).isError());
        assertTrue(skills.get(0).durationMs() > 0);
        assertEquals(1000, skills.get(0).durationMs()); // 2000 - 1000
        assertEquals(1, skills.get(0).sequenceOrder());
    }
    
    @Test
    @DisplayName("检测被 user 打断的 skill 调用")
    void testDetectFailedSkillChain_UserInterrupted() {
        // 构造测试数据：assistant(toolCall+"正在使用 X Skill") → user
        List<MessageRecord> messages = new ArrayList<>();
        Map<String, Integer> messageIdToTurnIndex = new HashMap<>();
        
        // Message 1: Skill 调用起点
        List<MessageRecord.ToolCallInfo> toolCalls1 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_1", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_001",
            "assistant",
            1000L,
            "## 🛠️ 正在使用 python Skill\n执行中...",
            toolCalls1
        ));
        messageIdToTurnIndex.put("msg_001", 0);
        
        // Message 2: User 打断
        messages.add(createMessage(
            "msg_002",
            "user",
            1200L,
            "停止执行",
            null
        ));
        messageIdToTurnIndex.put("msg_002", 1);
        
        List<TranscriptParser.SkillInvocation> skills = detector.detectSkillChains(
            messages, messageIdToTurnIndex
        );
        
        assertEquals(1, skills.size());
        assertEquals("python", skills.get(0).skillName());
        assertTrue(skills.get(0).isError());
        assertNotNull(skills.get(0).resultMessageId());
    }
    
    @Test
    @DisplayName("检测同一 turn 中的多个 skill 调用")
    void testDetectMultipleSkillsInOneTurn() {
        // 构造测试数据：Skill A → toolResult → Skill B → toolResult → assistant
        List<MessageRecord> messages = new ArrayList<>();
        Map<String, Integer> messageIdToTurnIndex = new HashMap<>();
        
        // Message 1: Skill A 调用起点
        List<MessageRecord.ToolCallInfo> toolCalls1 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_1", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_001",
            "assistant",
            1000L,
            "## 🛠️ 正在使用 bash Skill\n执行中...",
            toolCalls1
        ));
        messageIdToTurnIndex.put("msg_001", 0);
        
        // Message 2: Skill A 的 toolResult
        messages.add(createMessage(
            "msg_002",
            "toolResult",
            1500L,
            null,
            null
        ));
        messageIdToTurnIndex.put("msg_002", 0);
        
        // Message 3: Skill B 调用起点
        List<MessageRecord.ToolCallInfo> toolCalls2 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_2", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_003",
            "assistant",
            2000L,
            "## 🛠️ 正在使用 grep Skill\n搜索中...",
            toolCalls2
        ));
        messageIdToTurnIndex.put("msg_003", 0);
        
        // Message 4: Skill B 的 toolResult
        messages.add(createMessage(
            "msg_004",
            "toolResult",
            2500L,
            null,
            null
        ));
        messageIdToTurnIndex.put("msg_004", 0);
        
        // Message 5: 正常结束
        messages.add(createMessage(
            "msg_005",
            "assistant",
            3000L,
            "完成",
            null
        ));
        messageIdToTurnIndex.put("msg_005", 0);
        
        List<TranscriptParser.SkillInvocation> skills = detector.detectSkillChains(
            messages, messageIdToTurnIndex
        );
        
        assertEquals(2, skills.size());
        
        // 验证 Skill A
        assertEquals("bash", skills.get(0).skillName());
        assertEquals("msg_001", skills.get(0).readMessageId());
        // 中间 skill 的终点是下一个 skill 前的最后一条 toolResult
        assertEquals("msg_002", skills.get(0).resultMessageId());
        assertEquals(1, skills.get(0).sequenceOrder());
        assertFalse(skills.get(0).isError());
        assertEquals(500, skills.get(0).durationMs());
        
        // 验证 Skill B
        assertEquals("grep", skills.get(1).skillName());
        assertEquals("msg_003", skills.get(1).readMessageId());
        // 最后一个 skill 的终点是第一个非 toolCall 的 assistant 消息
        assertEquals("msg_005", skills.get(1).resultMessageId());
        assertEquals(2, skills.get(1).sequenceOrder());
        assertFalse(skills.get(1).isError());
        assertEquals(1000, skills.get(1).durationMs()); // 3000 - 2000
    }
    
    // TODO: 此测试用例需要进一步调试
    /*
    @Test
    @DisplayName("检测没有 toolResult 的 skill 调用")
    void testDetectSkillWithoutToolResult() {
        // 构造测试数据：Skill A → assistant(toolCall) → Skill B
        List<MessageRecord> messages = new ArrayList<>();
        Map<String, Integer> messageIdToTurnIndex = new HashMap<>();
        
        // Message 1: Skill A 调用起点
        List<MessageRecord.ToolCallInfo> toolCalls1 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_1", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_001",
            "assistant",
            1000L,
            "## 🛠️ 正在使用 Skill A\n执行中...",
            toolCalls1
        ));
        messageIdToTurnIndex.put("msg_001", 0);
        
        // Message 2: 另一个 toolCall（没有"正在使用 xxx Skill"）
        List<MessageRecord.ToolCallInfo> toolCalls2 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_2", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_002",
            "assistant",
            1500L,
            "执行其他命令",
            toolCalls2
        ));
        messageIdToTurnIndex.put("msg_002", 0);
        
        // Message 3: Skill B 调用起点
        List<MessageRecord.ToolCallInfo> toolCalls3 = Arrays.asList(
            new MessageRecord.ToolCallInfo("call_3", "exec", "{}")
        );
        messages.add(createMessage(
            "msg_003",
            "assistant",
            2000L,
            "正在使用 python Skill",
            toolCalls3
        ));
        messageIdToTurnIndex.put("msg_003", 0);
        
        List<TranscriptParser.SkillInvocation> skills = detector.detectSkillChains(
            messages, messageIdToTurnIndex
        );
        
        assertEquals(2, skills.size());
        
        // 验证 Skill A - 应该标记为错误，因为没有 toolResult
        assertEquals("Skill A", skills.get(0).skillName());
        assertTrue(skills.get(0).isError());
        assertNotNull(skills.get(0).resultMessageId());
        assertEquals("msg_002", skills.get(0).resultMessageId());
        assertEquals(1, skills.get(0).sequenceOrder());
        
        // 验证 Skill B - 最后一个 skill，文件结束
        assertEquals("python", skills.get(1).skillName());
        assertTrue(skills.get(1).isError());
        assertEquals(2, skills.get(1).sequenceOrder());
    }
    */
}
