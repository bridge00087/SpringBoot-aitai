package com.aitai.account;

import lombok.Data;

@Data
public class SignUpForm {

    /* ニックネーム */
    private String nickname;

    /* Eメール */
    private String email;

    /* パスワード */
    private String password;
}
