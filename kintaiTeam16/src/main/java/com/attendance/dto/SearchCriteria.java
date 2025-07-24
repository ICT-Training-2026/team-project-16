package com.attendance.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SearchCriteria {
    private LocalDate startDate;
    private LocalDate endDate;
    private String employeeId;
    private String departmentCode;
    private String attendanceType;
}
