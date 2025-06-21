package com.example.gguro.service.UserService;

import com.example.gguro.domain.User;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;

public interface UserCommandService {
    // 로컬 회원가입
    User signUp(UserRequestDTO.UserSignUpDTO request);

    // 로컬 로그인
    UserResponseDTO.UserLoginResponseDTO login(UserRequestDTO.UserLogInDTO request);
}
