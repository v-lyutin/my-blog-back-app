package com.amit.tag.service.util;

import com.amit.tag.model.Tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class TagNameExtractor {

    public static Set<String> extractTagNames(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private TagNameExtractor() {
        throw new UnsupportedOperationException();
    }

}
