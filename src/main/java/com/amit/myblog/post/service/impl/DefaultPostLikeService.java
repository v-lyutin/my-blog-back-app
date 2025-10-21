package com.amit.myblog.post.service.impl;

import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.post.repository.PostLikeRepository;
import com.amit.myblog.post.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class DefaultPostLikeService implements PostLikeService {

    private final PostLikeRepository postLikeRepository;

    @Autowired
    public DefaultPostLikeService(PostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository;
    }

    @Override
    @Transactional
    public long incrementPostLikes(long postId) {
        return this.postLikeRepository.incrementPostLikes(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID %d not found.".formatted(postId)));
    }

}
