package com.amit.myblog.common.api.dto;

import com.amit.myblog.common.util.ValidationErrorUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record ErrorResponse(
        String message,

        String timestamp,

        String path,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<ValidationErrorUtils.ValidationError> errors) {

    public ErrorResponse(String message, String path) {
        this(message, LocalDateTime.now().toString(), path, Collections.emptyList());
    }

    public ErrorResponse(String message, String path, List<ValidationErrorUtils.ValidationError> errors) {
        this(message, LocalDateTime.now().toString(), path, errors);
    }

}
