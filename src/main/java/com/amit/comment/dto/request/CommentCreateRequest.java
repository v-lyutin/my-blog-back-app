package com.amit.comment.dto.request;

public record CommentCreateRequest(
        String text,
        long postId) {
}
