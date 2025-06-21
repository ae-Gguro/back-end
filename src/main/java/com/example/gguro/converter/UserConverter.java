package com.example.gguro.converter;

import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.web.dto.UserRequestDTO;

public class UserConverter {

    public static User toUser(UserRequestDTO.UserSignUpDTO request, String encodPassword) {
        return User.builder()
                .username(request.getUsername())
                .password(encodPassword)
                .nickname(request.getNickname())
                .isSocialLogin(false)
                .oauthType(SocialType.NONE)
                .build();
    }
}
