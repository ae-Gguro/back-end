package com.example.gguro.service.DeviceService;

import com.example.gguro.domain.Device;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.device.DeviceRequestDTO;

public interface DeviceCommandService {

    Device registerDeviceToken(User user, DeviceRequestDTO.registerDeviceDTO request);

    void deactivateDeviceToken(Long userId, String token);

    void deleteDeviceToken(User user);

}
