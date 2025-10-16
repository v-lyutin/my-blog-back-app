package com.amit.myblog.comment.api.dto.request;

public record CommentUpdateRequest(
        long id,
        String text,
        long postId) {
}
