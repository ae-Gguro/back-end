package com.example.gguro.web.dto.apple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleUserInfoResponse {
    private String sub;    // 애플 고유 ID
    private String email;
    private String name;
}