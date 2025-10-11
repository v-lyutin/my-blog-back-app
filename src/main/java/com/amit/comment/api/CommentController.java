package com.amit.comment.api;

import com.amit.comment.api.dto.request.CommentCreateRequest;
import com.amit.comment.api.dto.response.CommentResponse;
import com.amit.comment.api.dto.request.CommentUpdateRequest;
import com.amit.comment.api.mapper.CommentMapper;
import com.amit.comment.model.Comment;
import com.amit.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public final class CommentController {

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    @Autowired
    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable(value = "postId") long postId) {
        List<Comment> comments = this.commentService.getAllByPostId(postId);
        return ResponseEntity.ok(comments.stream().map(commentMapper::toCommentResponse).toList());
    }

    @GetMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> getCommentForPostById(@PathVariable(value = "postId") long postId,
                                                                 @PathVariable(value = "commentId") long commentId) {
        Comment comment = this.commentService.getByPostIdAndId(postId, commentId);
        return ResponseEntity.ok(this.commentMapper.toCommentResponse(comment));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> addCommentForPost(@PathVariable(value = "postId") long postId,
                                                             @RequestBody CommentCreateRequest commentCreateRequest) {
        Comment comment = this.commentService.create(
                postId,
                this.commentMapper.toComment(commentCreateRequest)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentMapper.toCommentResponse(comment));
    }

    @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> updateCommentForPostById(@PathVariable(value = "postId") long postId,
                                                                    @PathVariable(value = "commentId") long commentId,
                                                                    @RequestBody CommentUpdateRequest commentUpdateRequest) {
        Comment comment = this.commentService.update(
                postId,
                commentId,
                this.commentMapper.toComment(commentUpdateRequest)
        );
        return ResponseEntity.ok(this.commentMapper.toCommentResponse(comment));
    }

    @DeleteMapping(value = "/{commentId}")
    public ResponseEntity<Void> deleteCommentForPostById(@PathVariable(value = "postId") long postId,
                                                         @PathVariable(value = "commentId") long commentId) {
        this.commentService.deleteByPostIdAndId(postId, commentId);
        return ResponseEntity.ok().build();
    }

}
