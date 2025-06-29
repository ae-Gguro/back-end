package com.example.gguro.web.dto.kakao;

import com.example.gguro.web.dto.TokenDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
public class KakaoLoginResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoUserInfo {
        private Long id; // 카카오에서 발급하는 사용자 고유 ID

        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;

        @Getter
        @ToString
        public static class KakaoAccount {
            private String email;
            private Profile profile;

            @Getter
            @ToString
            public static class Profile {
                private String nickname;
                @JsonProperty("profile_image_url")
                private String profileImageUrl;
            }
        }
    }
}
