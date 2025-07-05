package com.example.gguro.web.dto.profile;

import com.example.gguro.validation.annotation.ValidDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ProfileRequestDTO {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ValidDate
    public static class ProfileDTO{
        @NotNull(message = "이름(성) 작성은 필수입니다.")
        private String firstName;

        @NotNull(message = "이름 작성은 필수입니다.")
        private String lastName;

        @NotNull(message = "생년월일(년도) 작성은 필수입니다.")
        private Integer year;

        @NotNull(message = "생년월일(월) 작성은 필수입니다.")
        @Min(value = 1, message = "월은 1-12 사이여야 합니다.")
        @Max(value = 12, message = "월은 1-12 사이여야 합니다.")
        private Integer month;

        @NotNull(message = "생년월일(일) 작성은 필수입니다.")
        @Min(value = 1, message = "일은 1-31 사이여야 합니다.")
        @Max(value = 31, message = "일은 1-31 사이여야 합니다.")
        private Integer day;
    }
}
