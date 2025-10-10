package com.amit.post.service;

import com.amit.post.model.PostView;
import com.amit.post.service.exception.PostNotFoundException;

public interface PostCrudService {

    PostView getById(long postId);

    PostView create(PostView postView);

    PostView update(PostView postView);

    void deleteById(long postId);

    void ensurePostExists(long postId) throws PostNotFoundException;

}
