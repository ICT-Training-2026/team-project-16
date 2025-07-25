package com.example.demo.form;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SearchForm {
    private String userId; // 管理者用（一般社員は自動設定）
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer year;
    private Integer month;
}
