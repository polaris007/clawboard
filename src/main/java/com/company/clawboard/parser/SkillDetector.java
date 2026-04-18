package com.company.clawboard.parser;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SkillDetector {

    private final ClawboardProperties properties;
    private static final Pattern SKILL_PATH_PATTERN = Pattern.compile("skills/([^/]+)/");

    public String extractSkillName(String toolCallName) {
        if (toolCallName == null || !toolCallName.startsWith("skill_")) return null;
        String skillId = toolCallName.substring(6); // Remove "skill_" prefix
        return properties.getSkill().getNameMapping().getOrDefault(skillId, skillId);
    }

    public boolean isSkillToolCall(String toolCallName) {
        return toolCallName != null && toolCallName.startsWith("skill_");
    }
}
