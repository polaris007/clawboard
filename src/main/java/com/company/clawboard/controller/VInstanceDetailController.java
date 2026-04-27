package com.company.clawboard.controller;

import com.company.clawboard.dto.ApiResponse;
import com.company.clawboard.dto.OrgOption;
import com.company.clawboard.dto.UserOption;
import com.company.clawboard.dto.UserSearchRequest;
import com.company.clawboard.service.VInstanceDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OpenClaw 实例控制器
 */
@RestController
@RequestMapping("/api/v1/instances")
@RequiredArgsConstructor
@Slf4j
public class VInstanceDetailController {
    
    private final VInstanceDetailService vInstanceDetailService;
    
    /**
     * 获取机构列表
     * @return 机构选项列表
     */
    @GetMapping("/orgs/list")
    public ApiResponse<List<OrgOption>> getOrgOptions() {
        log.info("获取机构列表");
        List<OrgOption> orgs = vInstanceDetailService.getOrgOptions();
        return ApiResponse.ok(orgs);
    }
    
    /**
     * 根据机构查询用户列表
     * @param request 用户搜索请求
     * @return 用户选项列表
     */
    @PostMapping("/users/search")
    public ApiResponse<List<UserOption>> getUsersByOrgCodes(@RequestBody UserSearchRequest request) {
        log.info("根据机构代码查询用户: orgCodes={}", request.getOrgCodes());
        List<UserOption> users = vInstanceDetailService.getUsersByOrgCodes(request.getOrgCodes());
        return ApiResponse.ok(users);
    }
}
