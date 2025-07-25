package com.example.demo.controller;


import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Login;
import com.example.demo.entity.User;
import com.example.demo.form.LoginForm;
import com.example.demo.service.LoginService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm) {
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute LoginForm loginForm, Model model, HttpSession session) {
        // ログイン処理の実行
        Login login = new Login(loginForm.getUserId(), loginForm.getPass());
        User user = loginService.execute(login);

        // ログイン処理の成否によって処理を分岐
        if (user != null) { // ログイン成功時
            // セッションにユーザー情報を保存
            session.setAttribute("loginUser", user);
            return "redirect:/top";
        } else { // ログイン失敗時
            model.addAttribute("errorMessage", "ユーザーIDまたはパスワードが間違っています。");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
