package com.amit.post.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostLikeController {

    // TODO
    @PostMapping(value = "/{postId}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> incrementPostLikes(@PathVariable(value = "postId") long postId) {
        return ResponseEntity.ok(null);
    }

}
