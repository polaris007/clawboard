package com.company.clawboard.dto;

import lombok.Data;
import java.util.List;

@Data
public class TraceResponse {
    private Long turnId;
    private List<TraceNode> nodes;
}
