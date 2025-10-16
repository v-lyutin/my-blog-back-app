package com.amit.myblog.post.service.exception;

public final class InvalidPostException extends RuntimeException {

    public InvalidPostException(String message) {
        super(message);
    }

}
