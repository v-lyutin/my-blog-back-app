package com.amit.myblog.application.tag.service.util;

import com.amit.myblog.tag.model.Tag;
import com.amit.myblog.tag.service.util.TagNameExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TagNameExtractorTest {

    @Test
    @DisplayName(value = "Should return empty set when input set is null")
    void extractTagNames_shouldReturnEmptySetWhenInputSetIsNull() {
        Set<String> result = TagNameExtractor.extractTagNames(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName(value = "Should return empty set when input set is empty")
    void extractTagNames_shouldReturnEmptySetWhenInputSetIsEmpty() {
        Set<String> result = TagNameExtractor.extractTagNames(Set.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName(value = "Should extract single tag name from a set with one tag")
    void extractTagNames_shouldExtractSingleTagNameFromSetWithOneTag() {
        Set<Tag> tags = Set.of(new Tag(1L, "travel"));

        Set<String> result = TagNameExtractor.extractTagNames(tags);

        assertThat(result).containsExactly("travel");
    }

    @Test
    @DisplayName(value = "Should extract multiple unique tag names from a set with different tags")
    void extractTagNames_shouldExtractMultipleUniqueTagNamesFromSetWithDifferentTags() {
        Set<Tag> tags = Set.of(
                new Tag(1L, "travel"),
                new Tag(2L, "nature"),
                new Tag(3L, "river")
        );

        Set<String> result = TagNameExtractor.extractTagNames(tags);

        assertThat(result).containsExactlyInAnyOrder("travel", "nature", "river");
    }

    @Test
    @DisplayName(value = "Should collapse duplicate tag names into a unique set")
    void extractTagNames_shouldCollapseDuplicateTagNamesIntoUniqueSet() {
        Set<Tag> tags = Set.of(
                new Tag(1L, "travel"),
                new Tag(2L, "travel"),
                new Tag(3L, "TRAVEL")
        );

        Set<String> result = TagNameExtractor.extractTagNames(tags);

        assertThat(result).containsExactlyInAnyOrder("travel", "TRAVEL");
    }

    @Test
    @DisplayName(value = "Should exclude null when a tag has null name")
    void extractTagNames_shouldExcludeNullWhenTagHasNullNameCurrentBehavior() {
        Set<Tag> tags = Set.of(
                new Tag(1L, null),
                new Tag(2L, "ok")
        );

        Set<String> result = TagNameExtractor.extractTagNames(tags);

        assertThat(result).containsExactlyInAnyOrder("ok");
    }

}
