package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;
import com.example.demo.form.UserForm;

public interface UserService {
    boolean register(UserForm form);
    boolean update(UserForm form);
    boolean delete(String userId);
    User findByUserId(String userId);
    List<User> findAll();
}
