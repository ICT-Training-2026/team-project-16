package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String pass;
    private String deptCode;
    private String deptName;
    private String name;
    private boolean deleteFLG;
    
    // 管理者権限チェック（総務部門かどうか）
    public boolean isAdmin() {
        return "SOUMU".equals(this.deptCode);
    }
}