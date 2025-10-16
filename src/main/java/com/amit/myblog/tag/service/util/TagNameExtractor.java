package com.amit.myblog.tag.service.util;

import com.amit.myblog.tag.model.Tag;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class TagNameExtractor {

    public static Set<String> extractTagNames(Set<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptySet();
        }
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    private TagNameExtractor() {
        throw new UnsupportedOperationException();
    }

}
