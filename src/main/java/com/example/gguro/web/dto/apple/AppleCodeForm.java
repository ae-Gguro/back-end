package com.example.gguro.web.dto.apple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AppleCodeForm {
    @Schema(description = "Apple에서 받은 인증 코드", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "코드 내용")
    private String code;
}