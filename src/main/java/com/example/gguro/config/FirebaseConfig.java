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
    public void init() throws IOException {
        String firebaseKeyBase64 = System.getenv("FIREBASE_KEY_JSON");

        if (firebaseKeyBase64 == null || firebaseKeyBase64.isBlank()) {
            throw new IllegalArgumentException("FIREBASE_KEY_JSON 환경변수가 비어 있습니다!");
        }

        // base64 디코딩 추가
        byte[] decodedBytes = Base64.getDecoder().decode(firebaseKeyBase64);
        try (InputStream serviceAccount = new ByteArrayInputStream(decodedBytes)) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully!");
            }
        }
    }
}
