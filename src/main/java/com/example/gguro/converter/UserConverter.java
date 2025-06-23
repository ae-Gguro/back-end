package com.example.gguro.converter;

import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;

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

    public static UserResponseDTO.UserInfoDTO toUserInfoDTO(User user) {
        return UserResponseDTO.UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
