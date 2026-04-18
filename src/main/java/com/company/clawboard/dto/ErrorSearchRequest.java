package com.company.clawboard.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErrorSearchRequest extends TimeRangeRequest {
    private String errorType;
    private String severity;
}
