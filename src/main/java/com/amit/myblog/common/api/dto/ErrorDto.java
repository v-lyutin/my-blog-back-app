package com.amit.myblog.common.api.dto;

import java.time.LocalDateTime;

public record ErrorDto(
        String message,
        String timestamp,
        String path) {

    public ErrorDto(String message, String path) {
        this(message, LocalDateTime.now().toString(), path);
    }

}
