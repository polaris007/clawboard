package com.company.clawboard.mapper;

import com.company.clawboard.dto.OrgOption;
import com.company.clawboard.dto.UserOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * OpenClaw 实例视图 Mapper
 */
@Mapper
public interface VInstanceDetailMapper {
    
    /**
     * 获取机构列表（去重，排除 deleted 状态）
     * @return 机构选项列表
     */
    List<OrgOption> selectDistinctOrgCodes();
    
    /**
     * 根据机构代码查询用户列表（排除 deleted 状态）
     * @param orgCodes 机构代码列表
     * @return 用户选项列表
     */
    List<UserOption> selectUsersByOrgCodes(@Param("orgCodes") List<String> orgCodes);
    
    // ===== 以下为原有方法，保留以兼容现有代码 =====
    
    /**
     * 查询所有运行中的实例
     * @return 实例列表，每个实例包含 uid, user_config_name, user_config_org_code
     */
    List<Map<String, Object>> selectRunningInstances();
    
    /**
     * 根据 uid 查询实例状态
     * @param uid 用户 ID
     * @return status 字段值，如果不存在返回 null
     */
    String selectStatusByUid(@Param("uid") String uid);
    
    /**
     * 批量查询多个 uid 的实例状态
     * @param uids 用户 ID 列表
     * @return 实例状态列表，每个包含 uid 和 status
     */
    List<Map<String, Object>> selectStatusByUids(@Param("uids") List<String> uids);
    
    /**
     * 统计注册用户数（status != 'deleted' 的不重复 uid 数量）
     * @return 注册用户数
     */
    Integer countRegisteredUsers();
    
    /**
     * 统计 OpenClaw 实例总数量（排除 status = 'deleted'）
     * @return 实例总数量
     */
    Integer countInstanceTotal();
    
    /**
     * 统计 OpenClaw 异常实例数量（排除 status = 'deleted' 和 'running'）
     * @return 异常实例数量
     */
    Integer countInstanceAbnormal();
}
