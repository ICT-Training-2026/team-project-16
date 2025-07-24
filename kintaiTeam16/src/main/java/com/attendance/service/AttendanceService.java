package com.attendance.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.attendance.dto.SearchCriteria;
import com.attendance.dto.ValidationResult;
import com.attendance.entity.Attendance;
import com.attendance.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final ValidationService validationService;
    
    public void registerAttendance(Attendance attendance) {
        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }
    
    public void updateAttendance(Attendance attendance) {
        attendance.setUpdatedAt(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }
    
    public void deleteAttendance(Long attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }
    
    public List<Attendance> searchAttendance(SearchCriteria criteria) {
        if (criteria.getEmployeeId() != null && !criteria.getEmployeeId().isEmpty()) {
            return attendanceRepository.findByEmployeeIdAndDateRange(
                criteria.getEmployeeId(), criteria.getStartDate(), criteria.getEndDate());
        } else {
            return attendanceRepository.findByDateRange(criteria.getStartDate(), criteria.getEndDate());
        }
    }
    
    public Optional<Attendance> findById(Long attendanceId) {
        return attendanceRepository.findById(attendanceId);
    }
    
    public ValidationResult validateAttendanceData(Attendance attendance) {
        return validationService.validateAttendance(attendance);
    }
}
