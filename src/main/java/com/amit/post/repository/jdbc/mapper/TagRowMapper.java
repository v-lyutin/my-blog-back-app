package com.amit.post.repository.jdbc.mapper;

import com.amit.post.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component(value = "tagRowMapper")
public final class TagRowMapper implements RowMapper<Tag> {

    @Override
    public Tag mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(resultSet.getLong("id"));
        tag.setName(resultSet.getString("name"));
        return tag;
    }

}
