package com.example.demo.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class KintaiForm {
    @NotNull(message = "日付は必須です")
    private LocalDate workDate;
    
    @NotNull(message = "勤務形態は必須です")
    private String workType;
    
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakTime; // 分単位
    
    // 勤務時間入力が必要かどうかのチェック
    public boolean needsWorkTime() {
        return "出勤".equals(workType) || "振出".equals(workType);
    }
}
