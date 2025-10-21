package com.amit.myblog.tag.service;

import com.amit.myblog.tag.model.Tag;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface TagService {

    // Read-only: all tags of the post.
    Set<String> getTagsByPostId(long postId);

    // Ensures that given tag names exist and returns Tag entities.
    Set<String> ensureTagsExist(Collection<String> tagNames);

    // Replaces ALL tags of the post with the given names (creates missing tags).
    void replacePostTags(long postId, Collection<String> tagNames);

    // Appends (adds) given tags to the post without removing existing ones.
    void attachTagsToPost(long postId, Collection<String> tagNames);

    // Batch: tags for many posts in one go.
    Map<Long, Set<String>> getTagsByPostIds(Collection<Long> postIds);

}
