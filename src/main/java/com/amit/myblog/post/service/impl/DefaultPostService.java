package com.amit.myblog.post.service.impl;

import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.common.excpetion.ServiceException;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.model.PostView;
import com.amit.myblog.post.repository.PostRepository;
import com.amit.myblog.post.service.PostService;
import com.amit.myblog.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public final class DefaultPostService implements PostService {

    private final PostRepository postRepository;

    private final TagService tagService;

    @Autowired
    public DefaultPostService(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    @Override
    @Transactional(readOnly = true)
    public PostView getPostById(long postId) {
        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID %d not found.".formatted(postId)));
        Set<String> tags = this.tagService.getTagsByPostId(postId);
        return new PostView(post, tags);
    }

    @Override
    @Transactional
    public PostView addPost(PostView postView) {
        if (postView.post() == null) {
            throw new ServiceException("Post must not be null.");
        }
        Post createdPost = this.postRepository.save(postView.post());
        if (postView.tags() != null) {
            this.tagService.replacePostTags(createdPost.getId(), postView.tags());
        }
        Set<String> tags = this.tagService.getTagsByPostId(createdPost.getId());
        return new PostView(createdPost, tags);
    }

    @Override
    @Transactional
    public PostView editPost(long postId, PostView postView) {
        if (postView.post() == null) {
            throw new ServiceException("Post must not be null.");
        }
        if (postView.post().getId() == null) {
            throw new ServiceException("ID must not be null for update.");
        }
        if (postView.post().getId() != postId) {
            throw new ServiceException("IDs for post must not be different.");
        }
        Post updatedPost = this.postRepository.update(postView.post())
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID %d not found.".formatted(postView.post().getId())));
        if (postView.tags() != null) {
            this.tagService.replacePostTags(updatedPost.getId(), postView.tags());
        }
        Set<String> tags = this.tagService.getTagsByPostId(updatedPost.getId());
        return new PostView(updatedPost, tags);
    }

    @Override
    @Transactional
    public void deletePostById(long postId) {
        boolean isDeleted = this.postRepository.deleteById(postId);
        if (!isDeleted) {
            throw new ResourceNotFoundException("Post with ID %d not found.".formatted(postId));
        }
    }

}
