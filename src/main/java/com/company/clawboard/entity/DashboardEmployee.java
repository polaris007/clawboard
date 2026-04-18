package com.company.clawboard.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardEmployee {
    private String employeeId;
    private String employeeName;
    private String teamName;
    private Integer isActive;
    private LocalDateTime updatedAt;
}
