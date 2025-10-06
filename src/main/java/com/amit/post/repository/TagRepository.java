package com.amit.post.repository;

import com.amit.post.model.Tag;

import java.util.Collection;
import java.util.Set;

public interface TagRepository {

    Set<Tag> findTagsByPostId(long postId);

    Set<Tag> ensureTagsExist(Collection<String> tagNames);

    void attachTagsToPost(long postId, Collection<Long> tagIds);

    void replacePostTags(long postId, Collection<Long> tagIds);

}
