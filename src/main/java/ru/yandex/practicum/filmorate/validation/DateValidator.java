package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    private static final LocalDate MIN_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        if (releaseDate == null) return false;
        return releaseDate.isAfter(MIN_RELEASE);
    }
}
