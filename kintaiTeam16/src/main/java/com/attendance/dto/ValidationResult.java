package com.attendance.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ValidationResult {
    private boolean valid = true;
    private List<String> errorMessages = new ArrayList<>();
    
    public void addError(String message) {
        this.valid = false;
        this.errorMessages.add(message);
    }
    
    public boolean isValid() {
        return valid;
    }
}
