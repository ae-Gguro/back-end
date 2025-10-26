package com.example.gguro.service.OAuthService;

import com.example.gguro.web.dto.UserResponseDTO;


public interface NaverLoginCommandService {
    UserResponseDTO.UserLoginResponseDTO login(String accessToken);
}
