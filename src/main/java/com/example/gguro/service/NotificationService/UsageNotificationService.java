package com.example.gguro.service.NotificationService;

import com.example.gguro.domain.Device;
import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.NotificationType;
import com.example.gguro.repository.*;
import com.example.gguro.service.FcmService.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsageNotificationService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final DeviceRepository deviceRepository;
    private final FcmService fcmService;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 13,18 * * *", zone = "Asia/Seoul")
    public void sendChatroomUsageReminder() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Profile> profiles = profileRepository.findByUser(user);

            for (Profile profile : profiles) {
                // 1. 알림 설정 체크 (프로필별 USAGE_REMINDER on 여부)
                NotificationSetting setting = notificationSettingRepository.findByProfile(profile)
                        .orElse(null);

                if (setting == null) {
                    log.error("알림 설정이 없습니다: profileId={}", profile.getId());
                    continue;
                }

                if (!setting.isUsageNotificationEnabled()) {
                    log.debug("USAGE_REMINDER 꺼져있음 → skip, profileId={}", profile.getId());
                    continue;
                }

                log.debug("USAGE_REMINDER 켜져있음, profileId={}", profile.getId());

                // 2. 오늘 채팅방 생성한 기록 여부 조회 후 없다면 알림 발송
                try {
                    boolean createdToday = checkChatroomCreatedToday(profile.getId());

                    if (!createdToday) {
                        sendUsageReminderNotification(user, profile);
                    }

                } catch (Exception e) {
                    log.error("채팅방 생성 여부 확인 실패: profileId={}, error={}", profile.getId(), e.getMessage());
                }
            }
        }
    }

    private void sendUsageReminderNotification(User user, Profile profile) {
        List<Device> devices = deviceRepository.findByUserAndIsActiveTrue(user);

        String title = "꾸로";

        // 이름 + 아/야 처리
        String vocative = getVocativeName(profile.getFirstName());

        // 랜덤 멘트 후보
        List<String> messages = List.of(
                vocative + "~ 너를 기다리고 있어🥺",
                vocative + "~ 나랑 같이 놀자😝",
                vocative + "~ 나랑 같이 공부할래?🤓"
        );

        // 랜덤 선택
        String body = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));

        Map<String, String> data = new HashMap<>();
        data.put("notificationType", NotificationType.USAGE_REMINDER.name());
        data.put("profileId", profile.getId().toString());

        if (!devices.isEmpty()) {
            for (Device device : devices) {
                String token = device.getToken();
                if (fcmService.isTokenValid(token)) {
                    fcmService.sendNotificationToToken(token, title, body, data);
                    log.info("USAGE_REMINDER 알림 발송됨: userId={}, profileId={}, token={}, body={}, data={}",
                            user.getId(), profile.getId(), token, body, data);
                } else {
                    log.warn("USAGE_REMINDER 알림 발송 안됨: 유효하지 않은 토큰. userId={}, profileId={}, token={}, body={}",
                            user.getId(), profile.getId(), token, body);
                }
            }
        }
    }

    private String getVocativeName(String name) {
        if (name == null || name.isEmpty()) return "";

        char lastChar = name.charAt(name.length() - 1);
        // 한글 범위 (가 ~ 힣)
        if (lastChar < 0xAC00 || lastChar > 0xD7A3) {
            return name; // 한글 아님 → 그대로 반환
        }

        int code = lastChar - 0xAC00;
        int jong = code % 28; // 종성(받침)

        if (jong == 0) {
            return name + "야"; // 받침 없음
        } else {
            return name + "아"; // 받침 있음
        }
    }

    private boolean checkChatroomCreatedToday(Long profileId) {
        String sql = "SELECT COUNT(*) FROM chatroom WHERE profile_id = ? AND DATE(created_at) = CURRENT_DATE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, profileId);
        return count != null && count > 0;
    }
}
