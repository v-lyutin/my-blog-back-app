package com.amit.myblog.post.repository.jdbc.mapper;

import com.amit.myblog.post.model.Post;
import org.springframework.jdbc.core.RowMapper;

public final class PostRowMapper {

    private static final String ID_FIELD = "id";

    private static final String TITLE_FIELD = "title";

    private static final String TEXT_FIELD = "text";

    private static final String LIKES_COUNT_FIELD = "likes_count";

    private static final String COMMENTS_COUNT_FIELD = "comments_count";

    public static RowMapper<Post> rowMapper() {
        return (resultSet, rowNum) -> new Post(
                resultSet.getLong(ID_FIELD),
                resultSet.getString(TITLE_FIELD),
                resultSet.getString(TEXT_FIELD),
                resultSet.getLong(LIKES_COUNT_FIELD),
                resultSet.getLong(COMMENTS_COUNT_FIELD)
        );
    }

    private PostRowMapper() {
        throw new UnsupportedOperationException();
    }

}
