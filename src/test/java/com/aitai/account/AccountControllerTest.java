package com.aitai.account;

import com.aitai.domain.Account;
import com.aitai.mail.EmailMessage;
import com.aitai.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @MockBean
    EmailService emailService;

    @DisplayName("会員登録画面の表示確認テスト")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("会員登録処理；入力値エラー")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "yamada")
                .param("email", "email..")
                .param("password", "12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("会員登録処理；入力値正常")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "yamada")
                .param("email", "yamada@yaamda.yamada")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("yamada"));

        // Eメールアドレスが存在するか確認
        Account account = accountRepository.findByEmail("yamada@yaamda.yamada");
        assertNotNull(account);
        // パスワード暗号化確認
        assertNotEquals(account.getPassword(), "12345678");
        // EメールTokenが存在するか確認
        assertNotNull(account.getEmailCheckToken());
        // メール送信確認
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("認証Eメール確認：入力値エラー")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "safdsfgdsf")
                .param("email", "email.email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("認証Eメール確認：入力値正常")
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname("yamada")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("yamada"));
    }
}
