package com.amit.application.post.service;

import com.amit.post.repository.PostCrudRepository;
import com.amit.post.repository.PostImageRepository;
import com.amit.post.service.PostImageService;
import com.amit.post.service.exception.ImageUpsertException;
import com.amit.post.service.exception.InvalidImageException;
import com.amit.post.service.impl.DefaultPostImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultPostImageServiceTest.DefaultPostImageServiceTestConfiguration.class)
class DefaultPostImageServiceTest {

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostCrudRepository postCrudRepository;

    @Autowired
    private PostImageService postImageService;

    @BeforeEach
    void resetMocks() {
        reset(this.postImageRepository, this.postCrudRepository);
    }

    @Test
    @DisplayName(value = "Should return bytes when found")
    void getByPostId_foundImage() {
        long postId = 10L;
        byte[] bytes = new byte[]{1, 2, 3};
        when(this.postImageRepository.findByPostId(postId)).thenReturn(Optional.of(bytes));

        Optional<byte[]> result = this.postImageService.getByPostId(postId);

        assertTrue(result.isPresent());
        assertArrayEquals(bytes, result.get());
        verify(this.postImageRepository).findByPostId(postId);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should return empty when not found")
    void getByPostId_notFoundImage() {
        long postId = 11L;
        when(this.postImageRepository.findByPostId(postId)).thenReturn(Optional.empty());

        Optional<byte[]> result = this.postImageService.getByPostId(postId);

        assertTrue(result.isEmpty());
        verify(this.postImageRepository).findByPostId(postId);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should validate size and upsert successfully")
    void upsert_ok() {
        long postId = 12L;
        byte[] data = new byte[1024];
        when(postCrudRepository.existsById(postId)).thenReturn(true);
        when(this.postImageRepository.upsertByPostId(postId, data)).thenReturn(true);

        assertDoesNotThrow(() -> this.postImageService.upsertByPostId(postId, data));

        verify(postCrudRepository).existsById(postId);
        verify(this.postImageRepository).upsertByPostId(postId, data);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw ImageUpsertException when repository returns false")
    void upsertByPostId_repositoryFailure() {
        long postId = 13L;
        byte[] data = new byte[1024];
        when(postCrudRepository.existsById(postId)).thenReturn(true);
        when(this.postImageRepository.upsertByPostId(postId, data)).thenReturn(false);

        assertThrows(ImageUpsertException.class, () -> this.postImageService.upsertByPostId(postId, data));

        verify(postCrudRepository).existsById(postId);
        verify(this.postImageRepository).upsertByPostId(postId, data);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw when data is null or empty (validator)")
    void upsertByPostId_validatorRejects_nullOrEmpty() {
        long postId = 14L;

        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(postId, null));
        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(postId, new byte[0]));

        verifyNoInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw ImageUpsertException if post does not exist")
    void upsertByPostId_nonExistingPost_throwsImageUpsertException() {
        long postId = 999L;
        when(this.postCrudRepository.existsById(postId)).thenReturn(false);

        assertThrows(
                ImageUpsertException.class,
                () -> this.postImageService.upsertByPostId(postId, new byte[]{1, 2, 3})
        );

        verify(this.postCrudRepository).existsById(postId);
        verifyNoInteractions(postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw when data exceeds max size (validator)")
    void upsertByPostId_validatorRejects_tooLargeImage() {
        long id = 15L;
        byte[] largeSize = new byte[(int) (5L * 1024 * 1024) + 1];

        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(id, largeSize));

        verifyNoInteractions(this.postImageRepository);
    }

    @Configuration
    static class DefaultPostImageServiceTestConfiguration {

        @Bean
        PostImageRepository postImageRepository() {
            return mock(PostImageRepository.class);
        }

        @Bean
        PostCrudRepository postCrudRepository() {
            return mock(PostCrudRepository.class);
        }

        @Bean
        PostImageService postImageService(PostImageRepository postImageRepository, PostCrudRepository postCrudRepository) {
            return new DefaultPostImageService(postImageRepository, postCrudRepository);
        }

    }

}