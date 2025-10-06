package com.amit.post.repository.jdbc.mapper;

import com.amit.post.model.Post;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component(value = "postRowMapper")
public final class PostRowMapper implements RowMapper<Post> {

    @Override
    public Post mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setTitle(resultSet.getString("title"));
        post.setText(resultSet.getString("text"));
        post.setLikesCount(resultSet.getLong("likes_count"));
        post.setCommentsCount(resultSet.getLong("comments_count"));
        return post;
    }

}
