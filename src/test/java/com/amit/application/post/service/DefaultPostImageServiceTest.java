package com.amit.application.post.service;

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
    private PostImageService postImageService;

    @BeforeEach
    void resetMocks() {
        reset(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should return bytes when found")
    void getByPostId_foundImage() {
        long id = 10L;
        byte[] bytes = new byte[]{1, 2, 3};
        when(this.postImageRepository.findByPostId(id)).thenReturn(Optional.of(bytes));

        Optional<byte[]> result = this.postImageService.getByPostId(id);

        assertTrue(result.isPresent());
        assertArrayEquals(bytes, result.get());
        verify(this.postImageRepository).findByPostId(id);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should return empty when not found")
    void getByPostId_notFoundImage() {
        long id = 11L;
        when(this.postImageRepository.findByPostId(id)).thenReturn(Optional.empty());

        Optional<byte[]> result = this.postImageService.getByPostId(id);

        assertTrue(result.isEmpty());
        verify(this.postImageRepository).findByPostId(id);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should validate size and upsert successfully")
    void upsert_ok() {
        long id = 12L;
        byte[] data = new byte[1024]; // 1KB
        when(this.postImageRepository.upsertByPostId(id, data)).thenReturn(true);

        assertDoesNotThrow(() -> this.postImageService.upsertByPostId(id, data));

        verify(this.postImageRepository).upsertByPostId(id, data);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw ImageUpsertException when repository returns false")
    void upsert_repoFailure() {
        long id = 13L;
        byte[] data = new byte[1024];
        when(this.postImageRepository.upsertByPostId(id, data)).thenReturn(false);

        assertThrows(ImageUpsertException.class, () -> this.postImageService.upsertByPostId(id, data));

        verify(this.postImageRepository).upsertByPostId(id, data);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw when data is null or empty (validator)")
    void upsert_validatorRejects_nullOrEmpty() {
        long id = 14L;

        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(id, null));
        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(id, new byte[0]));

        verifyNoInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw when data exceeds max size (validator)")
    void upsert_validatorRejects_tooLarge() {
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
        PostImageService postImageService(PostImageRepository postImageRepository) {
            return new DefaultPostImageService(postImageRepository);
        }

    }

}