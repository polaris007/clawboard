package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardScanProgress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScanProgressMapper {
    int insert(DashboardScanProgress progress);
    int upsertProgress(DashboardScanProgress progress);
    DashboardScanProgress selectByEmployeeAndFile(String employeeId, String filePath);
}
