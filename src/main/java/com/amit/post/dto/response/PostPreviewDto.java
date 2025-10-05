package com.amit.post.dto.response;

import java.util.Set;

public record PostPreviewDto(
        long id,
        String title,
        String text,
        Set<String> tags,
        long likesCount,
        long commentsCount) {
}
