package com.example.gguro.converter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.web.dto.TokenDTO;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import com.example.gguro.web.dto.apple.AppleUserInfoResponse;

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
                .oauthType(socialType)
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

    public static User toUserWithOauthId(String oauthId, String email, String nickname, SocialType socialType) {
        return User.builder()
                .oauthType(socialType)
                .nickname(nickname)
                .isSocialLogin(true)
                .oauthId(oauthId)
                .email(email)
                .build();
    }

    public static AppleUserInfoResponse toAppleUserInfo(DecodedJWT jwt) {
        String sub = jwt.getClaim("sub").asString();
        String email = jwt.getClaim("email").asString();
        String firstName = jwt.getClaim("given_name").asString();     // firstName
        String lastName = jwt.getClaim("family_name").asString();     // lastName
        String name = ((lastName != null ? lastName : "") + (firstName != null ? firstName : "")).trim();

        return AppleUserInfoResponse.builder()
                .sub(sub)
                .email(email != null ? email : null)
                .name(!name.isEmpty() ? name : null)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
