package com.amit.myblog.application.post.service;

import com.amit.myblog.common.util.Page;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.model.PostView;
import com.amit.myblog.post.repository.PostSearchRepository;
import com.amit.myblog.post.service.impl.DefaultPostSearchService;
import com.amit.myblog.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class DefaultPostSearchServiceTest {

    @Mock
    private PostSearchRepository postSearchRepository;

    @Mock
    private TagService tagService;

    @InjectMocks
    private DefaultPostSearchService defaultPostSearchService;

    @Test
    @DisplayName(value = "Should return empty page when repository returns no posts")
    void search_shouldReturnEmptyPageWhenRepositoryReturnsNoPosts() {
        when(this.postSearchRepository.search(null, Set.of(), 10, 0)).thenReturn(Collections.emptyList());
        when(this.postSearchRepository.count(null, Set.of())).thenReturn(0L);

        Page<PostView> page = this.defaultPostSearchService.search("", 1, 10);

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
    void search_shouldParseRawQueryAndPassTitleAndTagsToRepository() {
        String rawQuery = "hello  #a   #b";
        String title = "hello";
        Set<String> tags = Set.of("a", "b");
        int page = 1, size = 10, offset = 0;

        Post post = buildPost(1L, "hello");

        when(this.postSearchRepository.search(eq(title), eq(tags), eq(size), eq(offset))).thenReturn(List.of(post));
        when(this.postSearchRepository.count(eq(title), eq(tags))).thenReturn(1L);
        when(this.tagService.getTagsByPostIds(List.of(1L))).thenReturn(Map.of(1L, Set.of("a", "b")));

        Page<PostView> result = this.defaultPostSearchService.search(rawQuery, page, size);

        assertEquals(1, result.items().size());
        PostView postView = result.items().getFirst();
        assertEquals(1L, postView.post().getId());
        assertEquals(Set.of("a", "b"), postView.tags());
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
    void search_shouldFetchTagsInBatchAndMapToPostView() {
        Post post1 = buildPost(1L, "A");
        Post post2 = buildPost(2L, "B");

        when(this.postSearchRepository.search(null, Set.of(), 5, 0)).thenReturn(List.of(post1, post2));
        when(this.postSearchRepository.count(null, Set.of())).thenReturn(2L);
        when(this.tagService.getTagsByPostIds(List.of(1L, 2L)))
                .thenReturn(Map.of(
                        1L, Set.of("travel"),
                        2L, Set.of("river", "nature")
                ));

        Page<PostView> page = this.defaultPostSearchService.search("   ", 1, 5);

        assertEquals(2, page.items().size());
        assertEquals(Set.of("travel"), page.items().get(0).tags());
        assertEquals(Set.of("river", "nature"), page.items().get(1).tags());

        verify(this.postSearchRepository).search(null, Set.of(), 5, 0);
        verify(this.postSearchRepository).count(null, Set.of());
        verify(this.tagService).getTagsByPostIds(List.of(1L, 2L));
        verifyNoMoreInteractions(this.postSearchRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should compute paging flags and lastPage correctly")
    void search_shouldComputePagingFlagsAndLastPageCorrectly() {
        when(this.postSearchRepository.search(null, Set.of(), 10, 10))
                .thenReturn(List.of(buildPost(11L, "x")));
        when(this.postSearchRepository.count(null, Set.of()))
                .thenReturn(23L);
        when(this.tagService.getTagsByPostIds(List.of(11L)))
                .thenReturn(Map.of(11L, Set.of()));

        Page<PostView> page = this.defaultPostSearchService.search("", 2, 10);

        assertEquals(3, page.lastPage());
        assertTrue(page.hasPrev());
        assertTrue(page.hasNext());
    }

    @Test
    @DisplayName(value = "Should clamp negative/zero pageNumber to offset=0")
    void search_shouldClampNegativeOrZeroPageNumberToZeroOffset() {
        when(this.postSearchRepository.search(null, Set.of(), 10, 0))
                .thenReturn(Collections.emptyList());
        when(this.postSearchRepository.count(null, Set.of()))
                .thenReturn(0L);

        Page<PostView> page = this.defaultPostSearchService.search(null, 0, 10);

        assertNotNull(page);

        verify(this.postSearchRepository).search(null, Set.of(), 10, 0);
        verify(this.postSearchRepository).count(null, Set.of());
        verifyNoInteractions(this.tagService);
    }


    private static Post buildPost(Long id, String title) {
        return new Post(id, title, "body", 0L, 0L);
    }

}
