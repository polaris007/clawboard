package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TimeRangeRequest {
    private String teamName;   // 机构号筛选
    private String userName;   // 姓名模糊匹配
    private String startTime;  // 开始时间，格式 YYYY-MM-DD HH:mm:ss
    private String endTime;    // 结束时间，格式 YYYY-MM-DD HH:mm:ss
    private Integer page;      // 默认 1
    private Integer pageSize;  // 默认 10

    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
    public int getOffset() { return (getPageOrDefault() - 1) * getPageSizeOrDefault(); }
}
