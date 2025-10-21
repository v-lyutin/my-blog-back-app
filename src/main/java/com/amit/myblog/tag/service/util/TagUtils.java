package com.amit.myblog.tag.service.util;

import com.amit.myblog.common.util.TagNameExtractor;
import com.amit.myblog.tag.model.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class TagUtils {

    public static Set<String> extractTagNames(Collection<Tag> tags) {
        return new HashSet<>(TagNameExtractor.extractTagNames(tags));
    }

    public static Set<Long> extractTagIds(Collection<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }
        return tags.stream()
                .map(Tag::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
