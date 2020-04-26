package com.aitai.settings.validator;

import com.aitai.account.AccountRepository;
import com.aitai.domain.Account;
import com.aitai.settings.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        // エラーの場合、エラ〜メッセージを返却する。
        if (byNickname != null) {
            errors.rejectValue("nickname", "wrong.value", "入力したニックネームは使用できません。");
        }
    }
}
