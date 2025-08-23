package com.example.gguro.web.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class DeviceRequestDTO {

    @Getter
    public static class registerDeviceDTO {
        private String token;
        @Schema(name = "deviceType", example = "iOS")
        private String deviceType;
    }
}
