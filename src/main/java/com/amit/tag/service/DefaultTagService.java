package com.amit.tag.service;

import com.amit.tag.model.Tag;
import com.amit.tag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
    @Transactional(readOnly = true)
    public Set<Tag> getTagsByPostId(long postId) {
        return this.tagRepository.findTagsByPostId(postId);
    }

    @Override
    @Transactional
    public Set<Tag> ensureTagsExist(Collection<String> tagNames) {
        return this.tagRepository.ensureTagsExist(tagNames);
    }

    @Override
    @Transactional
    public void replacePostTags(long postId, Collection<String> tagNames) {
        Set<Tag> tags = this.tagRepository.ensureTagsExist(tagNames);
        Set<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
        this.tagRepository.replacePostTags(postId, tagIds);
    }

    @Override
    @Transactional
    public void attachTagsToPost(long postId, Collection<String> tagNames) {
        Set<Tag> tags = this.tagRepository.ensureTagsExist(tagNames);
        Set<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
        this.tagRepository.attachTagsToPost(postId, tagIds);
    }

}
