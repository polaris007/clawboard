package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户搜索请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private List<String> orgCodes;  // 机构代码列表（支持多个）
    // TODO: 未来可扩展其他查询条件，如 userId, userName 等
}
