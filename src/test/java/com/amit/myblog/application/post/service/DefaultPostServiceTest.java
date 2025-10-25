package com.amit.myblog.application.post.service;

import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.common.excpetion.ServiceException;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.model.PostView;
import com.amit.myblog.post.repository.PostRepository;
import com.amit.myblog.post.service.impl.DefaultPostService;
import com.amit.myblog.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class DefaultPostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @InjectMocks
    private DefaultPostService defaultPostService;

    @Test
    @DisplayName(value = "Should return PostView with tags when post exists")
    void getPostById_shouldReturnPostViewWithTags_whenPostExists() {
        long postId = 10L;
        Post post = new Post(postId, "Title", "Text", 0L, 0L);

        when(this.postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(this.tagService.getTagsByPostId(postId)).thenReturn(Set.of("tag1", "tag2"));

        PostView postView = this.defaultPostService.getPostById(postId);

        assertThat(postView).isNotNull();
        assertThat(postView.post()).isEqualTo(post);
        assertThat(postView.tags()).containsExactlyInAnyOrder("tag1", "tag2");

        verify(this.postRepository).findById(postId);
        verify(this.tagService).getTagsByPostId(postId);
        verifyNoMoreInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when post does not exist")
    void getPostById_shouldThrowResourceNotFoundException_whenPostMissing() {
        long postId = 404L;
        when(this.postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.defaultPostService.getPostById(postId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(Long.toString(postId));

        verify(this.postRepository).findById(postId);
        verifyNoInteractions(this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when adding post with null body")
    void addPost_shouldThrowServiceException_whenPostIsNull() {
        PostView postView = new PostView(null, Set.of("tag1"));

        assertThatThrownBy(() -> this.defaultPostService.addPost(postView))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("must not be null");

        verifyNoInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should create post and not replace tags when tags is null")
    void addPost_shouldCreatePostAndNotReplaceTags_whenTagsAreNull() {
        Post postToCreate = new Post("Title", "Text");
        Post createdPost = new Post(7L, "Title", "Text", 0L, 0L);
        PostView postView = new PostView(postToCreate, null);

        when(this.postRepository.save(postToCreate)).thenReturn(createdPost);
        when(this.tagService.getTagsByPostId(7L)).thenReturn(Set.of());

        PostView out = this.defaultPostService.addPost(postView);

        assertThat(out.post()).isEqualTo(createdPost);
        assertThat(out.tags()).isEmpty();

        verify(this.postRepository).save(postToCreate);
        verify(this.tagService, never()).replacePostTags(anyLong(), anySet());
        verify(this.tagService).getTagsByPostId(7L);
        verifyNoMoreInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should create post and replace tags when tags provided")
    void addPost_shouldCreatePostAndReplaceTags_whenTagsProvided() {
        Post postToCreate = new Post("Title", "Text");
        Post createdPost = new Post(11L, "Title", "Text", 0L, 0L);
        Set<String> tags = Set.of("tag1", "tag2");
        PostView postView = new PostView(postToCreate, tags);

        when(this.postRepository.save(postToCreate)).thenReturn(createdPost);
        when(this.tagService.getTagsByPostId(11L)).thenReturn(tags);

        PostView out = this.defaultPostService.addPost(postView);

        assertThat(out.post()).isEqualTo(createdPost);
        assertThat(out.tags()).containsExactlyInAnyOrder("tag1", "tag2");

        InOrder inOrder = inOrder(this.postRepository, this.tagService);
        inOrder.verify(this.postRepository).save(postToCreate);
        inOrder.verify(this.tagService).replacePostTags(eq(11L), eq(tags));
        inOrder.verify(this.tagService).getTagsByPostId(11L);

        verifyNoMoreInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when editing with null post body")
    void editPost_shouldThrowServiceException_whenPostIsNull() {
        assertThatThrownBy(() -> this.defaultPostService.editPost(1L, new PostView(null, Set.of())))
                .isInstanceOf(ServiceException.class);

        verifyNoInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when editing with null id in post")
    void editPost_shouldThrowServiceException_whenPostIdIsNull() {
        Post postToUpdate = new Post(null, "Title", "Text");
        assertThatThrownBy(() -> this.defaultPostService.editPost(1L, new PostView(postToUpdate, Set.of())))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("ID must not be null");

        verifyNoInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when path id differs from body id")
    void editPost_shouldThrowServiceException_whenPathIdDiffersFromBodyId() {
        Post postToUpdate = new Post(2L, "Title", "Text");
        assertThatThrownBy(() -> this.defaultPostService.editPost(1L, new PostView(postToUpdate, Set.of())))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("IDs for post must not be different");

        verifyNoInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when updating non-existing post")
    void editPost_shouldThrowResourceNotFoundException_whenRepositoryUpdateReturnsEmpty() {
        Post postToUpdate = new Post(5L, "Text", "Title");
        when(this.postRepository.update(postToUpdate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.defaultPostService.editPost(5L, new PostView(postToUpdate, Set.of("tag1"))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("5");

        verify(this.postRepository).update(postToUpdate);
        verifyNoInteractions(this.tagService);
    }

    @Test
    @DisplayName(value = "Should update post and not replace tags when tags is null")
    void editPost_shouldUpdatePostAndNotReplaceTags_whenTagsAreNull() {
        Post postToUpdate = new Post(9L, "Title", "Text");
        Post updatedPost = new Post(9L, "Updated title", "Updated text", 0L, 0L);

        when(this.postRepository.update(postToUpdate)).thenReturn(Optional.of(updatedPost));
        when(this.tagService.getTagsByPostId(9L)).thenReturn(Set.of("tag1"));

        PostView out = this.defaultPostService.editPost(9L, new PostView(postToUpdate, null));

        assertThat(out.post()).isEqualTo(updatedPost);
        assertThat(out.tags()).containsExactly("tag1");

        verify(this.postRepository).update(postToUpdate);
        verify(this.tagService, never()).replacePostTags(anyLong(), anySet());
        verify(this.tagService).getTagsByPostId(9L);
        verifyNoMoreInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should update post and replace tags when tags provided")
    void editPost_shouldUpdatePostAndReplaceTags_whenTagsProvided() {
        Post postToUpdate = new Post(12L, "Title", "Text");
        Post updatedPost = new Post(12L, "Updated title", "Updated text", 0L, 0L);
        Set<String> newTags = Set.of("tag1", "tag2");

        when(this.postRepository.update(postToUpdate)).thenReturn(Optional.of(updatedPost));
        when(this.tagService.getTagsByPostId(12L)).thenReturn(newTags);

        PostView out = this.defaultPostService.editPost(12L, new PostView(postToUpdate, newTags));

        assertThat(out.post()).isEqualTo(updatedPost);
        assertThat(out.tags()).containsExactlyInAnyOrder("tag1", "tag2");

        InOrder inOrder = inOrder(this.postRepository, this.tagService);
        inOrder.verify(this.postRepository).update(postToUpdate);
        inOrder.verify(this.tagService).replacePostTags(12L, newTags);
        inOrder.verify(this.tagService).getTagsByPostId(12L);

        verifyNoMoreInteractions(this.postRepository, this.tagService);
    }

    @Test
    @DisplayName(value = "Should delete post when repository reports success")
    void deletePostById_shouldDeleteSuccessfully_whenRepositoryReturnsTrue() {
        when(this.postRepository.deleteById(77L)).thenReturn(true);

        this.defaultPostService.deletePostById(77L);

        verify(this.postRepository).deleteById(77L);
        verifyNoInteractions(this.tagService);
        verifyNoMoreInteractions(this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when deleting non-existing post")
    void deletePostById_shouldThrowResourceNotFoundException_whenRepositoryReturnsFalse() {
        when(this.postRepository.deleteById(88L)).thenReturn(false);

        assertThatThrownBy(() -> this.defaultPostService.deletePostById(88L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("88");

        verify(this.postRepository).deleteById(88L);
        verifyNoInteractions(this.tagService);
    }

}