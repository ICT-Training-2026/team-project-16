package com.example.demo.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shinshutsu {
    private String userId;
    private LocalDate shinshutsuDate;
    private boolean usedFlg; // 振休で使用済みかどうか
}
