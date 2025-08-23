package com.example.gguro.converter;

import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingRequestDTO;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingResponseDTO;

public class NotificationSettingConverter {

    // 사용자가 알림 세팅 조회할 때
    public static NotificationSettingResponseDTO notificationSetting(NotificationSetting notificationSetting) {
        return NotificationSettingResponseDTO.builder()
                .allEnabled(notificationSetting.isAllNotificationsEnabled())
                .usageReminderEnabled(notificationSetting.isUsageNotificationEnabled())
                .dailyReportEnabled(notificationSetting.isDailyNotificationEnabled())
                .weeklyReportEnabled(notificationSetting.isWeeklyNotificationEnabled())
                .build();
    }

}
