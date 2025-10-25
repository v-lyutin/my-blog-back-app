package com.amit.myblog.post.repository;

import com.amit.myblog.post.model.Post;

import java.util.List;
import java.util.Set;

public interface PostSearchRepository {

    List<Post> search(String titleQuery, Set<String> tagNames, int limit, int offset);

    long count(String titleQuery, Set<String> tagNames);

}
