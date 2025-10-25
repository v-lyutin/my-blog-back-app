package com.amit.myblog.application.tag.service.util;

import com.amit.myblog.tag.service.util.TagNameNormalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TagNameNormalizerTest {

    @Test
    @DisplayName(value = "Should return empty set when input collection is null")
    void normalizeTagNames_shouldReturnEmptySetWhenInputCollectionIsNull() {
        Set<String> result = TagNameNormalizer.normalizeTagNames(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName(value = "Should return empty set when all elements are null or blank")
    void normalizeTagNames_shouldReturnEmptySetWhenAllElementsAreNullOrBlank() {
        Collection<String> tagNames = Arrays.asList(null, " ", "\t", "", "   ");

        Set<String> result = TagNameNormalizer.normalizeTagNames(tagNames);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName(value = "Should trim whitespace from tag names")
    void normalizeTagNames_shouldTrimWhitespaceFromTagNames() {
        Collection<String> tagNames = List.of("  travel  ", "\t nature", "river\t");

        Set<String> result = TagNameNormalizer.normalizeTagNames(tagNames);

        assertThat(result).containsExactlyInAnyOrder("travel", "nature", "river");
    }

    @Test
    @DisplayName(value = "Should remove duplicates and keep unique tag names only")
    void normalizeTagNames_shouldRemoveDuplicatesAndKeepUniqueTagNamesOnly() {
        Collection<String> tagNames = List.of("travel", "travel", "nature", "travel");

        Set<String> result = TagNameNormalizer.normalizeTagNames(tagNames);

        assertThat(result).containsExactlyInAnyOrder("travel", "nature");
    }

    @Test
    @DisplayName(value = "Should preserve case sensitivity when normalizing tag names")
    void normalizeTagNames_shouldPreserveCaseSensitivityWhenNormalizingTagNames() {
        Collection<String> tagNames = List.of("Travel", "travel", "TRAVEL");

        Set<String> result = TagNameNormalizer.normalizeTagNames(tagNames);

        assertThat(result).containsExactlyInAnyOrder("Travel", "travel", "TRAVEL");
    }

    @Test
    @DisplayName(value = "Should handle mix of valid, null, and blank values correctly")
    void normalizeTagNames_shouldHandleMixOfValidNullAndBlankValuesCorrectly() {
        Collection<String> tagNames = Arrays.asList("travel", " ", null, "nature", "\t", "river");

        Set<String> result = TagNameNormalizer.normalizeTagNames(tagNames);

        assertThat(result).containsExactlyInAnyOrder("travel", "nature", "river");
    }

}