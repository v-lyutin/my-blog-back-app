package com.amit.myblog.application.post.repository;

import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import com.amit.myblog.post.repository.PostImageRepository;
import com.amit.myblog.post.repository.jdbc.JdbcPostImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcPostImageRepository.class)
class JdbcPostImageRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private PostImageRepository postImageRepository;

    @Test
    @DisplayName(value = "Should return Optional.empty() when image does not exist")
    void findByPostId_shouldReturnEmptyWhenImageDoesNotExist() {
        Optional<byte[]> data = this.postImageRepository.findByPostId(123L);

        assertThat(data).isEmpty();
    }

    @Test
    @DisplayName(value = "Should insert new image for existing post and find returns same bytes")
    void upsertByPostId_shouldInsertAndFindReturnsSameBytes() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "t", "b");
        byte[] data = generateBytes(16);

        boolean savedData = this.postImageRepository.upsertByPostId(postId, data);
        assertThat(savedData).isTrue();

        Optional<byte[]> loadedData = this.postImageRepository.findByPostId(postId);
        assertThat(loadedData).isPresent();
        assertArrayEquals(data, loadedData.get());
    }

    @Test
    @DisplayName(value = "Should update existing image and overwrite data on conflict")
    void upsertByPostId_shouldUpdateExistingImageAndOverwriteData() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "t2", "b2");
        byte[] firstData = generateBytes(8);
        byte[] secondData = generateBytes(12);

        assertThat(this.postImageRepository.upsertByPostId(postId, firstData)).isTrue();
        assertThat(this.postImageRepository.upsertByPostId(postId, secondData)).isTrue();

        Optional<byte[]> loadedData = this.postImageRepository.findByPostId(postId);
        assertThat(loadedData).isPresent();
        assertArrayEquals(secondData, loadedData.get());
    }

    @Test
    @DisplayName(value = "Should throw DataIntegrityViolationException when post does not exist")
    void upsertByPostId_shouldThrowDataIntegrityViolationExceptionWhenPostDoesNotExist() {
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