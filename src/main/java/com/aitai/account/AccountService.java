package com.aitai.account;

import com.aitai.config.AppProperties;
import com.aitai.domain.Account;
import com.aitai.domain.Tag;
import com.aitai.domain.Zone;
import com.aitai.mail.EmailMessage;
import com.aitai.mail.EmailService;
import com.aitai.settings.form.NicknameForm;
import com.aitai.settings.form.Notifications;
import com.aitai.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processNewAccount(SignUpForm signUpForm) {
        // アカウントの登録
        Account newAccount = saveNewAccount(signUpForm);
        // 確認Eメール送信
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        // EメールToken生成
        account.generateEmailCheckToken();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "Eメール認証");
        context.setVariable("message", "アイタイを利用するにはリンクをクリックしてください。");
        context.setVariable("host", appProperties.getHost());

       String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("アイタイ, 会員登録認証")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);

        // 該当するEメールが存在しない場合、ニックネームで再検索する。
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        // ニックネームでも該当するデータがない場合は例外処理をする。
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        // Eメール認証処理
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        // プロフィール更新処理
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        // パスワード変更処理
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        // お知らせ設定変更処理
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, NicknameForm nicknameForm) {
        // ニックネーム変更処理
        modelMapper.map(nicknameForm, account);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "アイタイログインする。");
        context.setVariable("message", "ログインするためにはリンクをクリックしてください。");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        // Eメール認証メールを発送処理
        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("アイタイ、ログインリンク")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage );
    }

    public void addTag(Account account, Tag tag) {
        // 関心テーマ追加処理
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        // 登録した関心テーマ取得
        Optional<Account> byId = accountRepository.findById(account.getId());
        // 情報が存在する場合、タグ情報をリターン（ない場合はエラー）
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        // 登録した関心テーマ削除
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        // 登録した活動地域の取得
        Optional<Account> byId = accountRepository.findById(account.getId());
        // 情報が存在する場合、地域情報をリターン（ない場合はエラー）
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        // 登録した活動地域の取得
        Optional<Account> byId = accountRepository.findById(account.getId());
        // 地域追加
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        // 登録した活動地域の取得
        Optional<Account> byId = accountRepository.findById(account.getId());
        // 地域削除
        byId.ifPresent(a -> a.getZones().remove(zone));
    }
}
