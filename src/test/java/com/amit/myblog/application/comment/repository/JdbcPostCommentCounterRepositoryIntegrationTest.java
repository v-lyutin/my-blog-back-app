package com.amit.myblog.application.comment.repository;

import com.amit.myblog.comment.repository.jdbc.JdbcPostCommentCounterRepository;
import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcPostCommentCounterRepository.class)
class JdbcPostCommentCounterRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private JdbcPostCommentCounterRepository jdbcPostCommentCounterRepository;

    @Test
    @DisplayName(value = "Should increment comments_count by post id")
    void incrementCommentsCountByPostId_shouldIncrementCommentsCountByPostId() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isZero();

        this.jdbcPostCommentCounterRepository.incrementCommentsCountByPostId(postId);
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isEqualTo(1L);

        this.jdbcPostCommentCounterRepository.incrementCommentsCountByPostId(postId);
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isEqualTo(2L);
    }

    @Test
    @DisplayName(value = "Should decrement comments_count by post id but not below zero when decremented after increments")
    void decrementCommentsCountByPostId_shouldDecrementCommentsCountByPostIdButNotBelowZeroWhenDecrementedAfterIncrements() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        this.jdbcPostCommentCounterRepository.incrementCommentsCountByPostId(postId);
        this.jdbcPostCommentCounterRepository.incrementCommentsCountByPostId(postId);
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isEqualTo(2L);

        this.jdbcPostCommentCounterRepository.decrementCommentsCountByPostId(postId);
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isEqualTo(1L);

        this.jdbcPostCommentCounterRepository.decrementCommentsCountByPostId(postId);
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isZero();

        this.jdbcPostCommentCounterRepository.decrementCommentsCountByPostId(postId);
        assertThat(PostDaoTestFixtures.selectCommentsCountByPostId(this.namedParameterJdbcTemplate, postId)).isZero();
    }

    @Test
    @DisplayName(value = "Should be no-op when incrementing or decrementing for non existent post id (no exception)")
    void shouldBeNoOpWhenIncrementingOrDecrementingForNonExistentPostIdNoException() {
        this.jdbcPostCommentCounterRepository.incrementCommentsCountByPostId(666L);
        this.jdbcPostCommentCounterRepository.decrementCommentsCountByPostId(666L);

        assertThat(true).isTrue();
    }

}