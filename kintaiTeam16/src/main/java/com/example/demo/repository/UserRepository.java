package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.Login;
import com.example.demo.entity.User;

public interface UserRepository {
    User findByLogin(Login login);
    List<User> findAll();
    User findByUserId(String userId);
    boolean create(User user);
    boolean update(User user);
    boolean delete(String userId);
}
