package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.domain.User;
import com.example.gguro.service.UserService.UserCommandService;
import com.example.gguro.web.dto.UserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원가입/로그인 API", description = "User의 회원가입화면/로그인화면의 API입니다.")
public class SignController {

    private final UserCommandService userCommandService;

    // 기본 회원가입 API
    @PostMapping(value = "/api/auth/signup")
    @Operation(summary = "기본 회원가입 API", description = "아이디/비밀번호를 기준으로 유저를 생성합니다.")
    public ApiResponse<User> signUp (
            @RequestBody @Valid UserRequestDTO.UserSignUpDTO request
    ){
        return ApiResponse.onSuccess(userCommandService.signUp(request));
    }
}