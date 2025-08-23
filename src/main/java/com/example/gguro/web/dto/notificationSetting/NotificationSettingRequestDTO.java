package com.example.gguro.web.dto.notificationSetting;

import com.example.gguro.domain.enums.NotificationType;

public record NotificationSettingRequestDTO(
    NotificationType type,  // 어떤 알림을 바꿀 건지
    boolean enabled         // ON/OFF 여부
){}
