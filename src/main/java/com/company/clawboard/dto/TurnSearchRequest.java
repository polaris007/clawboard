package com.company.clawboard.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TurnSearchRequest extends TimeRangeRequest {
    private String skillId;
}
