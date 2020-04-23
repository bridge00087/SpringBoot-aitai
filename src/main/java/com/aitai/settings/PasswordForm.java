package com.aitai.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    /* 新規パスワード */
    @Length(min = 8, max = 50)
    private String newPassword;

    /* 新規パスワード（確認） */
    @Length(min = 8, max = 50)
    private String newPasswordConfirm;
}
