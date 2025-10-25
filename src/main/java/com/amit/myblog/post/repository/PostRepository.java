package com.amit.myblog.post.repository;

import com.amit.myblog.post.model.Post;

import java.util.Optional;

public interface PostRepository {

    Optional<Post> findById(long postId);

    Post save(Post post);

    Optional<Post> update(Post post);

    boolean deleteById(long postId);

    boolean existsById(long postId);

}
