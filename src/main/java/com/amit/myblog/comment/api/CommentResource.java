package com.amit.myblog.comment.api;

import com.amit.myblog.comment.api.dto.request.CommentCreateRequest;
import com.amit.myblog.comment.api.dto.request.CommentUpdateRequest;
import com.amit.myblog.comment.api.dto.response.CommentResponse;
import com.amit.myblog.comment.api.mapper.CommentMapper;
import com.amit.myblog.comment.model.Comment;
import com.amit.myblog.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public final class CommentResource {

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    @Autowired
    public CommentResource(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentResponse>> getAllCommentsByPostId(@PathVariable(value = "postId") long postId) {
        List<Comment> comments = this.commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(comments.stream().map(commentMapper::toCommentResponse).toList());
    }

    @GetMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> getCommentByPostIdAndCommentId(@PathVariable(value = "postId") long postId,
                                                                          @PathVariable(value = "commentId") long commentId) {
        Comment comment = this.commentService.getCommentByPostIdAndCommentId(postId, commentId);
        return ResponseEntity.ok(this.commentMapper.toCommentResponse(comment));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> addComment(@PathVariable(value = "postId") long postId,
                                                      @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        Comment comment = this.commentService.addComment(
                postId,
                this.commentMapper.toComment(commentCreateRequest)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentMapper.toCommentResponse(comment));
    }

    @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> editComment(@PathVariable(value = "postId") long postId,
                                                       @PathVariable(value = "commentId") long commentId,
                                                       @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        Comment comment = this.commentService.editComment(
                postId,
                commentId,
                this.commentMapper.toComment(commentUpdateRequest)
        );
        return ResponseEntity.ok(this.commentMapper.toCommentResponse(comment));
    }

    @DeleteMapping(value = "/{commentId}")
    public ResponseEntity<Void> deleteCommentByPostIdAndCommentId(@PathVariable(value = "postId") long postId,
                                                                  @PathVariable(value = "commentId") long commentId) {
        this.commentService.deleteCommentByPostIdAndCommentId(postId, commentId);
        return ResponseEntity.ok().build();
    }

}
