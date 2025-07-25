package com.example.demo.form;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LoginForm {
    @NotNull(message = "ユーザーIDは必須です")
    private String userId;
    @NotNull(message = "パスワードは必須です")
    private String pass;
}
