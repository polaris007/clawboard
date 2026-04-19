package com.company.clawboard.parser;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SystemMessageFilter {

    private final ClawboardProperties properties;
    
    // Compile regex patterns for system message detection (matching Python behavior)
    private static final Pattern[] SYSTEM_MESSAGE_PATTERNS = {
        Pattern.compile("A new session was started via /new or /reset", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Run your Session Startup sequence", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Read HEARTBEAT\\.md if it exists", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("^System:\\s*\\[", Pattern.CASE_INSENSITIVE)  // System status messages
    };

    public boolean shouldFilterAssistant(String provider, String model) {
        if (!properties.getParser().isDeliveryMirrorFilter()) return false;
        return "openclaw".equals(provider) && "delivery-mirror".equals(model);
    }

    public boolean isSystemGeneratedUserMessage(String text) {
        if (text == null || text.isEmpty()) return false;
        
        // First check configured prefixes (backward compatibility)
        for (String prefix : properties.getParser().getSystemMessagePrefixes()) {
            if (text.startsWith(prefix)) return true;
        }
        
        // Then check regex patterns (matching Python behavior)
        for (Pattern pattern : SYSTEM_MESSAGE_PATTERNS) {
            if (pattern.matcher(text).find()) return true;
        }
        
        return false;
    }
}
