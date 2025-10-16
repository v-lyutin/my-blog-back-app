package com.amit.myblog.application.comment.repository;

import com.amit.myblog.comment.model.Comment;
import com.amit.myblog.comment.repository.jdbc.JdbcCommentRepository;
import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.CommentDaoTestFixtures;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcCommentRepository.class)
public class JdbcCommentRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private JdbcCommentRepository jdbcCommentRepository;

    @Test
    @DisplayName(value = "Should save comment and return saved entity with generated id")
    void save_shouldSaveCommentAndReturnSavedEntityWithGeneratedId() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        Comment commentToSave = new Comment(0L, "Comment", postId);
        Comment savedComment = this.jdbcCommentRepository.save(commentToSave);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isPositive();
        assertThat(savedComment.getText()).isEqualTo("Comment");
        assertThat(savedComment.getPostId()).isEqualTo(postId);

        long commentsCount = CommentDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId);
        assertThat(commentsCount).isEqualTo(1L);
    }

    @Test
    @DisplayName(value = "Should find all comments by post id and return list of comments in insertion order by id")
    void findAllByPostId_shouldFindAllCommentsByPostIdAndReturnListOfCommentsInInsertionOrderById() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");
        long comment1 = CommentDaoTestFixtures.insertCommentAndReturnId(this.jdbcTemplate, postId, "Comment 1");
        long comment2 = CommentDaoTestFixtures.insertCommentAndReturnId(this.jdbcTemplate, postId, "Comment 2");
        long comment3 = CommentDaoTestFixtures.insertCommentAndReturnId(this.jdbcTemplate, postId, "Comment 3");

        List<Comment> comments = this.jdbcCommentRepository.findAllByPostId(postId);

        assertThat(comments).hasSize(3);
        assertThat(comments.get(0).getId()).isEqualTo(comment1);
        assertThat(comments.get(1).getId()).isEqualTo(comment2);
        assertThat(comments.get(2).getId()).isEqualTo(comment3);
        assertThat(comments).extracting(Comment::getText).containsExactly("Comment 1", "Comment 2", "Comment 3");
    }

    @Test
    @DisplayName(value = "Should return empty list when there are no comments for given post id")
    void findAllByPostId_shouldReturnEmptyListWhenThereAreNoCommentsForGivenPostId() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title C", "Text C");

        List<Comment> comments = this.jdbcCommentRepository.findAllByPostId(postId);

        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName(value = "Should find comment by post id and comment id when record exists")
    void findByPostIdAndId_shouldFindCommentByPostIdAndCommentIdWhenRecordExists() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title D", "Text D");
        long commentId = CommentDaoTestFixtures.insertCommentAndReturnId(this.jdbcTemplate, postId, "target");

        Optional<Comment> comment = this.jdbcCommentRepository.findByPostIdAndId(postId, commentId);

        assertThat(comment).isPresent();
        assertThat(comment.get().getId()).isEqualTo(commentId);
        assertThat(comment.get().getText()).isEqualTo("target");
        assertThat(comment.get().getPostId()).isEqualTo(postId);
    }

    @Test
    @DisplayName(value = "Should return empty optional when comment is not found by post id and comment id")
    void findByPostIdAndId_shouldReturnEmptyOptionalWhenCommentIsNotFoundByPostIdAndCommentId() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title E", "Text E");

        Optional<Comment> comment = this.jdbcCommentRepository.findByPostIdAndId(postId, 666L);

        assertThat(comment).isEmpty();
    }

    @Test
    @DisplayName(value = "Should update comment text by post id and comment id and return updated entity")
    void update_shouldUpdateCommentTextByPostIdAndCommentIdAndReturnUpdatedEntity() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title F", "Text F");
        long commentId = CommentDaoTestFixtures.insertCommentAndReturnId(this.jdbcTemplate, postId, "before");
        Comment commentToUpdate = new Comment(commentId, "after", postId);

        Optional<Comment> updatedComment = this.jdbcCommentRepository.update(commentToUpdate);

        assertThat(updatedComment).isPresent();
        assertThat(updatedComment.get().getId()).isEqualTo(commentId);
        assertThat(updatedComment.get().getText()).isEqualTo("after");
        assertThat(updatedComment.get().getPostId()).isEqualTo(postId);

        Optional<Comment> loadedComment = this.jdbcCommentRepository.findByPostIdAndId(postId, commentId);
        assertThat(loadedComment).isPresent();
        assertThat(loadedComment.get().getText()).isEqualTo("after");
    }

    @Test
    @DisplayName(value = "Should return empty optional when updating comment that does not exist")
    void update_shouldReturnEmptyOptionalWhenUpdatingCommentThatDoesNotExist() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title G", "Text G");
        Comment commentToUpdate = new Comment(666L, "irrelevant", postId);

        Optional<Comment> updated = this.jdbcCommentRepository.update(commentToUpdate);

        assertThat(updated).isEmpty();
    }

    @Test
    @DisplayName(value = "Should delete comment by post id and comment id and return true when deletion happened")
    void deleteByPostIdAndId_shouldDeleteCommentByPostIdAndCommentIdAndReturnTrueWhenDeletionHappened() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title H", "Text H");
        long commentId = CommentDaoTestFixtures.insertCommentAndReturnId(this.jdbcTemplate, postId, "to delete");

        final boolean isDeleted = this.jdbcCommentRepository.deleteByPostIdAndId(postId, commentId);

        assertThat(isDeleted).isTrue();
        Optional<Comment> comment = this.jdbcCommentRepository.findByPostIdAndId(postId, commentId);
        assertThat(comment).isEmpty();
    }

    @Test
    @DisplayName(value = "Should return false when trying to delete non existent comment by post id and comment id")
    void deleteByPostIdAndId_shouldReturnFalseWhenTryingToDeleteNonExistentCommentByPostIdAndCommentId() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title I", "Text I");

        boolean isDeleted = this.jdbcCommentRepository.deleteByPostIdAndId(postId, 666L);

        assertThat(isDeleted).isFalse();
    }

}
