package com.amit.post.service.impl;

import com.amit.post.repository.PostImageRepository;
import com.amit.post.service.PostImageService;
import com.amit.post.service.exception.ImageUpsertException;
import com.amit.post.service.util.ImageValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public final class DefaultPostImageService implements PostImageService {

    private final PostImageRepository postImageRepository;

    public DefaultPostImageService(PostImageRepository postImageRepository) {
        this.postImageRepository = postImageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> getByPostId(long postId) {
        return this.postImageRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void upsertByPostId(long postId, byte[] data) {
        ImageValidator.validateSize(data);
        boolean isSaved = this.postImageRepository.upsertByPostId(postId, data);
        if (!isSaved) {
            throw new ImageUpsertException("Failed to upsert image for post %d".formatted(postId));
        }
    }

}
