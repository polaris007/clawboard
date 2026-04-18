package com.company.clawboard.dto;

import lombok.Data;
import java.util.List;

@Data
public class TurnSearchItem {
    private Long turnId;
    private Long timeStamp;
    private String userName;
    private String userInput;
    private Integer durationMs;
    private String resultStatus;
    private Integer qualityStatus;
    private TokenInfo tokens;
    private List<String> skills;
    private List<String> tools;
    private String logFileName;

    @Data
    public static class TokenInfo {
        private Integer total;
        private Integer input;
        private Integer output;
    }
}
