package com.amit.post.service.impl;

import com.amit.post.repository.PostLikeRepository;
import com.amit.post.service.PostLikeService;
import com.amit.post.service.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = DefaultPostLikeServiceTest.DefaultPostLikeServiceTestConfiguration.class)
class DefaultPostLikeServiceTest {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void resetMocks() {
        reset(this.postLikeRepository);
    }

    @Test
    @DisplayName(value = "Should increment likes and return new count for existing post")
    void incrementLikes_returnsNewCount() {
        long postId = 42L;
        long newCount = 101L;
        when(this.postLikeRepository.incrementPostLikes(postId)).thenReturn(OptionalLong.of(newCount));

        long result = this.postLikeService.incrementPostLikes(postId);

        assertEquals(newCount, result);
        verify(this.postLikeRepository).incrementPostLikes(postId);
        verifyNoMoreInteractions(this.postLikeRepository);
    }

    @Test
    @DisplayName(value = "Should throw PostNotFoundException when post does not exist")
    void incrementLikes_throwsPostNotFoundException() {
        long postId = 404L;
        when(this.postLikeRepository.incrementPostLikes(postId)).thenReturn(OptionalLong.empty());

        assertThrows(PostNotFoundException.class, () -> this.postLikeService.incrementPostLikes(postId));
        verify(this.postLikeRepository).incrementPostLikes(postId);
        verifyNoMoreInteractions(this.postLikeRepository);
    }

    @Configuration
    static class DefaultPostLikeServiceTestConfiguration {

        @Bean
        PostLikeRepository postLikeRepository() {
            return mock(PostLikeRepository.class);
        }

        @Bean
        PostLikeService postLikeService(PostLikeRepository postLikeRepository) {
            return new DefaultPostLikeService(postLikeRepository);
        }

    }

}