package com.amit.myblog.comment.model.event;

public record CommentCreatedEvent(
        long postId,
        long commentId) {
}
