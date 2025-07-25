package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Kintai;
import com.example.demo.entity.User;
import com.example.demo.form.SearchForm;
import com.example.demo.service.KintaiService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SearchController {
    
    private final KintaiService kintaiService;
    private final UserService userService;
    
    @GetMapping("/search")
    public String search(@ModelAttribute SearchForm searchForm, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者の場合は全ユーザーのリストを取得
        if (loginUser.isAdmin()) {
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
        }
        
        model.addAttribute("loginUser", loginUser);
        return "search";
    }
    
    @PostMapping("/search")
    public String searchPost(@ModelAttribute SearchForm searchForm, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        String targetUserId = loginUser.isAdmin() ? searchForm.getUserId() : loginUser.getUserId();
        
        List<Kintai> kintaiList;
        if (searchForm.getStartDate() != null && searchForm.getEndDate() != null) {
            kintaiList = kintaiService.getKintaiList(targetUserId, searchForm.getStartDate(), searchForm.getEndDate());
        } else {
            // デフォルトで今月のデータを取得
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            kintaiList = kintaiService.getKintaiList(targetUserId, startOfMonth, endOfMonth);
        }
        
        // 管理者の場合は全ユーザーのリストを取得
        if (loginUser.isAdmin()) {
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
        }
        
        model.addAttribute("kintaiList", kintaiList);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("targetUserId", targetUserId);
        
        return "search";
    }
}
