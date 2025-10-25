package com.amit.myblog.post.repository.jdbc;

import com.amit.myblog.post.repository.PostImageRepository;
import com.amit.myblog.post.repository.jdbc.mapper.PostImageRowMapper;
import com.amit.myblog.post.repository.jdbc.sql.PostImageQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPostImageRepository implements PostImageRepository {

    private static final RowMapper<byte[]> DATA_MAPPER = PostImageRowMapper.rowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcPostImageRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<byte[]> findByPostId(long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource("postId", postId);
        List<byte[]> rows = this.namedParameterJdbcTemplate.query(
                PostImageQueryHolder.FIND_BY_POST_ID,
                params,
                DATA_MAPPER
        );
        return rows.stream().findFirst();
    }

    @Override
    public boolean upsertByPostId(long postId, byte[] data) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("data", data);
        return this.namedParameterJdbcTemplate.update(PostImageQueryHolder.UPSERT_BY_POST_ID, params) > 0;
    }

}
