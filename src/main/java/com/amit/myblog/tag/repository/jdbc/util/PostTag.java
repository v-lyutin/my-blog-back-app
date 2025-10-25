package com.amit.myblog.tag.repository.jdbc.util;

import com.amit.myblog.tag.model.Tag;

public record PostTag(
        long postId,
        Tag tag) {
}
