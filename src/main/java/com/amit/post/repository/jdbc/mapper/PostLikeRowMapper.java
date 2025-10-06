package com.amit.post.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component(value = "postLikeRowMapper")
public final class PostLikeRowMapper implements RowMapper<Long> {

    @Override
    public Long mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("likes_count");
    }

}
