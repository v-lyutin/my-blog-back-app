package com.amit.myblog.post.api.mapper;

import com.amit.myblog.common.util.Page;
import com.amit.myblog.post.api.dto.request.PostCreateRequest;
import com.amit.myblog.post.api.dto.request.PostUpdateRequest;
import com.amit.myblog.post.api.dto.response.PostPreviewDto;
import com.amit.myblog.post.api.dto.response.PostResponse;
import com.amit.myblog.post.api.dto.response.PostSearchResponse;
import com.amit.myblog.post.api.util.TextTruncator;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.model.PostView;
import org.springframework.stereotype.Component;

import java.util.List;

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
                new Post(postUpdateRequest.id(), postUpdateRequest.title(), postUpdateRequest.text()),
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

    @Override
    public PostSearchResponse toPostSearchResponse(Page<PostView> posts) {
        List<PostPreviewDto> postPreviewDtos = posts.items().stream().map(this::toPostPreviewDto).toList();
        return new PostSearchResponse(
                postPreviewDtos,
                posts.hasPrev(),
                posts.hasNext(),
                posts.lastPage()
        );
    }

    private PostPreviewDto toPostPreviewDto(PostView postView) {
        return new PostPreviewDto(
                postView.post().getId(),
                postView.post().getTitle(),
                TextTruncator.truncate(postView.post().getText()),
                postView.tags(),
                postView.post().getLikesCount(),
                postView.post().getCommentsCount()
        );
    }

}
