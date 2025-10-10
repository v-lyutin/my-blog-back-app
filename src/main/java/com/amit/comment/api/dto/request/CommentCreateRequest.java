package com.amit.comment.api.dto.request;

public record CommentCreateRequest(
        String text,
        long postId) {
}
