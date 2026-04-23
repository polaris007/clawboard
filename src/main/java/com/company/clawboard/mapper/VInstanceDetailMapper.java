package com.company.clawboard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VInstanceDetailMapper {
    
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
}
