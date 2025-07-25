package com.example.demo.repository;


import java.time.LocalDate;
import java.util.List;

import com.example.demo.entity.Shinshutsu;

public interface ShinshutsuRepository {
    boolean create(Shinshutsu shinshutsu);
    boolean updateUsedFlg(String userId, LocalDate shinshutsuDate, boolean usedFlg);
    List<Shinshutsu> findUnusedByUserId(String userId);
    Shinshutsu findByUserIdAndDate(String userId, LocalDate shinshutsuDate);
    int countUnusedByUserId(String userId);
}