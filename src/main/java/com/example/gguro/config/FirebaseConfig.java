package com.example.gguro.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            String firebaseKeyBase64 = System.getenv("FIREBASE_KEY_JSON");

            if (firebaseKeyBase64 == null || firebaseKeyBase64.isBlank()) {
                System.out.println("FIREBASE_KEY_JSON 환경변수가 비어 있습니다.");
                return; // 앱 종료하지 않고 넘어감
            }

            // base64 → JSON 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(firebaseKeyBase64);
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
            System.err.println("Firebase initialization failed (non-fatal): " + e.getMessage());
            e.printStackTrace();
        }
    }
}