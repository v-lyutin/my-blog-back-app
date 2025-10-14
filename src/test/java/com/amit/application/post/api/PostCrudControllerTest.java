package com.amit.application.post.api;

import com.amit.common.api.GlobalExceptionHandler;
import com.amit.post.api.PostCrudController;
import com.amit.post.api.dto.request.PostCreateRequest;
import com.amit.post.api.dto.request.PostUpdateRequest;
import com.amit.post.api.dto.response.PostResponse;
import com.amit.post.api.mapper.PostMapper;
import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import com.amit.post.service.PostCrudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Set;

import static com.amit.common.util.ModelBuilder.buildPost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = PostCrudControllerTest.PostCrudControllerTestConfiguration.class)
class PostCrudControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostCrudService postCrudService;

    @Autowired
    private PostMapper postMapper;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        reset(this.postCrudService, this.postMapper);
    }

    @Test
    @DisplayName(value = "Should return 200 and PostResponse for getPostById")
    void getPostById_200() throws Exception {
        long postId = 1L;

        Post post = buildPost(postId, "Title");
        Set<String> tags = Set.of("tag1", "tag2");
        PostView postView = new PostView(post, tags);
        PostResponse postResponse = new PostResponse(
                postId,
                post.getTitle(),
                post.getText(),
                tags,
                post.getLikesCount(),
                post.getCommentsCount()
        );

        when(this.postCrudService.getById(postId)).thenReturn(postView);
        when(this.postMapper.toPostResponse(postView)).thenReturn(postResponse);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/posts/{postId}", postId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        PostResponse body = this.objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                PostResponse.class
        );
        assertEquals(postId, body.id());
        assertEquals(post.getTitle(), body.title());
        assertEquals(tags.size(), body.tags().size());
        assertEquals(post.getLikesCount(), body.likesCount());
        assertEquals(post.getCommentsCount(), body.commentsCount());

        verify(this.postCrudService).getById(postId);
        verify(this.postMapper).toPostResponse(postView);
        verifyNoMoreInteractions(this.postCrudService, this.postMapper);
    }

    @Test
    @DisplayName(value = "Should return 201 and PostResponse for createPost")
    void createPost_201() throws Exception {
        Set<String> tags = Set.of("t1", "t2");
        String json = """
                    {"title":"New","text":"Body","tags":["t1","t2"]}
                """;

        Post postToCreate = buildPost(null, "New");
        postToCreate.setText("Body");
        PostView toCreatePostView = new PostView(postToCreate, tags);

        Post createdPost = buildPost(10L, "New");
        createdPost.setText("Body");
        PostView createdPostView = new PostView(createdPost, tags);

        PostResponse response = new PostResponse(
                10L,
                "New",
                "Body",
                tags,
                createdPost.getLikesCount(),
                createdPost.getCommentsCount()
        );

        when(this.postMapper.toPostView(any(PostCreateRequest.class))).thenReturn(toCreatePostView);
        when(this.postCrudService.create(toCreatePostView)).thenReturn(createdPostView);
        when(this.postMapper.toPostResponse(createdPostView)).thenReturn(response);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        PostResponse body = this.objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                PostResponse.class
        );
        assertEquals(10L, body.id());
        assertEquals("New", body.title());
        assertEquals("Body", body.text());
        assertEquals(tags.size(), body.tags().size());
        assertEquals(createdPost.getLikesCount(), body.likesCount());
        assertEquals(createdPost.getCommentsCount(), body.commentsCount());

        verify(this.postMapper).toPostView(any(PostCreateRequest.class));
        verify(this.postCrudService).create(toCreatePostView);
        verify(this.postMapper).toPostResponse(createdPostView);
        verifyNoMoreInteractions(this.postCrudService, this.postMapper);
    }

    @Test
    @DisplayName(value = "Should return 200 and PostResponse for updatePostById")
    void updatePostById_200() throws Exception {
        long postId = 5L;
        Set<String> tags = Set.of("x", "y");
        String json = """
                    {"id":5,"title":"Updated","text":"Body+","tags":["x","y"]}
                """;

        Post postToUpdate = buildPost(null, "Updated");
        postToUpdate.setText("Body+");
        PostView postViewToUpdate = new PostView(postToUpdate, tags);

        Post updatedPost = buildPost(postId, "Updated");
        updatedPost.setText("Body+");
        updatedPost.setLikesCount(3);
        updatedPost.setCommentsCount(1);
        PostView updatedView = new PostView(updatedPost, tags);

        PostResponse postResponse = new PostResponse(
                postId,
                "Updated",
                "Body+",
                tags,
                3L,
                1L
        );

        when(this.postMapper.toPostView(any(PostUpdateRequest.class))).thenReturn(postViewToUpdate);
        when(this.postCrudService.update(postId, postViewToUpdate)).thenReturn(updatedView);
        when(this.postMapper.toPostResponse(updatedView)).thenReturn(postResponse);

        MvcResult mvcResult = this.mockMvc.perform(put("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        PostResponse body = this.objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                PostResponse.class
        );
        assertEquals(postId, body.id());
        assertEquals("Updated", body.title());
        assertEquals("Body+", body.text());
        assertEquals(tags.size(), body.tags().size());
        assertEquals(3L, body.likesCount());
        assertEquals(1L, body.commentsCount());

        verify(this.postMapper).toPostView(any(PostUpdateRequest.class));
        verify(this.postCrudService).update(eq(postId), eq(postViewToUpdate));
        verify(this.postMapper).toPostResponse(updatedView);
        verifyNoMoreInteractions(this.postCrudService, this.postMapper);
    }

    @Test
    @DisplayName(value = "Should return 200 with empty body for deletePostById")
    void deletePostById_200() throws Exception {
        long postId = 7L;

        doNothing().when(this.postCrudService).deleteById(postId);

        this.mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(this.postCrudService).deleteById(postId);
        verifyNoInteractions(this.postMapper);
        verifyNoMoreInteractions(this.postCrudService);
    }

    @EnableWebMvc
    @Configuration
    @Import(value = {
            PostCrudController.class,
            GlobalExceptionHandler.class}
    )
    static class PostCrudControllerTestConfiguration {

        @Bean
        PostCrudService postCrudService() {
            return mock(PostCrudService.class);
        }

        @Bean
        PostMapper postMapper() {
            return mock(PostMapper.class);
        }
    }

}