package com.amit.myblog.comment.repository.jdbc.mapper;

import com.amit.myblog.comment.api.mapper.CommentMapper;
import com.amit.myblog.comment.model.Comment;
import org.springframework.jdbc.core.RowMapper;

public final class CommentRowMapper {

    private static final String ID_FIELD = "id";

    private static final String TEXT_FIELD = "text";

    private static final String POST_ID_FIELD = "post_id";

    public static RowMapper<Comment> rowMapper() {
        return (resultSet, rowNum) -> new Comment(
                resultSet.getLong(ID_FIELD),
                resultSet.getString(TEXT_FIELD),
                resultSet.getLong(POST_ID_FIELD)
        );
    }

    private CommentMapper commentMapper() {
        throw new UnsupportedOperationException();
    }

}
