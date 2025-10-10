package com.amit.post.repository.jdbc.sql;

public final class PostQueryHolder {

    public static final String FIND_POST_BY_ID = """
        SELECT id, title, text, likes_count, comments_count
        FROM my_blog.posts
        WHERE id = :id
        """;

    public static final String SAVE_POST = """
        INSERT INTO my_blog.posts (title, text)
        VALUES (:title, :text)
        RETURNING id, title, text, likes_count, comments_count
        """;

    public static final String UPDATE_POST_BY_ID = """
        UPDATE my_blog.posts
        SET title = :title, text = :text
        WHERE id = :id
        RETURNING id, title, text, likes_count, comments_count
        """;

    public static final String DELETE_POST_BY_ID = "DELETE FROM my_blog.posts WHERE id = :id";

    private PostQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
