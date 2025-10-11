package com.amit.post.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component(value = "postImageRowMapper")
public final class PostImageRowMapper implements RowMapper<byte[]> {

    @Override
    public byte[] mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getBytes("data");
    }

}
