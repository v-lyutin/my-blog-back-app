package com.amit.comment.api.dto.request;

public record CommentUpdateRequest(
        String text,
        long postId) {
}
