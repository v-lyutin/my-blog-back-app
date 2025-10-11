package com.amit.application.comment.service;

import com.amit.comment.model.Comment;
import com.amit.comment.repository.CommentRepository;
import com.amit.comment.service.CommentService;
import com.amit.comment.service.DefaultCommentService;
import com.amit.comment.service.exception.CommentNotFoundException;
import com.amit.comment.service.exception.InvalidCommentException;
import com.amit.post.service.PostCrudService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultCommentServiceTest.DefaultCommentServiceConfiguration.class)
class DefaultCommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostCrudService postCrudService;

    @BeforeEach
    void resetMocks() {
        reset(this.commentRepository, this.postCrudService);
    }

    @Test
    @DisplayName(value = "Should return all comments for a given post ID")
    void getAllByPostId_returnsAllComments() {
        long postId = 10L;
        Comment comment1 = buildComment(postId, 1L, "A");
        Comment comment2 = buildComment(postId, 2L, "B");
        when(this.commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment1, comment2));

        List<Comment> out = this.commentService.getAllByPostId(postId);

        assertEquals(List.of(comment1, comment2), out);
        verify(this.commentRepository).findAllByPostId(postId);
    }

    @Test
    @DisplayName(value = "Should return comment by post ID and comment ID")
    void getByPostIdAndId_returnsComment() {
        long postId = 11L, id = 5L;
        Comment comment = buildComment(11L, 5L, "text");
        when(this.commentRepository.findByPostIdAndId(postId, id)).thenReturn(Optional.of(comment));

        Comment out = this.commentService.getByPostIdAndId(postId, id);

        assertSame(comment, out);
        verify(this.commentRepository).findByPostIdAndId(postId, id);
    }

    @Test
    @DisplayName(value = "Should throw exception when comment not found by post ID and comment ID")
    void getByPostIdAndId_throwsCommentNotFoundException() {
        long postId = 11L, id = 99L;
        when(this.commentRepository.findByPostIdAndId(postId, id)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> this.commentService.getByPostIdAndId(postId, id));
        verify(this.commentRepository).findByPostIdAndId(postId, id);
    }

    @Test
    @DisplayName(value = "Should throw InvalidCommentException when comment is null")
    void create_nullComment_throwsInvalidCommentException() {
        long postId = 7L;

        assertThrows(InvalidCommentException.class, () -> this.commentService.create(postId, null));

        verifyNoInteractions(this.commentRepository);
        verifyNoInteractions(this.postCrudService);
    }

    @Test
    @DisplayName(value = "Should throw IllegalArgumentException when path postId differs from payload postId")
    void create_postIdMismatch_throwsIllegalArgumentException() {
        long pathPostId = 7L;
        Comment comment = buildComment(8L, 666L, "Text");

        assertThrows(IllegalArgumentException.class, () -> this.commentService.create(pathPostId, comment));

        verifyNoInteractions(this.commentRepository);
        verifyNoInteractions(this.postCrudService);
    }

    @Test
    @DisplayName(value = "Should ensure post exists and create a new comment")
    void create_ok_savesComment() {
        long postId = 7L;
        Comment commentToCreate = buildComment(postId, null, "new");
        Comment savedComment = buildComment(postId, 42L, "new");

        when(commentRepository.create(commentToCreate)).thenReturn(savedComment);

        Comment out = this.commentService.create(postId, commentToCreate);

        assertSame(savedComment, out);
        verify(this.postCrudService).ensurePostExists(postId);
        verify(this.commentRepository).create(commentToCreate);
        verifyNoMoreInteractions(this.postCrudService, this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should throw InvalidCommentException when comment is null")
    void update_nullComment_throwsInvalidCommentException() {
        assertThrows(InvalidCommentException.class, () -> this.commentService.update(7L, 3L, null));

        verifyNoInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should throw IllegalArgumentException when path postId differs from payload postId")
    void update_postIdMismatch_throwsIllegalArgumentException() {
        long pathPostId = 7L;
        long pathCommentId = 3L;
        Comment comment = buildComment(8L, pathCommentId, "text");

        assertThrows(IllegalArgumentException.class, () -> this.commentService.update(pathPostId, pathCommentId, comment));

        verifyNoInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should throw IllegalArgumentException when path commentId differs from payload commentId")
    void update_commentIdMismatch_throwsIAE() {
        long pathPostId = 7L;
        long pathCommentId = 11L;
        Comment comment = buildComment(pathPostId, 12L, "text");

        assertThrows(IllegalArgumentException.class, () -> this.commentService.update(pathPostId, pathCommentId, comment));

        verifyNoInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should update existing comment and return it")
    void update_ok_updatesComment() {
        long postId = 8L;
        long commentId = 3L;
        Comment existingComment = buildComment(postId, commentId, "old");
        Comment updatedComment = buildComment(postId, commentId, "new");

        when(this.commentRepository.update(existingComment)).thenReturn(Optional.of(updatedComment));

        Comment out = this.commentService.update(postId, commentId, existingComment);

        assertSame(updatedComment, out);
        verify(this.commentRepository).update(existingComment);
        verifyNoMoreInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should throw CommentNotFoundException when repository returns empty on update")
    void update_notFound_throwsCommentNotFoundException() {
        long postId = 8L;
        long commentId = 404L;
        Comment missingComment = buildComment(postId, commentId, "Comment");

        when(this.commentRepository.update(missingComment)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> this.commentService.update(postId, commentId, missingComment));

        verify(this.commentRepository).update(missingComment);
        verifyNoMoreInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should delete existing comment")
    void delete_deletesComment() {
        long postId = 9L, id = 2L;
        when(this.commentRepository.deleteByPostIdAndId(postId, id)).thenReturn(true);

        assertDoesNotThrow(() -> this.commentService.deleteByPostIdAndId(postId, id));
        verify(this.commentRepository).deleteByPostIdAndId(postId, id);
    }

    @Test
    @DisplayName(value = "Should throw exception when deleting non-existent comment")
    void delete_throwsCommentNotFoundException() {
        long postId = 9L, id = 999L;
        when(this.commentRepository.deleteByPostIdAndId(postId, id)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> this.commentService.deleteByPostIdAndId(postId, id));
        verify(this.commentRepository).deleteByPostIdAndId(postId, id);
    }

    private static Comment buildComment(Long postId, Long id, String text) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setId(id);
        comment.setText(text);
        return comment;
    }

    @Configuration
    static class DefaultCommentServiceConfiguration {

        @Bean
        CommentRepository commentRepository() {
            return mock(CommentRepository.class);
        }

        @Bean
        PostCrudService postCrudService() {
            return mock(PostCrudService.class);
        }

        @Bean
        CommentService commentService(CommentRepository commentRepository, PostCrudService postCrudService) {
            return new DefaultCommentService(commentRepository, postCrudService);
        }

    }

}