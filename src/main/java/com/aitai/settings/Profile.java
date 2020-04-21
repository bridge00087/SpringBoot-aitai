package com.aitai.settings;

import com.aitai.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile {

    /* 自己紹介 */
    @Length(max = 35)
    private String bio;

    /* 自分のウェブサイトURL */
    @Length(max = 50)
    private String url;

    /* 職業 */
    @Length(max = 50)
    private String occupation;

    /* 活動場所 */
    @Length(max = 50)
    private String location;

    /* プロフィールイメージ */
    private String profileImage;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();
    }

}
