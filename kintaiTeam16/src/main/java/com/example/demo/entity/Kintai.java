package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kintai {
    private String userId;
    private LocalDate workDate;
    private String workType; // 出勤、振出、振休、年休、休日、欠勤
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakTime; // 分単位
    private Integer actualWorkTime; // 分単位（自動計算）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 実労働時間を計算するメソッド
    public void calculateActualWorkTime() {
        if (startTime != null && endTime != null && breakTime != null) {
            // 終業時間 - 始業時間 - 休憩時間（分単位で計算）
            long workMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
            this.actualWorkTime = (int)(workMinutes - breakTime);
        } else if ("年休".equals(workType)) {
            // 年休の場合は自動的に7時間（420分）
            this.actualWorkTime = 420;
        } else {
            this.actualWorkTime = 0;
        }
    }
    
    // 勤務時間入力が必要かどうかのチェック
    public boolean needsWorkTime() {
        return "出勤".equals(workType) || "振出".equals(workType);
    }
}