package com.example.gguro.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn;
    private String refreshToken;
    private String role;
}