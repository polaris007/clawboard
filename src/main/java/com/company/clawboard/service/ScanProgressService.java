package com.company.clawboard.service;

import com.company.clawboard.entity.DashboardScanProgress;
import com.company.clawboard.mapper.ScanProgressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScanProgressService {

    private final ScanProgressMapper scanProgressMapper;

    public DashboardScanProgress getProgress(String employeeId, String filePath) {
        return scanProgressMapper.selectByEmployeeAndFile(employeeId, filePath);
    }

    public void updateProgress(DashboardScanProgress progress) {
        scanProgressMapper.upsertProgress(progress);
    }
}
