package com.amit.myblog.post.repository.jdbc;

import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.repository.PostSearchRepository;
import com.amit.myblog.post.repository.jdbc.mapper.PostRowMapper;
import com.amit.myblog.post.repository.jdbc.sql.PostSearchQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Set;

@Repository
public final class JdbcPostSearchRepository implements PostSearchRepository {

    private static final RowMapper<Post> POST_MAPPER = PostRowMapper.rowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcPostSearchRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Post> search(String titleQuery, Set<String> tagNames, int limit, int offset) {
        MapSqlParameterSource params = this.buildParams(titleQuery, tagNames)
                .addValue("limit",  limit)
                .addValue("offset", offset);
        return this.namedParameterJdbcTemplate.query(PostSearchQueryHolder.SEARCH_BY_TITLE_QUERY_AND_TAGS, params, POST_MAPPER);
    }

    @Override
    public long count(String titleQuery, Set<String> tagNames) {
        MapSqlParameterSource params = this.buildParams(titleQuery, tagNames);
        Long total = this.namedParameterJdbcTemplate.queryForObject(PostSearchQueryHolder.COUNT_BY_TITLE_QUERY_AND_TAGS, params, Long.class);
        return total != null ? total : 0L;
    }

    private MapSqlParameterSource buildParams(String titleQuery, Set<String> tagNames) {
        String query = (titleQuery == null || titleQuery.isBlank())
                ? null
                : "%" + titleQuery + "%";
        String[] tags = (tagNames == null) ? new String[0] : tagNames.toArray(String[]::new);
        return new MapSqlParameterSource()
                .addValue("query", query, Types.VARCHAR)
                .addValue("tagCount", tags.length)
                .addValue("tagNames", tags);
    }

}
