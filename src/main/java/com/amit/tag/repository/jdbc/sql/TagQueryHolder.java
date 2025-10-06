package com.amit.tag.repository.jdbc.sql;

public final class TagQueryHolder {

    public static final String FIND_TAGS_BY_POST_ID = """
            SELECT tags.id, tags.name
            FROM tags
            JOIN post_tag ON post_tag.tag_id = tags.id
            WHERE post_tag.post_id = :postId
            """;

    public static final String SAVE_MISSING_TAGS = """
            INSERT INTO tags (name)
            SELECT unnest(:names)
            ON CONFLICT (name) DO NOTHING
            RETURNING id, name
            """;

    public static final String FIND_TAGS_BY_NAMES = """
            SELECT id, name
            FROM tags
            WHERE name IN (:names)
            """;

    public static final String ATTACH_TAGS_TO_POST = """
            INSERT INTO post_tag(post_id, tag_id)
            VALUES (:postId, :tagId)
            ON CONFLICT (post_id, tag_id) DO NOTHING
            """;

    public static final String DELETE_TAGS_BY_POST_ID = "DELETE FROM post_tag WHERE post_id = :postId";

    private TagQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
