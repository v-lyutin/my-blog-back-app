package com.amit.application.post.repository;

import com.amit.common.configuration.BaseDaoTest;
import com.amit.common.util.DaoTestHelper;
import com.amit.post.repository.PostLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcPostLikeRepositoryIntegrationTest extends BaseDaoTest {

    @Autowired
    private DaoTestHelper daoTestHelper;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    @DisplayName(value = "Should increment likes for existing post and return updated count")
    void incrementPostLikes_existingPost_returnsNewCount() throws Exception {
        long postId = this.daoTestHelper.insertPost("title", "body");

        OptionalLong likes = this.postLikeRepository.incrementPostLikes(postId);

        assertTrue(likes.isPresent());
        assertEquals(1L, likes.getAsLong());
    }

    @Test
    @DisplayName(value = "Should return empty optional when post does not exist")
    void incrementPostLikes_nonExistingPost_returnsEmpty() {
        OptionalLong likes = this.postLikeRepository.incrementPostLikes(99999L);
        assertTrue(likes.isEmpty());
    }

}