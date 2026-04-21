package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TrendDataPoint {
    private String timeLabel;
    private Long tokens;
    private Integer turns;
    private Integer skills;
}
