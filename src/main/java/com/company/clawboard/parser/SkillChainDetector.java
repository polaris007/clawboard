package com.company.clawboard.parser;

import com.company.clawboard.parser.model.MessageRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Skill 调用链检测器
 * 从 transcript 消息列表中识别并提取完整的 skill 调用链信息
 */
public class SkillChainDetector {
    
    private static final Pattern SKILL_USAGE_PATTERN = 
        Pattern.compile("正在使用\\s+(.+?)\\s+Skill");
    
    /**
     * 检测并提取所有 skill 调用链
     * @param messages 已解析的消息列表（按时间顺序）
     * @param messageIdToTurnIndex message ID 到 turn 索引的映射
     * @return 完整的 skill 调用列表
     */
    public List<TranscriptParser.SkillInvocation> detectSkillChains(
        List<MessageRecord> messages,
        Map<String, Integer> messageIdToTurnIndex
    ) {
        List<TranscriptParser.SkillInvocation> skillInvocations = new ArrayList<>();
        
        // 步骤 1: 识别所有 skill 起点
        List<Integer> skillStarts = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            if (isSkillStart(messages.get(i))) {
                skillStarts.add(i);
            }
        }
        
        // 步骤 2: 对每个 skill 起点确定终点
        for (int idx = 0; idx < skillStarts.size(); idx++) {
            int skillIndex = skillStarts.get(idx);
            int nextSkillIndex = (idx + 1 < skillStarts.size()) ? skillStarts.get(idx + 1) : -1;
            
            TranscriptParser.SkillInvocation skill;
            
            if (nextSkillIndex != -1) {
                // === 情况 1: 中间 skill ===
                skill = detectIntermediateSkill(
                    messages, skillIndex, nextSkillIndex, idx + 1, messageIdToTurnIndex
                );
            } else {
                // === 情况 2: 最后一个 skill ===
                skill = detectLastSkill(
                    messages, skillIndex, idx + 1, messageIdToTurnIndex
                );
            }
            
            skillInvocations.add(skill);
        }
        
        return skillInvocations;
    }
    
    /**
     * 检查消息是否是 skill 调用起点
     */
    private boolean isSkillStart(MessageRecord msg) {
        if (!"assistant".equals(msg.role())) {
            return false;
        }
        
        // 检查 textContent 中是否包含 "正在使用 xxx Skill" 文本
        String text = msg.textContent();
        return text != null && SKILL_USAGE_PATTERN.matcher(text).find();
    }
    
    /**
     * 从 skill 调用消息中提取 skill 名称
     */
    private String extractSkillName(MessageRecord msg) {
        String text = msg.textContent();
        if (text != null) {
            var matcher = SKILL_USAGE_PATTERN.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        
        return "unknown";
    }
    
    /**
     * 检查消息是否包含 toolCall
     */
    private boolean hasToolCall(MessageRecord msg) {
        return msg.toolCalls() != null && !msg.toolCalls().isEmpty();
    }
    
    /**
     * 检测中间 skill（后面还有其他 skill）
     */
    private TranscriptParser.SkillInvocation detectIntermediateSkill(
        List<MessageRecord> messages,
        int skillIndex,
        int nextSkillIndex,
        int sequenceOrder,
        Map<String, Integer> messageIdToTurnIndex
    ) {
        // 在 [skillIndex+1, nextSkillIndex-1] 范围内查找最后一条 toolResult
        int lastToolResultIndex = -1;
        for (int j = skillIndex + 1; j < nextSkillIndex; j++) {
            if ("toolResult".equals(messages.get(j).role())) {
                lastToolResultIndex = j;
            }
        }
        
        String resultMessageId;
        boolean isError;
        long durationMs;
        
        if (lastToolResultIndex != -1) {
            // ✅ 找到 toolResult，skill 成功
            resultMessageId = messages.get(lastToolResultIndex).id();
            isError = false;
            durationMs = messages.get(lastToolResultIndex).epochMs() - messages.get(skillIndex).epochMs();
        } else {
            // ❌ 没有 toolResult，skill 失败
            // 选项 B：记录最后一个消息的位置（即使是 assistant toolCall）
            int lastMessageIndex = nextSkillIndex - 1;
            resultMessageId = messages.get(lastMessageIndex).id();
            isError = true;
            durationMs = messages.get(lastMessageIndex).epochMs() - messages.get(skillIndex).epochMs();
        }
        
        Integer turnIdInt = messageIdToTurnIndex.get(messages.get(skillIndex).id());
        Long turnId = turnIdInt != null ? turnIdInt.longValue() : null;
        
        return new TranscriptParser.SkillInvocation(
            extractSkillName(messages.get(skillIndex)),
            messages.get(skillIndex).id(),
            turnId,
            messages.get(skillIndex).epochMs(),
            resultMessageId,
            isError,
            (int) durationMs,
            sequenceOrder
        );
    }
    
    /**
     * 检测最后一个 skill（后面没有其他 skill）
     */
    private TranscriptParser.SkillInvocation detectLastSkill(
        List<MessageRecord> messages,
        int skillIndex,
        int sequenceOrder,
        Map<String, Integer> messageIdToTurnIndex
    ) {
        int index = skillIndex + 1;
        String resultMessageId = null;
        boolean isError = false;
        long durationMs = 0;
        
        while (index < messages.size()) {
            MessageRecord nextMsg = messages.get(index);
            
            if ("user".equals(nextMsg.role())) {
                // User 打断，skill 失败
                resultMessageId = messages.get(index - 1).id();
                isError = true;
                durationMs = messages.get(index - 1).epochMs() - messages.get(skillIndex).epochMs();
                break;
            }
            
            if ("assistant".equals(nextMsg.role()) && !hasToolCall(nextMsg)) {
                // 正常结束，skill 成功
                resultMessageId = nextMsg.id();
                isError = false;
                durationMs = nextMsg.epochMs() - messages.get(skillIndex).epochMs();
                break;
            }
            
            index++;
        }
        
        if (index >= messages.size()) {
            // 文件结束都没找到终点
            resultMessageId = messages.get(messages.size() - 1).id();
            isError = true;
            durationMs = messages.get(messages.size() - 1).epochMs() - messages.get(skillIndex).epochMs();
        }
        
        Integer turnIdInt = messageIdToTurnIndex.get(messages.get(skillIndex).id());
        Long turnId = turnIdInt != null ? turnIdInt.longValue() : null;
        
        return new TranscriptParser.SkillInvocation(
            extractSkillName(messages.get(skillIndex)),
            messages.get(skillIndex).id(),
            turnId != null ? turnId : null,
            messages.get(skillIndex).epochMs(),
            resultMessageId,
            isError,
            (int) durationMs,
            sequenceOrder
        );
    }
}
