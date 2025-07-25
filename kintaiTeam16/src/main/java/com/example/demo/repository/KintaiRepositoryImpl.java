package com.example.demo.repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Kintai;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KintaiRepositoryImpl implements KintaiRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean create(Kintai kintai) {
        String sql = "INSERT INTO KINTAI (USER_ID, WORK_DATE, WORK_TYPE, START_TIME, END_TIME, BREAK_TIME, ACTUAL_WORK_TIME, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        LocalDateTime now = LocalDateTime.now();
        int result = jdbcTemplate.update(sql,
            kintai.getUserId(),
            kintai.getWorkDate(),
            kintai.getWorkType(),
            kintai.getStartTime(),
            kintai.getEndTime(),
            kintai.getBreakTime(),
            kintai.getActualWorkTime(),
            now,
            now);
        
        return result == 1;
    }

    @Override
    public boolean update(Kintai kintai) {
        String sql = "UPDATE KINTAI SET WORK_TYPE = ?, START_TIME = ?, END_TIME = ?, BREAK_TIME = ?, ACTUAL_WORK_TIME = ?, UPDATED_AT = ? WHERE USER_ID = ? AND WORK_DATE = ?";
        
        LocalDateTime now = LocalDateTime.now();
        int result = jdbcTemplate.update(sql,
            kintai.getWorkType(),
            kintai.getStartTime(),
            kintai.getEndTime(),
            kintai.getBreakTime(),
            kintai.getActualWorkTime(),
            now,
            kintai.getUserId(),
            kintai.getWorkDate());
        
        return result == 1;
    }

    @Override
    public boolean delete(String userId, LocalDate workDate) {
        String sql = "DELETE FROM KINTAI WHERE USER_ID = ? AND WORK_DATE = ?";
        
        int result = jdbcTemplate.update(sql, userId, workDate);
        return result == 1;
    }

    @Override
    public Kintai findByUserIdAndDate(String userId, LocalDate workDate) {
        String sql = "SELECT USER_ID, WORK_DATE, WORK_TYPE, START_TIME, END_TIME, BREAK_TIME, ACTUAL_WORK_TIME, CREATED_AT, UPDATED_AT FROM KINTAI WHERE USER_ID = ? AND WORK_DATE = ?";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId, workDate);
            return mapToKintai(result);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Kintai> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT USER_ID, WORK_DATE, WORK_TYPE, START_TIME, END_TIME, BREAK_TIME, ACTUAL_WORK_TIME, CREATED_AT, UPDATED_AT FROM KINTAI WHERE USER_ID = ? AND WORK_DATE BETWEEN ? AND ? ORDER BY WORK_DATE";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId, startDate, endDate);
        return list.stream()
                .map(this::mapToKintai)
                .toList();
    }

    @Override
    public List<Kintai> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT USER_ID, WORK_DATE, WORK_TYPE, START_TIME, END_TIME, BREAK_TIME, ACTUAL_WORK_TIME, CREATED_AT, UPDATED_AT FROM KINTAI WHERE WORK_DATE BETWEEN ? AND ? ORDER BY USER_ID, WORK_DATE";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, startDate, endDate);
        return list.stream()
                .map(this::mapToKintai)
                .toList();
    }

    @Override
    public List<Kintai> findByUserIdAndMonth(String userId, int year, int month) {
        String sql = "SELECT USER_ID, WORK_DATE, WORK_TYPE, START_TIME, END_TIME, BREAK_TIME, ACTUAL_WORK_TIME, CREATED_AT, UPDATED_AT FROM KINTAI WHERE USER_ID = ? AND EXTRACT(YEAR FROM WORK_DATE) = ? AND EXTRACT(MONTH FROM WORK_DATE) = ? ORDER BY WORK_DATE";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId, year, month);
        return list.stream()
                .map(this::mapToKintai)
                .toList();
    }

    @Override
    public List<Kintai> findByMonth(int year, int month) {
        String sql = "SELECT USER_ID, WORK_DATE, WORK_TYPE, START_TIME, END_TIME, BREAK_TIME, ACTUAL_WORK_TIME, CREATED_AT, UPDATED_AT FROM KINTAI WHERE EXTRACT(YEAR FROM WORK_DATE) = ? AND EXTRACT(MONTH FROM WORK_DATE) = ? ORDER BY USER_ID, WORK_DATE";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, year, month);
        return list.stream()
                .map(this::mapToKintai)
                .toList();
    }

    private Kintai mapToKintai(Map<String, Object> map) {
        Kintai kintai = new Kintai();
        kintai.setUserId((String) map.get("USER_ID"));
        kintai.setWorkDate(((java.sql.Date) map.get("WORK_DATE")).toLocalDate());
        kintai.setWorkType((String) map.get("WORK_TYPE"));
        
        if (map.get("START_TIME") != null) {
            kintai.setStartTime(((java.sql.Time) map.get("START_TIME")).toLocalTime());
        }
        if (map.get("END_TIME") != null) {
            kintai.setEndTime(((java.sql.Time) map.get("END_TIME")).toLocalTime());
        }
        
        kintai.setBreakTime((Integer) map.get("BREAK_TIME"));
        kintai.setActualWorkTime((Integer) map.get("ACTUAL_WORK_TIME"));
        
        if (map.get("CREATED_AT") != null) {
            kintai.setCreatedAt(((java.sql.Timestamp) map.get("CREATED_AT")).toLocalDateTime());
        }
        if (map.get("UPDATED_AT") != null) {
            kintai.setUpdatedAt(((java.sql.Timestamp) map.get("UPDATED_AT")).toLocalDateTime());
        }
        
        return kintai;
    }
}
