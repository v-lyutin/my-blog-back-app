package com.amit.myblog.application.tag.service;

import com.amit.myblog.tag.model.Tag;
import com.amit.myblog.tag.repository.TagRepository;
import com.amit.myblog.tag.service.DefaultTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class DefaultTagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private DefaultTagService defaultTagService;

    @Test
    @DisplayName(value = "Should return tag names for given post id")
    void getTagsByPostId_shouldReturnTagNames() {
        long postId = 42L;
        Set<Tag> repoOut = Set.of(new Tag(1L, "tag1"), new Tag(2L, "tag2"));
        when(this.tagRepository.findTagsByPostId(postId)).thenReturn(repoOut);

        Set<String> out = this.defaultTagService.getTagsByPostId(postId);

        assertThat(out).containsExactlyInAnyOrder("tag1", "tag2");
        verify(this.tagRepository).findTagsByPostId(postId);
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should return empty set when ensureTagsExist called with null")
    void ensureTagsExist_shouldReturnEmpty_whenNull() {
        Set<String> out = this.defaultTagService.ensureTagsExist(null);

        assertThat(out).isEmpty();
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should return empty set when ensureTagsExist called with empty collection")
    void ensureTagsExist_shouldReturnEmpty_whenEmpty() {
        Set<String> tags = this.defaultTagService.ensureTagsExist(Collections.emptyList());

        assertThat(tags).isEmpty();
        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should normalize input, ensure tags exist and return names")
    void ensureTagsExist_shouldNormalizeAndReturnNames() {
        Collection<String> input = Arrays.asList("  tag1 ", null, "tag2", "tag1");

        Set<Tag> ensured = Set.of(
                new Tag(10L, "tag1"),
                new Tag(11L, "tag2")
        );
        when(this.tagRepository.ensureTagsExist(any())).thenReturn(ensured);

        Set<String> out = this.defaultTagService.ensureTagsExist(input);

        assertThat(out)
                .isNotEmpty()
                .containsExactlyInAnyOrder("tag1", "tag2");

        verify(this.tagRepository).ensureTagsExist(any());
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should clear all tags when replacePostTags called with empty or normalizes to empty")
    void replacePostTags_shouldClear_whenEmptyAfterNormalization() {
        long postId = 7L;

        this.defaultTagService.replacePostTags(postId, Arrays.asList("   ", "", "  "));

        verify(this.tagRepository).replacePostTags(postId, Collections.emptySet());
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should ensure tags exist and replace with their ids when non-empty")
    void replacePostTags_shouldEnsureAndReplace_whenNonEmpty() {
        long postId = 8L;
        Collection<String> input = Arrays.asList(" tag1", "tag2  ", "tag1");
        Set<Tag> ensuredTags = Set.of(new Tag(1L, "tag1"), new Tag(2L, "tag2"));

        when(this.tagRepository.ensureTagsExist(argThat(collection -> new HashSet<>(collection).equals(Set.of("tag1", "tag2")))))
                .thenReturn(ensuredTags);

        this.defaultTagService.replacePostTags(postId, input);

        InOrder inOrder = inOrder(this.tagRepository);
        inOrder.verify(this.tagRepository).ensureTagsExist(argThat(collection -> new HashSet<>(collection).equals(Set.of("tag1", "tag2"))));
        inOrder.verify(this.tagRepository).replacePostTags(postId, Set.of(1L, 2L));
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should no-op on attachTagsToPost when input is null or normalizes to empty")
    void attachTagsToPost_shouldNoop_whenEmpty() {
        long postId = 9L;

        this.defaultTagService.attachTagsToPost(postId, null);
        this.defaultTagService.attachTagsToPost(postId, List.of("  ", "\t"));

        verifyNoInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should ensure tags exist and attach using their ids when non-empty")
    void attachTagsToPost_shouldEnsureAndAttach_whenNonEmpty() {
        long postId = 10L;
        Collection<String> input = Arrays.asList("tag1", "  tag2 ", "tag1");
        Set<Tag> ensuredTags = Set.of(new Tag(100L, "tag1"), new Tag(101L, "tag2"));

        when(this.tagRepository.ensureTagsExist(argThat(c -> new HashSet<>(c).equals(Set.of("tag1", "tag2")))))
                .thenReturn(ensuredTags);

        this.defaultTagService.attachTagsToPost(postId, input);

        InOrder inOrder = inOrder(this.tagRepository);
        inOrder.verify(this.tagRepository).ensureTagsExist(argThat(collection -> new HashSet<>(collection).equals(Set.of("tag1", "tag2"))));
        inOrder.verify(this.tagRepository).attachTagsToPost(postId, Set.of(100L, 101L));
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should return empty map when repository returns empty for getTagsByPostIds")
    void getTagsByPostIds_shouldReturnEmptyMap_whenRepositoryEmpty() {
        when(this.tagRepository.findTagsByPostIds(List.of(1L, 2L))).thenReturn(Collections.emptyMap());

        Map<Long, Set<String>> out = this.defaultTagService.getTagsByPostIds(List.of(1L, 2L));

        assertThat(out).isEmpty();
        verify(this.tagRepository).findTagsByPostIds(List.of(1L, 2L));
        verifyNoMoreInteractions(this.tagRepository);
    }

    @Test
    @DisplayName(value = "Should map repository tags by post id to tag names")
    void getTagsByPostIds_shouldMapToNames() {
        Map<Long, Set<Tag>> postTagsStorage = new HashMap<>();
        postTagsStorage.put(1L, Set.of(new Tag(1L, "tag1")));
        postTagsStorage.put(2L, Set.of(new Tag(2L, "tag2"), new Tag(3L, "tag3")));

        when(this.tagRepository.findTagsByPostIds(List.of(1L, 2L))).thenReturn(postTagsStorage);

        Map<Long, Set<String>> out = this.defaultTagService.getTagsByPostIds(List.of(1L, 2L));

        assertThat(out).hasSize(2);
        assertThat(out.get(1L)).containsExactlyInAnyOrder("tag1");
        assertThat(out.get(2L)).containsExactlyInAnyOrder("tag2", "tag3");

        verify(this.tagRepository).findTagsByPostIds(List.of(1L, 2L));
        verifyNoMoreInteractions(this.tagRepository);
    }

}