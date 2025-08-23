package com.example.gguro.service.NotificationSettingService;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingRequestDTO;

public interface NotificationSettingCommandService {

    // 디폴트로 알림 세팅
    void createDefaultSettings(Profile profile);

    // 알림 온오프 세팅
    void updateNotificationSetting(User user, Long profileId, NotificationSettingRequestDTO notificationSettingRequestDTO);

}
