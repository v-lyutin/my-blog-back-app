package com.amit.myblog.application.post.service.util;

import com.amit.myblog.post.service.exception.InvalidImageException;
import com.amit.myblog.post.service.util.ImageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidatorTest {

    @Test
    @DisplayName(value = "Should throw when data is null")
    void validateSize_shouldThrowInvalidImageExceptionWhenDataIsNull() {
        assertThrows(InvalidImageException.class, () -> ImageValidator.validateSize(null));
    }

    @Test
    @DisplayName(value = "Should throw when data is empty")
    void validateSize_shouldThrowInvalidImageExceptionWhenDataIsEmpty() {
        assertThrows(InvalidImageException.class, () -> ImageValidator.validateSize(new byte[0]));
    }

    @Test
    @DisplayName(value = "Should throw when data exceeds 5MB")
    void validateSize_shouldThrowInvalidImageExceptionWhenDataSizeExceeds5MbLimit() {
        byte[] largeSize = new byte[(int) (5L * 1024 * 1024) + 1];
        assertThrows(InvalidImageException.class, () -> ImageValidator.validateSize(largeSize));
    }

    @Test
    @DisplayName(value = "Should pass when data fits under 5MB")
    void validateSize_shouldNotThrowWhenDataSizeIsWithin5MbLimit() {
        byte[] validSize = new byte[1024];
        assertDoesNotThrow(() -> ImageValidator.validateSize(validSize));
    }

}
