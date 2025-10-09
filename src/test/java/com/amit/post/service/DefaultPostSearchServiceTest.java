package com.amit.post.service;

import com.amit.common.util.Page;
import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import com.amit.post.repository.PostSearchRepository;
import com.amit.post.service.impl.DefaultPostSearchService;
import com.amit.tag.model.Tag;
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
import java.util.stream.Collectors;

import static com.amit.testutil.ModelBuilder.buildPost;
import static com.amit.testutil.ModelBuilder.buildTag;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultPostSearchServiceTest.DefaultPostSearchServiceTestConfiguration.class)
class DefaultPostSearchServiceTest {

    @Autowired
    private PostSearchRepository postSearchRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostSearchService postSearchService;

    @BeforeEach
    void resetMocks() {
        reset(this.postSearchRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should return empty page when repository returns no posts")
    void search_emptyResult() {
        when(this.postSearchRepository.search(null, Set.of(), 10, 0)).thenReturn(Collections.emptyList());
        when(this.postSearchRepository.count(null, Set.of())).thenReturn(0L);

        Page<PostView> page = this.postSearchService.search("", 1, 10);

        assertTrue(page.items().isEmpty());
        assertFalse(page.hasPrev());
        assertFalse(page.hasNext());
        assertEquals(1, page.lastPage());

        verify(this.postSearchRepository).search(null, Set.of(), 10, 0);
        verify(this.postSearchRepository).count(null, Set.of());
        verifyNoInteractions(this.tagService);
        verifyNoMoreInteractions(this.postSearchRepository);
    }

    @Test
    @DisplayName(value = "Should parse raw query and pass title/tags to repository")
    void search_parsesAndPassesCriteria_simplified() {
        String rawQuery = "hello  #a   #b";
        String title = "hello";
        Set<String> tags = Set.of("a", "b");
        int page = 1, size = 10, offset = 0;

        Post post = buildPost(1L, "hello");

        when(this.postSearchRepository.search(eq(title), eq(tags), eq(size), eq(offset))).thenReturn(List.of(post));
        when(this.postSearchRepository.count(eq(title), eq(tags))).thenReturn(1L);
        when(this.tagService.getTagsByPostIds(List.of(1L))).thenReturn(Map.of(1L, Set.of(buildTag(10, "a"), buildTag(11, "b"))));

        Page<PostView> result = this.postSearchService.search(rawQuery, page, size);

        assertEquals(1, result.items().size());
        PostView postView = result.items().getFirst();
        assertEquals(1L, postView.post().getId());
        assertEquals(Set.of("a", "b"), postView.tags().stream().map(Tag::getName).collect(Collectors.toSet()));
        assertEquals(1, result.lastPage());
        assertFalse(result.hasPrev());
        assertFalse(result.hasNext());

        verify(this.postSearchRepository).search(eq(title), eq(tags), eq(size), eq(offset));
        verify(this.postSearchRepository).count(eq(title), eq(tags));
        verify(this.tagService).getTagsByPostIds(List.of(1L));
        verifyNoMoreInteractions(this.postSearchRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should fetch tags in batch and map to PostView")
    void search_attachesTagsInBatch() {
        Post post1 = buildPost(1L, "A");
        Post post2 = buildPost(2L, "B");
        when(this.postSearchRepository.search(null, Set.of(), 5, 0)).thenReturn(List.of(post1, post2));
        when(this.postSearchRepository.count(null, Set.of())).thenReturn(2L);
        Map<Long, Set<Tag>> tags = new HashMap<>();
        tags.put(1L, Set.of(buildTag(100, "travel")));
        tags.put(2L, Set.of(buildTag(101, "river"), buildTag(102, "nature")));
        when(this.tagService.getTagsByPostIds(List.of(1L, 2L))).thenReturn(tags);

        Page<PostView> page = this.postSearchService.search("   ", 1, 5);

        assertEquals(2, page.items().size());
        assertEquals(
                Set.of("travel"),
                page.items().get(0).tags().stream().map(Tag::getName).collect(Collectors.toSet())
        );
        assertEquals(
                Set.of("river", "nature"),
                page.items().get(1).tags().stream().map(Tag::getName).collect(Collectors.toSet())
        );

        verify(this.postSearchRepository).search(null, Set.of(), 5, 0);
        verify(this.postSearchRepository).count(null, Set.of());
        verify(this.tagService).getTagsByPostIds(List.of(1L, 2L));
        verifyNoMoreInteractions(this.postSearchRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should compute paging flags and lastPage correctly")
    void search_computesPagingFlags() {
        when(this.postSearchRepository.search(null, Set.of(), 10, 10)).thenReturn(List.of(buildPost(11L, "x")));
        when(this.postSearchRepository.count(null, Set.of())).thenReturn(23L);
        when(tagService.getTagsByPostIds(List.of(11L))).thenReturn(Map.of(11L, Set.of()));

        Page<PostView> page = this.postSearchService.search("", 2, 10);

        assertEquals(3, page.lastPage());
        assertTrue(page.hasPrev());
        assertTrue(page.hasNext());
    }

    @Test
    @DisplayName(value = "Should clamp negative/zero pageNumber to offset=0")
    void search_clampsNegativeOffsetToZero() {
        when(this.postSearchRepository.search(null, Set.of(), 10, 0)).thenReturn(Collections.emptyList());
        when(this.postSearchRepository.count(null, Set.of())).thenReturn(0L);

        Page<PostView> page = this.postSearchService.search(null, 0, 10);

        assertNotNull(page);

        verify(this.postSearchRepository).search(null, Set.of(), 10, 0);
        verify(this.postSearchRepository).count(null, Set.of());
        verifyNoInteractions(tagService);
    }

    @Configuration
    static class DefaultPostSearchServiceTestConfiguration {

        @Bean
        PostSearchRepository postSearchRepository() {
            return mock(PostSearchRepository.class);
        }

        @Bean
        TagService tagService() {
            return mock(TagService.class);
        }

        @Bean
        PostSearchService postSearchService(PostSearchRepository postSearchRepository, TagService tagService) {
            return new DefaultPostSearchService(postSearchRepository, tagService);
        }

    }

}