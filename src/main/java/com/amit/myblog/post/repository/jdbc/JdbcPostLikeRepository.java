package com.amit.myblog.post.repository.jdbc;

import com.amit.myblog.post.repository.PostLikeRepository;
import com.amit.myblog.post.repository.jdbc.mapper.PostLikeRowMapper;
import com.amit.myblog.post.repository.jdbc.sql.PostLikeQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.OptionalLong;

@Repository
public class JdbcPostLikeRepository implements PostLikeRepository {

    private static final RowMapper<Long> POST_LIKE_MAPPER = PostLikeRowMapper.rowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcPostLikeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public OptionalLong incrementPostLikes(long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource("postId", postId);
        List<Long> likesCount = this.namedParameterJdbcTemplate.query(
                PostLikeQueryHolder.INCREMENT_POST_LIKES,
                params,
                POST_LIKE_MAPPER
        );
        if (likesCount.isEmpty()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(likesCount.getFirst());
    }

}
