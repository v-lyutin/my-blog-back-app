package com.amit.post.model;

import com.amit.tag.model.Tag;

import java.util.Set;

public record PostView(
        Post post,
        Set<Tag> tags) {
}
