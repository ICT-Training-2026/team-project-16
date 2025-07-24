package com.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.attendance.entity.Attendance;

@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
    
    @Query("SELECT * FROM attendances WHERE employee_id = :employeeId " +
           "AND target_date BETWEEN :startDate AND :endDate " +
           "ORDER BY target_date DESC")
    List<Attendance> findByEmployeeIdAndDateRange(@Param("employeeId") String employeeId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    @Query("SELECT * FROM attendances WHERE target_date BETWEEN :startDate AND :endDate " +
           "ORDER BY target_date DESC, employee_id")
    List<Attendance> findByDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
}
