package com.example.gguro.service.FcmService;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.MessagingErrorCode;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    public void sendNotificationToToken(String token, String title, String body, Map<String, String> data) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("푸시 알림 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("푸시 알림 실패: {}", e.getMessage(), e);
        }
    }

    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .build();

            FirebaseMessaging.getInstance().send(message);
            log.debug("토큰 유효성 검사 성공: {}", token);
            return true;
        } catch (FirebaseMessagingException e) {
            log.warn("토큰 유효성 검사 실패: token={}, 이유={}", token, e.getMessage());

            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                log.error("토큰 형식이 유효하지 않습니다: {}", token);
            }
            return false;
        }
    }
}