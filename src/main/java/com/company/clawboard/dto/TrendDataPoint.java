package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TrendDataPoint {
    private Long timeLabel;
    private Long tokens;
    private Integer turns;
    private Integer skills;
}
