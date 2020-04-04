package com.aitai.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    /* Eメール確認済みフラグ */
    private boolean emailVerified;

    private String emailCheckToken;

    /* 会員登録日時 */
    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

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
    }
}
