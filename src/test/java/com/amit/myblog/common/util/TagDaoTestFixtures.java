package com.amit.myblog.common.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class TagDaoTestFixtures {

    public static long insertTagAndReturnId(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String name) {
        String query = """
                INSERT INTO my_blog.tags (name)
                VALUES (:name)
                RETURNING id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", name);
        Long tagId = namedParameterJdbcTemplate.queryForObject(query, params, Long.class);
        if (tagId == null) {
            throw new IllegalStateException("Failed to insert tag");
        }
        return tagId;
    }

    private TagDaoTestFixtures() {
        throw new UnsupportedOperationException();
    }

}
