package com.amit.myblog.post.repository.jdbc.sql;

public final class PostQueryHolder {

    public static final String FIND_BY_ID = """
            SELECT id, title, text, likes_count, comments_count
            FROM my_blog.posts
            WHERE id = :id
            """;

    public static final String SAVE = """
            INSERT INTO my_blog.posts (title, text)
            VALUES (:title, :text)
            RETURNING id, title, text, likes_count, comments_count
            """;

    public static final String UPDATE_BY_ID = """
            UPDATE my_blog.posts
            SET title = :title, text = :text
            WHERE id = :id
            RETURNING id, title, text, likes_count, comments_count
            """;

    public static final String DELETE_BY_ID = "DELETE FROM my_blog.posts WHERE id = :id";

    public static final String EXISTS_BY_ID = "SELECT EXISTS(SELECT 1 FROM my_blog.posts WHERE id = :postId)";

    private PostQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
