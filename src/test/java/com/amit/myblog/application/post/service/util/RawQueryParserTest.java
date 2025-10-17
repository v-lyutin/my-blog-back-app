package com.amit.myblog.application.post.service.util;

import com.amit.myblog.post.service.util.RawQueryParser;
import com.amit.myblog.post.service.util.SearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RawQueryParserTest {

    @ParameterizedTest(name = "[{index}] rawQuery='{0}' → titleQuery=null, tags=empty")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t \n"})
    @DisplayName(value = "Should return titleQuery = null and empty tags when input is null/empty/blank")
    void parse_shouldReturnNullTitleAndEmptyTagsWhenInputIsNullOrBlank(String rawQuery) {
        SearchCriteria searchCriteria = RawQueryParser.parse(rawQuery);
        assertNull(searchCriteria.titleQuery());
        assertTrue(searchCriteria.tagNames().isEmpty());
    }

    @Test
    @DisplayName(value = "Should ignore lone '#' tokens")
    void parse_shouldIgnoreLoneHashTokens() {
        SearchCriteria searchCriteria = RawQueryParser.parse("#   #   #");
        assertNull(searchCriteria.titleQuery());
        assertTrue(searchCriteria.tagNames().isEmpty());
    }

    @Test
    @DisplayName(value = "Should join text tokens with single spaces and produce no tags")
    void parse_shouldJoinTextTokensWithSingleSpacesAndNoTags() {
        SearchCriteria searchCriteria = RawQueryParser.parse("river  Chusovaya  autumn");
        assertEquals("river Chusovaya autumn", searchCriteria.titleQuery());
        assertTrue(searchCriteria.tagNames().isEmpty());
    }

    @Test
    @DisplayName(value = "Should collect multiple tags into a set regardless of order")
    void parse_shouldCollectMultipleTagsIntoSetRegardlessOfOrder() {
        SearchCriteria searchCriteria = RawQueryParser.parse("#travel   #river   #nature");
        assertNull(searchCriteria.titleQuery());
        assertEquals(Set.of("travel", "river", "nature"), searchCriteria.tagNames());
    }

    @Test
    @DisplayName(value = "Should collapse duplicate tags into a single entry")
    void parse_shouldCollapseDuplicateTagsIntoSingleEntry() {
        SearchCriteria searchCriteria = RawQueryParser.parse("#tag #tag #tag");
        assertEquals(Set.of("tag"), searchCriteria.tagNames());
    }

    @Test
    @DisplayName(value = "Should split mixed input into title words and tags")
    void parse_shouldSplitMixedInputIntoTitleWordsAndTags() {
        SearchCriteria searchCriteria = RawQueryParser.parse("river #chusovaya Chusovaya #travel");
        assertEquals("river Chusovaya", searchCriteria.titleQuery());
        assertEquals(Set.of("chusovaya", "travel"), searchCriteria.tagNames());
    }

    @Test
    @DisplayName(value = "Should handle tags at beginning, middle, and end equally")
    void parse_shouldHandleTagsAtBeginningMiddleAndEndEqually() {
        SearchCriteria searchCriteria = RawQueryParser.parse("#a  text  #b more  #c");
        assertEquals("text more", searchCriteria.titleQuery());
        assertEquals(Set.of("a", "b", "c"), searchCriteria.tagNames());
    }

    @Test
    @DisplayName(value = "Should support single-letter tags")
    void parse_shouldSupportSingleLetterTags() {
        SearchCriteria searchCriteria = RawQueryParser.parse("x #y z");
        assertEquals("x z", searchCriteria.titleQuery());
        assertEquals(Set.of("y"), searchCriteria.tagNames());
    }

    @Test
    @DisplayName(value = "Should support Unicode text and preserve tag case")
    void parse_shouldSupportUnicodeTextAndPreserveTagCase() {
        SearchCriteria searchCriteria = RawQueryParser.parse("Море #Путешествия море");
        assertEquals("Море море", searchCriteria.titleQuery());
        assertEquals(Set.of("Путешествия"), searchCriteria.tagNames());
    }

}
