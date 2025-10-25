package com.amit.myblog.tag.service.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class TagNameNormalizer {

    public static Set<String> normalizeTagNames(Collection<String> tagNames) {
        if (tagNames == null) {
            return Collections.emptySet();
        }
        return tagNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tagName -> !tagName.isEmpty())
                .collect(Collectors.toSet());
    }

    private TagNameNormalizer() {
        throw new UnsupportedOperationException();
    }

}
