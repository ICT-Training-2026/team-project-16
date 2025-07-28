package com.example.demo.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Login;
import com.example.demo.entity.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User findByLogin(Login login) {
        String sql = "SELECT USER_ID, PASS, DEPT_CODE, DEPT_NAME, NAME, DELETE_FLG FROM USERS WHERE USER_ID = ? AND PASS = ? AND DELETE_FLG = false";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, login.getUserId(), login.getPass());
            return mapToUser(result);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT USER_ID, PASS, DEPT_CODE, DEPT_NAME, NAME, DELETE_FLG FROM USERS WHERE DELETE_FLG = false ORDER BY USER_ID";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list.stream()
                .map(this::mapToUser)
                .toList();
    }

    @Override
    public User findByUserId(String userId) {
        String sql = "SELECT USER_ID, PASS, DEPT_CODE, DEPT_NAME, NAME, DELETE_FLG FROM USERS WHERE USER_ID = ?";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId);
            return mapToUser(result);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean create(User user) {
        String sql = "INSERT INTO USERS (USER_ID, PASS, DEPT_CODE, DEPT_NAME, NAME, DELETE_FLG) VALUES (?, ?, ?, ?, ?, false)";
        
        int result = jdbcTemplate.update(sql,
            user.getUserId(),
            user.getPass(),
            user.getDeptCode(),
            user.getDeptName(),
            user.getName());
        
        return result == 1;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE USERS SET PASS = ?, DEPT_CODE = ?, DEPT_NAME = ?, NAME = ? WHERE USER_ID = ?";
        
        int result = jdbcTemplate.update(sql,
            user.getPass(),
            user.getDeptCode(),
            user.getDeptName(),
            user.getName(),
            user.getUserId());
        return result == 1;
    }

    @Override
    public boolean delete(String userId) {
        String sql = "UPDATE USERS SET DELETE_FLG = true WHERE USER_ID = ?";
        
        int result = jdbcTemplate.update(sql, userId);
        return result == 1;
    }

    private User mapToUser(Map<String, Object> map) {
        User user = new User();
        user.setUserId((String) map.get("USER_ID"));
        user.setPass((String) map.get("PASS"));
        user.setDeptCode((String) map.get("DEPT_CODE"));
        user.setDeptName((String) map.get("DEPT_NAME"));
        user.setName((String) map.get("NAME"));
        user.setDeleteFLG((Boolean) map.get("DELETE_FLG"));
        return user;
    }
}