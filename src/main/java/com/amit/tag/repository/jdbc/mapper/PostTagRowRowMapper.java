package com.amit.tag.repository.jdbc.mapper;

import com.amit.tag.model.Tag;
import com.amit.tag.repository.jdbc.util.PostTagRow;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component(value = "postTagRowRowMapper")
public final class PostTagRowRowMapper implements RowMapper<PostTagRow> {

    @Override
    public PostTagRow mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        long postId =  resultSet.getLong("post_id");
        long tagId = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Tag tag = new Tag(tagId, name);
        return new PostTagRow(postId, tag);
    }

}
