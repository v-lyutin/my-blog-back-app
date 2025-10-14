package com.amit.post.repository.jdbc;

import com.amit.post.repository.PostLikeRepository;
import com.amit.post.repository.jdbc.sql.PostLikeQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.OptionalLong;

@Repository
public final class JdbcPostLikeRepository implements PostLikeRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Long> rowMapper;

    @Autowired
    public JdbcPostLikeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                  @Qualifier(value = "postLikeRowMapper") RowMapper<Long> rowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public OptionalLong incrementPostLikes(long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource("postId", postId);
        List<Long> likesCount = this.namedParameterJdbcTemplate.query(
                PostLikeQueryHolder.INCREMENT_POST_LIKES,
                params,
                this.rowMapper
        );
        if (likesCount.isEmpty()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(likesCount.getFirst());
    }

}
