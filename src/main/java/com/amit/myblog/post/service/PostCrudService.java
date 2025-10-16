package com.amit.myblog.post.service;

import com.amit.myblog.post.model.PostView;

public interface PostCrudService {

    PostView getById(long postId);

    PostView create(PostView postView);

    PostView update(long postId, PostView postView);

    void deleteById(long postId);

}
