package com.example.gguro.converter;

import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.web.dto.TokenDTO;
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

    public static User toUserWithEmail(String email, SocialType socialType, String nickname) {
        return User.builder()
                .isSocialLogin(true)
                .email(email)
                .oauthType(SocialType.KAKAO)
                .nickname(nickname)
                .build();
    }

    public static UserResponseDTO.UserInfoDTO toUserInfoDTO(User user) {
        return UserResponseDTO.UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    public static UserResponseDTO.UserLoginResponseDTO toUserLoginResponseDTO(TokenDTO tokenDTO) {
        return UserResponseDTO.UserLoginResponseDTO.builder()
                .accessToken(tokenDTO.getAccessToken())
                .refreshToken(tokenDTO.getRefreshToken())
                .grantType(tokenDTO.getGrantType())
                .expiresIn(tokenDTO.getAccessTokenExpiresIn())
                .build();
    }

}
