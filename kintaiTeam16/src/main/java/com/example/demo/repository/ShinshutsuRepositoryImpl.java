package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Shinshutsu;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShinshutsuRepositoryImpl implements ShinshutsuRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean create(Shinshutsu shinshutsu) {
        String sql = "INSERT INTO SHINSHUTSU (USER_ID, SHINSHUTSU_DATE, USED_FLG) VALUES (?, ?, ?)";
        
        int result = jdbcTemplate.update(sql,
            shinshutsu.getUserId(),
            shinshutsu.getShinshutsuDate(),
            shinshutsu.isUsedFlg());
        
        return result == 1;
    }

    @Override
    public boolean updateUsedFlg(String userId, LocalDate shinshutsuDate, boolean usedFlg) {
        String sql = "UPDATE SHINSHUTSU SET USED_FLG = ? WHERE USER_ID = ? AND SHINSHUTSU_DATE = ?";
        
        int result = jdbcTemplate.update(sql, usedFlg, userId, shinshutsuDate);
        return result == 1;
    }

    @Override
    public List<Shinshutsu> findUnusedByUserId(String userId) {
        String sql = "SELECT USER_ID, SHINSHUTSU_DATE, USED_FLG FROM SHINSHUTSU WHERE USER_ID = ? AND USED_FLG = false ORDER BY SHINSHUTSU_DATE";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return list.stream()
                .map(this::mapToShinshutsu)
                .toList();
    }

    @Override
    public Shinshutsu findByUserIdAndDate(String userId, LocalDate shinshutsuDate) {
        String sql = "SELECT USER_ID, SHINSHUTSU_DATE, USED_FLG FROM SHINSHUTSU WHERE USER_ID = ? AND SHINSHUTSU_DATE = ?";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId, shinshutsuDate);
            return mapToShinshutsu(result);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int countUnusedByUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM SHINSHUTSU WHERE USER_ID = ? AND USED_FLG = false";
        
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    private Shinshutsu mapToShinshutsu(Map<String, Object> map) {
        Shinshutsu shinshutsu = new Shinshutsu();
        shinshutsu.setUserId((String) map.get("USER_ID"));
        shinshutsu.setShinshutsuDate(((java.sql.Date) map.get("SHINSHUTSU_DATE")).toLocalDate());
        shinshutsu.setUsedFlg((Boolean) map.get("USED_FLG"));
        return shinshutsu;
    }
}