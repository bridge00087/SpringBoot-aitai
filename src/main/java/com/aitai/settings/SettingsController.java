package com.aitai.settings;

import com.aitai.account.AccountService;
import com.aitai.account.CurrentUser;
import com.aitai.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) { webDataBinder.addValidators(new PasswordFormValidator()); }

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    private final AccountService accountService;

    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
         model.addAttribute(account);
         model.addAttribute(new Profile(account));
         // プロフィール更新画面表示
         return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            // エラーの場合、自画面を再表示する。
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        // プロフィール更新処理
        accountService.updateProfile(account, profile);
        // リダイレクトメッセージを搭載
        attributes.addFlashAttribute("message","プロフィールを修正しました。");
        // 更新後、自画面へリダイレクト
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        // パスワード変更画面表示
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            // エラーの場合、自画面を再表示する。
            return SETTINGS_PASSWORD_VIEW_NAME;
        }
        // パスワード更新処理
        accountService.updatePassword(account, passwordForm.getNewPassword());
        // リダイレクトメッセージを搭載
        attributes.addFlashAttribute("message","パスワードを変更しました。");
        // 更新後、自画面へリダイレクト
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }
}
