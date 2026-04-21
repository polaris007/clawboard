package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TurnSearchRequest {
    private String userId;     // 工号（employee_id）精确匹配
    private String startTime;  // 开始时间，格式 YYYY-MM-DD HH:mm:ss
    private String endTime;    // 结束时间，格式 YYYY-MM-DD HH:mm:ss
    private String skillId;    // 技能ID精确匹配
    private Integer page;      // 默认 1
    private Integer pageSize;  // 默认 10

    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
}
