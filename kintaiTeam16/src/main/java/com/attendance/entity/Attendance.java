package com.attendance.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("attendances")
public class Attendance {
    @Id
    private Long attendanceId;
    private String employeeId;
    private LocalDate targetDate;
    private String attendanceType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakTime;
    private BigDecimal actualWorkTime;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    
    public boolean isWorkingDay() {
        return "WORK".equals(attendanceType);
    }
}
