package com.example.gguro.web.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ProfileResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileViewDTO {
        private Long profileId;
        private String profileName;
        private LocalDate profileBirthDate;
        private String profileImageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileListViewDTO {
        List<ProfileViewDTO> profiles;
    }
}
