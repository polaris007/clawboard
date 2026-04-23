package com.company.clawboard.dto;

import lombok.Data;

@Data
public class GlobalStatsResponse {
    private Long totalTokens;
    private Integer totalTurns;
    private Integer totalSkillCalls;
    private Integer totalUsers;
    private Integer registeredUsers;  // 新增字段：注册用户总数
}
