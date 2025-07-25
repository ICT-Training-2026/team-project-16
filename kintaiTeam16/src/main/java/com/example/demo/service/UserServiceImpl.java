package com.example.demo.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Nenkyuu;
import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import com.example.demo.repository.NenkyuuRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final NenkyuuRepository nenkyuuRepository;

    @Override
    public boolean register(UserForm form) {
        // ユーザー登録
        User user = new User();
        user.setUserId(form.getUserId());
        user.setPass(form.getPass());
        user.setDeptCode(form.getDeptCode());
        user.setDeptName(form.getDeptName());
        user.setName(form.getName());
        user.setDeleteFLG(false);
        
        boolean userResult = userRepository.create(user);
        
        // 有給残日数の初期設定（20日）
        if (userResult) {
            Nenkyuu nenkyuu = new Nenkyuu(form.getUserId(), 20);
            nenkyuuRepository.create(nenkyuu);
        }
        
        return userResult;
    }

    @Override
    public boolean update(UserForm form) {
        User user = new User();
        user.setUserId(form.getUserId());
        user.setPass(form.getPass());
        user.setDeptCode(form.getDeptCode());
        user.setDeptName(form.getDeptName());
        user.setName(form.getName());
        
        return userRepository.update(user);
    }

    @Override
    public boolean delete(String userId) {
        return userRepository.delete(userId);
    }

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
