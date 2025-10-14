package com.amit.post.api;

import com.amit.common.util.Page;
import com.amit.post.api.dto.response.PostSearchResponse;
import com.amit.post.api.mapper.PostMapper;
import com.amit.post.model.PostView;
import com.amit.post.service.PostSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/posts")
public final class PostSearchController {

    private final PostSearchService postSearchService;

    private final PostMapper postMapper;

    @Autowired
    public PostSearchController(PostSearchService postSearchService, PostMapper postMapper) {
        this.postSearchService = postSearchService;
        this.postMapper = postMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostSearchResponse> searchPosts(@RequestParam(value = "search") String search,
                                                          @RequestParam(value = "pageNumber") int pageNumber,
                                                          @RequestParam(value = "pageSize") int pageSize) {
        Page<PostView> posts = this.postSearchService.search(search, pageNumber, pageSize);
        return ResponseEntity.ok(this.postMapper.toPostSearchResponse(posts));
    }

}
