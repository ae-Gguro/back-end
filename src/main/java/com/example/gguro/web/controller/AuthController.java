package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.domain.User;
import com.example.gguro.service.OAuthService.KakaoLoginCommandService;
import com.example.gguro.service.UserService.UserCommandService;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import com.example.gguro.web.dto.kakao.KakaoLoginRequestDTO;
import com.example.gguro.web.dto.kakao.KakaoLoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원가입/로그인 API", description = "User의 회원가입화면/로그인화면의 API입니다.")
public class AuthController {

    private final UserCommandService userCommandService;
    private final KakaoLoginCommandService kakaoLoginService;

    // 기본 회원가입 API
    @PostMapping("/api/auth/signup")
    @Operation(summary = "기본 회원가입 API", description = "아이디/비밀번호를 기준으로 유저를 생성합니다.")
    public ApiResponse<User> signUp (
            @RequestBody @Valid UserRequestDTO.UserSignUpDTO request
    ){
        return ApiResponse.onSuccess(userCommandService.signUp(request));
    }

    // 기본 로그인 API
    @PostMapping("/api/auth/login")
    @Operation(summary = "기본 로그인 API", description = "아이디/비밀번호를 기준으로 로그인합니다.")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> logIn (
            @RequestBody @Valid UserRequestDTO.UserLogInDTO request
    ){
        return ApiResponse.onSuccess(userCommandService.login(request));
    }

    @PostMapping("/api/auth/kakao")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> kakaoLogin(@RequestBody @Valid KakaoLoginRequestDTO request) {
        // KakaoLoginRequest 에는 프론트가 보내준 accessToken 이 담겨있음
        UserResponseDTO.UserLoginResponseDTO serviceToken = kakaoLoginService.login(request.getAccessToken());
        return ApiResponse.onSuccess(serviceToken);
    }

}