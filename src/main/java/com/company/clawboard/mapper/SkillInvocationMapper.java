package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardSkillInvocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SkillInvocationMapper {
    int batchInsertIgnore(List<DashboardSkillInvocation> invocations);
    List<String> selectDistinctSkills();
    
    /**
     * 删除指定 session 的所有 Skill 调用记录
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
