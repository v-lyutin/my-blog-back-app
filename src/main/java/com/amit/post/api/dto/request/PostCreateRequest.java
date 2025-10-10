package com.amit.post.api.dto.request;

import java.util.Set;

public record PostCreateRequest(
        String title,
        String text,
        Set<String> tags) {
}
