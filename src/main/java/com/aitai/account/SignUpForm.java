package com.aitai.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    /* ニックネーム */
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[亜-熙ぁ-んァ-ヶ-a-z0-9_-]{3,20}$")
    private String nickname;

    /* Eメール */
    @Email
    @NotBlank
    private String email;

    /* パスワード */
    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}
