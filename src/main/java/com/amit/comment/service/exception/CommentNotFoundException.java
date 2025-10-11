package com.amit.comment.service.exception;

public final class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(String message) {
        super(message);
    }

}
