package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TimeRangeRequest {
    private String teamName;
    private String userName;
    private Long startTime;   // epoch millis
    private Long endTime;     // epoch millis
    private Integer page;     // default 1
    private Integer pageSize; // default 10

    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
    public int getOffset() { return (getPageOrDefault() - 1) * getPageSizeOrDefault(); }
}
