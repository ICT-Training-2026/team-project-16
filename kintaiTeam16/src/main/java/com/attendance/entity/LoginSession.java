package com.attendance.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("login_sessions")
public class LoginSession {
    @Id
    private String sessionId;
    private String employeeId;
    private LocalDateTime loginDatetime;
    private LocalDateTime lastAccessDatetime;
    private LocalDateTime expireDatetime;
    private String ipAddress;
}
