package com.amit.myblog.application.post.service;

import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.post.repository.PostImageRepository;
import com.amit.myblog.post.repository.PostRepository;
import com.amit.myblog.post.service.exception.ImageUpsertException;
import com.amit.myblog.post.service.exception.InvalidImageException;
import com.amit.myblog.post.service.impl.DefaultPostImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class DefaultPostImageServiceTest {

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private DefaultPostImageService defaultPostImageService;

    @Test
    @DisplayName(value = "Should return image bytes when repository finds image by post id")
    void getByPostId_shouldReturnBytes_whenImageExists() {
        long postId = 7L;
        byte[] data = new byte[] {1, 2, 3};
        when(this.postImageRepository.findByPostId(postId)).thenReturn(Optional.of(data));

        Optional<byte[]> out = this.defaultPostImageService.getByPostId(postId);

        assertThat(out).isPresent();
        assertThat(out.get()).containsExactly(1, 2, 3);
        verify(this.postImageRepository).findByPostId(postId);
        verifyNoMoreInteractions(this.postImageRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should return empty optional when repository does not find image by post id")
    void getByPostId_shouldReturnEmpty_whenImageMissing() {
        long postId = 8L;
        when(this.postImageRepository.findByPostId(postId)).thenReturn(Optional.empty());

        Optional<byte[]> out = this.defaultPostImageService.getByPostId(postId);

        assertThat(out).isEmpty();
        verify(this.postImageRepository).findByPostId(postId);
        verifyNoMoreInteractions(this.postImageRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should throw InvalidImageException when multipart file is null")
    void upsertByPostId_shouldThrowInvalidImageException_whenFileIsNull() {
        InvalidImageException ex = assertThrows(
                InvalidImageException.class,
                () -> this.defaultPostImageService.upsertByPostId(1L, null)
        );
        assertThat(ex).hasMessageContaining("empty");
        verifyNoInteractions(this.postRepository, this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw InvalidImageException when multipart file is empty")
    void upsertByPostId_shouldThrowInvalidImageException_whenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        InvalidImageException ex = assertThrows(
                InvalidImageException.class,
                () -> this.defaultPostImageService.upsertByPostId(1L, file)
        );
        assertThat(ex).hasMessageContaining("empty");
        verifyNoInteractions(this.postRepository, this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when post does not exist")
    void upsertByPostId_shouldThrowResourceNotFound_whenPostMissing() throws Exception {
        long postId = 42L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(this.postRepository.existsById(postId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> this.defaultPostImageService.upsertByPostId(postId, file)
        );
        assertThat(exception).hasMessageContaining("Post with ID 42 not found");

        verify(this.postRepository).existsById(postId);
        verifyNoMoreInteractions(this.postRepository);
        verifyNoInteractions(this.postImageRepository);
        verify(file, never()).getBytes();
    }

    @Test
    @DisplayName(value = "Should throw ImageUpsertException when reading multipart bytes fails")
    void upsertByPostId_shouldThrowImageUpsertException_whenReadingBytesFails() throws Exception {
        long postId = 5L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(this.postRepository.existsById(postId)).thenReturn(true);
        when(file.getBytes()).thenThrow(new IOException("IO boom"));

        ImageUpsertException ex = assertThrows(
                ImageUpsertException.class,
                () -> this.defaultPostImageService.upsertByPostId(postId, file)
        );
        assertThat(ex).hasMessageContaining("Failed to read uploaded image");

        InOrder inOrder = inOrder(this.postRepository, file);
        inOrder.verify(this.postRepository).existsById(postId);
        inOrder.verify(file).getBytes();

        verifyNoInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw InvalidImageException when image exceeds max size")
    void upsertByPostId_shouldThrowInvalidImageException_whenImageTooBig() throws Exception {
        long postId = 10L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(this.postRepository.existsById(postId)).thenReturn(true);

        byte[] tooBig = new byte[(int) (5L * 1024 * 1024 + 1)];
        when(file.getBytes()).thenReturn(tooBig);

        InvalidImageException ex = assertThrows(
                InvalidImageException.class,
                () -> this.defaultPostImageService.upsertByPostId(postId, file)
        );
        assertThat(ex).hasMessageContaining("exceeds max size");

        verify(this.postRepository).existsById(postId);
        verify(file).getBytes();
        verifyNoInteractions(this.postImageRepository);
    }

    @Test
    @DisplayName(value = "Should throw ImageUpsertException when repository upsert returns false")
    void upsertByPostId_shouldThrowImageUpsertException_whenRepositoryReturnsFalse() throws Exception {
        long postId = 12L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(this.postRepository.existsById(postId)).thenReturn(true);
        when(file.getBytes()).thenReturn(new byte[] {1, 2});
        when(this.postImageRepository.upsertByPostId(eq(postId), any(byte[].class))).thenReturn(false);

        ImageUpsertException exception = assertThrows(
                ImageUpsertException.class,
                () -> this.defaultPostImageService.upsertByPostId(postId, file)
        );
        assertThat(exception).hasMessageContaining("Failed to upsert image for post 12");

        InOrder inOrder = inOrder(this.postRepository, file, this.postImageRepository);
        inOrder.verify(this.postRepository).existsById(postId);
        inOrder.verify(file).getBytes();
        inOrder.verify(this.postImageRepository).upsertByPostId(eq(postId), any(byte[].class));
        verifyNoMoreInteractions(this.postImageRepository, this.postRepository);
    }

    @Test
    @DisplayName(value = "Should upsert image successfully when input is valid and post exists")
    void upsertByPostId_shouldUpsertSuccessfully_whenValid() throws Exception {
        long postId = 99L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(this.postRepository.existsById(postId)).thenReturn(true);
        when(file.getBytes()).thenReturn(new byte[] {1, 2, 3});
        when(this.postImageRepository.upsertByPostId(eq(postId), any(byte[].class))).thenReturn(true);

        this.defaultPostImageService.upsertByPostId(postId, file);

        InOrder inOrder = inOrder(this.postRepository, file, this.postImageRepository);
        inOrder.verify(this.postRepository).existsById(postId);
        inOrder.verify(file).getBytes();
        inOrder.verify(this.postImageRepository).upsertByPostId(eq(postId), any(byte[].class));
        verifyNoMoreInteractions(this.postImageRepository, this.postRepository);
    }

}