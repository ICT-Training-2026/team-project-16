package com.attendance.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.attendance.entity.Employee;
import com.attendance.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final AuthenticationService authenticationService;
    
    @GetMapping("/")
    public String showTopPage() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }
    
    @PostMapping("/login")
//    public String processLogin(@RequestParam String userId, 
    public String processLogin(@RequestParam String employeeId,     		
                              @RequestParam String password,
                              HttpServletRequest request,
                              HttpSession session,
                              Model model) {
        
//        Optional<Employee> employee = authenticationService.authenticate(userId, password);
        Optional<Employee> employee = authenticationService.authenticate(employeeId, password);

        
        if (employee.isPresent()) {
            String sessionId = authenticationService.createSession(employee.get(), request.getRemoteAddr());
            session.setAttribute("sessionId", sessionId);
            session.setAttribute("employee", employee.get());
            return "redirect:/main";
        } else {
            model.addAttribute("error", "ユーザーIDまたはパスワードが正しくありません");
            return "login";
        }
    }
    
    @PostMapping("/logout")
    public String processLogout(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId != null) {
            authenticationService.logout(sessionId);
        }
        session.invalidate();
        return "redirect:/login";
    }
}
