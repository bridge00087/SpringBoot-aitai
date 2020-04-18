package com.aitai.domain;

import lombok.*;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    /* ID */
    @Id @GeneratedValue
    private Long id;

    /* Eメール */
    @Column(unique = true)
    private String email;

    /* ニックネーム */
    @Column(unique = true)
    private String nickname;

    /* パスワード */
    private String password;

    /* Eメール確認済みフラグ */
    private boolean emailVerified;

    /* EメールチェックToken */
    private String emailCheckToken;

    /* EメールチェックToken生成時間 */
    private LocalDateTime emailCheckTokenGeneratedAt;

    /* 会員登録日時 */
    private LocalDateTime joinedAt;

    /* 紹介文 */
    private String bio;

    /* 自分のウェブサイトURL */
    private String url;

    /* 職業 */
    private String occupation;

    /* 活動場所 */
    private String location;

    /* プロフィールイメージ */
    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean meetingCreatedByEmail;

    private boolean meetingCreatedByWeb;

    private boolean meetingEnrollmentResultByEmail;

    private boolean meetingEnrollmentResultByWeb;

    private boolean meetingUpdatedByEmail;

    private boolean meetingUpdatedByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
    return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
