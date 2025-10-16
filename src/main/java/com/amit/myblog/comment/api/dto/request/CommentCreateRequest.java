package com.amit.myblog.comment.api.dto.request;

public record CommentCreateRequest(
        String text,
        long postId) {
}
