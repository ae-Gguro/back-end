package com.example.gguro.service.NotificationService;

import com.example.gguro.domain.Device;
import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.NotificationType;
import com.example.gguro.jwt.TokenProvider;
import com.example.gguro.repository.DeviceRepository;
import com.example.gguro.repository.NotificationSettingRepository;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.service.FcmService.FcmService;
import com.example.gguro.web.dto.ChatroomCheckResponseDTO;
import com.example.gguro.web.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final TokenProvider tokenProvider;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CHECK_TODAY_URL = "http://localhost:8000/api/chatrooms/check-today/";

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

                // 2. 오늘 채팅방 생성 여부 확인
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user.getId(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));

                TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
                String accessToken = tokenDTO.getAccessToken();

                String url = CHECK_TODAY_URL + profile.getId();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                try {
                    ResponseEntity<ChatroomCheckResponseDTO> response =
                            restTemplate.exchange(url, HttpMethod.GET, entity, ChatroomCheckResponseDTO.class);

                    if (response.getBody() != null && !response.getBody().isCreatedToday()) {
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
                    log.info("USAGE_REMINDER 알림 발송됨: userId={}, profileId={}, token={}, body={}",
                            user.getId(), profile.getId(), token, body);
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
}
