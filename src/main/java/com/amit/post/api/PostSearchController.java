package com.amit.post.api;

import com.amit.post.api.dto.response.PostSearchResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostSearchController {

    // TODO
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostSearchResponse> searchPosts(@RequestParam(value = "search") String search,
                                                          @RequestParam(value = "pageNumber") int pageNumber,
                                                          @RequestParam(value = "pageSize") int pageSize) {
        return ResponseEntity.ok(null);
    }

}
