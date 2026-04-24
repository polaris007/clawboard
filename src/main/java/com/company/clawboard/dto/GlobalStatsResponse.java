package com.company.clawboard.dto;

import lombok.Data;

@Data
public class GlobalStatsResponse {
    private Long totalTokens;
    private Integer totalTurns;
    private Integer totalSkillCalls;
    private Integer totalUsers;
    private Integer registeredUsers;  // 注册用户总数
    private Integer instanceTotalCount;  // OpenClaw实例总数量（排除deleted）
    private Integer instanceAbnormalCount;  // OpenClaw异常实例数量（排除deleted和running）
}
