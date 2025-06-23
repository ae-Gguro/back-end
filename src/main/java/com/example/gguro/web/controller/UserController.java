package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.service.UserService.UserQueryService;
import com.example.gguro.web.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "유저 API", description = "유저 관련 API입니다.")
public class UserController {

    private final UserQueryService userQueryService;

    // 기본 회원가입 API
    @PostMapping("/api/user/info")
    @Operation(summary = "유저 정보 조회 API - 인증 필요", description = "유저 정보를 조회하는 API입니다.")
    public ApiResponse<UserResponseDTO.UserInfoDTO> getUserInfo (
    ){
        return ApiResponse.onSuccess(userQueryService.getUserInfo());
    }
}
