package com.amit.myblog.post.service.exception;

public final class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }

}
