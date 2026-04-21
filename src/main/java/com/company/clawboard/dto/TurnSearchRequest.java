package com.company.clawboard.dto;

import lombok.Data;

@Data
public class TurnSearchRequest {
    private String userId;     // 工号（employee_id）精确匹配
    private Long startTime;    // 开始时间戳（毫秒）
    private Long endTime;      // 结束时间戳（毫秒）
    private String skillId;    // 技能ID精确匹配
    private Integer page;      // 默认 1
    private Integer pageSize;  // 默认 10

    public int getPageOrDefault() { return page != null ? page : 1; }
    public int getPageSizeOrDefault() { return pageSize != null ? pageSize : 10; }
}
