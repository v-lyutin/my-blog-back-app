package com.amit.myblog.application.post.api.mapper;

import com.amit.myblog.post.api.util.TextTruncator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPostMapperTest {

    @Test
    @DisplayName(value = "Should return same string when input text is blank")
    void truncate_shouldReturnSameStringWhenInputTextIsBlank() {
        String result = TextTruncator.truncate("   ");

        assertThat(result).isEqualTo("   ");
    }

    @Test
    @DisplayName(value = "Should return original text when its length is less than or equal to 128 characters")
    void truncate_shouldReturnOriginalTextWhenLengthIsLessOrEqualToMax() {
        String text = "A".repeat(128);

        String result = TextTruncator.truncate(text);

        assertThat(result).isEqualTo(text);
    }

    @Test
    @DisplayName(value = "Should truncate text and append ellipsis when its length exceeds 128 characters")
    void truncate_shouldTruncateTextAndAppendEllipsisWhenLengthExceedsMax() {
        String longText = "X".repeat(200);

        String result = TextTruncator.truncate(longText);

        assertThat(result)
                .hasSize(131)
                .endsWith("...")
                .startsWith("X".repeat(10));
    }

    @Test
    @DisplayName(value = "Should handle text just one character longer than limit correctly")
    void truncate_shouldHandleTextJustOneCharacterLongerThanLimitCorrectly() {
        String text = "Y".repeat(129);

        String result = TextTruncator.truncate(text);

        assertThat(result)
                .hasSize(131)
                .endsWith("...");
    }

    @Test
    @DisplayName(value = "Should preserve whitespace and punctuation in truncated text")
    void truncate_shouldPreserveWhitespaceAndPunctuationInTruncatedText() {
        String text = "A ".repeat(70);
        String result = TextTruncator.truncate(text);

        assertThat(result).contains("A ");
        assertThat(result).endsWith("...");
    }

}