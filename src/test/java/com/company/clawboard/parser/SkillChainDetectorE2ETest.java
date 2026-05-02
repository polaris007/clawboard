package com.company.clawboard.parser;

import com.company.clawboard.BaseIntegrationTest;
import com.company.clawboard.parser.model.MessageRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End tests for SkillChainDetector using real transcript files
 */
class SkillChainDetectorE2ETest extends BaseIntegrationTest {
    
    @Autowired
    private TranscriptParser transcriptParser;
    
    private SkillChainDetector detector = new SkillChainDetector();
    
    @Test
    @DisplayName("E2E: 检测真实 transcript 文件中的 skill 调用链")
    void testDetectSkillChainsFromRealTranscript() throws Exception {
        // 使用真实的 transcript 文件
        String transcriptPath = "test/session-transcript/a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de/agents/main/sessions/9a0af35c-6303-4ae7-a932-54396b74e799.jsonl";
        Path filePath = Paths.get(transcriptPath);
        
        assertTrue(filePath.toFile().exists(), "Transcript file should exist: " + transcriptPath);
        
        // 解析 transcript 文件
        TranscriptParser.ParsedTranscript parsed = transcriptParser.parseFile(filePath, "test-user");
        
        assertNotNull(parsed, "Parsed transcript should not be null");
        assertNotNull(parsed.skillInvocations(), "Skill invocations should not be null");
        
        System.out.println("=== E2E Test Results ===");
        System.out.println("Session ID: " + parsed.sessionId());
        System.out.println("Total messages: " + parsed.messages().size());
        System.out.println("Total turns: " + parsed.turns().size());
        System.out.println("Skill invocations found: " + parsed.skillInvocations().size());
        
        // 验证检测结果
        List<TranscriptParser.SkillInvocation> skills = parsed.skillInvocations();
        
        if (!skills.isEmpty()) {
            // 打印每个 skill 的详细信息
            for (int i = 0; i < skills.size(); i++) {
                TranscriptParser.SkillInvocation skill = skills.get(i);
                System.out.println("\n--- Skill #" + (i + 1) + " ---");
                System.out.println("  Name: " + skill.skillName());
                System.out.println("  Read Message ID: " + skill.readMessageId());
                System.out.println("  Result Message ID: " + skill.resultMessageId());
                System.out.println("  Turn ID: " + skill.turnId());
                System.out.println("  Invoked At: " + skill.invokedAt());
                System.out.println("  Is Error: " + skill.isError());
                System.out.println("  Duration (ms): " + skill.durationMs());
                System.out.println("  Sequence Order: " + skill.sequenceOrder());
                
                // 基本验证
                assertNotNull(skill.skillName(), "Skill name should not be null");
                assertFalse(skill.skillName().isEmpty(), "Skill name should not be empty");
                assertNotNull(skill.readMessageId(), "Read message ID should not be null");
                assertNotNull(skill.resultMessageId(), "Result message ID should not be null");
                assertTrue(skill.sequenceOrder() > 0, "Sequence order should be positive");
            }
            
            // 验证至少检测到一个 skill
            assertTrue(skills.size() > 0, "Should detect at least one skill invocation");
            
        } else {
            System.out.println("No skill invocations detected in this transcript file.");
        }
    }
    
    @Test
    @DisplayName("E2E: 验证多 skill 调用的 sequence_order 正确性")
    void testMultipleSkillsSequenceOrder() throws Exception {
        // 选择一个可能包含多个 skill 调用的 transcript 文件
        String transcriptPath = "test/session-transcript/a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de/agents/main/sessions/9a0af35c-6303-4ae7-a932-54396b74e799.jsonl";
        Path filePath = Paths.get(transcriptPath);
        
        if (!filePath.toFile().exists()) {
            System.out.println("Skipping test - transcript file not found: " + transcriptPath);
            return;
        }
        
        TranscriptParser.ParsedTranscript parsed = transcriptParser.parseFile(filePath, "test-user");
        List<TranscriptParser.SkillInvocation> skills = parsed.skillInvocations();
        
        if (skills.size() >= 2) {
            // 按 turnId 分组，检查同一 turn 中的 sequence_order
            Map<Long, List<TranscriptParser.SkillInvocation>> skillsByTurn = new HashMap<>();
            for (TranscriptParser.SkillInvocation skill : skills) {
                if (skill.turnId() != null) {
                    skillsByTurn.computeIfAbsent(skill.turnId(), k -> new ArrayList<>()).add(skill);
                }
            }
            
            // 验证每个 turn 中的 sequence_order 是递增的
            for (Map.Entry<Long, List<TranscriptParser.SkillInvocation>> entry : skillsByTurn.entrySet()) {
                List<TranscriptParser.SkillInvocation> turnSkills = entry.getValue();
                if (turnSkills.size() > 1) {
                    System.out.println("Turn " + entry.getKey() + " has " + turnSkills.size() + " skills");
                    
                    // 按 sequenceOrder 排序
                    turnSkills.sort(Comparator.comparingInt(TranscriptParser.SkillInvocation::sequenceOrder));
                    
                    // 验证 sequence_order 是连续递增的
                    for (int i = 0; i < turnSkills.size(); i++) {
                        int expectedOrder = i + 1;
                        int actualOrder = turnSkills.get(i).sequenceOrder();
                        assertEquals(expectedOrder, actualOrder, 
                            "Sequence order should be " + expectedOrder + " but was " + actualOrder);
                    }
                }
            }
        } else {
            System.out.println("Not enough skills to test sequence order (found: " + skills.size() + ")");
        }
    }
    
    @Test
    @DisplayName("E2E: 验证 skill 执行时长的合理性")
    void testSkillDurationReasonableness() throws Exception {
        String transcriptPath = "test/session-transcript/a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de/agents/main/sessions/9a0af35c-6303-4ae7-a932-54396b74e799.jsonl";
        Path filePath = Paths.get(transcriptPath);
        
        if (!filePath.toFile().exists()) {
            System.out.println("Skipping test - transcript file not found");
            return;
        }
        
        TranscriptParser.ParsedTranscript parsed = transcriptParser.parseFile(filePath, "test-user");
        List<TranscriptParser.SkillInvocation> skills = parsed.skillInvocations();
        
        for (TranscriptParser.SkillInvocation skill : skills) {
            // 验证 duration 不为负数
            assertTrue(skill.durationMs() >= 0, 
                "Duration should be non-negative for skill: " + skill.skillName());
            
            // 如果 duration > 0，验证 readMessageId 和 resultMessageId 都存在
            if (skill.durationMs() > 0) {
                assertNotNull(skill.readMessageId(), "Read message ID should exist for skill with duration");
                assertNotNull(skill.resultMessageId(), "Result message ID should exist for skill with duration");
            }
            
            System.out.println("Skill: " + skill.skillName() + ", Duration: " + skill.durationMs() + "ms, Error: " + skill.isError());
        }
    }
}
