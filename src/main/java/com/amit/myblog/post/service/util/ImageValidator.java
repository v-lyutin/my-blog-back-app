package com.amit.myblog.post.service.util;

import com.amit.myblog.post.service.exception.InvalidImageException;

public final class ImageValidator {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    public static void validateSize(byte[] data) {
        if (data == null || data.length == 0) {
            throw new InvalidImageException("Image must not be null or empty.");
        }
        if (data.length > MAX_IMAGE_BYTES) {
            throw new InvalidImageException("Image exceeds max size: %d > %d.".formatted(data.length, MAX_IMAGE_BYTES));
        }
    }

    private ImageValidator() {
        throw new UnsupportedOperationException();
    }

}
