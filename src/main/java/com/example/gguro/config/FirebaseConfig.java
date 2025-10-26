package com.example.gguro.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            String firebaseKeyBase64 = System.getenv("FIREBASE_KEY_JSON");

            if (firebaseKeyBase64 == null || firebaseKeyBase64.isBlank()) {
                throw new IllegalArgumentException("FIREBASE_KEY_JSON 환경변수가 비어 있습니다!");
            }

            // 로그 확인용 (길면 잘라서 출력)
            System.out.println("FIREBASE_KEY_JSON (first 50 chars): "
                    + firebaseKeyBase64.substring(0, Math.min(50, firebaseKeyBase64.length())));

            // base64 → JSON 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(firebaseKeyBase64);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            // 실제 JSON 일부 출력 (디버깅용)
            System.out.println("Decoded Firebase JSON starts with: "
                    + decodedString.substring(0, Math.min(80, decodedString.length())));

            try (InputStream serviceAccount = new ByteArrayInputStream(decodedBytes)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized successfully!");
                } else {
                    System.out.println("Firebase already initialized.");
                }
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}