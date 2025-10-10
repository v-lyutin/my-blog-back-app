package com.amit.post.repository;

import com.amit.post.model.Post;

import java.util.Optional;

public interface PostCrudRepository {

    Optional<Post> findById(long postId);

    Post create(Post post);

    Optional<Post> update(Post post);

    boolean deleteById(long postId);

    boolean existsById(long postId);

}
