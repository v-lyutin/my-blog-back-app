package com.amit.myblog.post.repository.jdbc.sql;

public final class PostImageQueryHolder {

    public static final String FIND_BY_POST_ID = """
            SELECT data
            FROM my_blog.post_images
            WHERE post_id = :postId
            """;

    public static final String UPSERT_BY_POST_ID = """
            INSERT INTO my_blog.post_images (post_id, data)
            VALUES (:postId, :data)
            ON CONFLICT (post_id) DO UPDATE
            SET data = EXCLUDED.data
            """;

    private PostImageQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
