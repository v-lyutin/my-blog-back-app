package com.amit.comment.api.dto.request;

public record CommentUpdateRequest(
        long id,
        String text,
        long postId) {
}
