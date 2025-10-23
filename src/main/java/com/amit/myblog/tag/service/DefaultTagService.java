package com.amit.myblog.tag.service;

import com.amit.myblog.tag.model.Tag;
import com.amit.myblog.tag.repository.TagRepository;
import com.amit.myblog.tag.service.util.TagNameNormalizer;
import com.amit.myblog.tag.service.util.TagUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultTagService implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public DefaultTagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Set<String> getTagsByPostId(long postId) {
        return TagUtils.extractTagNames(this.tagRepository.findTagsByPostId(postId));
    }

    @Override
    public Set<String> ensureTagsExist(Collection<String> tagNames) {
        Set<String> normalizedTagNames = TagNameNormalizer.normalizeTagNames(tagNames);
        if (normalizedTagNames.isEmpty()) {
            return Collections.emptySet();
        }
        return TagUtils.extractTagNames(this.tagRepository.ensureTagsExist(normalizedTagNames));
    }

    @Override
    public void replacePostTags(long postId, Collection<String> tagNames) {
        Set<String> normalizedTagNames = TagNameNormalizer.normalizeTagNames(tagNames);
        if (normalizedTagNames.isEmpty()) {
            this.tagRepository.replacePostTags(postId, Collections.emptySet());
            return;
        }
        Set<Long> tagIds = TagUtils.extractTagIds(this.tagRepository.ensureTagsExist(normalizedTagNames));
        this.tagRepository.replacePostTags(postId, tagIds);
    }

    @Override
    public void attachTagsToPost(long postId, Collection<String> tagNames) {
        Set<String> normalizedTagNames = TagNameNormalizer.normalizeTagNames(tagNames);
        if (normalizedTagNames.isEmpty()) {
            return;
        }
        Set<Long> tagIds = TagUtils.extractTagIds(this.tagRepository.ensureTagsExist(normalizedTagNames));
        this.tagRepository.attachTagsToPost(postId, tagIds);
    }

    @Override
    public Map<Long, Set<String>> getTagsByPostIds(Collection<Long> postIds) {
        Map<Long, Set<Tag>> postTagsStorage = this.tagRepository.findTagsByPostIds(postIds);
        if (postTagsStorage.isEmpty()) {
            return Collections.emptyMap();
        }
        return postTagsStorage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> TagUtils.extractTagNames(entry.getValue())
                ));
    }

}
