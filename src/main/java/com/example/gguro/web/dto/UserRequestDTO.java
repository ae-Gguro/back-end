package com.example.gguro.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequestDTO {

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSignUpDTO {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @NotBlank
        private String nickname;

        @NotBlank
        private String checkPassword;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogInDTO {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }
}
