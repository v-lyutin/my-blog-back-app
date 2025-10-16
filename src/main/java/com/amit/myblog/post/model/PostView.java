package com.amit.myblog.post.model;

import java.util.Set;

public record PostView(
        Post post,
        Set<String> tags) {
}
