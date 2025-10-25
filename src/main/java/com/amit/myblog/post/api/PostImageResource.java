package com.amit.myblog.post.api;

import com.amit.myblog.post.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public final class PostImageResource {

    private final PostImageService postImageService;

    @Autowired
    public PostImageResource(PostImageService postImageService) {
        this.postImageService = postImageService;
    }

    @PutMapping(value = "/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upsertImageByPostId(@PathVariable(value = "postId") long postId,
                                                    @RequestParam("image") MultipartFile multipartFile) {
        this.postImageService.upsertImageByPostId(postId, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{postId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getImageByPostId(@PathVariable(value = "postId") long postId) {
        Optional<byte[]> bytes = this.postImageService.getImageByPostId(postId);
        return bytes.map(data -> ResponseEntity.ok().body(data))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

}
