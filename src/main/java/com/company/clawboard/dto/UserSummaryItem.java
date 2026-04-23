package com.company.clawboard.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserSummaryItem {
    private String userId;
    private String userName;
    private String orgCode;
    private Boolean status;  // 龙虾是否在线：true active / false inactive
    private Long lastHeartbeat;
    private TokenStats tokens;
    private TurnStats turns;
    private SkillStats skillCalls;
    private ToolStats toolCalls;
    private List<String> topSkills;

    @Data
    public static class TokenStats {
        private Long total;
        private Long input;
        private Long output;
    }

    @Data
    public static class TurnStats {
        private Integer total;
        private Integer complete;
        private Integer error;
    }

    @Data
    public static class SkillStats {
        private Integer total;
        private Integer success;
        private Integer error;
    }

    @Data
    public static class ToolStats {
        private Integer total;
        private Integer success;
        private Integer error;
    }
}
