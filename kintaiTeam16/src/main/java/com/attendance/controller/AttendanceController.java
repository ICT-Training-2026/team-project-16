package com.attendance.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.attendance.dto.SearchCriteria;
import com.attendance.dto.ValidationResult;
import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import com.attendance.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @GetMapping("/register")
    public String showAttendanceRegistration(Model model) {
        model.addAttribute("attendance", new Attendance());
        return "attendance/register";
    }
    
    @PostMapping("/register")
    public String registerAttendance(@ModelAttribute Attendance attendance,
                                   HttpSession session,
                                   Model model) {
        
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) {
            return "redirect:/login";
        }
        
        attendance.setEmployeeId(employee.getEmployeeId());
        attendance.setCreatedBy(employee.getEmployeeId());
        
        ValidationResult validationResult = attendanceService.validateAttendanceData(attendance);
        
        if (!validationResult.isValid()) {
            model.addAttribute("errors", validationResult.getErrorMessages());
            model.addAttribute("attendance", attendance);
            return "attendance/register";
        }
        
        attendanceService.registerAttendance(attendance);
        model.addAttribute("message", "勤怠情報を登録しました");
        return "attendance/complete";
    }
    
    @GetMapping("/search")
    public String showAttendanceSearch(Model model) {
        model.addAttribute("criteria", new SearchCriteria());
        return "attendance/search";
    }
    
    @PostMapping("/search")
    public String searchAttendance(@ModelAttribute SearchCriteria criteria, Model model) {
        List<Attendance> results = attendanceService.searchAttendance(criteria);
        model.addAttribute("results", results);
        model.addAttribute("criteria", criteria);
        return "attendance/search";
    }
    
    @GetMapping("/edit/{id}")
    public String showAttendanceEdit(@PathVariable Long id, Model model) {
        Optional<Attendance> attendance = attendanceService.findById(id);
        if (attendance.isPresent()) {
            model.addAttribute("attendance", attendance.get());
            return "attendance/edit";
        }
        return "redirect:/attendance/search";
    }
    
    @PostMapping("/update")
    public String updateAttendance(@ModelAttribute Attendance attendance,
                                 HttpSession session,
                                 Model model) {
        
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) {
            return "redirect:/login";
        }
        
        attendance.setUpdatedBy(employee.getEmployeeId());
        
        ValidationResult validationResult = attendanceService.validateAttendanceData(attendance);
        
        if (!validationResult.isValid()) {
            model.addAttribute("errors", validationResult.getErrorMessages());
            model.addAttribute("attendance", attendance);
            return "attendance/edit";
        }
        
        attendanceService.updateAttendance(attendance);
        model.addAttribute("message", "勤怠情報を更新しました");
        return "attendance/complete";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteAttendance(@PathVariable Long id, Model model) {
        attendanceService.deleteAttendance(id);
        model.addAttribute("message", "勤怠情報を削除しました");
        return "attendance/complete";
    }
}
