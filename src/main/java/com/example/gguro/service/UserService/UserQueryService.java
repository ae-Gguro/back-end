package com.example.gguro.service.UserService;

import com.example.gguro.web.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserQueryService {
    UserResponseDTO.UserInfoDTO getUserInfo(Long userId);
}
