package com.amit.myblog.comment.repository.jdbc;

import com.amit.myblog.comment.repository.PostCommentCounterRepository;
import com.amit.myblog.comment.repository.jdbc.sql.PostCommentQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPostCommentCounterRepository implements PostCommentCounterRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcPostCommentCounterRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void incrementCommentsCountByPostId(long postId) {
        this.namedParameterJdbcTemplate.update(
                PostCommentQueryHolder.INCREMENT_COMMENTS_COUNT_BY_POST_ID,
                new MapSqlParameterSource("postId", postId)
        );
    }

    @Override
    public void decrementCommentsCountByPostId(long postId) {
        this.namedParameterJdbcTemplate.update(
                PostCommentQueryHolder.DECREMENT_COMMENTS_COUNT_BY_POST_ID,
                new MapSqlParameterSource("postId", postId)
        );
    }

}
