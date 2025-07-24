package com.attendance.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("departments")
public class Department {
    @Id
    private String departmentCode;
    private String departmentName;
    private Integer displayOrder;
    private String deleteFlag;
}
