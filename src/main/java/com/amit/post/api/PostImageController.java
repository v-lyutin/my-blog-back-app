package com.amit.post.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public final class PostImageController {

    // TODO
    @PutMapping(value = "/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPostImage(@PathVariable(value = "postId") long postId,
                                                @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok().build();
    }

    // TODO
    @GetMapping(value = "/{postId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getPostImage(@PathVariable(value = "postId") long postId) {
        return ResponseEntity.ok(null);
    }

}
