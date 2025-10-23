package com.amit.myblog.comment.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotNull(message = "must not be null")
        long id,

        @NotBlank(message = "must not be blank")
        @Size(max = 4096, message = "must be at most 4096 characters long")
        String text,

        @NotNull(message = "must not be null")
        long postId) {
}
