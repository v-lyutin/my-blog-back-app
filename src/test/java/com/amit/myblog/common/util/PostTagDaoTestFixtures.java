package com.amit.myblog.common.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class PostTagDaoTestFixtures {

    public static void linkTagToPost(NamedParameterJdbcTemplate namedParameterJdbcTemplate, long postId, long tagId) {
        String query = """
                INSERT INTO my_blog.post_tag (post_id, tag_id)
                VALUES (:postId, :tagId)
                ON CONFLICT (post_id, tag_id) DO NOTHING
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("tagId", tagId);
        int isUpdated = namedParameterJdbcTemplate.update(query, params);
        if (isUpdated == 0) {
            throw new IllegalStateException("Failed to link post %d with tag %d".formatted(postId, tagId));
        }
    }

    private PostTagDaoTestFixtures() {
        throw new UnsupportedOperationException();
    }

}
