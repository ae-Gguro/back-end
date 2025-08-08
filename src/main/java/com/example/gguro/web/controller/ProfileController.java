package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.service.ProfileService.ProfileCommandService;
import com.example.gguro.service.ProfileService.ProfileQueryService;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.gguro.jwt.FindLoginUser.getCurrentUser;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "프로필 API", description = "프로필 관련 API입니다.")
public class ProfileController {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    @PostMapping(value = "/api/profile/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "아이 프로필 생성", description = "아이 프로필을 생성하는 페이지입니다.\n"+"생년월일 작성 시 숫자 앞 0은 작성하지 말아주세요. 나쁜 예시 : 2003 04 04")
    public ApiResponse<Profile> createProfile(
            @RequestPart("request") @Valid ProfileRequestDTO.ProfileDTO request,
            @RequestPart(required = false) MultipartFile image
    ){
        User user = getCurrentUser();
        return ApiResponse.onSuccess(profileCommandService.createProfile(user, request, image));
    }

    @GetMapping("/api/profile/{profileId}")
    @Operation(summary = "아이 프로필 세부 조회", description = "아이 프로필의 세부 내용을 조회할 수 있는 페이지입니다.")
    public ApiResponse<ProfileResponseDTO.ProfileViewDTO> getProfile(
            @PathVariable Long profileId
    ){
        User user = getCurrentUser();
        return ApiResponse.onSuccess(profileQueryService.getProfile(user, profileId));
    }

    @GetMapping("/api/profiles")
    @Operation(summary = "아이 프로필 리스트 조회", description = "아이 프로필 리스트를 조회할 수 있는 페이지입니다.")
    public ApiResponse<ProfileResponseDTO.ProfileListViewDTO> getProfiles(){
        User user = getCurrentUser();
        return ApiResponse.onSuccess(profileQueryService.getProfileList(user));
    }

    @DeleteMapping("/api/profile/{profileId}")
    @Operation(summary = "아이 프로필 삭제", description = "아이 프로필을 삭제할 수 있는 페이지입니다.")
    public ApiResponse<?> deleteProfile(
            @PathVariable Long profileId
    ){
        User user = getCurrentUser();
        profileCommandService.deleteProfile(user, profileId);
        return ApiResponse.onSuccess(null);
    }
    @PatchMapping(value = "/api/profile/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "아이 프로필 수정", description = "아이 프로필를 수정할 수 있는 페이지입니다.")
    public ApiResponse<ProfileResponseDTO.ProfileViewDTO> updateProfile(
            @PathVariable Long profileId,
            @RequestPart("request") @Valid ProfileRequestDTO.ProfileDTO request,
            @RequestPart(required = false) MultipartFile image
    ) {
        User user = getCurrentUser();
        return ApiResponse.onSuccess(profileCommandService.updateProfile(user, profileId, request, image));
    }
}
