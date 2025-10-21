package com.amit.myblog.post.service;

import com.amit.myblog.post.model.PostView;

public interface PostService {

    PostView getPostById(long postId);

    PostView addPost(PostView postView);

    PostView editPost(long postId, PostView postView);

    void deletePostById(long postId);

}
