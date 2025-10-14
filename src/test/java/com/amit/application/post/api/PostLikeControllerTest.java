package com.amit.application.post.api;

import com.amit.common.api.GlobalExceptionHandler;
import com.amit.post.api.PostLikeController;
import com.amit.post.service.PostLikeService;
import com.amit.post.service.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = PostLikeControllerTest.PostLikeControllerTestConfiguration.class)
class PostLikeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostLikeService postLikeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName(value = "Should return 200 OK and likes count when incrementing likes")
    void incrementPostLikes_returnsOkAndCount() throws Exception {
        long postId = 42L;
        when(this.postLikeService.incrementPostLikes(postId)).thenReturn(7L);

        this.mockMvc.perform(post("/api/posts/{postId}/likes", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("7"));

        verify(this.postLikeService).incrementPostLikes(postId);
        verifyNoMoreInteractions(this.postLikeService);
    }

    @Test
    @DisplayName(value = "Should return 404 with ErrorDto when post is missing")
    void incrementPostLikes_postMissing_returns404WithErrorDto() throws Exception {
        long postId = 999L;
        String message = "Post with ID %d not found.".formatted(postId);
        when(this.postLikeService.incrementPostLikes(postId)).thenThrow(new PostNotFoundException(message));

        String expectedJson = """
                    {"message":"%s","path":"/api/posts/%d/likes"}
                """.formatted(message, postId);

        this.mockMvc.perform(post("/api/posts/{postId}/likes", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @EnableWebMvc
    @Configuration
    @Import(value = {
            PostLikeController.class,
            GlobalExceptionHandler.class}
    )
    static class PostLikeControllerTestConfiguration {

        @Bean
        public PostLikeService postLikeService() {
            return Mockito.mock(PostLikeService.class);
        }

    }

}