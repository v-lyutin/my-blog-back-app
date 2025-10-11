package com.amit.application.post.repository;

import com.amit.common.configuration.BaseDaoTest;
import com.amit.common.util.DaoTestHelper;
import com.amit.post.model.Post;
import com.amit.post.repository.PostCrudRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcPostCrudRepositoryIntegrationTest extends BaseDaoTest {

    @Autowired
    private DaoTestHelper daoTestHelper;

    @Autowired
    private PostCrudRepository postCrudRepository;

    @Test
    @DisplayName(value = "Should find post by id when it exists")
    void findById_existingPost_returnsPost() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");

        Optional<Post> out = this.postCrudRepository.findById(postId);

        assertTrue(out.isPresent());
        assertEquals(postId, out.get().getId());
        assertEquals("Title", out.get().getTitle());
        assertEquals("Text", out.get().getText());
    }

    @Test
    @DisplayName(value = "Should return empty when post does not exist")
    void findById_missingPost_returnsEmpty() {
        Optional<Post> out = this.postCrudRepository.findById(666L);
        assertTrue(out.isEmpty());
    }

    @Test
    @DisplayName(value = "Should create post and return generated entity")
    void create_persistsAndReturnsGenerated() {
        Post postToCreate = new Post();
        postToCreate.setTitle("Title");
        postToCreate.setText("Text");

        Post createdPost = this.postCrudRepository.create(postToCreate);

        assertNotNull(createdPost.getId());
        assertTrue(createdPost.getId() > 0);
        assertEquals("Title", createdPost.getTitle());
        assertEquals("Text", createdPost.getText());
        assertEquals(0, createdPost.getCommentsCount());
        assertEquals(0, createdPost.getLikesCount());

        Optional<Post> reloadedPost = this.postCrudRepository.findById(createdPost.getId());
        assertTrue(reloadedPost.isPresent());
        assertEquals("Title", reloadedPost.get().getTitle());
        assertEquals("Text", reloadedPost.get().getText());
        assertEquals(0, reloadedPost.get().getCommentsCount());
        assertEquals(0, reloadedPost.get().getLikesCount());
    }

    @Test
    @DisplayName(value = "Should update existing post and return updated entity")
    void update_existingPost_updatesAndReturns() {
        long postId = this.daoTestHelper.insertPost("Old title", "Old text");

        Post postToUpdate = new Post();
        postToUpdate.setId(postId);
        postToUpdate.setTitle("New title");
        postToUpdate.setText("New text");

        Optional<Post> updatedPost = this.postCrudRepository.update(postToUpdate);

        assertTrue(updatedPost.isPresent());
        assertEquals(postId, updatedPost.get().getId());
        assertEquals("New title", updatedPost.get().getTitle());
        assertEquals("New text", updatedPost.get().getText());

        Optional<Post> reloaded = this.postCrudRepository.findById(postId);
        assertTrue(reloaded.isPresent());
        assertEquals("New title", reloaded.get().getTitle());
        assertEquals("New text", reloaded.get().getText());
    }

    @Test
    @DisplayName(value = "Should return empty when updating non-existing post")
    void update_missingPost_returnsEmpty() {
        Post toUpdate = new Post();
        toUpdate.setId(666L);
        toUpdate.setTitle("A");
        toUpdate.setText("B");

        Optional<Post> updatedPost = this.postCrudRepository.update(toUpdate);

        assertTrue(updatedPost.isEmpty());
    }

    @Test
    @DisplayName("Should delete existing post and return true")
    void delete_existingPost_returnsTrueAndRemoves() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");

        boolean isDeleted = this.postCrudRepository.deleteById(postId);

        assertTrue(isDeleted);
        assertTrue(this.postCrudRepository.findById(postId).isEmpty());
    }

    @Test
    @DisplayName(value = "Should return false when deleting non-existing post")
    void delete_missingPost_returnsFalse() {
        boolean isDeleted = this.postCrudRepository.deleteById(666L);
        assertFalse(isDeleted);
    }

    @Test
    @DisplayName(value = "Should return true for existsById when post exists")
    void existsById_existingPost_returnsTrue() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        assertTrue(this.postCrudRepository.existsById(postId));
    }

    @Test
    @DisplayName(value = "Should return false for existsById when post is missing")
    void existsById_missingPost_returnsFalse() {
        assertFalse(this.postCrudRepository.existsById(666L));
    }

}