package com.amit.tag.repository;

import com.amit.tag.model.Tag;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface TagRepository {

    Set<Tag> findTagsByPostId(long postId);

    Set<Tag> ensureTagsExist(Collection<String> tagNames);

    void attachTagsToPost(long postId, Collection<Long> tagIds);

    void replacePostTags(long postId, Collection<Long> tagIds);

    Map<Long, Set<Tag>> findTagsByPostIds(Collection<Long> postIds);

}
