package com.example.demo.repository;


import com.example.demo.entity.Nenkyuu;

public interface NenkyuuRepository {
    Nenkyuu findByUserId(String userId);
    boolean create(Nenkyuu nenkyuu);
    boolean updateRemainingDays(String userId, int remainingDays);
    boolean decreaseRemainingDays(String userId);
}
