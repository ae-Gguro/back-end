package com.example.gguro.service.DeviceService;

import com.example.gguro.domain.User;
import com.example.gguro.web.dto.device.DeviceResponseDTO;

import java.util.List;

public interface DeviceQueryService {

    List<DeviceResponseDTO.DeviceResultDTO> getDeviceTokens(User user);
}
