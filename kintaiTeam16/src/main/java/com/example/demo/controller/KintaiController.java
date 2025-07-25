package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Kintai;
import com.example.demo.entity.User;
import com.example.demo.form.KintaiForm;
import com.example.demo.service.KintaiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KintaiController {
    
    private final KintaiService kintaiService;
    
    @GetMapping("/kintai")
    public String kintai(@ModelAttribute KintaiForm kintaiForm, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 今日の日付をデフォルト設定
        if (kintaiForm.getWorkDate() == null) {
            kintaiForm.setWorkDate(LocalDate.now());
        }
        
        // 勤務形態の選択肢
        model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
        model.addAttribute("loginUser", loginUser);
        
        // 振休と年休の申請可能チェック
        model.addAttribute("canRegisterShinkyuu", kintaiService.canRegisterShinkyuu(loginUser.getUserId()));
        model.addAttribute("canRegisterNenkyuu", kintaiService.canRegisterNenkyuu(loginUser.getUserId()));
        
        return "kintai";
    }
    
    @PostMapping("/kintai")
    public String kintaiPost(@Valid @ModelAttribute KintaiForm kintaiForm, 
                            BindingResult bindingResult,
                            HttpSession session, 
                            Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("canRegisterShinkyuu", kintaiService.canRegisterShinkyuu(loginUser.getUserId()));
            model.addAttribute("canRegisterNenkyuu", kintaiService.canRegisterNenkyuu(loginUser.getUserId()));
            return "kintai";
        }
        
        // 勤務形態別のバリデーション
        String validationError = validateKintaiForm(kintaiForm, loginUser.getUserId());
        if (validationError != null) {
            model.addAttribute("errorMessage", validationError);
            model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("canRegisterShinkyuu", kintaiService.canRegisterShinkyuu(loginUser.getUserId()));
            model.addAttribute("canRegisterNenkyuu", kintaiService.canRegisterNenkyuu(loginUser.getUserId()));
            return "kintai";
        }
        
        // 勤怠登録処理
        boolean result = kintaiService.register(loginUser.getUserId(), kintaiForm);
        
        if (result) {
            // 確認画面へ遷移
            model.addAttribute("kintaiForm", kintaiForm);
            model.addAttribute("loginUser", loginUser);
            return "kintai-confirm";
        } else {
            model.addAttribute("errorMessage", "勤怠登録に失敗しました。");
            model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
            model.addAttribute("loginUser", loginUser);
            return "kintai";
        }
    }
    
    @GetMapping("/kintai/edit")
    public String kintaiEdit(@RequestParam("date") String dateStr, 
                            @ModelAttribute KintaiForm kintaiForm,
                            HttpSession session, 
                            Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        LocalDate workDate = LocalDate.parse(dateStr);
        Kintai kintai = kintaiService.getKintai(loginUser.getUserId(), workDate);
        
        if (kintai == null) {
            return "redirect:/kintai/list";
        }
        
        // エンティティからフォームに変換
        kintaiForm.setWorkDate(kintai.getWorkDate());
        kintaiForm.setWorkType(kintai.getWorkType());
        kintaiForm.setStartTime(kintai.getStartTime());
        kintaiForm.setEndTime(kintai.getEndTime());
        kintaiForm.setBreakTime(kintai.getBreakTime());
        
        model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("canRegisterShinkyuu", kintaiService.canRegisterShinkyuu(loginUser.getUserId()));
        model.addAttribute("canRegisterNenkyuu", kintaiService.canRegisterNenkyuu(loginUser.getUserId()));
        model.addAttribute("isEdit", true);
        
        return "kintai";
    }
    
    @PostMapping("/kintai/edit")
    public String kintaiEditPost(@Valid @ModelAttribute KintaiForm kintaiForm,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("isEdit", true);
            return "kintai";
        }
        
        // 勤怠更新処理
        boolean result = kintaiService.update(loginUser.getUserId(), kintaiForm);
        
        if (result) {
            return "redirect:/kintai/list";
        } else {
            model.addAttribute("errorMessage", "勤怠更新に失敗しました。");
            model.addAttribute("workTypes", List.of("出勤", "振出", "振休", "年休", "休日", "欠勤"));
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("isEdit", true);
            return "kintai";
        }
    }
    
    @PostMapping("/kintai/delete")
    public String kintaiDelete(@RequestParam("date") String dateStr,
                              HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        LocalDate workDate = LocalDate.parse(dateStr);
        kintaiService.delete(loginUser.getUserId(), workDate);
        
        return "redirect:/kintai/list";
    }
    
    @GetMapping("/kintai/list")
    public String kintaiList(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 先月の締めから今日までの期間を計算
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1).minusMonths(1);
        
        List<Kintai> kintaiList = kintaiService.getKintaiList(loginUser.getUserId(), startDate, today);
        
        model.addAttribute("kintaiList", kintaiList);
        model.addAttribute("loginUser", loginUser);
        
        return "kintai-list";
    }
    
    private String validateKintaiForm(KintaiForm form, String userId) {
        switch (form.getWorkType()) {
            case "出勤":
            case "振出":
                if (form.getStartTime() == null || form.getEndTime() == null || form.getBreakTime() == null) {
                    return "出勤・振出の場合は始業時間、終業時間、休憩時間の入力が必要です。";
                }
                if (form.getStartTime().isAfter(form.getEndTime())) {
                    return "終業時間は始業時間より後である必要があります。";
                }
                break;
            case "振休":
                if (!kintaiService.canRegisterShinkyuu(userId)) {
                    return "振出を取得していないため、振休を申請できません。";
                }
                break;
            case "年休":
                if (!kintaiService.canRegisterNenkyuu(userId)) {
                    return "有給休暇日数が残っていないため、年休を申請できません。";
                }
                break;
        }
        return null;
    }
}