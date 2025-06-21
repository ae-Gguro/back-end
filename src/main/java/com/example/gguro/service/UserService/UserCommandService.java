package com.example.gguro.service.UserService;

import com.example.gguro.domain.User;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;

public abstract class UserCommandService {
    // 로컬 회원가입
    public abstract User signUp(UserRequestDTO.UserSignUpDTO request);

    // 로컬 로그인
    public abstract UserResponseDTO.UserLoginResponseDTO login(UserRequestDTO.UserLogInDTO request);
}
