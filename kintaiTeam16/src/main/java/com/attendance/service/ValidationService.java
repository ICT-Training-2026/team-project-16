package com.attendance.service;

import org.springframework.stereotype.Service;

import com.attendance.dto.ValidationResult;
import com.attendance.entity.Attendance;

@Service
public class ValidationService {
    
    public ValidationResult validateAttendance(Attendance attendance) {
        ValidationResult result = new ValidationResult();
        
        if (attendance.getEmployeeId() == null || attendance.getEmployeeId().trim().isEmpty()) {
            result.addError("社員IDは必須です");
        }
        
        if (attendance.getTargetDate() == null) {
            result.addError("対象日は必須です");
        }
        
        if (attendance.getStartTime() != null && attendance.getEndTime() != null) {
            if (attendance.getStartTime().isAfter(attendance.getEndTime())) {
                result.addError("開始時刻は終了時刻より前である必要があります");
            }
        }
        
        return result;
    }
    
    public ValidationResult validateRequired(String value, String fieldName) {
        ValidationResult result = new ValidationResult();
        if (value == null || value.trim().isEmpty()) {
            result.addError(fieldName + "は必須です");
        }
        return result;
    }
    
    public ValidationResult validatePasswordStrength(String password) {
        ValidationResult result = new ValidationResult();
        if (password == null || password.length() < 8) {
            result.addError("パスワードは8文字以上で入力してください");
        }
        return result;
    }
}
