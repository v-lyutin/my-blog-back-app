package com.amit.myblog.post.api.mapper;

import com.amit.myblog.common.util.Page;
import com.amit.myblog.post.api.dto.request.PostCreateRequest;
import com.amit.myblog.post.api.dto.request.PostUpdateRequest;
import com.amit.myblog.post.api.dto.response.PostResponse;
import com.amit.myblog.post.api.dto.response.PostSearchResponse;
import com.amit.myblog.post.model.PostView;

public interface PostMapper {

    PostView toPostView(PostCreateRequest postCreateRequest);

    PostView toPostView(PostUpdateRequest postUpdateRequest);

    PostResponse toPostResponse(PostView postView);

    PostSearchResponse toPostSearchResponse(Page<PostView> posts);

}
