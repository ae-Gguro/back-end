package com.example.gguro.service.DeviceService;

import com.example.gguro.converter.DeviceConverter;
import com.example.gguro.domain.User;
import com.example.gguro.repository.DeviceRepository;
import com.example.gguro.web.dto.device.DeviceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final DeviceRepository deviceRepository;

    @Override
    public List<DeviceResponseDTO.DeviceResultDTO> getDeviceTokens(User member) {
        return deviceRepository.findAllByUser(member).stream()
                .map(DeviceConverter::deviceResultDTO)
                .toList();
    }
}
