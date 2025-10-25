package com.amit.myblog.common.util;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

public final class ValidationErrorUtils {

    public static List<ValidationError> extractValidationErrors(MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ValidationErrorUtils::mapObjectError)
                .collect(Collectors.toList());
    }

    private static ValidationError mapObjectError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return new ValidationError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
        return new ValidationError(
                error.getObjectName(),
                error.getDefaultMessage()
        );
    }

    private ValidationErrorUtils() {
        throw new UnsupportedOperationException();
    }

    public record ValidationError(String field, String message) {}

}
