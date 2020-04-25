package com.aitai.settings;

import com.aitai.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean meetingCreatedByEmail;

    private boolean meetingCreatedByWeb;

    private boolean meetingEnrollmentResultByEmail;

    private boolean meetingEnrollmentResultByWeb;

    private boolean meetingUpdatedByEmail;

    private boolean meetingUpdatedByWeb;

    public Notifications(Account account) {
        this.meetingCreatedByEmail = account.isMeetingCreatedByEmail();
        this.meetingCreatedByWeb = account.isMeetingCreatedByWeb();
        this.meetingEnrollmentResultByEmail = account.isMeetingEnrollmentResultByEmail();
        this.meetingEnrollmentResultByWeb = account.isMeetingEnrollmentResultByWeb();
        this.meetingUpdatedByEmail = account.isMeetingUpdatedByEmail();
        this.meetingUpdatedByWeb = account.isMeetingUpdatedByWeb();
    }
}
