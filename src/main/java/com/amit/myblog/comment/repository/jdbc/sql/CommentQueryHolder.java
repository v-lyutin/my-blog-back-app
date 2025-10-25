package com.amit.myblog.comment.repository.jdbc.sql;

public final class CommentQueryHolder {

    public static final String FIND_ALL_BY_POST_ID = """
            SELECT id, text, post_id
            FROM my_blog.comments
            WHERE post_id = :postId
            ORDER BY id
            """;

    public static final String FIND_BY_POST_ID_AND_COMMENT_ID = """
            SELECT id, text, post_id
            FROM my_blog.comments
            WHERE post_id = :postId AND id = :commentId
            """;

    public static final String SAVE = """
            INSERT INTO my_blog.comments (text, post_id)
            VALUES (:text, :postId)
            RETURNING id, text, post_id
            """;

    public static final String UPDATE_TEXT_BY_POST_ID_AND_COMMENT_ID = """
            UPDATE my_blog.comments
            SET text = :text
            WHERE post_id = :postId AND id = :commentId
            RETURNING id, text, post_id
            """;

    public static final String DELETE_BY_POST_ID_AND_COMMENT_ID = """
            DELETE FROM my_blog.comments
            WHERE post_id = :postId AND id = :commentId
            """;

    private CommentQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
