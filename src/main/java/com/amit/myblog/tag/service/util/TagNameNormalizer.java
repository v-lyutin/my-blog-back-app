package com.amit.myblog.tag.service.util;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class TagNameNormalizer {

    public static Set<String> normalizeTagNames(Collection<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
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
