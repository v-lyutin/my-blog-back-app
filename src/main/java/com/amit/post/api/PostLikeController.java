package com.amit.post.api;

import com.amit.post.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostLikeController {

    private final PostLikeService postLikeService;

    @Autowired
    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping(value = "/{postId}/likes")
    public ResponseEntity<Long> incrementPostLikes(@PathVariable(value = "postId") long postId) {
        long likesCount = this.postLikeService.incrementPostLikes(postId);
        return ResponseEntity.ok(likesCount);
    }

}
