package com.amit.post.repository.jdbc;

import com.amit.post.repository.PostImageRepository;
import com.amit.post.repository.jdbc.sql.PostImageQueryHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPostImageRepository implements PostImageRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<byte[]> rowMapper;

    public JdbcPostImageRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                   @Qualifier(value = "postImageRowMapper") RowMapper<byte[]> rowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<byte[]> findByPostId(long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource("postId", postId);
        List<byte[]> rows = this.namedParameterJdbcTemplate.query(
                PostImageQueryHolder.FIND_POST_BY_POST_ID,
                params,
                this.rowMapper
        );
        return rows.stream().findFirst();
    }

    @Override
    public boolean upsertByPostId(long postId, byte[] data) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("data", data);
        return this.namedParameterJdbcTemplate.update(PostImageQueryHolder.UPSERT_IMAGE_BY_POST_ID, params) > 0;
    }

}
