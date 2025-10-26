package com.example.gguro.web.controller;

import com.example.gguro.apiPayload.ApiResponse;
import com.example.gguro.domain.User;
import com.example.gguro.service.NotificationSettingService.NotificationSettingCommandService;
import com.example.gguro.service.NotificationSettingService.NotificationSettingQueryService;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingRequestDTO;
import com.example.gguro.web.dto.notificationSetting.NotificationSettingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.gguro.jwt.FindLoginUser.getCurrentUser;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "알림 세팅 API", description = "알림 세팅 관련 API입니다.")
public class NotificationSettingController {

    private final NotificationSettingCommandService notificationSettingCommandService;
    private final NotificationSettingQueryService notificationSettingQueryService;

    @PatchMapping("/api/notification_setting/{profileId}")
    @Operation(summary = "해당 프로필의 알림 세팅 변경", description = "해당 프로필의 알림 세팅을 변경할 수 있는 페이지입니다.")
    public ApiResponse<String> updateNotificationSetting(
            @PathVariable("profileId") Long profileId,
            @RequestBody NotificationSettingRequestDTO request
    ) {
        User user = getCurrentUser();
        notificationSettingCommandService.updateNotificationSetting(user, profileId, request);
        return ApiResponse.onSuccess("성공적으로 알림 세팅이 변경되었습니다.");
    }

    @GetMapping("/api/notification_setting/{profileId}")
    @Operation(summary = "해당 프로필의 알림 세팅 조회", description = "해당 프로필의 알림 세팅을 조회할 수 있는 페이지입니다.")
    public ApiResponse<NotificationSettingResponseDTO> getNotificationSetting(
            @PathVariable("profileId") Long profileId
    ) {
        User user = getCurrentUser();
        return ApiResponse.onSuccess(
                notificationSettingQueryService.getNotificationSetting(user, profileId)
        );
    }

}
