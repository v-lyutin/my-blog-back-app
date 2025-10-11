package com.amit.post.service.util;

import java.util.Set;

public record SearchCriteria(
        String titleQuery,
        Set<String> tagNames) {
}
