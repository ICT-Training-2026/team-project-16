package com.example.demo.form;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserForm {
    @NotNull(message = "ユーザーIDは必須です")
    private String userId;
    
    @NotNull(message = "パスワードは必須です")
    private String pass;
    
    @NotNull(message = "所属コードは必須です")
    private String deptCode;
    
    @NotNull(message = "所属名は必須です")
    private String deptName;
    
    @NotNull(message = "氏名は必須です")
    private String name;
}