package com.example.demo.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.form.SearchForm;
import com.example.demo.service.KintaiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReportController {
    
    private final KintaiService kintaiService;
    
    @GetMapping("/report")
    public String report(@ModelAttribute SearchForm searchForm, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }
        
        // デフォルトで今月を設定
        LocalDate now = LocalDate.now();
        if (searchForm.getYear() == null) {
            searchForm.setYear(now.getYear());
        }
        if (searchForm.getMonth() == null) {
            searchForm.setMonth(now.getMonthValue());
        }
        
        model.addAttribute("loginUser", loginUser);
        
        return "report";
    }
    
    @PostMapping("/report")
    public String reportPost(@ModelAttribute SearchForm searchForm, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }
        
        // 全社員の月間勤怠情報を取得
        List<Map<String, Object>> workInfoList = kintaiService.calculateAllUsersMonthlyWorkInfo(
            searchForm.getYear(), searchForm.getMonth());
        
        model.addAttribute("workInfoList", workInfoList);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("targetYear", searchForm.getYear());
        model.addAttribute("targetMonth", searchForm.getMonth());
        
        return "report";
    }
    
    @GetMapping("/export")
    public ResponseEntity<String> export(@RequestParam("year") int year,
                                        @RequestParam("month") int month,
                                        HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        // CSV形式でエクスポート
        List<Map<String, Object>> workInfoList = kintaiService.calculateAllUsersMonthlyWorkInfo(year, month);
        
        StringBuilder csv = new StringBuilder();
        csv.append("社員番号,氏名,所属部門,所定労働時間,実労働時間,残業時間,出勤日数\n");
        
        for (Map<String, Object> workInfo : workInfoList) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                workInfo.get("userId"),
                workInfo.get("userName"),
                workInfo.get("deptName"),
                workInfo.get("scheduledWorkHours"),
                workInfo.get("totalWorkHours"),
                workInfo.get("overtimeHours"),
                workInfo.get("workDays")
            ));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", 
            String.format("勤怠情報_%d年%d月.csv", year, month));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }
}
