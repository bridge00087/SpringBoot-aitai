package com.aitai.settings;

import com.aitai.account.AccountService;
import com.aitai.account.CurrentUser;
import com.aitai.domain.Account;
import com.aitai.settings.form.NicknameForm;
import com.aitai.settings.form.Notifications;
import com.aitai.settings.form.PasswordForm;
import com.aitai.settings.form.Profile;
import com.aitai.settings.validator.NicknameValidator;
import com.aitai.settings.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;

    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;

    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/" + SETTINGS_NOTIFICATIONS_VIEW_NAME;

    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) { webDataBinder.addValidators(new PasswordFormValidator()); }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) { webDataBinder.addValidators(nicknameValidator); }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
         model.addAttribute(account);
         model.addAttribute(modelMapper.map(account, Profile.class));
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

    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));

        // お知らせ変更画面表示
        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            // エラーの場合、自画面を再表示する。
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }
        // パスワード更新処理
        accountService.updateNotifications(account, notifications);
        // リダイレクトメッセージを搭載
        attributes.addFlashAttribute("message","お知らせ設定を変更しました。");
        // 更新後、自画面へリダイレクト
        return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));

        // お知らせ変更画面表示
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            // エラーの場合、自画面を再表示する。
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        // ニックネーム更新処理
        accountService.updateNickname(account, nicknameForm);
        // リダイレクトメッセージを搭載
        attributes.addFlashAttribute("message","ニックネームを変更しました。");
        // 更新後、自画面へリダイレクト
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }
}
