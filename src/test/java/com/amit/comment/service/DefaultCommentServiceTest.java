package com.amit.comment.service;

import com.amit.comment.model.Comment;
import com.amit.comment.repository.CommentRepository;
import com.amit.comment.service.exception.CommentNotFoundException;
import com.amit.comment.service.exception.InvalidCommentException;
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

    @BeforeEach
    void resetMocks() {
        reset(this.commentRepository);
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
    @DisplayName(value = "Should throw exception when comment is null")
    void create_throwsInvalidCommentException() {
        assertThrows(InvalidCommentException.class, () -> this.commentService.create(null));
        verifyNoInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should create a new comment and increment post counter")
    void create_savesComment() {
        Comment commentToCreate = buildComment(7L, 0L, "new");
        Comment savedComment = buildComment(7L, 42L, "new");
        when(this.commentRepository.create(commentToCreate)).thenReturn(savedComment);

        Comment out = this.commentService.create(commentToCreate);

        assertSame(savedComment, out);
        verify(this.commentRepository).create(commentToCreate);
    }

    @Test
    @DisplayName(value = "Should throw exception when comment is null")
    void update_throwsInvalidCommentException() {
        assertThrows(InvalidCommentException.class, () -> this.commentService.update(null));
        verifyNoInteractions(this.commentRepository);
    }

    @Test
    @DisplayName(value = "Should update existing comment")
    void update_updatesComment() {
        Comment existingComment = buildComment(8L, 3L, "old");
        Comment updatedComment = buildComment(8L, 3L, "new");
        when(this.commentRepository.update(existingComment)).thenReturn(Optional.of(updatedComment));

        Comment out = this.commentService.update(existingComment);

        assertSame(updatedComment, out);
        verify(this.commentRepository).update(existingComment);
    }

    @Test
    @DisplayName(value = "Should throw exception when updating non-existent comment")
    void update_throwsCommentNotFoundException() {
        Comment missingComment = buildComment(8L, 404L, "x");
        when(this.commentRepository.update(missingComment)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> this.commentService.update(missingComment));
        verify(this.commentRepository).update(missingComment);
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

    private static Comment buildComment(long postId, long id, String text) {
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
        CommentService commentService(CommentRepository commentRepository) {
            return new DefaultCommentService(commentRepository);
        }

    }

}