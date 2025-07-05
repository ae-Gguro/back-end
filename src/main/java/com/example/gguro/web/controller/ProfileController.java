package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.service.ProfileService.ProfileCommandService;
import com.example.gguro.web.dto.ProfileRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.gguro.jwt.FindLoginUser.getCurrentUserId;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "프로필 API", description = "프로필 관련 API입니다.")
public class ProfileController {
    private final ProfileCommandService profileCommandService;

    @PostMapping("/api/profile/create")
    @Operation(summary = "아이 프로필 생성", description = "아이 프로필을 생성하는 페이지입니다.\n"+"생년월일 작성 시 숫자 앞 0은 작성하지 말아주세요. 나쁜 예시 : 2003 04 04")
    public ApiResponse<Profile> createProfile(
            @RequestBody @Valid ProfileRequestDTO.ProfileDTO request
    ){
        User user = getCurrentUserId();
        return ApiResponse.onSuccess(profileCommandService.createProfile(user, request));
    }
}
