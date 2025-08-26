package com.example.gguro.web.dto.apple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AppleCodeForm {
    @Schema(description = "Apple에서 받은 인증 코드", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "cd6f93025f866449b99200f06e1f3add3.0.pxsy.yx-QnoXE42wi-3FVb2Lacw")
    private String code;
}