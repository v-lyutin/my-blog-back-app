package com.amit.post.service.crud.exception;

public final class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }

}
