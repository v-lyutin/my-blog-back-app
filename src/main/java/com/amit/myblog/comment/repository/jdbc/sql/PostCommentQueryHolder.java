package com.amit.myblog.comment.repository.jdbc.sql;

public final class PostCommentQueryHolder {

    public static final String INCREMENT_COMMENTS_COUNT_BY_POST_ID = """
            UPDATE my_blog.posts
            SET comments_count = comments_count + 1
            WHERE id = :postId
            """;

    public static final String DECREMENT_COMMENTS_COUNT_BY_POST_ID = """
            UPDATE my_blog.posts
            SET comments_count = GREATEST(comments_count - 1, 0)
            WHERE id = :postId
            """;

    private PostCommentQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
