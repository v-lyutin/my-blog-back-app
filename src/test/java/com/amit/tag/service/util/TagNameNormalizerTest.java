package com.amit.tag.service.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TagNameNormalizerTest {

    @Test
    @DisplayName("Trims whitespace and filters null/blank items")
    void normalize_trimsWhitespace_andFiltersNullsAndBlanks() {
        List<String> input = Arrays.asList("  travel ", null, " ", "\t", "river", "  nature");
        Set<String> result = TagNameNormalizer.normalize(input);

        assertEquals(Set.of("travel", "river", "nature"), result);
    }

    @Test
    @DisplayName("Deduplicates exact matches (case-sensitive)")
    void normalize_deduplicatesExactMatches_caseSensitive() {
        List<String> input = List.of("tag", "tag", " tag ", "Tag");
        Set<String> result = TagNameNormalizer.normalize(input);

        assertEquals(Set.of("tag", "Tag"), result);
    }

    @Test
    @DisplayName("Returns empty set for empty input")
    void normalize_returnsEmptySet_forEmptyInput() {
        Set<String> result = TagNameNormalizer.normalize(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Returns empty set when only nulls and blanks are provided")
    void normalize_returnsEmptySet_whenOnlyNullsAndBlanks() {
        List<String> input = Arrays.asList(null, " ", "\n", "\t", "   ");
        Set<String> result = TagNameNormalizer.normalize(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Does not alter the original collection")
    void normalize_doesNotAffectOriginalCollection() {
        List<String> input = Arrays.asList(" tag ", "x");

        Set<String> result = TagNameNormalizer.normalize(input);

        assertEquals(Set.of("tag", "x"), result);
    }

}