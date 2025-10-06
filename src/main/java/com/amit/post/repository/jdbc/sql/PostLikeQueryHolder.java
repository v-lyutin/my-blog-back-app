package com.amit.post.repository.jdbc.sql;

public final class PostLikeQueryHolder {

    public static final String INCREMENT_POST_LIKES = """
        UPDATE posts
        SET likes_count = likes_count + 1
        WHERE id = :postId
        RETURNING likes_count
        """;

    private PostLikeQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
