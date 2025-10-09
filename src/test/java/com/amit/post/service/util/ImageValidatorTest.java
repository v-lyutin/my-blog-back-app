package com.amit.post.service.util;

import com.amit.post.service.exception.InvalidImageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidatorTest {

    @Test
    @DisplayName(value = "Should throw when data is null")
    void validateSize_throwsInvalidImageExceptionWhenNull() {
        assertThrows(InvalidImageException.class, () -> ImageValidator.validateSize(null));
    }

    @Test
    @DisplayName(value = "Should throw when data is empty")
    void validateSize_throwsWhenEmpty() {
        assertThrows(InvalidImageException.class, () -> ImageValidator.validateSize(new byte[0]));
    }

    @Test
    @DisplayName(value = "Should throw when data exceeds 5MB")
    void validateSize_throwsInvalidImageExceptionWhenTooLarge() {
        byte[] largeSize = new byte[(int) (5L * 1024 * 1024) + 1];
        assertThrows(InvalidImageException.class, () -> ImageValidator.validateSize(largeSize));
    }

    @Test
    @DisplayName(value = "Should pass when data fits under 5MB")
    void validateSize_okWhenWithinLimit() {
        byte[] validSize = new byte[1024];
        assertDoesNotThrow(() -> ImageValidator.validateSize(validSize));
    }

}