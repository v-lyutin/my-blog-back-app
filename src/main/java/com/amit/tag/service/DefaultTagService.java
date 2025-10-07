package com.amit.tag.service;

import com.amit.tag.model.Tag;
import com.amit.tag.repository.TagRepository;
import com.amit.tag.service.util.TagNameNormalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class DefaultTagService implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public DefaultTagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Set<Tag> getTagsByPostId(long postId) {
        return this.tagRepository.findTagsByPostId(postId);
    }

    @Override
    public Set<Tag> ensureTagsExist(Collection<String> tagNames) {
        Set<String> normalizedTagNames = TagNameNormalizer.normalize(tagNames);
        return this.tagRepository.ensureTagsExist(normalizedTagNames);
    }

    @Override
    public void replacePostTags(long postId, Collection<String> tagNames) {
        Set<String> normalizedTagNames = TagNameNormalizer.normalize(tagNames);
        Set<Tag> tags = this.tagRepository.ensureTagsExist(normalizedTagNames);
        Set<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
        this.tagRepository.replacePostTags(postId, tagIds);
    }

    @Override
    public void attachTagsToPost(long postId, Collection<String> tagNames) {
        Set<String> normalizedTagNames = TagNameNormalizer.normalize(tagNames);
        Set<Tag> tags = this.tagRepository.ensureTagsExist(normalizedTagNames);
        Set<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
        this.tagRepository.attachTagsToPost(postId, tagIds);
    }

    // FIXME
    @Override
    public Map<Long, Set<Tag>> getTagsByPostIds(Collection<Long> postIds) {
        Map<Long, Set<Tag>> postsTagsStorage = new HashMap<>();
        if (postIds == null || postIds.isEmpty()) {
            return postsTagsStorage;
        }
        for (Long postId : postIds) {
            if (postId == null) {
                continue;
            }
            Set<Tag> tags = this.tagRepository.findTagsByPostId(postId);
            postsTagsStorage.put(postId, tags);
        }
        return postsTagsStorage;
    }

}
