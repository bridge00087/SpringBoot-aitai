package com.aitai.settings;

import com.aitai.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {

    /* 自己紹介 */
    private String bio;

    /* 自分のウェブサイトURL */
    private String url;

    /* 職業 */
    private String occupation;

    /* 活動場所 */
    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }

}
