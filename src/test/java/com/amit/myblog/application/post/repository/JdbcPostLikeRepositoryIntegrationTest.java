package com.amit.myblog.application.post.repository;

import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import com.amit.myblog.post.repository.PostLikeRepository;
import com.amit.myblog.post.repository.jdbc.JdbcPostLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcPostLikeRepository.class)
class JdbcPostLikeRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    @DisplayName(value = "Should increment likes for existing post and return updated count")
    void incrementPostLikes_shouldReturnUpdatedCountForExistingPost() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "title", "body");

        OptionalLong likesCount = this.postLikeRepository.incrementPostLikes(postId);

        assertThat(likesCount).isPresent();
        assertThat(likesCount.getAsLong()).isEqualTo(1L);
    }

    @Test
    @DisplayName(value = "Should return empty optional when post does not exist")
    void incrementPostLikes_shouldReturnEmptyWhenPostDoesNotExist() {
        OptionalLong likesCount = this.postLikeRepository.incrementPostLikes(666L);

        assertThat(likesCount).isEmpty();
    }

}