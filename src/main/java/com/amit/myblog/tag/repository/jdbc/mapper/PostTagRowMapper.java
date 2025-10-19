package com.amit.myblog.tag.repository.jdbc.mapper;

import com.amit.myblog.tag.model.Tag;
import com.amit.myblog.tag.repository.jdbc.util.PostTag;
import org.springframework.jdbc.core.RowMapper;

public final class PostTagRowMapper {

    private static final String POST_ID_FIELD = "post_id";

    private static final String ID_FIELD = "id";

    private static final String NAME_FIELD = "name";

    public static RowMapper<PostTag> rowMapper() {
        return (resultSet, rowNum) -> new PostTag(
                resultSet.getLong(POST_ID_FIELD),
                new Tag(
                        resultSet.getLong(ID_FIELD),
                        resultSet.getString(NAME_FIELD)
                )
        );
    }

    private PostTagRowMapper() {
        throw new UnsupportedOperationException();
    }

}
