package com.amit.post.service.search.util;

import com.amit.post.service.search.model.SearchCriteria;

import java.util.*;

public final class RawQueryParser {

    public static SearchCriteria parse(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return new SearchCriteria(null, Set.of());
        }
        String[] tokens = rawQuery.trim().split("\\s+");

        List<String> titleParts = new ArrayList<>();
        Set<String> tagNames = new HashSet<>();

        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            if (token.charAt(0) == '#') {
                String name = token.replaceFirst("^#+", "");
                if (!name.isBlank()) {
                    tagNames.add(name);
                }
                continue;
            }
            titleParts.add(token);
        }

        String titleQuery = titleParts.isEmpty() ? null : String.join(" ", titleParts);
        return new SearchCriteria(titleQuery, tagNames);
    }

    private RawQueryParser() {
        throw new UnsupportedOperationException();
    }

}
