package com.attendance.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("paid_leaves")
public class PaidLeave {
    @Id
    private Long paidLeaveId;
    private String employeeId;
    private Integer targetYear;
    private Integer grantedDays;
    private Integer usedDays;
    private Integer remainingDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
