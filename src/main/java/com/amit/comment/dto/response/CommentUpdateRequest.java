package com.amit.comment.dto.response;

public record CommentUpdateRequest(
        String text,
        long postId) {
}
