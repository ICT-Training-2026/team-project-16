package com.example.demo.service;


import com.example.demo.entity.Login;
import com.example.demo.entity.User;

public interface LoginService {
    User execute(Login login);
}
