package com.example.gguro.service.OAuthService;

import com.example.gguro.web.dto.UserResponseDTO;

public interface KakaoLoginCommandService {
    UserResponseDTO.UserLoginResponseDTO login(String accessToken);
}
