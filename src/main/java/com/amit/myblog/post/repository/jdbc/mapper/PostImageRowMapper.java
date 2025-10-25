package com.amit.myblog.post.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;

public final class PostImageRowMapper {

    private static final String DATA_FIELD = "data";

    public static RowMapper<byte[]> rowMapper() {
        return (resultSet, rowNum) -> resultSet.getBytes(DATA_FIELD);
    }

    private PostImageRowMapper() {
        throw new UnsupportedOperationException();
    }

}
