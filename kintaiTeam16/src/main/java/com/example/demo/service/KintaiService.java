package com.example.demo.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.demo.entity.Kintai;
import com.example.demo.form.KintaiForm;

public interface KintaiService {
    boolean register(String userId, KintaiForm form);
    boolean update(String userId, KintaiForm form);
    boolean delete(String userId, LocalDate workDate);
    List<Kintai> getKintaiList(String userId, LocalDate startDate, LocalDate endDate);
    List<Kintai> getAllKintaiList(LocalDate startDate, LocalDate endDate);
    Kintai getKintai(String userId, LocalDate workDate);
    boolean canRegisterShinkyuu(String userId);
    boolean canRegisterNenkyuu(String userId);
    Map<String, Object> calculateMonthlyWorkInfo(String userId, int year, int month);
    List<Map<String, Object>> calculateAllUsersMonthlyWorkInfo(int year, int month);
}
