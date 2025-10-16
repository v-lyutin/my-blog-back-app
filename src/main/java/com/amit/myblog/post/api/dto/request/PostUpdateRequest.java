package com.amit.myblog.post.api.dto.request;

import java.util.Set;

public record PostUpdateRequest(
        long id,
        String title,
        String text,
        Set<String> tags) {
}
