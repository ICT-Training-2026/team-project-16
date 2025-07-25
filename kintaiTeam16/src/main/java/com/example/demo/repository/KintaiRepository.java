package com.example.demo.repository;


import java.time.LocalDate;
import java.util.List;

import com.example.demo.entity.Kintai;

public interface KintaiRepository {
    boolean create(Kintai kintai);
    boolean update(Kintai kintai);
    boolean delete(String userId, LocalDate workDate);
    Kintai findByUserIdAndDate(String userId, LocalDate workDate);
    List<Kintai> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);
    List<Kintai> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Kintai> findByUserIdAndMonth(String userId, int year, int month);
    List<Kintai> findByMonth(int year, int month);
}
