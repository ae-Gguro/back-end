package com.example.gguro.service.DeviceService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.UserHandler;
import com.example.gguro.converter.DeviceConverter;
import com.example.gguro.domain.Device;
import com.example.gguro.domain.User;
import com.example.gguro.repository.DeviceRepository;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.device.DeviceRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Override
    // 사용자의 디바이스 토큰을 등록하거나 재활성화하는 기능(로그아웃했다가 로그인하는 과정을 고려)
    public Device registerDeviceToken(User user, DeviceRequestDTO.registerDeviceDTO request) {
        return deviceRepository.findByUserAndToken(user, request.getToken())
                .map(device -> {
                    // 해당 유저에게 동일한 토큰이 존재한다면 활성화시킴
                    device.updateActive(true);
                    return device;
                })
                .orElseGet(() -> {
                    // 만약 토큰이 존재하지 않다면 저장
                    Device device = DeviceConverter.toDevice(user, request.getToken(), request.getDeviceType());
                    return deviceRepository.save(device);
                });
    }

    @Override
    // 디바이스 토큰 비활성화 (로그아웃 경우)
    public void deactivateDeviceToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        deviceRepository.findByUserAndToken(user, token)
                .ifPresent(device -> device.updateActive(false));
    }

}