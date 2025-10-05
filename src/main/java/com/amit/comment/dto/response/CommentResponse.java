package com.amit.comment.dto.response;

public record CommentResponse(
        long id,
        String text,
        long postId) {
}
