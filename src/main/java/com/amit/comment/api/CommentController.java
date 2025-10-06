package com.amit.comment.api;

import com.amit.comment.dto.request.CommentCreateRequest;
import com.amit.comment.dto.response.CommentResponse;
import com.amit.comment.dto.response.CommentUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public final class CommentController {

    // TODO
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable(value = "postId") long postId) {
        return ResponseEntity.ok(null);
    }

    // TODO
    @GetMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> getCommentForPostById(@PathVariable(value = "postId") long postId,
                                                                 @PathVariable(value = "commentId") long commentId) {
        return ResponseEntity.ok(null);
    }

    // TODO
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> addCommentForPost(@PathVariable(value = "postId") long postId,
                                                             @RequestBody CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    // TODO
    @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> updateCommentForPostById(@PathVariable(value = "postId") long postId,
                                                                    @PathVariable(value = "commentId") long commentId,
                                                                    @RequestBody CommentUpdateRequest commentUpdateRequest) {
        return ResponseEntity.ok(null);
    }

    // TODO
    @DeleteMapping(value = "/{commentId}")
    public ResponseEntity<Void> deleteCommentForPostById(@PathVariable(value = "postId") long postId,
                                                         @PathVariable(value = "commentId") long commentId) {
        return ResponseEntity.ok().build();
    }

}
