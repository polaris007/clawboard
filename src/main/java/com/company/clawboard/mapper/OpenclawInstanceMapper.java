package com.company.clawboard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OpenclawInstanceMapper {
    @Select("SELECT uid, user_config_json FROM openclaw_instances WHERE status = 'running' AND deleted_at IS NULL")
    List<Map<String, Object>> selectRunningInstances();
}
