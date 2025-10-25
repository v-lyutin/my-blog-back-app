package com.amit.myblog.application.comment.service;

import com.amit.myblog.comment.model.Comment;
import com.amit.myblog.comment.model.event.CommentCreatedEvent;
import com.amit.myblog.comment.model.event.CommentDeletedEvent;
import com.amit.myblog.comment.repository.CommentRepository;
import com.amit.myblog.comment.service.DefaultCommentService;
import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.common.excpetion.ServiceException;
import com.amit.myblog.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class DefaultCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private DefaultCommentService defaultCommentService;

    @Test
    @DisplayName(value = "Should delegate to repository and return list of comments for post")
    void getAllCommentsByPostId_shouldReturnCommentsFromRepository() {
        long postId = 10L;
        List<Comment> comments = List.of(
                new Comment(1L, "Comment 1", postId),
                new Comment(2L, "Comment 2", postId)
        );
        when(this.commentRepository.findAllByPostId(postId)).thenReturn(comments);

        List<Comment> out = this.defaultCommentService.getAllCommentsByPostId(postId);

        assertThat(out).containsExactlyElementsOf(comments);
        verify(this.commentRepository).findAllByPostId(postId);
        verifyNoMoreInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should return comment when found by post id and comment id")
    void getCommentByPostIdAndCommentId_shouldReturnCommentWhenFound() {
        long postId = 5L;
        long commentId = 7L;
        Comment comment = new Comment(commentId, "Comment", postId);
        when(this.commentRepository.findByPostIdAndId(postId, commentId)).thenReturn(Optional.of(comment));

        Comment out = this.defaultCommentService.getCommentByPostIdAndCommentId(postId, commentId);

        assertThat(out).isEqualTo(comment);
        verify(this.commentRepository).findByPostIdAndId(postId, commentId);
        verifyNoMoreInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when comment not found by ids")
    void getCommentByPostIdAndCommentId_shouldThrowWhenNotFound() {
        long postId = 5L;
        long commentId = 999L;
        when(this.commentRepository.findByPostIdAndId(postId, commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.defaultCommentService.getCommentByPostIdAndCommentId(postId, commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment with ID " + commentId)
                .hasMessageContaining("post with ID " + postId);

        verify(this.commentRepository).findByPostIdAndId(postId, commentId);
        verifyNoMoreInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when input comment is null")
    void addComment_shouldThrowServiceExceptionWhenCommentIsNull() {
        assertThatThrownBy(() -> this.defaultCommentService.addComment(1L, null))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("must not be null");

        verifyNoInteractions(this.commentRepository, this.postRepository, this.applicationEventPublisher);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when post id in path differs from comment.postId")
    void addComment_shouldThrowWhenPostIdDiffersFromCommentPostId() {
        long pathPostId = 1L;
        Comment commentToCreate = new Comment(0L, "Comment", 2L);

        assertThatThrownBy(() -> this.defaultCommentService.addComment(pathPostId, commentToCreate))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("IDs for post must not be different");

        verifyNoInteractions(this.commentRepository, this.postRepository, this.applicationEventPublisher);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when post does not exist")
    void addComment_shouldThrowResourceNotFoundWhenPostMissing() {
        long postId = 3L;
        Comment commentToCreate = new Comment(0L, "Comment", postId);
        when(this.postRepository.existsById(postId)).thenReturn(false);

        assertThatThrownBy(() -> this.defaultCommentService.addComment(postId, commentToCreate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post with ID " + postId + " not found");

        verify(this.postRepository).existsById(postId);
        verifyNoMoreInteractions(this.postRepository);
        verifyNoInteractions(this.commentRepository, this.applicationEventPublisher);
    }

    @Test
    @DisplayName(value = "Should save comment then publish CommentCreatedEvent")
    void addComment_shouldSaveThenPublishCreatedEvent() {
        long postId = 4L;
        Comment commentToCreate = new Comment(0L, "Comment", postId);
        Comment createdComment  = new Comment(42L, "Comment", postId);

        when(this.postRepository.existsById(postId)).thenReturn(true);
        when(this.commentRepository.save(commentToCreate)).thenReturn(createdComment);

        Comment out = this.defaultCommentService.addComment(postId, commentToCreate);

        assertThat(out).isEqualTo(createdComment);

        InOrder inOrder = inOrder(this.postRepository, this.commentRepository, this.applicationEventPublisher);
        inOrder.verify(this.postRepository).existsById(postId);
        inOrder.verify(this.commentRepository).save(commentToCreate);
        inOrder.verify(this.applicationEventPublisher).publishEvent(
                argThat((ArgumentMatcher<Object>) event -> {
                    if (event instanceof CommentCreatedEvent(long expectedId, long expectedCommentId)) {
                        return expectedId == postId && expectedCommentId == 42L;
                    }
                    if (event instanceof PayloadApplicationEvent<?> payloadApplicationEvent) {
                        Object payloadApplicationEventPayload = payloadApplicationEvent.getPayload();
                        return payloadApplicationEventPayload instanceof CommentCreatedEvent(long expectedId, long expectedCommentId) && expectedId == postId && expectedCommentId == 42L;
                    }
                    return false;
                })
        );

        verifyNoMoreInteractions(this.postRepository, this.commentRepository, this.applicationEventPublisher);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when input comment is null on edit")
    void editComment_shouldThrowServiceExceptionWhenNull() {
        assertThatThrownBy(() -> this.defaultCommentService.editComment(1L, 2L, null))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("must not be null");

        verifyNoInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when post id differs from comment.postId on edit")
    void editComment_shouldThrowWhenPostIdDiffers() {
        long postId = 1L;
        long commentId = 10L;
        Comment commentToUpdate = new Comment(commentId, "Updated comment", 2L);

        assertThatThrownBy(() -> this.defaultCommentService.editComment(postId, commentId, commentToUpdate))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("IDs for post must not be different");

        verifyNoInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw ServiceException when path comment id differs from comment.id on edit")
    void editComment_shouldThrowWhenCommentIdDiffers() {
        long postId = 1L;
        long pathCommentId = 10L;
        Comment commentToUpdate = new Comment(11L, "Updated comment", postId);

        assertThatThrownBy(() -> this.defaultCommentService.editComment(postId, pathCommentId, commentToUpdate))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("IDs for comment must not be different");

        verifyNoInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when repository did not update (comment absent)")
    void editComment_shouldThrowResourceNotFoundWhenUpdateReturnsEmpty() {
        long postId = 1L;
        long commentId = 10L;
        Comment commentToUpdate = new Comment(commentId, "Updated comment", postId);
        when(this.commentRepository.update(commentToUpdate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.defaultCommentService.editComment(postId, commentId, commentToUpdate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment with ID " + commentId)
                .hasMessageContaining("post with ID " + postId);

        verify(this.commentRepository).update(commentToUpdate);
        verifyNoMoreInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should return updated comment when repository updated it")
    void editComment_shouldReturnUpdatedWhenPresent() {
        long postId = 1L;
        long commentId = 10L;
        Comment commentToUpdate = new Comment(commentId, "Updated comment", postId);
        Comment updatedComment = new Comment(commentId, "Updated comment", postId);
        when(this.commentRepository.update(commentToUpdate)).thenReturn(Optional.of(updatedComment));

        Comment out = this.defaultCommentService.editComment(postId, commentId, commentToUpdate);

        assertThat(out).isEqualTo(updatedComment);
        verify(this.commentRepository).update(commentToUpdate);
        verifyNoMoreInteractions(this.commentRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should delete comment first, then decrement post comments counter when deletion happened")
    void deleteCommentByPostIdAndCommentId_shouldDeleteThenDecrementWhenDeleted() {
        long postId = 2L;
        long commentId = 33L;
        when(this.commentRepository.deleteByPostIdAndId(postId, commentId)).thenReturn(true);

        this.defaultCommentService.deleteCommentByPostIdAndCommentId(postId, commentId);

        InOrder inOrder = inOrder(this.commentRepository, this.applicationEventPublisher);
        inOrder.verify(this.commentRepository).deleteByPostIdAndId(postId, commentId);
        inOrder.verify(this.applicationEventPublisher).publishEvent(
                argThat((ArgumentMatcher<Object>) event -> {
                    if (event instanceof CommentDeletedEvent(long expectedId, long expectedCommentId)) {
                        return expectedId == postId && expectedCommentId == 33L;
                    }
                    if (event instanceof PayloadApplicationEvent<?> payloadApplicationEvent) {
                        Object payloadApplicationEventPayload = payloadApplicationEvent.getPayload();
                        return payloadApplicationEventPayload instanceof CommentDeletedEvent(long expectedId, long expectedCommentId) && expectedId == postId && expectedCommentId == 33L;
                    }
                    return false;
                })
        );

        verifyNoMoreInteractions(this.commentRepository, this.applicationEventPublisher);
    }

    @Test
    @DisplayName(value = "Should not decrement counter and throw when deletion did not happen")
    void deleteCommentByPostIdAndCommentId_shouldThrowAndNotDecrementWhenNotDeleted() {
        long postId = 2L;
        long commentId = 33L;
        when(this.commentRepository.deleteByPostIdAndId(postId, commentId)).thenReturn(false);

        assertThatThrownBy(() -> this.defaultCommentService.deleteCommentByPostIdAndCommentId(postId, commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment with ID " + commentId)
                .hasMessageContaining("post with ID " + postId);

        verify(this.commentRepository).deleteByPostIdAndId(postId, commentId);
        verifyNoMoreInteractions(this.commentRepository);
        verifyNoInteractions(this.applicationEventPublisher);
    }

}