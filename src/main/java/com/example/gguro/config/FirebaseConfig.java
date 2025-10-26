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

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String firebaseKey = System.getenv("FIREBASE_KEY_JSON");
        if (firebaseKey == null || firebaseKey.isBlank()) {
            throw new IllegalArgumentException("FIREBASE_KEY_JSON 환경변수가 비어 있습니다!");
        }

        try (InputStream serviceAccount =
                     new ByteArrayInputStream(firebaseKey.getBytes(StandardCharsets.UTF_8))) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
