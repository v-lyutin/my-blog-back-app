package com.amit.myblog.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PostUpdateRequest(
        @NotNull(message = "must not be null")
        long id,

        @NotBlank(message = "must not be blank")
        @Size(max = 64, message = "must be at most 64 characters long")
        String title,

        @NotBlank(message = "must not be blank")
        @Size(max = 4096, message = "must be at most 4096 characters long")
        String text,

        @Size(max = 16, message = "must contain at most 16 tags")
        Set<@Size(min = 1, max = 64, message = "must be between 1 and 64 characters long") String> tags) {
}
