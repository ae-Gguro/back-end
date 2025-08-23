package com.example.gguro.web.dto.notificationSetting;

import lombok.Builder;

@Builder
public record NotificationSettingResponseDTO (
    boolean allEnabled,        // 전체 알림 ON/OFF
    boolean usageReminderEnabled, // 사용해보세요 알림 ON/OFF
    boolean dailyReportEnabled,   // 하루 리포트 알림 ON/OFF
    boolean weeklyReportEnabled   // 일주일 리포트 알림 ON/OFF
){}
