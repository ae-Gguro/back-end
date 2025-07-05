package com.example.gguro.validation.validator;

import com.example.gguro.validation.annotation.ValidDate;
import com.example.gguro.web.dto.ProfileRequestDTO.ProfileDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.LocalDate;

public class ValidDateValidator implements ConstraintValidator<ValidDate, ProfileDTO> {

    @Override
    public boolean isValid(ProfileDTO dto, ConstraintValidatorContext context) {
        if (dto.getYear() == null || dto.getMonth() == null || dto.getDay() == null) {
            return true;
        }

        try {
            LocalDate birthDate = LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDay());
            LocalDate today = LocalDate.now();

            // 유효한 날짜인지 확인 + 현재보다 미래인지 체크
            if (birthDate.isAfter(today)) {
                return false;
            }

            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}