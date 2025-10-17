package com.amit.myblog.post.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;

public class PostLikeRowMapper {

    private static final String LIKES_COUNT_FIELD = "likes_count";

    public static RowMapper<Long> rowMapper() {
        return (resultSet, rowNum) -> resultSet.getLong(LIKES_COUNT_FIELD);
    }

    private PostLikeRowMapper() {
        throw new UnsupportedOperationException();
    }

}
