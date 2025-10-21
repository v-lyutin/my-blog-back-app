package com.amit.myblog.post.api;

import com.amit.myblog.post.api.dto.request.PostCreateRequest;
import com.amit.myblog.post.api.dto.request.PostUpdateRequest;
import com.amit.myblog.post.api.dto.response.PostResponse;
import com.amit.myblog.post.api.mapper.PostMapper;
import com.amit.myblog.post.model.PostView;
import com.amit.myblog.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostResource {

    private final PostService postService;

    private final PostMapper postMapper;

    @Autowired
    public PostResource(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping(value = "/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> getPostById(@PathVariable(value = "postId") long postId) {
        PostView postView = this.postService.getPostById(postId);
        return ResponseEntity.ok(this.postMapper.toPostResponse(postView));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> addPost(@RequestBody PostCreateRequest postCreateRequest) {
        PostView postView = this.postService.addPost(this.postMapper.toPostView(postCreateRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.postMapper.toPostResponse(postView));
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> editPost(@PathVariable(value = "postId") long postId,
                                                 @RequestBody PostUpdateRequest postUpdateRequest) {
        PostView postView = this.postService.editPost(postId, this.postMapper.toPostView(postUpdateRequest));
        return ResponseEntity.ok(this.postMapper.toPostResponse(postView));
    }

    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<Void> deletePostById(@PathVariable(value = "postId") long postId) {
        this.postService.deletePostById(postId);
        return ResponseEntity.ok().build();
    }

}
