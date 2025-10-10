package com.amit.comment.api.dto.response;

public record CommentResponse(
        long id,
        String text,
        long postId) {
}
