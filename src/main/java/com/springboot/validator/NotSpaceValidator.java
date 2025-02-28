package com.springboot.validator;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotSpaceValidator implements ConstraintValidator<NotSpace,String> {
    @Override
    public void initialize(NotSpace constraintAnnotion){
        ConstraintValidator.super.initialize(constraintAnnotion);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
        return value == null || StringUtils.hasText(value);
    }
}
