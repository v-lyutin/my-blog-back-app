package com.amit.myblog.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PostCreateRequest(
        @NotBlank(message = "Title must not be blank.")
        @Size(max = 64, message = "Title must be at most 64 characters long.")
        String title,

        @NotBlank(message = "Text must not be blank.")
        @Size(max = 4096, message = "Text must be at most 4096 characters long.")
        String text,

        @Size(max = 16, message = "A post can have at most 16 tags.")
        Set<@Size(min = 1, max = 64, message = "Each tag must be between 1 and 64 characters long.") String> tags) {
}
