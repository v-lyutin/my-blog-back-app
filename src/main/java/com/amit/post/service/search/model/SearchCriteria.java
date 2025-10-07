package com.amit.post.service.search.model;

import java.util.Set;

public record SearchCriteria(
        String titleQuery,
        Set<String> tagNames) {
}
