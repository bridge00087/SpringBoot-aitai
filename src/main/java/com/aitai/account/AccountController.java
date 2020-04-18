package com.aitai.account;

import com.aitai.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        // エラーの場合、登録画面を再表示
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        // サービスの呼び出し
        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        // 会員登録処理
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        // 該当するEメール無し
        if (account == null) {
            model.addAttribute("error", "wrong.email");

            return view;
        }

        // Tokenが一致するかチェック
        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");

            return view;
        }

        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());

        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            // 認証Eメール送信時間チェック
            model.addAttribute("error", "認証Eメールは1時間ごとに1回のみ転送できます。");
            model.addAttribute("email", account.getEmail());

            return "account/check-email";
        }

        // 認証Eメール再送信
        accountService.sendSignUpConfirmEmail(account);

        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
        Account byNickName = accountRepository.findByNickname(nickname);
        if (nickname == null) {
            // ニックネームで検索する。
            throw new IllegalArgumentException(nickname + "に該当する利用者が存在しません。");
        }

        model.addAttribute(byNickName);
        model.addAttribute("isOwner", byNickName.equals(account));

        return "account/profile";
    }
}
