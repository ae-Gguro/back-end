package com.example.gguro.web.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class DeviceResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeviceResultDTO {
        private Long id;
        private String token;
        private String deviceType;
        private boolean isActive;
        private LocalDateTime createdAt;
    }
}