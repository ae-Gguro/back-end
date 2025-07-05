package com.example.gguro.validation.annotation;

import com.example.gguro.validation.validator.ValidDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "유효하지 않은 생년월일입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}