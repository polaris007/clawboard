package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TraceNode {
    private Integer stepOrder;
    private String nodeType;
    private String nodeName;
    private Boolean status;
    private Long timeStamp;
    private Integer durationMs;
}
