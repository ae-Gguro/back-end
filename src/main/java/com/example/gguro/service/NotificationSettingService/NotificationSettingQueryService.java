package com.example.gguro.service.NotificationSettingService;

import com.example.gguro.domain.User;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingResponseDTO;

public interface NotificationSettingQueryService {

    NotificationSettingResponseDTO getNotificationSetting(User user, Long profileId);
}
