package com.amit.application.post.service;

import com.amit.post.repository.PostImageRepository;
import com.amit.post.service.PostCrudService;
import com.amit.post.service.PostImageService;
import com.amit.post.service.exception.ImageUpsertException;
import com.amit.post.service.exception.InvalidImageException;
import com.amit.post.service.impl.DefaultPostImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultPostImageServiceTest.DefaultPostImageServiceTestConfiguration.class)
class DefaultPostImageServiceTest {

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostCrudService postCrudService;

    @Autowired
    private PostImageService postImageService;

    @BeforeEach
    void resetMocks() {
        reset(this.postImageRepository, this.postCrudService);
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
    void upsert_ok() throws Exception {
        long postId = 12L;
        byte[] data = new byte[1024];

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenReturn(data);
        when(this.postImageRepository.upsertByPostId(postId, data)).thenReturn(true);

        assertDoesNotThrow(() -> this.postImageService.upsertByPostId(postId, multipartFile));

        InOrder inOrder = inOrder(this.postCrudService, multipartFile, this.postImageRepository);
        inOrder.verify(this.postCrudService).ensurePostExists(postId);
        inOrder.verify(multipartFile).getBytes();
        inOrder.verify(this.postImageRepository).upsertByPostId(postId, data);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw ImageUpsertException when repository returns false")
    void upsert_repositoryFailure() throws Exception {
        long postId = 13L;
        byte[] data = new byte[1024];

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenReturn(data);
        when(this.postImageRepository.upsertByPostId(postId, data)).thenReturn(false);

        assertThrows(ImageUpsertException.class, () -> this.postImageService.upsertByPostId(postId, multipartFile));

        InOrder inOrder = inOrder(this.postCrudService, multipartFile, this.postImageRepository);
        inOrder.verify(this.postCrudService).ensurePostExists(postId);
        inOrder.verify(multipartFile).getBytes();
        inOrder.verify(this.postImageRepository).upsertByPostId(postId, data);
        verifyNoMoreInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw when data is null or empty (validator)")
    void upsert_validatorRejects_nullOrEmpty() throws Exception {
        long postId = 14L;

        MultipartFile emptyMultipartFile = mock(MultipartFile.class);
        when(emptyMultipartFile.getBytes()).thenReturn(new byte[0]);

        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(postId, emptyMultipartFile));

        verify(this.postCrudService).ensurePostExists(postId);
        verify(emptyMultipartFile).getBytes();
        verifyNoInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw when data exceeds max size (validator)")
    void upsert_validatorRejects_tooLargeImage() throws Exception {
        long postId = 15L;
        byte[] tooLargeSize = new byte[(int) (5L * 1024 * 1024) + 1];

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenReturn(tooLargeSize);

        assertThrows(InvalidImageException.class, () -> this.postImageService.upsertByPostId(postId, multipartFile));

        verify(this.postCrudService).ensurePostExists(postId);
        verify(multipartFile).getBytes();
        verifyNoInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should wrap IOException from MultipartFile into ImageUpsertException")
    void upsert_wrapsIoException() throws Exception {
        long postId = 16L;
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(new IOException("Boom"));

        assertThrows(ImageUpsertException.class, () -> this.postImageService.upsertByPostId(postId, multipartFile));

        verify(this.postCrudService).ensurePostExists(postId);
        verify(multipartFile).getBytes();
        verifyNoInteractions(this.postImageRepository);
    }

    @Configuration
    static class DefaultPostImageServiceTestConfiguration {

        @Bean
        PostImageRepository postImageRepository() {
            return mock(PostImageRepository.class);
        }

        @Bean
        PostCrudService postCrudService() {
            return mock(PostCrudService.class);
        }

        @Bean
        PostImageService postImageService(PostImageRepository postImageRepository, PostCrudService postCrudService) {
            return new DefaultPostImageService(postImageRepository, postCrudService);
        }

    }

}