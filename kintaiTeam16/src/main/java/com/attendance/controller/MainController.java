package com.attendance.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.attendance.entity.Employee;

@Controller
public class MainController {
    
    @GetMapping("/main")
    public String showMainPage(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("employee", employee);
        return "main";
    }
}
