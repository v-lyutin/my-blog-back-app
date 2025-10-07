package com.amit.tag.service.util;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class TagNameNormalizer {

    public static Set<String> normalize(Collection<String> tagNames) {
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
