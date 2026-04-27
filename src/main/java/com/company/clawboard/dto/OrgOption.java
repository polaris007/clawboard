package com.company.clawboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机构选项 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgOption {
    private String orgCode;   // 机构代码
    private String orgName;   // 机构名称（目前与 orgCode 相同）
}
