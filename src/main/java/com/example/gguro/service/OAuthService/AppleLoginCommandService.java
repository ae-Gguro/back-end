package com.example.gguro.service.OAuthService;

import com.example.gguro.web.dto.UserResponseDTO;

import java.io.IOException;

public interface AppleLoginCommandService {

    UserResponseDTO.UserLoginResponseDTO appleLogin(String authorizationCode) throws IOException;
}