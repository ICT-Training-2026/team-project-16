package com.attendance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("employees")
public class Employee {
    @Id
    private String employeeId;
    private String employeeName;
    private String departmentCode;
    private String authorityType;
    private String password;
    private BigDecimal hourlyWage;
    private String deleteFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public boolean isGeneralUser() {
        return "GENERAL".equals(authorityType);
    }
    
    public boolean isHRDepartment() {
        return "HR".equals(authorityType);
    }
}
