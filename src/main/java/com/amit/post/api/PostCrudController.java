package com.amit.post.api;

import com.amit.post.api.dto.request.PostCreateRequest;
import com.amit.post.api.dto.request.PostUpdateRequest;
import com.amit.post.api.dto.response.PostResponse;
import com.amit.post.api.mapper.PostMapper;
import com.amit.post.model.PostView;
import com.amit.post.service.PostCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostCrudController {

    private final PostCrudService postCrudService;

    private final PostMapper postMapper;

    @Autowired
    public PostCrudController(PostCrudService postCrudService, PostMapper postMapper) {
        this.postCrudService = postCrudService;
        this.postMapper = postMapper;
    }

    @GetMapping(value = "/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> getPostById(@PathVariable(value = "postId") long postId) {
        PostView postView = this.postCrudService.getById(postId);
        return ResponseEntity.ok(this.postMapper.toPostResponse(postView));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest postCreateRequest) {
        PostView postView = this.postCrudService.create(this.postMapper.toPostView(postCreateRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.postMapper.toPostResponse(postView));
    }

    // FIXME
    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> updatePostById(@PathVariable(value = "postId") long postId,
                                                       @RequestBody PostUpdateRequest postUpdateRequest) {
        PostView postView = this.postCrudService.update(this.postMapper.toPostView(postUpdateRequest));
        return ResponseEntity.ok(this.postMapper.toPostResponse(postView));
    }

    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<Void> deletePostById(@PathVariable(value = "postId") long postId) {
        this.postCrudService.deleteById(postId);
        return ResponseEntity.ok().build();
    }

}
