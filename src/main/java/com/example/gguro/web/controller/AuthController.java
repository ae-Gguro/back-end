package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.domain.User;
import com.example.gguro.service.OAuthService.AppleLoginCommandService;
import com.example.gguro.service.OAuthService.KakaoLoginCommandService;
import com.example.gguro.service.OAuthService.NaverLoginCommandService;
import com.example.gguro.service.UserService.UserCommandService;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import com.example.gguro.web.dto.kakao.KakaoLoginRequestDTO;
import com.example.gguro.web.dto.naver.NaverLoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.gguro.jwt.FindLoginUser.getCurrentUser;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원가입/로그인 API", description = "User의 회원가입화면/로그인화면의 API입니다.")
public class AuthController {

    private final UserCommandService userCommandService;
    private final KakaoLoginCommandService kakaoLoginCommandService;
    private final NaverLoginCommandService naverLoginCommandService;
    private final AppleLoginCommandService appleLoginCommandService;

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

    // AccessToken 재발급
    @PostMapping("/api/auth/reissue")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> reissueToken(
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        return ApiResponse.onSuccess(userCommandService.reissueToken(refreshToken));
    }

    // 카카오 로그인
    @PostMapping("/api/auth/kakao")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> kakaoLogin(@RequestBody @Valid KakaoLoginRequestDTO request) {
        UserResponseDTO.UserLoginResponseDTO serviceToken = kakaoLoginCommandService.login(request.getAccessToken());
        return ApiResponse.onSuccess(serviceToken);
    }

    // 네이버 로그인
    @PostMapping("/api/auth/naver")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> naverLogin(@RequestBody @Valid NaverLoginRequestDTO request) {
        UserResponseDTO.UserLoginResponseDTO serviceToken = naverLoginCommandService.login(request.getAccessToken());
        return ApiResponse.onSuccess(serviceToken);
    }

    // 애플 로그인
    @PostMapping("/api/auth/apple")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> appleLogin(
            @RequestParam("code") String code,
            @RequestParam(value = "user", required = false) String userJson
    ) {
        return ApiResponse.onSuccess(appleLoginCommandService.appleLogin(code, userJson));
    }

    // 유저 로그아웃
    @PostMapping("/api/auth/logout")
    public ApiResponse<String> logout(
            HttpServletRequest request,
            String deviceToken
    ) {
        userCommandService.logout(request, deviceToken);
        return ApiResponse.onSuccess("로그아웃 되었습니다.");
    }

    // 유저 탈퇴
    @DeleteMapping("/api/auth/delete")
    public ApiResponse<String> deleteUser(){
        User user = getCurrentUser();
        userCommandService.deleteUser(user);
        return ApiResponse.onSuccess("계정 탈퇴를 성공하였습니다.");
    }

}