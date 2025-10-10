package com.amit.application.comment.repository;

import com.amit.comment.model.Comment;
import com.amit.comment.repository.CommentRepository;
import com.amit.common.configuration.BaseDaoTest;
import com.amit.common.util.DaoTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcCommentRepositoryIntegrationTest extends BaseDaoTest {

    @Autowired
    private DaoTestHelper daoTestHelper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    @DisplayName(value = "Should create comment and increment post comments counter")
    void create_shouldIncrementCounter() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");

        Comment commentToCreate = new Comment();
        commentToCreate.setPostId(postId);
        commentToCreate.setText("Comment");

        Comment savedComment = this.commentRepository.create(commentToCreate);

        assertNotNull(savedComment.getId());
        assertEquals(postId, savedComment.getPostId());
        assertEquals("Comment", savedComment.getText());

        assertEquals(1L, getPostCommentsCount(postId));
    }

    @Test
    @DisplayName(value = "Should find all comments by post id (isolated per post)")
    void findAllByPostId_isolated() {
        long post1 = this.daoTestHelper.insertPost("Title", "Text");
        long post2 = this.daoTestHelper.insertPost("Title", "Text");

        this.commentRepository.create(buildComment(post1, "Comment 1"));
        this.commentRepository.create(buildComment(post1, "Comment 2"));

        this.commentRepository.create(buildComment(post2, "Comment 3"));

        List<Comment> commentForPost1 = this.commentRepository.findAllByPostId(post1);
        assertEquals(2, commentForPost1.size());
        assertTrue(commentForPost1.stream().allMatch(comment -> comment.getPostId() == post1));

        List<Comment> commentsForPost2 = this.commentRepository.findAllByPostId(post2);
        assertEquals(1, commentsForPost2.size());
        assertTrue(commentsForPost2.stream().allMatch(comment -> comment.getPostId() == post2));
    }

    @Test
    @DisplayName(value = "Should find comment by postId and id when exists")
    void findByPostIdAndId_exists() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        Comment savedComment = this.commentRepository.create(buildComment(postId, "2b || !2b"));

        Optional<Comment> comment = this.commentRepository.findByPostIdAndId(postId, savedComment.getId());
        assertTrue(comment.isPresent());
        assertEquals(savedComment.getId(), comment.get().getId());
        assertEquals("2b || !2b", comment.get().getText());
    }

    @Test
    @DisplayName(value = "Should return empty when findByPostIdAndId target does not exist")
    void findByPostIdAndId_notExistsComment() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        Optional<Comment> comment = this.commentRepository.findByPostIdAndId(postId, 666L);
        assertTrue(comment.isEmpty());
    }

    @Test
    @DisplayName(value = "Should update text and keep post comments counter unchanged")
    void update_updatesText_counterUnchanged() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        Comment savedComment = this.commentRepository.create(buildComment(postId, "Old comment text"));
        assertEquals(1L, getPostCommentsCount(postId));

        Comment commentToUpdate = new Comment();
        commentToUpdate.setId(savedComment.getId());
        commentToUpdate.setPostId(postId);
        commentToUpdate.setText("New comment text");

        Optional<Comment> updatedComment = this.commentRepository.update(commentToUpdate);
        assertTrue(updatedComment.isPresent());
        assertEquals("New comment text", updatedComment.get().getText());

        assertEquals(1L, getPostCommentsCount(postId));
    }

    @Test
    @DisplayName(value = "Should return empty on update when comment does not exist")
    void update_notExistsComment_returnsEmpty() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");

        Comment nonExistingComment = new Comment();
        nonExistingComment.setId(666L);
        nonExistingComment.setPostId(postId);
        nonExistingComment.setText("Does not matter");

        Optional<Comment> updatedComment = this.commentRepository.update(nonExistingComment);
        assertTrue(updatedComment.isEmpty());
        assertEquals(0L, getPostCommentsCount(postId));
    }

    @Test
    @DisplayName(value = "Should delete by postId and id and decrement post comments counter")
    void delete_existingComment_decrementsCounter() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        Comment comment1 = this.commentRepository.create(buildComment(postId, "Comment 1"));
        Comment comment2 = this.commentRepository.create(buildComment(postId, "Comment 2"));
        assertEquals(2L, getPostCommentsCount(postId));

        boolean isDeleted1Comment = this.commentRepository.deleteByPostIdAndId(postId, comment1.getId());
        assertTrue(isDeleted1Comment);
        assertEquals(1L, getPostCommentsCount(postId));

        boolean isDeleted2Comment = this.commentRepository.deleteByPostIdAndId(postId, comment2.getId());
        assertTrue(isDeleted2Comment);
        assertEquals(0L, getPostCommentsCount(postId));
    }

    @Test
    @DisplayName(value = "Should return false when deleting non-existing comment and not change counter")
    void delete_notExistingComment_returnsFalse_andCounterStays() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        this.commentRepository.create(buildComment(postId, "Comment"));
        assertEquals(1L, getPostCommentsCount(postId));

        boolean isDeleted = this.commentRepository.deleteByPostIdAndId(postId, 666L);
        assertFalse(isDeleted);
        assertEquals(1L, getPostCommentsCount(postId));
    }

    private static Comment buildComment(long postId, String text) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setText(text);
        return comment;
    }

    private long getPostCommentsCount(long postId) {
        Long postCommentsCount = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT comments_count FROM my_blog.posts WHERE id = :id",
                new MapSqlParameterSource("id", postId),
                Long.class
        );
        return (postCommentsCount == null) ? 0L : postCommentsCount;
    }

}