package com.aitai.settings;

import com.aitai.WithAccount;
import com.aitai.account.AccountRepository;
import com.aitai.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        // テスト終了後はテストユーザーデータを削除する。
        accountRepository.deleteAll();
    }

    @WithAccount("yamada")
    @DisplayName("プロフィール修正フォーム")
    @Test
    void updateProfileForm() throws Exception {

        String bio = "テスト自己紹介";

        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("yamada")
    @DisplayName("プロフィール修正：入力値正常")
    @Test
    void updateProfile() throws Exception {

        String bio = "テスト自己紹介";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account yamada = accountRepository.findByNickname("yamada");
        assertEquals(bio, yamada.getBio());
    }

    @WithAccount("yamada")
    @DisplayName("プロフィール修正：入力値エラー")
    @Test
    void updateProfile_error() throws Exception {

        String bio = "35桁以上。35桁以上。35桁以上。35桁以上。35桁以上。35桁以上。";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account yamada = accountRepository.findByNickname("yamada");
        assertNull(yamada.getBio());
    }
}