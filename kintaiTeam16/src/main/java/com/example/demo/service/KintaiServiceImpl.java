package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Kintai;
import com.example.demo.entity.Nenkyuu;
import com.example.demo.entity.Shinshutsu;
import com.example.demo.entity.User;
import com.example.demo.form.KintaiForm;
import com.example.demo.repository.KintaiRepository;
import com.example.demo.repository.NenkyuuRepository;
import com.example.demo.repository.ShinshutsuRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class KintaiServiceImpl implements KintaiService {
    
    private final KintaiRepository kintaiRepository;
    private final ShinshutsuRepository shinshutsuRepository;
    private final NenkyuuRepository nenkyuuRepository;
    private final UserRepository userRepository;

    @Override
    public boolean register(String userId, KintaiForm form) {
        // フォームからエンティティに変換
        Kintai kintai = new Kintai();
        kintai.setUserId(userId);
        kintai.setWorkDate(form.getWorkDate());
        kintai.setWorkType(form.getWorkType());
        kintai.setStartTime(form.getStartTime());
        kintai.setEndTime(form.getEndTime());
        kintai.setBreakTime(form.getBreakTime());
        
        // 実労働時間の計算
        kintai.calculateActualWorkTime();
        
        // 勤務形態別の処理
        switch (form.getWorkType()) {
            case "振出":
                // 振出データの登録
                Shinshutsu shinshutsu = new Shinshutsu(userId, form.getWorkDate(), false);
                shinshutsuRepository.create(shinshutsu);
                break;
            case "振休":
                // 振出データの使用フラグを更新
                List<Shinshutsu> unusedList = shinshutsuRepository.findUnusedByUserId(userId);
                if (!unusedList.isEmpty()) {
                    Shinshutsu oldest = unusedList.get(0);
                    shinshutsuRepository.updateUsedFlg(oldest.getUserId(), oldest.getShinshutsuDate(), true);
                }
                break;
            case "年休":
                // 有給残日数を減らす
                nenkyuuRepository.decreaseRemainingDays(userId);
                break;
        }
        
        return kintaiRepository.create(kintai);
    }

    @Override
    public boolean update(String userId, KintaiForm form) {
        // 既存データの取得
        Kintai existingKintai = kintaiRepository.findByUserIdAndDate(userId, form.getWorkDate());
        if (existingKintai == null) {
            return false;
        }
        
        // 勤務形態が変更された場合の処理
        if (!existingKintai.getWorkType().equals(form.getWorkType())) {
            // 旧勤務形態の取り消し処理
            handleWorkTypeRollback(userId, existingKintai);
            
            // 新勤務形態の処理
            handleWorkTypeChange(userId, form);
        }
        
        // 更新データの設定
        Kintai kintai = new Kintai();
        kintai.setUserId(userId);
        kintai.setWorkDate(form.getWorkDate());
        kintai.setWorkType(form.getWorkType());
        kintai.setStartTime(form.getStartTime());
        kintai.setEndTime(form.getEndTime());
        kintai.setBreakTime(form.getBreakTime());
        
        // 実労働時間の計算
        kintai.calculateActualWorkTime();
        
        return kintaiRepository.update(kintai);
    }

    @Override
    public boolean delete(String userId, LocalDate workDate) {
        // 削除前に勤務形態に応じた取り消し処理
        Kintai existingKintai = kintaiRepository.findByUserIdAndDate(userId, workDate);
        if (existingKintai != null) {
            handleWorkTypeRollback(userId, existingKintai);
        }
        
        return kintaiRepository.delete(userId, workDate);
    }

    @Override
    public List<Kintai> getKintaiList(String userId, LocalDate startDate, LocalDate endDate) {
        return kintaiRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public List<Kintai> getAllKintaiList(LocalDate startDate, LocalDate endDate) {
        return kintaiRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public Kintai getKintai(String userId, LocalDate workDate) {
        return kintaiRepository.findByUserIdAndDate(userId, workDate);
    }

    @Override
    public boolean canRegisterShinkyuu(String userId) {
        return shinshutsuRepository.countUnusedByUserId(userId) > 0;
    }

    @Override
    public boolean canRegisterNenkyuu(String userId) {
        Nenkyuu nenkyuu = nenkyuuRepository.findByUserId(userId);
        return nenkyuu != null && nenkyuu.getRemainingDays() > 0;
    }

    @Override
    public Map<String, Object> calculateMonthlyWorkInfo(String userId, int year, int month) {
        List<Kintai> kintaiList = kintaiRepository.findByUserIdAndMonth(userId, year, month);
        return calculateWorkSummary(kintaiList, year, month);
    }

    @Override
    public List<Map<String, Object>> calculateAllUsersMonthlyWorkInfo(int year, int month) {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : users) {
            List<Kintai> kintaiList = kintaiRepository.findByUserIdAndMonth(user.getUserId(), year, month);
            Map<String, Object> workInfo = calculateWorkSummary(kintaiList, year, month);
            workInfo.put("userId", user.getUserId());
            workInfo.put("userName", user.getName());
            workInfo.put("deptName", user.getDeptName());
            result.add(workInfo);
        }
        
        return result;
    }

    private void handleWorkTypeRollback(String userId, Kintai kintai) {
        switch (kintai.getWorkType()) {
            case "振出":
                // 振出データを削除
                shinshutsuRepository.updateUsedFlg(userId, kintai.getWorkDate(), true);
                break;
            case "振休":
                // 使用された振出を未使用に戻す
                // 実装の簡略化のため、最新の使用済み振出を未使用に戻す
                break;
            case "年休":
                // 有給残日数を戻す
                Nenkyuu nenkyuu = nenkyuuRepository.findByUserId(userId);
                if (nenkyuu != null) {
                    nenkyuuRepository.updateRemainingDays(userId, nenkyuu.getRemainingDays() + 1);
                }
                break;
        }
    }

    private void handleWorkTypeChange(String userId, KintaiForm form) {
        switch (form.getWorkType()) {
            case "振出":
                Shinshutsu shinshutsu = new Shinshutsu(userId, form.getWorkDate(), false);
                shinshutsuRepository.create(shinshutsu);
                break;
            case "振休":
                List<Shinshutsu> unusedList = shinshutsuRepository.findUnusedByUserId(userId);
                if (!unusedList.isEmpty()) {
                    Shinshutsu oldest = unusedList.get(0);
                    shinshutsuRepository.updateUsedFlg(oldest.getUserId(), oldest.getShinshutsuDate(), true);
                }
                break;
            case "年休":
                nenkyuuRepository.decreaseRemainingDays(userId);
                break;
        }
    }

    private Map<String, Object> calculateWorkSummary(List<Kintai> kintaiList, int year, int month) {
        Map<String, Object> result = new HashMap<>();
        
        // 月の総日数
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth();
        
        // 実労働時間の合計（分）
        int totalWorkMinutes = kintaiList.stream()
                .mapToInt(k -> k.getActualWorkTime() != null ? k.getActualWorkTime() : 0)
                .sum();
        
        // 出勤日数、振休日数をカウント
        long workDays = kintaiList.stream()
                .filter(k -> "出勤".equals(k.getWorkType()) || "振出".equals(k.getWorkType()) || "年休".equals(k.getWorkType()))
                .count();
        
        long shinkyuuDays = kintaiList.stream()
                .filter(k -> "振休".equals(k.getWorkType()))
                .count();
        
        // 月間所定労働日数（総日数 - 会社休日 - 振休日数）
        // 簡略化のため、土日を休日として計算
        int businessDays = calculateBusinessDays(year, month);
        int scheduledWorkDays = businessDays - (int)shinkyuuDays;
        
        // 月間所定労働時間（所定労働日数 × 7時間）
        int scheduledWorkMinutes = scheduledWorkDays * 7 * 60;
        
        // 残業時間
        int overtimeMinutes = Math.max(0, totalWorkMinutes - scheduledWorkMinutes);
        
        result.put("year", year);
        result.put("month", month);
        result.put("totalWorkMinutes", totalWorkMinutes);
        result.put("totalWorkHours", String.format("%.1f", totalWorkMinutes / 60.0));
        result.put("scheduledWorkMinutes", scheduledWorkMinutes);
        result.put("scheduledWorkHours", String.format("%.1f", scheduledWorkMinutes / 60.0));
        result.put("overtimeMinutes", overtimeMinutes);
        result.put("overtimeHours", String.format("%.1f", overtimeMinutes / 60.0));
        result.put("workDays", workDays);
        result.put("scheduledWorkDays", scheduledWorkDays);
        
        return result;
    }

    private int calculateBusinessDays(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int businessDays = 0;
        
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            // 土曜日(6)、日曜日(7)以外を営業日とする
            if (date.getDayOfWeek().getValue() < 6) {
                businessDays++;
            }
        }
        
        return businessDays;
    }
}
