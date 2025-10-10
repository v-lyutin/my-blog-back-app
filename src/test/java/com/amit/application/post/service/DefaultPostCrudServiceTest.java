package com.amit.application.post.service;

import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import com.amit.post.repository.PostCrudRepository;
import com.amit.post.service.PostCrudService;
import com.amit.post.service.exception.InvalidPostException;
import com.amit.post.service.exception.PostNotFoundException;
import com.amit.post.service.impl.DefaultPostCrudService;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.amit.common.util.ModelBuilder.buildPost;
import static com.amit.common.util.ModelBuilder.buildTag;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultPostCrudServiceTest.DefaultPostCrudServiceTestConfiguration.class)
class DefaultPostCrudServiceTest {

    @Autowired
    private PostCrudRepository postCrudRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostCrudService postCrudService;

    @BeforeEach
    void resetMocks() {
        reset(postCrudRepository, tagService);
    }

    @Test
    @DisplayName(value = "Should return PostView by id (post + tags)")
    void getById_returnsPostView() {
        long postId = 10L;
        Post post = buildPost(postId, "t", "x", 2, 3);
        Set<Tag> tags = Set.of(buildTag(1, "a"), buildTag(2, "b"));

        when(this.postCrudRepository.findById(postId)).thenReturn(Optional.of(post));
        when(this.tagService.getTagsByPostId(postId)).thenReturn(tags);

        PostView postView = this.postCrudService.getById(postId);

        assertSame(post, postView.post());
        assertEquals(tags, postView.tags());

        verify(this.postCrudRepository).findById(postId);
        verify(this.tagService).getTagsByPostId(postId);
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw PostNotFoundException when post not found by id")
    void getById_throwsPostNotFoundExceptionWhenNotFound() {
        long postId = 404L;
        when(this.postCrudRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> this.postCrudService.getById(postId));

        verify(this.postCrudRepository).findById(postId);
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should create post, attach tags (when provided) and return PostView")
    void create_attachesTagsAndReturnsView() {
        Post postToCreate = buildPost(null, "T", "Body", 0, 0);
        Post createdPost  = buildPost(100L, "T", "Body", 0, 0);
        List<String> tagNames = List.of("travel", "river");
        Set<Tag> tags = Set.of(buildTag(1,"travel"), buildTag(2,"river"));

        when(this.postCrudRepository.create(postToCreate)).thenReturn(createdPost);
        when(this.tagService.getTagsByPostId(createdPost.getId())).thenReturn(tags);

        PostView postView = this.postCrudService.create(postToCreate, tagNames);

        assertSame(createdPost, postView.post());
        assertEquals(tags, postView.tags());

        verify(this.postCrudRepository).create(postToCreate);
        verify(this.tagService).replacePostTags(createdPost.getId(), tagNames);
        verify(this.tagService).getTagsByPostId(createdPost.getId());
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should create post without touching tags when tagNames is null")
    void create_withoutTags() {
        Post postToCreate = buildPost(null, "T", "Body", 0, 0);
        Post createdPost  = buildPost(101L, "T", "Body", 0, 0);
        Set<Tag> tags = Set.of();

        when(this.postCrudRepository.create(postToCreate)).thenReturn(createdPost);
        when(this.tagService.getTagsByPostId(createdPost.getId())).thenReturn(tags);

        PostView postView = this.postCrudService.create(postToCreate, null);

        assertSame(createdPost, postView.post());
        assertEquals(tags, postView.tags());

        verify(this.postCrudRepository).create(postToCreate);
        verify(this.tagService, never()).replacePostTags(anyLong(), any());
        verify(this.tagService).getTagsByPostId(createdPost.getId());
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw InvalidPostException when creating null post")
    void create_throwsInvalidPostException() {
        assertThrows(InvalidPostException.class, () -> this.postCrudService.create(null, List.of("x")));
        verifyNoInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should update post, replace tags (when provided) and return PostView")
    void update_replacesTagsAndReturnsView() {
        Post postToUpdate = buildPost(200L, "New", "Txt", 5, 6);
        Post updatedPost  = buildPost(200L, "New", "Txt", 5, 6);
        List<String> tagNames = List.of("a","b");
        Set<Tag> tags = Set.of(buildTag(7,"a"), buildTag(8,"b"));

        when(this.postCrudRepository.update(postToUpdate)).thenReturn(Optional.of(updatedPost));
        when(this.tagService.getTagsByPostId(updatedPost.getId())).thenReturn(tags);

        PostView postView = this.postCrudService.update(postToUpdate, tagNames);

        assertSame(updatedPost, postView.post());
        assertEquals(tags, postView.tags());

        verify(this.postCrudRepository).update(postToUpdate);
        verify(this.tagService).replacePostTags(updatedPost.getId(), tagNames);
        verify(this.tagService).getTagsByPostId(updatedPost.getId());
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw InvalidPostException when updating null post")
    void update_throwsInvalidPostExceptionOnNull() {
        assertThrows(InvalidPostException.class, () -> this.postCrudService.update(null, List.of("x")));
        verifyNoInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw InvalidPostException when updating post without id")
    void update_throwsInvalidPostExceptionOnMissingId() {
        Post post = buildPost(null, "t","x",0,0);
        assertThrows(InvalidPostException.class, () -> this.postCrudService.update(post, List.of("x")));
        verifyNoInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw PostNotFoundException when updating non-existent post")
    void update_throwsPostNotFoundExceptionWhenNotFound() {
        Post post = buildPost(300L, "t","x", 0, 0);
        when(this.postCrudRepository.update(post)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> this.postCrudService.update(post, List.of("x")));

        verify(this.postCrudRepository).update(post);
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should delete existing post")
    void delete_deletesPost() {
        long postId = 400L;
        when(this.postCrudRepository.deleteById(postId)).thenReturn(true);

        assertDoesNotThrow(() -> this.postCrudService.deleteById(postId));

        verify(this.postCrudRepository).deleteById(postId);
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw PostNotFoundException when deleting non-existent post")
    void delete_throwsPostNotFoundExceptionWhenNotFound() {
        long postId = 401L;
        when(this.postCrudRepository.deleteById(postId)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> this.postCrudService.deleteById(postId));

        verify(this.postCrudRepository).deleteById(postId);
        verifyNoMoreInteractions(this.postCrudRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should do nothing when post exists")
    void ensurePostExists_ok() {
        long postId = 42L;
        when(this.postCrudRepository.existsById(postId)).thenReturn(true);

        assertDoesNotThrow(() -> this.postCrudService.ensurePostExists(postId));

        verify(this.postCrudRepository).existsById(postId);
        verifyNoMoreInteractions(this.postCrudRepository);
    }

    @Test
    @DisplayName(value = "Should throw PostNotFoundException when post is missing")
    void ensurePostExists_missing_throws() {
        long postId = 404L;
        when(this.postCrudRepository.existsById(postId)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> this.postCrudService.ensurePostExists(postId));

        verify(this.postCrudRepository).existsById(postId);
        verifyNoMoreInteractions(this.postCrudRepository);
    }

    @Configuration
    static class DefaultPostCrudServiceTestConfiguration {

        @Bean
        PostCrudRepository postCrudRepository() {
            return mock(PostCrudRepository.class);
        }

        @Bean
        TagService tagService() {
            return mock(TagService.class);
        }

        @Bean
        PostCrudService postCrudService(PostCrudRepository postCrudRepository, TagService tagService) {
            return new DefaultPostCrudService(postCrudRepository, tagService);
        }

    }

}