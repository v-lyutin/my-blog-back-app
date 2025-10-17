package com.amit.myblog.post.repository.jdbc.sql;

public final class PostSearchQueryHolder {

    public static final String SEARCH_BY_TITLE_QUERY_AND_TAGS = """
            SELECT p.id, p.title, p.text, p.likes_count, p.comments_count
            FROM my_blog.posts p
            LEFT JOIN my_blog.post_tag pt ON pt.post_id = p.id
            LEFT JOIN my_blog.tags t ON t.id = pt.tag_id
            WHERE (:query IS NULL OR p.title ILIKE :query) AND (:tagCount = 0 OR t.name = ANY(:tagNames))
            GROUP BY p.id
            HAVING (:tagCount = 0 OR COUNT(DISTINCT t.name) = :tagCount)
            ORDER BY p.id DESC
            LIMIT :limit OFFSET :offset
            """;

    public static final String COUNT_BY_TITLE_QUERY_AND_TAGS = """
            SELECT COUNT(*) AS total
            FROM (
                SELECT p.id
                FROM my_blog.posts p
                LEFT JOIN my_blog.post_tag pt ON pt.post_id = p.id
                LEFT JOIN my_blog.tags t ON t.id = pt.tag_id
                WHERE (:query IS NULL OR p.title ILIKE :query) AND (:tagCount = 0 OR t.name = ANY(:tagNames))
                GROUP BY p.id
                HAVING (:tagCount = 0 OR COUNT(DISTINCT t.name) = :tagCount)
            ) s
            """;

    private PostSearchQueryHolder() {
        throw new UnsupportedOperationException();
    }

}
