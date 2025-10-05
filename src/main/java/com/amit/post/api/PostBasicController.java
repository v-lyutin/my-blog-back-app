package com.amit.post.api;

import com.amit.post.dto.request.PostCreateRequest;
import com.amit.post.dto.response.PostResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostBasicController {

    // TODO
    @GetMapping(value = "/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> getPostById(@PathVariable(value = "postId") long postId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    // TODO
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest postCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    // TODO
    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> updatePostById(@PathVariable(value = "postId") long postId) {
        return ResponseEntity.ok(null);
    }

    // TODO
    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<Void> deletePostById(@PathVariable(value = "postId") long postId) {
        return ResponseEntity.ok().build();
    }

}
