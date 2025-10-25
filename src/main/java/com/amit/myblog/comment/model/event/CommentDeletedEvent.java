package com.amit.myblog.comment.model.event;

public record CommentDeletedEvent(
        long postId,
        long commentId) {
}
