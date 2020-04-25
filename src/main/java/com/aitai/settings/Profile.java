package com.aitai.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
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

}
