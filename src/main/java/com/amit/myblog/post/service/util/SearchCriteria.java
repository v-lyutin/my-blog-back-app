package com.amit.myblog.post.service.util;

import java.util.Set;

public record SearchCriteria(
        String titleQuery,
        Set<String> tagNames) {
}
