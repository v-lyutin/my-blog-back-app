package com.amit.myblog.tag.repository.jdbc.mapper;

import com.amit.myblog.tag.model.Tag;
import org.springframework.jdbc.core.RowMapper;

public final class TagRowMapper {

    private static final String ID_FIELD = "id";

    private static final String NAME_FIELD = "name";

    public static RowMapper<Tag> rowMapper() {
        return (resultSet, rowNum) -> new Tag(
                resultSet.getLong(ID_FIELD),
                resultSet.getString(NAME_FIELD)
        );
    }

    private TagRowMapper() {
        throw new UnsupportedOperationException();
    }

}
