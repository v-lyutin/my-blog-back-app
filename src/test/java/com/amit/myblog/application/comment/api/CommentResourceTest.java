package com.amit.myblog.application.comment.api;

import com.amit.myblog.comment.api.CommentResource;
import com.amit.myblog.comment.api.dto.request.CommentCreateRequest;
import com.amit.myblog.comment.api.dto.request.CommentUpdateRequest;
import com.amit.myblog.comment.api.dto.response.CommentResponse;
import com.amit.myblog.comment.api.mapper.CommentMapper;
import com.amit.myblog.comment.model.Comment;
import com.amit.myblog.comment.service.CommentService;
import com.amit.myblog.common.api.GlobalExceptionHandler;
import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentResource.class)
@Import(GlobalExceptionHandler.class)
class CommentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CommentMapper commentMapper;

    @Test
    @DisplayName(value = "Should return 200 and list of comments for post")
    void getAllCommentsByPostId_shouldReturnOkWithComments() throws Exception {
        long postId = 10L;

        Comment comment1 = new Comment(1L, "Comment 1", postId);
        Comment comment2 = new Comment(2L, "Comment 2", postId);
        CommentResponse commentResponse1 = new CommentResponse(1L, "Comment 1", postId);
        CommentResponse commentResponse2 = new CommentResponse(2L, "Comment 2", postId);

        when(this.commentService.getAllCommentsByPostId(postId)).thenReturn(List.of(comment1, comment2));
        when(this.commentMapper.toCommentResponse(comment1)).thenReturn(commentResponse1);
        when(this.commentMapper.toCommentResponse(comment2)).thenReturn(commentResponse2);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("Comment 1"))
                .andExpect(jsonPath("$[0].postId").value(postId))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(this.commentService).getAllCommentsByPostId(postId);
        verify(this.commentMapper).toCommentResponse(comment1);
        verify(this.commentMapper).toCommentResponse(comment2);

        verifyNoMoreInteractions(commentService, commentMapper);
    }

    @Test
    @DisplayName(value = "Should return 200 and comment by postId and commentId")
    void getCommentByPostIdAndCommentId_shouldReturnOkWithComment() throws Exception {
        long postId = 5L, commentId = 7L;

        Comment comment = new Comment(commentId, "Comment", postId);
        CommentResponse commentResponse = new CommentResponse(commentId, "Comment", postId);

        when(this.commentService.getCommentByPostIdAndCommentId(postId, commentId)).thenReturn(comment);
        when(this.commentMapper.toCommentResponse(comment)).thenReturn(commentResponse);

        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", postId, commentId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.text").value("Comment"))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(this.commentService).getCommentByPostIdAndCommentId(postId, commentId);
        verify(this.commentMapper).toCommentResponse(comment);
        verifyNoMoreInteractions(this.commentService, this.commentMapper);
    }

    @Test
    @DisplayName(value = "Should return 404 when comment not found")
    void getCommentByPostIdAndCommentId_shouldReturnNotFound() throws Exception {
        long postId = 5L, commentId = 999L;

        when(this.commentService.getCommentByPostIdAndCommentId(postId, commentId))
                .thenThrow(new ResourceNotFoundException("Comment with ID %d for post with ID %d not found.".formatted(commentId, postId)));

        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", postId, commentId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Comment with ID " + commentId)))
                .andExpect(jsonPath("$.path").value("/api/posts/" + postId + "/comments/" + commentId));

        verify(this.commentService).getCommentByPostIdAndCommentId(postId, commentId);
        verifyNoInteractions(this.commentMapper);
        verifyNoMoreInteractions(this.commentService);
    }

    @Test
    @DisplayName(value = "Should create comment and return 201 with body")
    void addComment_shouldCreateAndReturnCreated() throws Exception {
        long postId = 3L;

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("New comment", postId);
        Comment comment = new Comment(0L, "New comment", postId);
        Comment savedComment = new Comment(42L, "New comment", postId);
        CommentResponse commentResponse = new CommentResponse(42L, "New comment", postId);

        when(this.commentMapper.toComment(commentCreateRequest)).thenReturn(comment);
        when(this.commentService.addComment(postId, comment)).thenReturn(savedComment);
        when(this.commentMapper.toCommentResponse(savedComment)).thenReturn(commentResponse);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.text").value("New comment"))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(this.commentMapper).toComment(commentCreateRequest);
        verify(this.commentService).addComment(postId, comment);
        verify(this.commentMapper).toCommentResponse(savedComment);
        verifyNoMoreInteractions(this.commentService, this.commentMapper);
    }

    @Test
    @DisplayName(value = "Should return 400 with validation errors when create request invalid")
    void addComment_shouldReturnBadRequestOnValidationError() throws Exception {
        long postId = 3L;
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("   ", postId);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/posts/" + postId + "/comments"))
                .andExpect(jsonPath("$.errors", not(empty())));

        verifyNoInteractions(this.commentService, this.commentMapper);
    }

    @Test
    @DisplayName(value = "Should update comment and return 200 with body")
    void editComment_shouldUpdateAndReturnOk() throws Exception {
        long postId = 9L, commentId = 100L;

        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(commentId, "Edited", postId);
        Comment comment = new Comment(commentId, "Edited", postId);
        Comment updatedComment = new Comment(commentId, "Edited", postId);
        CommentResponse commentResponse = new CommentResponse(commentId, "Edited", postId);

        when(this.commentMapper.toComment(commentUpdateRequest)).thenReturn(comment);
        when(this.commentService.editComment(postId, commentId, comment)).thenReturn(updatedComment);
        when(this.commentMapper.toCommentResponse(updatedComment)).thenReturn(commentResponse);

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.text").value("Edited"))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(this.commentMapper).toComment(commentUpdateRequest);
        verify(this.commentService).editComment(postId, commentId, comment);
        verify(this.commentMapper).toCommentResponse(updatedComment);
        verifyNoMoreInteractions(this.commentService, this.commentMapper);
    }

    @Test
    @DisplayName(value = "Should return 400 with validation errors when update request invalid")
    void editComment_shouldReturnBadRequestOnValidationError() throws Exception {
        long postId = 9L, commentId = 100L;
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(commentId, "  ", postId);

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(commentUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/posts/" + postId + "/comments/" + commentId))
                .andExpect(jsonPath("$.errors", not(empty())));

        verifyNoInteractions(this.commentService, this.commentMapper);
    }

    @Test
    @DisplayName(value = "Should delete comment and return 200 with empty body")
    void deleteCommentByPostIdAndCommentId_shouldReturnOk() throws Exception {
        long postId = 2L, commentId = 33L;

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));

        verify(this.commentService).deleteCommentByPostIdAndCommentId(postId, commentId);
        verifyNoMoreInteractions(this.commentService, this.commentMapper);
    }

    @Test
    @DisplayName(value = "Should return 404 when delete target not found")
    void deleteCommentByPostIdAndCommentId_shouldReturnNotFound() throws Exception {
        long postId = 2L, commentId = 404L;

        doThrow(new ResourceNotFoundException("Comment with ID %d for post with ID %d not found.".formatted(commentId, postId)))
                .when(this.commentService)
                .deleteCommentByPostIdAndCommentId(postId, commentId);

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Comment with ID " + commentId)))
                .andExpect(jsonPath("$.path").value("/api/posts/" + postId + "/comments/" + commentId));

        verify(this.commentService).deleteCommentByPostIdAndCommentId(postId, commentId);
        verifyNoInteractions(this.commentMapper);
        verifyNoMoreInteractions(this.commentService);
    }

}