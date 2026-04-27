package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户选项 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOption {
    private String userId;      // 工号（uid）
    private String userName;    // 姓名（user_config_name）
}
