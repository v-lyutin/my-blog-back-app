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

    public long insertTag(String name) {
        String query = """
                INSERT INTO my_blog.tags (name)
                VALUES (:name)
                RETURNING id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);

        Long tagId = this.namedParameterJdbcTemplate.queryForObject(query, params, Long.class);
        if (tagId == null) {
            throw new IllegalStateException("Failed to insert tag");
        }
        return tagId;
    }

    public void linkTag(long postId, long tagId) {
        String query = """
                INSERT INTO my_blog.post_tag (post_id, tag_id)
                VALUES (:postId, :tagId)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("tagId", tagId);

        int isUpdated = this.namedParameterJdbcTemplate.update(query, params);
        if (isUpdated == 0) {
            throw new IllegalStateException("Failed to link post %d with tag %d".formatted(postId, tagId));
        }
    }

}
