package com.example.demo.repository;

import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Nenkyuu;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NenkyuuRepositoryImpl implements NenkyuuRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Nenkyuu findByUserId(String userId) {
        String sql = "SELECT USER_ID, REMAINING_DAYS FROM NENKYUU WHERE USER_ID = ?";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId);
            return mapToNenkyuu(result);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean create(Nenkyuu nenkyuu) {
        String sql = "INSERT INTO NENKYUU (USER_ID, REMAINING_DAYS) VALUES (?, ?)";
        
        int result = jdbcTemplate.update(sql,
            nenkyuu.getUserId(),
            nenkyuu.getRemainingDays());
        
        return result == 1;
    }

    @Override
    public boolean updateRemainingDays(String userId, int remainingDays) {
        String sql = "UPDATE NENKYUU SET REMAINING_DAYS = ? WHERE USER_ID = ?";
        
        int result = jdbcTemplate.update(sql, remainingDays, userId);
        return result == 1;
    }

    @Override
    public boolean decreaseRemainingDays(String userId) {
        String sql = "UPDATE NENKYUU SET REMAINING_DAYS = REMAINING_DAYS - 1 WHERE USER_ID = ?";
        
        int result = jdbcTemplate.update(sql, userId);
        return result == 1;
    }

    private Nenkyuu mapToNenkyuu(Map<String, Object> map) {
        Nenkyuu nenkyuu = new Nenkyuu();
        nenkyuu.setUserId((String) map.get("USER_ID"));
        nenkyuu.setRemainingDays((Integer) map.get("REMAINING_DAYS"));
        return nenkyuu;
    }
}
