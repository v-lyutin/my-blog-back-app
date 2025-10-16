package com.amit.myblog.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class PostDaoTestFixtures {

    public static long insertPostAndReturnId(JdbcTemplate jdbcTemplate, String title, String text) {
        String query = """
                INSERT INTO my_blog.posts (title, text)
                VALUES (?, ?)
                RETURNING id
                """;
        final Long postId = jdbcTemplate.queryForObject(
                query,
                Long.class,
                title,
                text
        );
        return postId != null ? postId : 0L;
    }

    public static Long selectCommentsCountByPostId(NamedParameterJdbcTemplate namedParameterJdbcTemplate, long postId) {
        final String sql = "SELECT comments_count FROM my_blog.posts WHERE id = :postId";
        return namedParameterJdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("postId", postId),
                Long.class
        );
    }

    private PostDaoTestFixtures postDaoTestFixtures() {
        throw new UnsupportedOperationException();
    }

}
