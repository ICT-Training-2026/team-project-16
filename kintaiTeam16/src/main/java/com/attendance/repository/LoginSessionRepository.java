package com.attendance.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.attendance.entity.LoginSession;

@Repository
public interface LoginSessionRepository extends CrudRepository<LoginSession, String> {
    Optional<LoginSession> findBySessionIdAndEmployeeId(String sessionId, String employeeId);
    void deleteBySessionId(String sessionId);
}
