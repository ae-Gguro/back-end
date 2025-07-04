package com.example.gguro.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

public class ProfileRequestDTO {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDTO{
        @NotNull(message = "이름(성) 작성은 필수입니다.")
        private String firstName;

        @NotNull(message = "이름 작성은 필수입니다.")
        private String lastName;

        @NotNull(message = "생년월일(년도) 작성은 필수입니다.")
        private Integer year;

        @NotNull(message = "생년월일(월) 작성은 필수입니다.")
        private Integer month;

        @NotNull(message = "생년월일(일) 작성은 필수입니다.")
        private Integer day;
    }
}
