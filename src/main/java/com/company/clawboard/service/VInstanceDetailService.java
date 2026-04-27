package com.company.clawboard.service;

import com.company.clawboard.dto.OrgOption;
import com.company.clawboard.dto.UserOption;
import com.company.clawboard.mapper.VInstanceDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * OpenClaw 实例服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VInstanceDetailService {
    
    private final VInstanceDetailMapper vInstanceDetailMapper;
    
    /**
     * 获取机构列表
     * @return 机构选项列表
     */
    public List<OrgOption> getOrgOptions() {
        log.info("获取机构列表");
        List<OrgOption> orgs = vInstanceDetailMapper.selectDistinctOrgCodes();
        return orgs != null ? orgs : Collections.emptyList();
    }
    
    /**
     * 根据机构代码查询用户列表
     * @param orgCodes 机构代码列表
     * @return 用户选项列表
     */
    public List<UserOption> getUsersByOrgCodes(List<String> orgCodes) {
        if (orgCodes == null || orgCodes.isEmpty()) {
            log.warn("机构代码列表为空");
            return Collections.emptyList();
        }
        
        log.info("根据机构代码查询用户: orgCodes={}", orgCodes);
        List<UserOption> users = vInstanceDetailMapper.selectUsersByOrgCodes(orgCodes);
        return users != null ? users : Collections.emptyList();
    }
}
