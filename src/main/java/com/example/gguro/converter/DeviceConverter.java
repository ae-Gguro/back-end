package com.example.gguro.converter;

import com.example.gguro.domain.Device;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.device.DeviceResponseDTO;

public class DeviceConverter {

    public static Device toDevice(User user, String token, String deviceType) {
        return Device.builder()
                .user(user)
                .token(token)
                .deviceType(deviceType != null ? deviceType : "iOS")
                .isActive(true)
                .build();
    }

    public static DeviceResponseDTO.DeviceResultDTO deviceResultDTO(Device device) {
        return DeviceResponseDTO.DeviceResultDTO.builder()
                .id(device.getId())
                .token(device.getToken())
                .deviceType(device.getDeviceType())
                .isActive(device.isActive())
                .createdAt(device.getCreatedAt())
                .build();
    }
}
