package com.amit.post.service;

import com.amit.post.model.Post;
import com.amit.post.model.PostView;

import java.util.Collection;

public interface PostCrudService {

    PostView getById(long postId);

    PostView create(Post post, Collection<String> tagNames);

    PostView update(Post post, Collection<String> tagNames);

    boolean deleteById(long postId);

}
