package com.amit.comment.repository.jdbc;

import com.amit.comment.model.Comment;
import com.amit.comment.repository.CommentRepository;
import com.amit.comment.repository.jdbc.sql.CommentQueryHolder;
import com.amit.comment.repository.jdbc.sql.PostCommentCounterQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public final class JdbcCommentRepository implements CommentRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Comment> rowMapper;

    @Autowired
    public JdbcCommentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                 @Qualifier(value = "commentRowMapper") RowMapper<Comment> rowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public List<Comment> findAllByPostId(long postId) {
        return this.namedParameterJdbcTemplate.query(
                CommentQueryHolder.FIND_ALL_BY_POST_ID,
                new MapSqlParameterSource("postId", postId),
                this.rowMapper
        );
    }

    @Override
    public Optional<Comment> findByPostIdAndId(long postId, long commentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("commentId", commentId);
        List<Comment> comments = this.namedParameterJdbcTemplate.query(
                CommentQueryHolder.FIND_BY_POST_AND_ID,
                params,
                this.rowMapper
        );
        return comments.stream().findFirst();
    }

    @Override
    public Comment create(Comment comment) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", comment.getPostId())
                .addValue("text", comment.getText());
        Comment savedComment = this.namedParameterJdbcTemplate.queryForObject(
                CommentQueryHolder.SAVE_COMMENT,
                params,
                this.rowMapper
        );
        this.namedParameterJdbcTemplate.update(
                PostCommentCounterQueryHolder.INCREMENT_COMMENTS_BY_POST_ID,
                new MapSqlParameterSource("postId", comment.getPostId())
        );
        return savedComment;
    }

    @Override
    public Optional<Comment> update(Comment comment) {
        if (comment == null) {
            return Optional.empty();
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", comment.getPostId())
                .addValue("commentId", comment.getId())
                .addValue("text", comment.getText());
        List<Comment> comments = this.namedParameterJdbcTemplate.query(
                CommentQueryHolder.UPDATE_TEXT_BY_POST_ID_AND_ID,
                params,
                this.rowMapper
        );
        return comments.stream().findFirst();
    }

    @Override
    public boolean deleteByPostIdAndId(long postId, long commentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("commentId", commentId);
        int isDeleted = this.namedParameterJdbcTemplate.update(CommentQueryHolder.DELETE_BY_POST_ID_AND_ID, params);
        if (isDeleted == 0) {
            return false;
        }
        this.namedParameterJdbcTemplate.update(
                PostCommentCounterQueryHolder.DECREMENT_COMMENTS_BY_POST_ID,
                new MapSqlParameterSource("postId", postId)
        );
        return true;
    }

}
