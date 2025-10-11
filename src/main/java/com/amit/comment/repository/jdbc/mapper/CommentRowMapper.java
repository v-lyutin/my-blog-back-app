package com.amit.comment.repository.jdbc.mapper;

import com.amit.comment.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component(value = "commentRowMapper")
public final class CommentRowMapper implements RowMapper<Comment> {

    @Override
    public Comment mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setText(resultSet.getString("text"));
        comment.setPostId(resultSet.getLong("post_id"));
        return comment;
    }

}
