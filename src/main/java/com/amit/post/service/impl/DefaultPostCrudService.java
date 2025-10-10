package com.amit.post.service.impl;

import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import com.amit.post.repository.PostCrudRepository;
import com.amit.post.service.PostCrudService;
import com.amit.post.service.exception.InvalidPostException;
import com.amit.post.service.exception.PostNotFoundException;
import com.amit.tag.model.Tag;
import com.amit.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class DefaultPostCrudService implements PostCrudService {

    private final PostCrudRepository postCrudRepository;

    private final TagService tagService;

    @Autowired
    public DefaultPostCrudService(PostCrudRepository postCrudRepository, TagService tagService) {
        this.postCrudRepository = postCrudRepository;
        this.tagService = tagService;
    }

    @Override
    @Transactional(readOnly = true)
    public PostView getById(long postId) {
        Post post = this.postCrudRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID %d not found.".formatted(postId)));
        Set<Tag> tags = this.tagService.getTagsByPostId(postId);
        return new PostView(post, this.getTagNames(tags));
    }

    @Override
    @Transactional
    public PostView create(PostView postView) {
        if (postView.post() == null) {
            throw new InvalidPostException("Post must not be null.");
        }
        Post createdPost = this.postCrudRepository.create(postView.post());
        if (postView.tags() != null) {
            this.tagService.replacePostTags(createdPost.getId(), postView.tags());
        }
        Set<Tag> tags = this.tagService.getTagsByPostId(createdPost.getId());
        return new PostView(createdPost, this.getTagNames(tags));
    }

    @Override
    @Transactional
    public PostView update(long postId, PostView postView) {
        if (postView.post() == null) {
            throw new InvalidPostException("Post must not be null.");
        }
        if (postView.post().getId() == null) {
            throw new InvalidPostException("ID must not be null for update.");
        }
        if (postView.post().getId() != postId) {
            throw new IllegalArgumentException("IDs for post must not be different");
        }
        Post updatedPost = this.postCrudRepository.update(postView.post())
                .orElseThrow(() -> new PostNotFoundException("Post with ID %d not found.".formatted(postView.post().getId())));
        if (postView.tags() != null) {
            this.tagService.replacePostTags(updatedPost.getId(), postView.tags());
        }
        Set<Tag> tags = this.tagService.getTagsByPostId(updatedPost.getId());
        return new PostView(updatedPost, this.getTagNames(tags));
    }

    @Override
    @Transactional
    public void deleteById(long postId) {
        boolean isDeleted = this.postCrudRepository.deleteById(postId);
        if (!isDeleted) {
            throw new PostNotFoundException(
                    "Post with ID %d not found.".formatted(postId)
            );
        }
    }

    @Override
    public void ensurePostExists(long postId) throws PostNotFoundException {
        if (!this.postCrudRepository.existsById(postId)) {
            throw new PostNotFoundException("Post with ID %d not found.".formatted(postId));
        }
    }

    private Set<String> getTagNames(Set<Tag> tags) {
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

}
