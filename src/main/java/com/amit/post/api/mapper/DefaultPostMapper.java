package com.amit.post.api.mapper;

import com.amit.post.api.dto.request.PostCreateRequest;
import com.amit.post.api.dto.request.PostUpdateRequest;
import com.amit.post.api.dto.response.PostResponse;
import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import org.springframework.stereotype.Component;

@Component
public final class DefaultPostMapper implements PostMapper {

    @Override
    public PostView toPostView(PostCreateRequest postCreateRequest) {
        return new PostView(
                new Post(postCreateRequest.title(), postCreateRequest.text()),
                postCreateRequest.tags()
        );
    }

    @Override
    public PostView toPostView(PostUpdateRequest postUpdateRequest) {
        return new PostView(
                new Post(postUpdateRequest.title(), postUpdateRequest.text()),
                postUpdateRequest.tags()
        );
    }

    @Override
    public PostResponse toPostResponse(PostView postView) {
        return new PostResponse(
                postView.post().getId(),
                postView.post().getTitle(),
                postView.post().getText(),
                postView.tags(),
                postView.post().getLikesCount(),
                postView.post().getCommentsCount()
        );
    }

}
