package com.amit.myblog.application.post.service;

import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.post.repository.PostLikeRepository;
import com.amit.myblog.post.service.impl.DefaultPostLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class DefaultPostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private DefaultPostLikeService defaultPostLikeService;

    @Test
    @DisplayName(value = "Should increment likes and return new count for existing post")
    void incrementLikes_shouldReturnNewLikesCount() {
        long postId = 42L;
        long newCount = 101L;
        when(this.postLikeRepository.incrementPostLikes(postId)).thenReturn(OptionalLong.of(newCount));

        long result = this.defaultPostLikeService.incrementPostLikes(postId);

        assertEquals(newCount, result);
        verify(this.postLikeRepository).incrementPostLikes(postId);
        verifyNoMoreInteractions(this.postLikeRepository);
    }

    @Test
    @DisplayName(value = "Should throw ResourceNotFoundException when post does not exist")
    void incrementLikes_shouldThrowResourceNotFoundException_whenPostDoesNotExists() {
        long postId = 404L;
        when(this.postLikeRepository.incrementPostLikes(postId)).thenReturn(OptionalLong.empty());

        assertThrows(ResourceNotFoundException.class, () -> this.defaultPostLikeService.incrementPostLikes(postId));
        verify(this.postLikeRepository).incrementPostLikes(postId);
        verifyNoMoreInteractions(this.postLikeRepository);
    }

}