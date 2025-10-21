package com.amit.myblog.comment.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank(message = "Comment text must not be blank.")
        @Size(max = 4096, message = "Comment text must be at most 4096 characters long.")
        String text,

        @NotNull(message = "Post ID must not be null.")
        long postId) {
}
