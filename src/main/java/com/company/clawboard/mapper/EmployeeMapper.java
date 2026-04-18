package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardEmployee;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    int insert(DashboardEmployee employee);
    int upsert(DashboardEmployee employee);
    DashboardEmployee selectByEmployeeId(String employeeId);
    List<DashboardEmployee> selectAll();
}
