package com.amit.myblog.common.util;

import com.amit.myblog.tag.model.Tag;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TagNameExtractor {

    public static Collection<String> extractTagNames(Collection<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptySet();
        }
        return tags.stream()
                .map(Tag::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private TagNameExtractor() {
        throw new UnsupportedOperationException();
    }

}
