package com.example.gguro.service.NotificationSettingService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.NotificationSettingHandler;
import com.example.gguro.apiPayload.exception.handler.ProfileHandler;
import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.NotificationType;
import com.example.gguro.repository.NotificationSettingRepository;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationSettingCommandServiceImpl implements NotificationSettingCommandService {

    private final ProfileRepository profileRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    public void createDefaultSettings(Profile profile) {
        notificationSettingRepository.findByProfile(profile).orElseGet(() -> {
                try {
                    return notificationSettingRepository.save(
                            NotificationSetting.builder()
                                    .profile(profile)
                                    .allNotificationsEnabled(true)
                                    .usageNotificationEnabled(true)
                                    .dailyNotificationEnabled(true)
                                    .weeklyNotificationEnabled(true)
                                    .build()
                    );
                } catch (DataIntegrityViolationException e) {
                    // 다른 트랜잭션에서 동시에 생성된 경우 → 다시 조회
                    return notificationSettingRepository.findByProfile(profile)
                            .orElseThrow(() -> new NotificationSettingHandler(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND));
                }
            }
        );
    }

    @Override
    public void updateNotificationSetting(User user, Long profileId, NotificationSettingRequestDTO request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        if (!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        NotificationSetting setting = notificationSettingRepository.findByProfile(profile)
                .orElseThrow(() -> new NotificationSettingHandler(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND));

        // 전체 알림 꺼져 있는데 개별 알림 수정 요청 → 에러
        if (!setting.isAllNotificationsEnabled() && request.type() != NotificationType.ALL) {
            throw new NotificationSettingHandler(ErrorStatus.ALL_NOTIFICATION_DISABLED);
        }

        // 타입별 업데이트
        switch (request.type()) {
            case USAGE_REMINDER -> setting.updateUsageNotificationEnabled(request.enabled());
            case DAILY_REPORT   -> setting.updateDailyNotificationEnabled(request.enabled());
            case WEEKLY_REPORT  -> setting.updateWeeklyNotificationEnabled(request.enabled());
            case ALL -> {
                setting.updateAllNotificationsEnabled(request.enabled());
                setting.updateUsageNotificationEnabled(request.enabled());
                setting.updateDailyNotificationEnabled(request.enabled());
                setting.updateWeeklyNotificationEnabled(request.enabled());
            }
            default -> throw new NotificationSettingHandler(ErrorStatus.INVALID_NOTIFICATION_TYPE);
        }
    }
}
