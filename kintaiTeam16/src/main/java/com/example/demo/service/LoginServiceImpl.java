package com.example.demo.service;


import org.springframework.stereotype.Service;

import com.example.demo.entity.Login;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    
    private final UserRepository userRepository;

    @Override
    public User execute(Login login) {
        return userRepository.findByLogin(login);
    }
}
