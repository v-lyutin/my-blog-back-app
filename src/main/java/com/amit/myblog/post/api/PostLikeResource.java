package com.amit.myblog.post.api;

import com.amit.myblog.post.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostLikeResource {

    private final PostLikeService postLikeService;

    @Autowired
    public PostLikeResource(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping(value = "/{postId}/likes")
    public ResponseEntity<Long> incrementPostLikes(@PathVariable(value = "postId") long postId) {
        long likesCount = this.postLikeService.incrementPostLikes(postId);
        return ResponseEntity.ok(likesCount);
    }

}
