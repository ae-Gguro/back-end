package com.example.gguro.service.NotificationService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.NotificationSettingHandler;
import com.example.gguro.domain.Device;
import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.NotificationType;
import com.example.gguro.repository.DeviceRepository;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.service.FcmService.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportNotificationService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final FcmService fcmService;

    // 매일 10:00 알림
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void sendDailyReportReminder() {
        List<User> users = userRepository.findAllWithProfilesAndSettings();

        for (User user : users) {
            for (Profile profile : user.getProfileList()) {
                NotificationSetting setting = profile.getNotificationSetting();
                if (setting == null) {
                    throw new NotificationSettingHandler(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND);
                }

                if (!setting.isDailyNotificationEnabled()) {
                    log.debug("DailyReportReminder 꺼져있음 → skip, profileId={}", profile.getId());
                    continue;
                }

                sendToDevices(user, profile,
                        "꾸로",
                        "🔔 띵동! 오늘의 리포트가 도착했어요.",
                        NotificationType.DAILY_REPORT);
            }
        }
    }

    // 매주 일요일 22시 알림
    @Scheduled(cron = "0 0 22 * * SUN", zone = "Asia/Seoul")
    public void sendWeeklyReportReminder() {
        List<User> users = userRepository.findAllWithProfilesAndSettings();

        for (User user : users) {
            for (Profile profile : user.getProfileList()) {
                NotificationSetting setting = profile.getNotificationSetting();
                if (setting == null) {
                    throw new NotificationSettingHandler(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND);
                }

                if (!setting.isWeeklyNotificationEnabled()) {
                    log.debug("WeeklyReportReminder 꺼져있음 → skip, profileId={}", profile.getId());
                    continue;
                }

                sendToDevices(user, profile,
                        "꾸로",
                        "🔔 띵동! 지난 주의 리포트가 도착했어요.",
                        NotificationType.WEEKLY_REPORT);
            }
        }
    }

    // 공통 발송 로직
    private void sendToDevices(User user, Profile profile, String title, String body, NotificationType type) {
        Map<String, String> data = Map.of(
                "notificationType", type.name(),
                "profileId", profile.getId().toString()
        );

        List<Device> devices = deviceRepository.findByUserAndIsActiveTrue(user);
        if (devices.isEmpty()) {
            log.debug("알림 보낼 디바이스 없음: userId={}, profileId={}", user.getId(), profile.getId());
            return;
        }

        for (Device device : devices) {
            String token = device.getToken();
            if (fcmService.isTokenValid(token)) {
                fcmService.sendNotificationToToken(token, title, body, data);
                log.info("{} 알림 발송됨: userId={}, profileId={}, token={}",
                        type.name(), user.getId(), profile.getId(), token);
            } else {
                log.warn("{} 알림 발송 안됨: 유효하지 않은 토큰. userId={}, profileId={}, token={}",
                        type.name(), user.getId(), profile.getId(), token);
            }
        }
    }
}
