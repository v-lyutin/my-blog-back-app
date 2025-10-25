package com.amit.myblog.application.post.repository;

import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.repository.PostRepository;
import com.amit.myblog.post.repository.jdbc.JdbcPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcPostRepository.class)
class JdbcPostRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName(value = "Should find post by id when it exists")
    void findById_shouldReturnPostWhenItExists() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        Optional<Post> post = this.postRepository.findById(postId);

        assertThat(post).isPresent();
        assertThat(post.get().getId()).isEqualTo(postId);
        assertThat(post.get().getTitle()).isEqualTo("Title");
        assertThat(post.get().getText()).isEqualTo("Text");
    }

    @Test
    @DisplayName(value = "Should return empty when post does not exist")
    void findById_shouldReturnEmptyWhenPostDoesNotExist() {
        Optional<Post> post = this.postRepository.findById(666L);

        assertThat(post).isEmpty();
    }

    @Test
    @DisplayName(value = "Should create post and return generated entity")
    void save_shouldPersistAndReturnGeneratedEntity() {
        Post postToCreate = new Post("Title", "Text");

        Post createdPost = this.postRepository.save(postToCreate);

        assertThat(createdPost.getId()).isNotNull().isPositive();
        assertThat(createdPost.getTitle()).isEqualTo("Title");
        assertThat(createdPost.getText()).isEqualTo("Text");
        assertThat(createdPost.getCommentsCount()).isZero();
        assertThat(createdPost.getLikesCount()).isZero();

        Optional<Post> reloadedPost = this.postRepository.findById(createdPost.getId());
        assertThat(reloadedPost).isPresent();
        assertThat(reloadedPost.get().getTitle()).isEqualTo("Title");
        assertThat(reloadedPost.get().getText()).isEqualTo("Text");
        assertThat(reloadedPost.get().getCommentsCount()).isZero();
        assertThat(reloadedPost.get().getLikesCount()).isZero();
    }

    @Test
    @DisplayName(value = "Should update existing post and return updated entity")
    void update_shouldModifyExistingPostAndReturnUpdatedEntity() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Old title", "Old text");

        Post postToUpdate = new Post(postId, "New title", "New text");

        Optional<Post> updatedPost = this.postRepository.update(postToUpdate);

        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getId()).isEqualTo(postId);
        assertThat(updatedPost.get().getTitle()).isEqualTo("New title");
        assertThat(updatedPost.get().getText()).isEqualTo("New text");

        Optional<Post> reloadedPost = this.postRepository.findById(postId);
        assertThat(reloadedPost).isPresent();
        assertThat(reloadedPost.get().getTitle()).isEqualTo("New title");
        assertThat(reloadedPost.get().getText()).isEqualTo("New text");
    }

    @Test
    @DisplayName(value = "Should return empty when updating non-existing post")
    void update_shouldReturnEmptyWhenPostDoesNotExist() {
        Post postToUpdate = new Post(666L, "A", "B");

        Optional<Post> updatedPost = this.postRepository.update(postToUpdate);

        assertThat(updatedPost).isEmpty();
    }

    @Test
    @DisplayName(value = "Should delete existing post and return true")
    void deleteById_shouldReturnTrueAndRemoveEntityWhenItExists() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        boolean isDeleted = this.postRepository.deleteById(postId);

        assertThat(isDeleted).isTrue();
        assertThat(this.postRepository.findById(postId)).isEmpty();
    }

    @Test
    @DisplayName(value = "Should return false when deleting non-existing post")
    void deleteById_shouldReturnFalseWhenPostDoesNotExist() {
        boolean isDeleted = this.postRepository.deleteById(666L);

        assertThat(isDeleted).isFalse();
    }

    @Test
    @DisplayName(value = "Should return true for existsById when post exists")
    void existsById_shouldReturnTrueWhenPostExists() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        boolean isExists = this.postRepository.existsById(postId);

        assertThat(isExists).isTrue();
    }

    @Test
    @DisplayName(value = "Should return false for existsById when post is missing")
    void existsById_shouldReturnFalseWhenPostIsMissing() {
        boolean isExists = this.postRepository.existsById(666L);

        assertThat(isExists).isFalse();
    }

}