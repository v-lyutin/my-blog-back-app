package com.amit.application.tag.service;

import com.amit.tag.model.Tag;
import com.amit.tag.repository.TagRepository;
import com.amit.tag.service.DefaultTagService;
import com.amit.tag.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.amit.common.util.ModelBuilder.buildTag;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultTagServiceTest.DefaultTagServiceTestConfiguration.class)
class DefaultTagServiceTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagService tagService;

    @BeforeEach
    void resetMocks() {
        reset(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should delegates to repository and returns set")
    void getTagsByPostId_ok() {
        long postId = 10L;
        Set<Tag> tags = Set.of(buildTag(1, "a"), buildTag(2, "b"));
        when(this.tagRepository.findTagsByPostId(postId)).thenReturn(tags);

        Set<Tag> result = this.tagService.getTagsByPostId(postId);

        assertEquals(tags, result);
        verify(this.tagRepository).findTagsByPostId(postId);
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should returns empty and skips repository when input is empty")
    void ensureTagsExist_emptyInput() {
        Set<Tag> result = this.tagService.ensureTagsExist(Collections.emptyList());
        assertTrue(result.isEmpty());
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should normalize tag names and delegate to repository when input is non-empty")
    void ensureTagsExist_normalizesAndDelegates() {
        List<String> inputTags = Arrays.asList("  travel ", "river", "  ", "nature", "travel");
        Set<Tag> ensuredTags = Set.of(
                buildTag(1, "travel"),
                buildTag(2, "river"),
                buildTag(3, "nature")
        );

        when(this.tagRepository.ensureTagsExist(eq(Set.of("travel", "river", "nature")))).thenReturn(ensuredTags);

        Set<Tag> out = this.tagService.ensureTagsExist(inputTags);

        assertEquals(ensuredTags, out);
        verify(this.tagRepository).ensureTagsExist(eq(Set.of("travel", "river", "nature")));
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should not throws NPE when input is null (propagates from normalizer)")
    void ensureTagsExist_nullInput() {
        assertDoesNotThrow(() -> this.tagService.ensureTagsExist(null));
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should does nothing when input is empty (no repo calls)")
    void replacePostTags_emptyInput() {
        this.tagService.replacePostTags(77L, Collections.emptyList());
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should normalize, ensure tags, extract IDs and replace post tags")
    void replacePostTags_ok() {
        long postId = 77L;
        List<String> inputTags = Arrays.asList(" A ", "b", "A");
        Set<Tag> ensuredTags = Set.of(buildTag(10, "A"), buildTag(20, "b"));

        when(this.tagRepository.ensureTagsExist(Set.of("A", "b"))).thenReturn(ensuredTags);

        this.tagService.replacePostTags(postId, inputTags);

        verify(this.tagRepository).ensureTagsExist(Set.of("A", "b"));
        verify(this.tagRepository).replacePostTags(
                eq(postId),
                argThat(ids -> new HashSet<>(ids).equals(Set.of(10L, 20L)))
        );
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should not throws NPE when input is null")
    void replacePostTags_nullInput() {
        assertDoesNotThrow(() -> this.tagService.replacePostTags(1L, null));
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should does nothing when input is empty (no repository calls)")
    void attachTagsToPost_emptyInput() {
        this.tagService.attachTagsToPost(5L, Collections.emptyList());
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should normalizes, ensures tags, extracts IDs")
    void attachTagsToPost_ok() {
        long postId = 5L;
        List<String> inputTags = Arrays.asList("x", "  y ", "x");
        Set<Tag> ensuredTags = Set.of(buildTag(1, "x"), buildTag(2, "y"));

        when(this.tagRepository.ensureTagsExist(Set.of("x", "y"))).thenReturn(ensuredTags);

        this.tagService.attachTagsToPost(postId, inputTags);

        verify(this.tagRepository).ensureTagsExist(Set.of("x", "y"));
        verify(this.tagRepository).attachTagsToPost(
                eq(postId),
                argThat(ids -> new HashSet<>(ids).equals(Set.of(1L, 2L)))
        );
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should not throws NPE when input is null")
    void attachTagsToPost_nullInput() {
        assertDoesNotThrow(() -> this.tagService.attachTagsToPost(1L, null));
        verifyNoInteractions(this.tagRepository);
    }

    @Configuration
    static class DefaultTagServiceTestConfiguration {

        @Bean
        TagRepository tagRepository() {
            return mock(TagRepository.class);
        }

        @Bean
        TagService tagService() {
            return new DefaultTagService(tagRepository());
        }

    }

}