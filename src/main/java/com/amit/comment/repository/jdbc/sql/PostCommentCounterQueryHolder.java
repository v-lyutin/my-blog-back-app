package com.amit.comment.repository.jdbc.sql;

public final class PostCommentCounterQueryHolder {

    public static final String INCREMENT_COMMENTS_BY_POST_ID = """
        UPDATE posts
        SET comments_count = comments_count + 1
        WHERE id = :postId
        """;

    public static final String DECREMENT_COMMENTS_BY_POST_ID = """
        UPDATE posts
        SET comments_count = GREATEST(comments_count - 1, 0)
        WHERE id = :postId
        """;

    private PostCommentCounterQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
