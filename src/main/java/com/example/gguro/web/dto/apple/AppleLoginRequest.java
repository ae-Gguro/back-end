package com.example.gguro.web.dto.apple;

import lombok.Getter;

public class AppleLoginRequest {
    private String code;
    private String idToken;
    private AppleUser user;

    @Getter
    public static class AppleUser {
        private Name name;
        private String email;

        @Getter
        public static class Name {
            private String firstName;
            private String lastName;
        }
    }
}
