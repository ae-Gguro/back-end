package com.example.gguro.web.dto.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

public class NaverLoginResponseDTO {

    @Getter
    @ToString
    public static class NaverUserInfo {
        private String resultcode;
        private String message;

        private Response response;

        @Getter
        @ToString
        public static class Response {
            private String id;
            private String email;
            private String nickname;

            @JsonProperty("profile_image")
            private String profileImage;

            private String name;
        }
    }
}
