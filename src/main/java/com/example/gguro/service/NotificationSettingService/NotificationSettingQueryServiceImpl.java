package com.example.gguro.service.NotificationSettingService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.NotificationSettingHandler;
import com.example.gguro.apiPayload.exception.handler.ProfileHandler;
import com.example.gguro.converter.NotificationSettingConverter;
import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.repository.NotificationSettingRepository;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationSettingQueryServiceImpl implements NotificationSettingQueryService {

    private final ProfileRepository profileRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    public NotificationSettingResponseDTO getNotificationSetting(User user, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        if (!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        NotificationSetting setting = notificationSettingRepository.findByProfile(profile)
                .orElseThrow(() -> new NotificationSettingHandler(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND));

        return NotificationSettingConverter.notificationSetting(setting);
    }
}
