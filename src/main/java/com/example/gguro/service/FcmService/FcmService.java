package com.example.gguro.service.FcmService;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.MessagingErrorCode;

import java.util.Map;

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
            System.out.println("푸시 알림 성공: "+response);
        } catch (FirebaseMessagingException e) {
            System.err.println("푸시 알림 실패: "+e.getMessage());
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
            System.out.println("토큰 유효성 검사 성공: "+token);
            return true;
        } catch (FirebaseMessagingException e) {
            System.err.println("토큰 유효성 검사 실패: "+token);
            System.out.println("이유: "+e.getMessage());

            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                System.err.println("토큰 형식이 유효하지 않습니다.");
            }
            return false;
        }
    }
}