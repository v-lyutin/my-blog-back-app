package com.amit.post.api.dto.response;

import java.util.Set;

public record PostResponse(
        long id,
        String title,
        String text,
        Set<String> tags,
        long likesCount,
        long commentsCount) {
}
