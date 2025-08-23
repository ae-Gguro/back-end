package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.converter.DeviceConverter;
import com.example.gguro.domain.User;
import com.example.gguro.service.DeviceService.DeviceCommandService;
import com.example.gguro.service.DeviceService.DeviceQueryService;
import com.example.gguro.web.dto.device.DeviceRequestDTO;
import com.example.gguro.web.dto.device.DeviceResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.gguro.jwt.FindLoginUser.getCurrentUser;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "디바이스 토큰 API", description = "User의 디바이스 토큰 관련 API입니다.")
public class DeviceController {

    private final DeviceCommandService deviceCommandService;
    private final DeviceQueryService deviceQueryService;

    @PostMapping("/api/device-token")
    public ApiResponse<DeviceResponseDTO.DeviceResultDTO> registerDeviceToken(
            @RequestBody DeviceRequestDTO.registerDeviceDTO request
    ){
        User user = getCurrentUser();
        return ApiResponse.onSuccess(DeviceConverter.deviceResultDTO(
                deviceCommandService.registerDeviceToken(user, request)
        ));
    }

    @GetMapping("/api/device-tokens")
    public ApiResponse<List<DeviceResponseDTO.DeviceResultDTO>> getDevices() {
        User user = getCurrentUser();
        return ApiResponse.onSuccess(deviceQueryService.getDeviceTokens(user));
    }
}
