package com.amit.myblog.comment.repository.jdbc;

import com.amit.myblog.comment.model.Comment;
import com.amit.myblog.comment.repository.CommentRepository;
import com.amit.myblog.comment.repository.jdbc.mapper.CommentRowMapper;
import com.amit.myblog.comment.repository.jdbc.sql.CommentQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCommentRepository implements CommentRepository {

    private static final RowMapper<Comment> COMMENT_MAPPER = CommentRowMapper.rowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcCommentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Comment> findAllByPostId(long postId) {
        return this.namedParameterJdbcTemplate.query(
                CommentQueryHolder.FIND_ALL_BY_POST_ID,
                new MapSqlParameterSource("postId", postId),
                COMMENT_MAPPER
        );
    }

    @Override
    public Optional<Comment> findByPostIdAndId(long postId, long commentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("commentId", commentId);
        List<Comment> comments = this.namedParameterJdbcTemplate.query(
                CommentQueryHolder.FIND_BY_POST_ID_AND_COMMENT_ID,
                params,
                COMMENT_MAPPER
        );
        return comments.stream().findFirst();
    }

    @Override
    public Comment save(Comment comment) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", comment.getPostId())
                .addValue("text", comment.getText());
        return this.namedParameterJdbcTemplate.queryForObject(
                CommentQueryHolder.SAVE,
                params,
                COMMENT_MAPPER
        );
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
                CommentQueryHolder.UPDATE_TEXT_BY_POST_ID_AND_COMMENT_ID,
                params,
                COMMENT_MAPPER
        );
        return comments.stream().findFirst();
    }

    @Override
    public boolean deleteByPostIdAndId(long postId, long commentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("commentId", commentId);
        return this.namedParameterJdbcTemplate.update(CommentQueryHolder.DELETE_BY_POST_ID_AND_COMMENT_ID, params) > 0;
    }

}
