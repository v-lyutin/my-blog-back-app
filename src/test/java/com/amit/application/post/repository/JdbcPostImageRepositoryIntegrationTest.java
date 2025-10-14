package com.amit.application.post.repository;

import com.amit.common.configuration.BaseDaoTest;
import com.amit.common.util.DaoTestHelper;
import com.amit.post.repository.PostImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class JdbcPostImageRepositoryIntegrationTest extends BaseDaoTest {

    @Autowired
    private DaoTestHelper daoTestHelper;

    @Autowired
    private PostImageRepository postImageRepository;

    @Test
    @DisplayName(value = "Should returns Optional.empty() when image does not exist")
    void findByPostId_nonExistingPost_returnsEmpty() {
        Optional<byte[]> out = this.postImageRepository.findByPostId(123L);
        assertTrue(out.isEmpty());
    }

    @Test
    @DisplayName(value = "Should inserts new image for existing post and find returns same bytes")
    void upsertByPostId_insertsAndFind_returnsBytes() {
        long postId = this.daoTestHelper.insertPost("t", "b");
        byte[] data = generateBytes(16);

        boolean savedImage = this.postImageRepository.upsertByPostId(postId, data);
        assertTrue(savedImage);

        Optional<byte[]> loadedImage = this.postImageRepository.findByPostId(postId);
        assertTrue(loadedImage.isPresent());
        assertArrayEquals(data, loadedImage.get());
    }

    @Test
    @DisplayName(value = "Should updates existing image (overwrites data)")
    void upsertByPostId_updates_overwrites() {
        long postId = this.daoTestHelper.insertPost("t2", "b2");
        byte[] firstImage = generateBytes(8);
        byte[] secondImage = generateBytes(12);

        assertTrue(this.postImageRepository.upsertByPostId(postId, firstImage));
        assertTrue(this.postImageRepository.upsertByPostId(postId, secondImage));

        Optional<byte[]> loadedImage = this.postImageRepository.findByPostId(postId);
        assertTrue(loadedImage.isPresent());
        assertArrayEquals(secondImage, loadedImage.get());
    }

    @Test
    @DisplayName(value = "Should throws DataIntegrityViolationException for non-existing post")
    void upsertByPostId_nonExistingPost_throwsDataIntegrityViolationException() {
        byte[] data = generateBytes(10);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> this.postImageRepository.upsertByPostId(999L, data)
        );
    }

    private static byte[] generateBytes(int size) {
        byte[] bytes = new byte[size];
        new Random(42L + size).nextBytes(bytes);
        return bytes;
    }

}