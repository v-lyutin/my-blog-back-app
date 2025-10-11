package com.amit.post.repository.jdbc;

import com.amit.post.model.Post;
import com.amit.post.repository.PostCrudRepository;
import com.amit.post.repository.jdbc.sql.PostQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public final class JdbcPostCrudRepository implements PostCrudRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Post> rowMapper;

    @Autowired
    public JdbcPostCrudRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                  @Qualifier(value = "postRowMapper") RowMapper<Post> rowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<Post> findById(long postId) {
        List<Post> posts = this.namedParameterJdbcTemplate.query(
                PostQueryHolder.FIND_POST_BY_ID,
                new MapSqlParameterSource("id", postId),
                this.rowMapper
        );
        return posts.stream().findFirst();
    }

    @Override
    public Post create(Post post) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", post.getTitle())
                .addValue("text", post.getText());
        return this.namedParameterJdbcTemplate.queryForObject(PostQueryHolder.SAVE_POST, params, this.rowMapper);
    }

    @Override
    public Optional<Post> update(Post post) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", post.getId())
                .addValue("title", post.getTitle())
                .addValue("text", post.getText());
        List<Post> posts = this.namedParameterJdbcTemplate.query(PostQueryHolder.UPDATE_POST_BY_ID, params, this.rowMapper);
        return posts.stream().findFirst();
    }

    @Override
    public boolean deleteById(long postId) {
        return this.namedParameterJdbcTemplate.update(PostQueryHolder.DELETE_POST_BY_ID, new MapSqlParameterSource("id", postId)) > 0;
    }

    @Override
    public boolean existsById(long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource("postId", postId);
        Boolean exists = this.namedParameterJdbcTemplate.queryForObject(
                PostQueryHolder.EXISTS_POST_BY_ID,
                params,
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

}
