package com.amit.post.dto.request;

import java.util.Set;

public record PostUpdateRequest(
        long id,
        String title,
        String text,
        Set<String> tags) {
}
