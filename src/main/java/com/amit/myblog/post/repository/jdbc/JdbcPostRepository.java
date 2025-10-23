package com.amit.myblog.post.repository.jdbc;

import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.repository.PostRepository;
import com.amit.myblog.post.repository.jdbc.mapper.PostRowMapper;
import com.amit.myblog.post.repository.jdbc.sql.PostQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPostRepository implements PostRepository {

    private static final RowMapper<Post> POST_MAPPER = PostRowMapper.rowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcPostRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<Post> findById(long postId) {
        List<Post> posts = this.namedParameterJdbcTemplate.query(
                PostQueryHolder.FIND_BY_ID,
                new MapSqlParameterSource("id", postId),
                POST_MAPPER
        );
        return posts.stream().findFirst();
    }

    @Override
    public Post save(Post post) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", post.getTitle())
                .addValue("text", post.getText());
        return this.namedParameterJdbcTemplate.queryForObject(PostQueryHolder.SAVE, params, POST_MAPPER);
    }

    @Override
    public Optional<Post> update(Post post) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", post.getId())
                .addValue("title", post.getTitle())
                .addValue("text", post.getText());
        List<Post> posts = this.namedParameterJdbcTemplate.query(PostQueryHolder.UPDATE_BY_ID, params, POST_MAPPER);
        return posts.stream().findFirst();
    }

    @Override
    public boolean deleteById(long postId) {
        return this.namedParameterJdbcTemplate.update(PostQueryHolder.DELETE_BY_ID, new MapSqlParameterSource("id", postId)) > 0;
    }

    @Override
    public boolean existsById(long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource("postId", postId);
        Boolean exists = this.namedParameterJdbcTemplate.queryForObject(
                PostQueryHolder.EXISTS_BY_ID,
                params,
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

}
