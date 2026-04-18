package com.company.clawboard.parser;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemMessageFilter {

    private final ClawboardProperties properties;

    public boolean shouldFilterAssistant(String provider, String model) {
        if (!properties.getParser().isDeliveryMirrorFilter()) return false;
        return "openclaw".equals(provider) && "delivery-mirror".equals(model);
    }

    public boolean isSystemGeneratedUserMessage(String text) {
        if (text == null || text.isEmpty()) return false;
        for (String prefix : properties.getParser().getSystemMessagePrefixes()) {
            if (text.startsWith(prefix)) return true;
        }
        return false;
    }
}
