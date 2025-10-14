package com.amit.tag.repository.jdbc.util;

import com.amit.tag.model.Tag;

public final class PostTagRow {

    final long postId;

    final Tag tag;

    public PostTagRow(long postId, Tag tag) {
        this.postId = postId;
        this.tag = tag;
    }

    public long getPostId() {
        return this.postId;
    }

    public Tag getTag() {
        return this.tag;
    }

}
