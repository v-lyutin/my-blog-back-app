package com.amit.post.api;

import com.amit.post.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public final class PostImageController {

    private final PostImageService postImageService;

    @Autowired
    public PostImageController(PostImageService postImageService) {
        this.postImageService = postImageService;
    }

    @PutMapping(value = "/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPostImage(@PathVariable(value = "postId") long postId,
                                                @RequestParam("image") MultipartFile multipartFile) {
        this.postImageService.upsertByPostId(postId, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{postId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getPostImage(@PathVariable(value = "postId") long postId) {
        Optional<byte[]> bytes = this.postImageService.getByPostId(postId);
        return bytes.map(data -> ResponseEntity.ok().body(data))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

}
