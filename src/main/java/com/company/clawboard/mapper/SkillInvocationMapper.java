package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardSkillInvocation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SkillInvocationMapper {
    int batchInsertIgnore(List<DashboardSkillInvocation> invocations);
    List<String> selectDistinctSkills();
}
