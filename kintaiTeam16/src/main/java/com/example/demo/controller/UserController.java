package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.form.UserForm;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/user")
    public String user(@ModelAttribute UserForm userForm, HttpSession session, Model model) {
        User loginUser = (User)session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("successMessage", "");
        model.addAttribute("errorMessage", "");
        // 新規登録用のフォームオブジェクトを明示的に作成
        model.addAttribute("newUserForm", new UserForm());

        return "user";
    }
    
    @PostMapping("/user/register")
    public String userRegister(@Valid @ModelAttribute("newUserForm") UserForm userForm,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("newUserForm", userForm);
            return "user";
        }
        
        // 既存ユーザーチェック
        User existingUser = userService.findByUserId(userForm.getUserId());
        if (existingUser != null) {
            model.addAttribute("errorMessage", "そのユーザーIDは既に存在します。");
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("newUserForm", userForm);
            return "user";
        }
        
        boolean result = userService.register(userForm);
        
        if (result) {
            model.addAttribute("successMessage", "ユーザーを登録しました。");
            model.addAttribute("newUserForm", new UserForm());
        } else {
            model.addAttribute("errorMessage", "ユーザー登録に失敗しました。");
            model.addAttribute("newUserForm", userForm);
        }
        
        model.addAttribute("loginUser", loginUser);
        
        return "user";
    }
    
    @GetMapping("/user/search")
    public String userSearch(@RequestParam("userId") String userId,
                            HttpSession session,
                            Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }
        
        User targetUser = userService.findByUserId(userId);
        
        if (targetUser == null) {
            model.addAttribute("errorMessage", "ユーザーが見つかりません。");
            model.addAttribute("userForm", new UserForm());
        } else {
            // エンティティからフォームに変換（編集用）
            UserForm userForm = new UserForm();
            userForm.setUserId(targetUser.getUserId());
            userForm.setPass(targetUser.getPass());
            userForm.setDeptCode(targetUser.getDeptCode());
            userForm.setDeptName(targetUser.getDeptName());
            userForm.setName(targetUser.getName());
            
            model.addAttribute("userForm", userForm);
            model.addAttribute("targetUser", targetUser);
        }
        
        // 新規登録用のフォームも別途追加
        model.addAttribute("newUserForm", new UserForm());
        model.addAttribute("loginUser", loginUser);
        
        return "user";
    }
    
    @PostMapping("/user/update")
    public String userUpdate(@Valid @ModelAttribute UserForm userForm,
                            BindingResult bindingResult,
                            HttpSession session,
                            Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }
        
        // userIdが重複している場合の修正処理
        String cleanUserId = userForm.getUserId();
        if (cleanUserId != null && cleanUserId.contains(",")) {
            String[] parts = cleanUserId.split(",");
            cleanUserId = parts[0].trim();
            userForm.setUserId(cleanUserId);
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("targetUser", userService.findByUserId(cleanUserId));
            model.addAttribute("newUserForm", new UserForm());
            return "user";
        }
        
        boolean result = userService.update(userForm);
        
        if (result) {
            model.addAttribute("successMessage", "ユーザー情報を更新しました。");
        } else {
            model.addAttribute("errorMessage", "ユーザー情報の更新に失敗しました。");
        }
        
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("targetUser", userService.findByUserId(cleanUserId));
        model.addAttribute("newUserForm", new UserForm());
        
        return "user";
    }
    
    @PostMapping("/user/delete")
    public String userDelete(@RequestParam("userId") String userId,
                            HttpSession session,
                            Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!loginUser.isAdmin()) {
            return "redirect:/top";
        }
        
        boolean result = userService.delete(userId);
        
        if (result) {
            model.addAttribute("successMessage", "ユーザーを削除しました。");
        } else {
            model.addAttribute("errorMessage", "ユーザーの削除に失敗しました。");
        }
        
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("newUserForm", new UserForm());
        
        return "user";
    }
}