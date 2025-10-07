package com.amit.post.service.crud;

import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import com.amit.post.repository.PostCrudRepository;
import com.amit.post.service.crud.exception.PostNotFoundException;
import com.amit.tag.model.Tag;
import com.amit.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

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
                .orElseThrow(() -> new PostNotFoundException(String.format("Post with ID %d not found.", postId)));
        Set<Tag> tags = this.tagService.getTagsByPostId(postId);
        return new PostView(post, tags);
    }

    @Override
    @Transactional
    public PostView create(Post post, Collection<String> tagNames) {
        Post createdPost = this.postCrudRepository.create(post);
        if (tagNames != null) {
            this.tagService.replacePostTags(createdPost.getId(), tagNames);
        }
        Set<Tag> tags = this.tagService.getTagsByPostId(createdPost.getId());
        return new PostView(createdPost, tags);
    }

    @Override
    @Transactional
    public PostView update(Post post, Collection<String> tagNames) {
        if (post.getId() == null) {
            throw new IllegalArgumentException("ID must not be null for update.");
        }
        Post updatedPost = this.postCrudRepository.update(post)
                .orElseThrow(() -> new PostNotFoundException(String.format("Post with ID %d not found.", post.getId())));
        if (tagNames != null) {
            this.tagService.replacePostTags(updatedPost.getId(), tagNames);
        }
        Set<Tag> tags = this.tagService.getTagsByPostId(updatedPost.getId());
        return new PostView(updatedPost, tags);
    }

    @Override
    @Transactional
    public boolean deleteById(long postId) {
        return this.postCrudRepository.deleteById(postId);
    }

}
