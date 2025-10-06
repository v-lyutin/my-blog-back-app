package com.amit.post.repository.jdbc;

import com.amit.post.model.Post;
import com.amit.post.repository.PostSearchRepository;
import com.amit.post.repository.jdbc.sql.PostSearchQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public final class JdbcSearchRepository implements PostSearchRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Post> postRowMapper;

    @Autowired
    public JdbcSearchRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                @Qualifier("postRowMapper") RowMapper<Post> postRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.postRowMapper = postRowMapper;
    }

    @Override
    public List<Post> search(String titleQuery, Set<String> tagNames, int limit, int offset) {
        MapSqlParameterSource params = this.buildParams(titleQuery, tagNames)
                .addValue("limit",  limit)
                .addValue("offset", offset);
        return this.namedParameterJdbcTemplate.query(PostSearchQueryHolder.SEARCH_POSTS_BY_TITLE_QUERY_AND_TAGS, params, this.postRowMapper);
    }

    @Override
    public long count(String titleQuery, Set<String> tagNames) {
        MapSqlParameterSource params = this.buildParams(titleQuery, tagNames);
        Long total = this.namedParameterJdbcTemplate.queryForObject(PostSearchQueryHolder.COUNT_POSTS_BY_TITLE_QUERY_AND_TAGS, params, Long.class);
        return total != null ? total : 0L;
    }

    private MapSqlParameterSource buildParams(String titleQuery, Set<String> tagNames) {
        String query = (titleQuery == null || titleQuery.isBlank())
                ? null
                : "%" + titleQuery + "%";
        Set<String> tags = (tagNames == null) ? Set.of() : tagNames;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("query", query)
                .addValue("tagCount", tags.size());
        if (tags.isEmpty()) {
            params.addValue("tagNames", new String[0]);
        } else {
            params.addValue("tagNames", tags);
        }
        return params;
    }

}
