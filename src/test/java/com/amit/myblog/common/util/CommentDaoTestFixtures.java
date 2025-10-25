package com.amit.myblog.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class CommentDaoTestFixtures {

    public static long insertCommentAndReturnId(final JdbcTemplate jdbcTemplate,
                                                final long postId,
                                                final String text) {
        String query = """
                INSERT INTO my_blog.comments (text, post_id)
                VALUES (?, ?)
                RETURNING id
                """;
        final Long id = jdbcTemplate.queryForObject(
                query,
                Long.class,
                text,
                postId
        );
        return id != null ? id : 0L;
    }

    public static long selectCommentsCountByPostId(final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                                   final long postId) {
        final var params = new MapSqlParameterSource("postId", postId);
        String query = "SELECT COUNT(*) FROM my_blog.comments WHERE post_id = :postId";
        final Long count = namedParameterJdbcTemplate.queryForObject(
                query,
                params,
                Long.class
        );
        return count != null ? count : 0L;
    }

    private CommentDaoTestFixtures commentDaoTestFixtures() {
        throw new UnsupportedOperationException();
    }

}
