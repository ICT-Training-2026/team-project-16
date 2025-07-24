package com.attendance.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.attendance.entity.Employee;
import com.attendance.entity.LoginSession;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.LoginSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final EmployeeRepository employeeRepository;
    private final LoginSessionRepository loginSessionRepository;
    
    public Optional<Employee> authenticate(String userId, String password) {
        Optional<Employee> employee = employeeRepository.findByEmployeeIdAndDeleteFlag(userId, "0");
        if (employee.isPresent() && password.equals(employee.get().getPassword())) {
            return employee;
        }
        return Optional.empty();
    }
    
    public String createSession(Employee employee, String ipAddress) {
        String sessionId = UUID.randomUUID().toString();
        LoginSession session = new LoginSession();
        session.setSessionId(sessionId);
        session.setEmployeeId(employee.getEmployeeId());
        session.setLoginDatetime(LocalDateTime.now());
        session.setLastAccessDatetime(LocalDateTime.now());
        session.setExpireDatetime(LocalDateTime.now().plusHours(8));
        session.setIpAddress(ipAddress);
        
        loginSessionRepository.save(session);
        return sessionId;
    }
    
    public boolean validateSession(String sessionId, String employeeId) {
        Optional<LoginSession> session = loginSessionRepository.findBySessionIdAndEmployeeId(sessionId, employeeId);
        if (session.isPresent() && session.get().getExpireDatetime().isAfter(LocalDateTime.now())) {
            // セッションの最終アクセス時間を更新
            session.get().setLastAccessDatetime(LocalDateTime.now());
            loginSessionRepository.save(session.get());
            return true;
        }
        return false;
    }
    
    public void logout(String sessionId) {
        loginSessionRepository.deleteBySessionId(sessionId);
    }
}
