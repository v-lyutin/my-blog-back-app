package com.amit.common.util;

import com.amit.post.model.Post;
import com.amit.tag.model.Tag;

public final class ModelBuilder {

    public static Post buildPost(Long id, String title) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setText("text");
        post.setLikesCount(0);
        post.setCommentsCount(0);
        return post;
    }

    public static Post buildPost(Long id, String title, String text, int likesCount, int commentsCount) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setText(text);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        return post;
    }

    public static Tag buildTag(long id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }

}
