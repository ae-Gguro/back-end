package com.example.gguro.service.OAuthService;

import com.example.gguro.web.dto.UserResponseDTO;

public interface AppleLoginCommandService {

    UserResponseDTO.UserLoginResponseDTO appleLogin(String code);
}