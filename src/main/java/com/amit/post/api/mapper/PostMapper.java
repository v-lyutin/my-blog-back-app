package com.amit.post.api.mapper;

import com.amit.post.api.dto.request.PostCreateRequest;
import com.amit.post.api.dto.request.PostUpdateRequest;
import com.amit.post.api.dto.response.PostResponse;
import com.amit.post.model.PostView;

public interface PostMapper {

    PostView toPostView(PostCreateRequest postCreateRequest);

    PostView toPostView(PostUpdateRequest postUpdateRequest);

    PostResponse toPostResponse(PostView postView);

}
