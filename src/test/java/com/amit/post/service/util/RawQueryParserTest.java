package com.amit.post.service.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RawQueryParserTest {

    @Nested
    @DisplayName(value = "Empty and trivial strings")
    class EmptyCases {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t \n"})
        @DisplayName("Null / empty / blank string → titleQuery = null, tags = empty")
        void parse_nullOrBlank(String rawQuery) {
            SearchCriteria searchCriteria = RawQueryParser.parse(rawQuery);
            assertNull(searchCriteria.titleQuery());
            assertTrue(searchCriteria.tagNames().isEmpty());
        }

        @Test
        @DisplayName("Single '#' tokens are ignored")
        void parse_lonelyHashIgnored() {
            SearchCriteria searchCriteria = RawQueryParser.parse("#   #   #");
            assertNull(searchCriteria.titleQuery());
            assertTrue(searchCriteria.tagNames().isEmpty());
        }

    }

    @Nested
    @DisplayName("Text only")
    class TextOnly {

        @Test
        @DisplayName("Words are joined with space")
        void parse_wordsJoined() {
            SearchCriteria searchCriteria = RawQueryParser.parse("river  Chusovaya  autumn");
            assertEquals("river Chusovaya autumn", searchCriteria.titleQuery());
            assertTrue(searchCriteria.tagNames().isEmpty());
        }

    }

    @Nested
    @DisplayName("Tags only")
    class TagsOnly {

        @Test
        @DisplayName("Multiple tags go into a set, order doesn't matter")
        void parse_manyTags() {
            SearchCriteria searchCriteria = RawQueryParser.parse("#travel   #river   #nature");
            assertNull(searchCriteria.titleQuery());
            assertEquals(Set.of("travel", "river", "nature"), searchCriteria.tagNames());
        }

        @Test
        @DisplayName("Duplicate tags are collapsed")
        void parse_duplicateTagsCollapsed() {
            SearchCriteria searchCriteria = RawQueryParser.parse("#tag #tag #tag");
            assertEquals(Set.of("tag"), searchCriteria.tagNames());
        }

    }

    @Nested
    @DisplayName("Mixed queries")
    class Mixed {

        @Test
        @DisplayName("Text + tags (combined)")
        void parse_textAndTags() {
            SearchCriteria searchCriteria = RawQueryParser.parse("river #chusovaya Chusovaya #travel");
            assertEquals("river Chusovaya", searchCriteria.titleQuery());
            assertEquals(Set.of("chusovaya", "travel"), searchCriteria.tagNames());
        }

        @Test
        @DisplayName("Tags at the beginning, middle, or end work the same")
        void parse_tagsAnywhere() {
            SearchCriteria searchCriteria = RawQueryParser.parse("#a  text  #b more  #c");
            assertEquals("text more", searchCriteria.titleQuery());
            assertEquals(Set.of("a", "b", "c"), searchCriteria.tagNames());
        }

        @Test
        @DisplayName("Single letter tag works")
        void parse_singleLetterTag() {
            SearchCriteria searchCriteria = RawQueryParser.parse("x #y z");
            assertEquals("x z", searchCriteria.titleQuery());
            assertEquals(Set.of("y"), searchCriteria.tagNames());
        }

    }

    @Nested
    @DisplayName("Unicode and case")
    class Unicode {

        @Test
        @DisplayName("Unicode is supported, tag case is preserved")
        void parse_unicodeOk() {
            SearchCriteria searchCriteria = RawQueryParser.parse("Море #Путешествия море");
            assertEquals("Море море", searchCriteria.titleQuery());
            assertEquals(Set.of("Путешествия"), searchCriteria.tagNames());
        }

    }

}