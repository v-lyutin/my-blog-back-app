package com.amit.common.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DaoTestHelper {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DaoTestHelper(NamedParameterJdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = jdbcTemplate;
    }

    public Long insertPost(String title, String text) {
        String query = """
                INSERT INTO my_blog.posts (title, text)
                VALUES (:title, :text)
                RETURNING id
                """;
        return this.namedParameterJdbcTemplate.query(
                query,
                new MapSqlParameterSource().addValue("title", title).addValue("text", text),
                resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                    throw new IllegalStateException("No ID returned by INSERT");
                });
    }

}
