package com.example.gguro.domain;

import com.example.gguro.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 설정은 프로필 단위로
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    private boolean allNotificationsEnabled;
    private boolean usageNotificationEnabled;
    private boolean dailyNotificationEnabled;
    private boolean weeklyNotificationEnabled;

    public void updateAllNotificationsEnabled(boolean enabled) {this.allNotificationsEnabled = enabled;}
    public void updateUsageNotificationEnabled(boolean enabled) {this.usageNotificationEnabled = enabled;}
    public void updateDailyNotificationEnabled(boolean enabled) {this.dailyNotificationEnabled = enabled;}
    public void updateWeeklyNotificationEnabled(boolean enabled) {this.weeklyNotificationEnabled = enabled;}
}
